package org.osgi.security.server.provider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.security.test.api.RemoteTestingConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * 
 */

@Component(name = "org.osgi.security.server", service = {})
public class TestServer implements RemoteTestingConstants
{
	@interface Config {
		int port() default 2009;
		String log_file() default "generated/log.txt";
		boolean redirect_sysout() default false;
	}
	
	private Thread worker;
	private BundleContext context;
	
	@Activate
	void start(final Config config, BundleContext context) throws Exception
	{
		this.context = context;
		
		worker = new Thread() 
		{	
			public void run()
			{
				System.out.println("Server started...");
				
				try (ServerSocket ss = new ServerSocket(config.port())) {
					ss.setSoTimeout(10000);
					for(;;) {
						Map<String, Bundle> installed = new HashMap<>();
						System.out.println("Waiting...");
						try (Socket s = ss.accept()) {
							
							DataInputStream input = new DataInputStream(s.getInputStream());
							DataOutputStream output = new DataOutputStream(s.getOutputStream());
							commands: for (;;) {
								
								String command = input.readUTF();
								switch(command) {
									case RUN_TEST :
										runTest(installed, input, output);
										break;
									case END :
										break commands;
									default :
										System.err.println("Received an unexpected command " 
												+ command + " exiting");
										break commands;
								}
							}
						} catch (SocketTimeoutException ste) {
							
						}
						
						for(Bundle b : installed.values()) {
							String location = b.getLocation();
							b.uninstall();
							new File(location).delete();
						}
						
						if(Thread.currentThread().isInterrupted()) {
							break;
						}
					}
				} catch (BindException e) {
					System.err.println("!!: Le port " + config.port() + " est déjà utilisé !");
				} catch (Exception e) {
					e.printStackTrace(System.err);
			    }
			}
			
		};
		worker.start();
    }

    private void runTest(Map<String, Bundle> installed, DataInputStream input, DataOutputStream output) throws Exception {
    	String symbolicName = input.readUTF();
		Version version = Version.parseVersion(input.readUTF());
		String testClass = input.readUTF();
		String testName = input.readUTF();
		
		Bundle testBundle = getTestBundle(installed, input, output, symbolicName, version);
		
		Class<?> testClazz = testBundle.loadClass(testClass);
		
		Result result = new JUnitCore().run(Request.method(testClazz, testName));
		
		if(result.wasSuccessful()) {
			output.writeUTF(SUCCESS);
		} else {
			Failure failure = result.getFailures().get(0);
			output.writeUTF(FAIL);
			output.writeUTF(failure.getMessage());
		}
		
		output.flush();
	}

	private Bundle getTestBundle(Map<String, Bundle> installed, DataInputStream input, DataOutputStream output, String symbolicName,
			Version version) throws IOException, Exception, BundleException {
		Bundle testBundle = null;
		for (Bundle b : context.getBundles()) {
			if(b.getSymbolicName().equals(symbolicName) && b.getVersion().equals(version)) {
				testBundle = b;
				break;
			}
		}
		
		if(testBundle == null) {
			output.writeUTF(GET_BUNDLE);
			output.writeUTF(symbolicName);
			output.writeUTF(version.toString());
			output.flush();
			
			String response = input.readUTF();
			if(!BUNDLE.equals(response)) {
				throw new IllegalStateException("Unexpected response to  " + 
						GET_BUNDLE + ", got " + response);
			}
			
			String id = input.readUTF() + "#" + input.readUTF();
			
			if(!id.equals(symbolicName + "#" + version)) {
				throw new IllegalStateException("Unexpected bundle, wanted  " + 
						symbolicName + "#" + version + ", got " + id);
			}
			
			testBundle = installBundle(input, input.readInt());
			installed.put(id, testBundle);
			testBundle.start();
		}
		return testBundle;
	}

	private Bundle installBundle(DataInputStream input, int toRead) throws Exception {

    	File tempDir = context.getDataFile("resources");
    	tempDir.mkdirs();
    	
		File dataFile = File.createTempFile("download","bundle", tempDir);
    	dataFile.deleteOnExit();
    	
    	byte[] buffer = new byte[4096];
    	
    	try (FileOutputStream fos = new FileOutputStream(dataFile)) {
	    	while(toRead > 0) {
	    		int block = Math.min(toRead, buffer.length);
				input.readFully(buffer, 0, block);
	    		fos.write(buffer, 0, block);
	    		toRead -= buffer.length;
	    	}
    	}
    	
    	return context.installBundle(dataFile.toURI().toString());
	}

	@Deactivate
	void stop() throws Exception
	{
    	worker.interrupt();
	}
}