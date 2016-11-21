package org.osgi.security.test.api;

import static org.osgi.framework.FrameworkUtil.getBundle;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * 
 **/

public class OSGiSecurityTestRunner extends BlockJUnit4ClassRunner implements RemoteTestingConstants {
	
	private final BundleContext ctx;
	
	private final Socket serverConnection;
	
	public OSGiSecurityTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		ctx = getBundleContext();
		
		String server = ctx.getProperty("server.ip");
		
		if (server != null) {
			String port = ctx.getProperty("server.port");
			if (port == null) {
				port = "2009";
			}
			try {
				serverConnection = new Socket(server, Integer.parseInt(port));
			} catch (IOException ioe) {
				throw new InitializationError(new RuntimeException(
						"Unable to connect to server " + server + ":" + port, ioe));
			}
		} else {
			serverConnection = null;
		}
	}
	
	private BundleContext getBundleContext() throws InitializationError {
		Bundle bundle = getBundle(OSGiSecurityTestRunner.class);
	
		if(bundle == null) {
			throw new InitializationError("This test must be run inside an OSGi framework");
		}
	
		BundleContext ctx = bundle.getBundleContext();
		if (ctx == null) {
			// The Util bundle may not be started, and it needs to
			// be
			try {
				bundle.start();
			} catch (BundleException be) {
				throw new InitializationError(be);
			}
			ctx = bundle.getBundleContext();
		}
	
		if(ctx == null) {
			throw new InitializationError("Unable to activate the tester bundle");
		}
		return ctx;
	}

	@Override
	protected Statement withAfterClasses(final Statement statement) {
		final Statement s = super.withAfterClasses(statement);
		
		return serverConnection == null ? s : new Statement() {
				@Override
				public void evaluate() throws Throwable {
					try {
						s.evaluate();
					} finally {
						try {
							new DataOutputStream(serverConnection.getOutputStream()).writeUTF(END);
						} catch (Exception e) {
							// Never mind
						} finally {
							serverConnection.close();
						}
					}
				}
			};
	}

	@Override
	protected Statement withBefores(final FrameworkMethod method, Object target, Statement statement) {
		final Statement s = super.withBefores(method, target, statement);
		
		return serverConnection == null ? s : new Statement() {
			@Override
			public void evaluate() throws Throwable {
				runRemoteTest(method.getName());
			}
		};
	}

	private void runRemoteTest(String methodName)
			throws Exception {
	
		DataOutputStream dataOutput = new DataOutputStream(serverConnection.getOutputStream());
		DataInputStream dataInput = new DataInputStream(serverConnection.getInputStream());

		dataOutput.writeUTF(RUN_TEST);
		dataOutput.flush();

		Bundle b = getBundle(getTestClass().getJavaClass());

		dataOutput.writeUTF(b.getSymbolicName());
		dataOutput.writeUTF(b.getVersion().toString());
		dataOutput.writeUTF(getTestClass().getName());
		dataOutput.writeUTF(methodName);
		dataOutput.flush();

		test: for (;;) {
			String command = dataInput.readUTF();
			switch (command) {
			case GET_BUNDLE:
				sendBundle(dataInput, dataOutput);
				break;
			case SUCCESS:
				break test;
			case FAIL:
				throw new AssertionError(dataInput.readUTF());
			}
		}
	}

	private void sendBundle(DataInput dataInput, DataOutputStream dataOutput)
			throws IOException {
		String symbolicName = dataInput.readUTF();
		Version version = Version.parseVersion(dataInput.readUTF());
	
		String location = null;
	
		for (Bundle b : ctx.getBundles()) {
			if (b.getSymbolicName().equals(symbolicName) && b.getVersion().equals(version)) {
				location = b.getLocation();
				break;
			}
		}
	
		if (location == null) {
			throw new IllegalArgumentException(
					"No bundle exists with name " + symbolicName + " and version " + version);
		} else if (location.startsWith("reference:")) {
			//Equinox is unkind, and reference: urls are not readable :(
			//Remove reference: and it should be a normal file URL
			location = location.substring(10);
		}
	
		File f = new File(URI.create(location));
	
		try (InputStream is = new FileInputStream(f)) {
			dataOutput.writeUTF(BUNDLE);
			dataOutput.writeUTF(symbolicName);
			dataOutput.writeUTF(version.toString());
			dataOutput.writeInt((int) f.length());
	
			byte[] buffer = new byte[4096];
			int i;
	
			while ((i = is.read(buffer, 0, buffer.length)) != -1) {
				dataOutput.write(buffer, 0, i);
			}
			dataOutput.flush();
		}
	}
}