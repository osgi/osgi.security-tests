package org.osgi.security.util.api;

import java.lang.Thread;
import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.lang.System;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.String;
import java.lang.Exception;

import org.osgi.framework.BundleContext;
import org.osgi.security.util.api.Util;

/**
 * This is the implementation.It registers a Util service.
 */
public class UtilImpl implements Util 
{
	private String bundleName;

    private static boolean debug = false;
    private static BundleContext bundleContext;

    private boolean displayConsole, displayTest, displayServer, initFile=false;
    private String IP;
    private int port, dport, sport;

    private File output, outputFolder = new File("/tmp/features/F1/output");
    private FileWriter fw;
    private BufferedWriter bw;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    static BundleContext getContext()
    {
        return bundleContext;
    }

    public UtilImpl(BundleContext context, boolean displayBox, boolean displayServer, String serverIP, int serverPort,
			int serverDPort, int serverSPort, boolean automatedTesting) throws Exception
    {
		// TODO Auto-generated constructor stub
    
        if (automatedTesting)
        {
            this.displayConsole = false;
            this.displayTest    = true;
        }
        else
        {
            this.displayConsole = displayBox;
            this.displayTest    = false;
        }
        
        this.displayServer    = displayServer;
        this.IP               = serverIP;
        this.port             = serverPort;
        this.dport            = serverDPort;
        this.sport            = serverSPort;
        
        UtilImpl.bundleContext    = context;

        // If server config is not null
        if (displayServer && IP != null && port != 0)
        {
            try
            {
                socket = new Socket(IP,port);
            }
            catch (UnknownHostException e)
            {
                throw new Exception("The hostname specified in the file 'com.sogetiht.otb.properties.cfg' cannot be resolved to a network address");
            }
            catch(IOException e)
            {
                throw new Exception("An error has occurred while establishing a connection to the server. Please check your network connection and ensure that the server is running.");
            }

            if (socket != null)
            {
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader (socket.getInputStream()));
            }
            else
            {
                System.out.println("The socket is null or not connected.");
            }
        }
        else
        {
            if (displayServer)
            {
                System.out.println("The server IP and/or port are not specified in the file 'com.sogetiht.otb.properties.cfg'.");
            }
        }
    }

    
    public String getResourcePath(String name, Class<?> bundle) throws Exception
    {
        throw new Exception("Not implemented because of IS2T Framework");
    }

    
    public InputStream getResourcePathAsStream(String name, Class<?> bundle)
    {
    	System.out.println("bundle.getResourceAsStream : " + name);
        return bundle.getResourceAsStream("/META-INF/resources/" + name);
    }


    public String[] split(String str, char limit)
    {
        Vector<String> matchList = new Vector<String>();
        String[] matchArray;
        int start=0;

        for (int end=0; end<str.length(); end++)
        {
            if (str.charAt(end)==limit)
            {
                matchList.addElement(str.substring(start,end));
                start=end+1;
            }
        }
        
        if (start!=str.length()+1)
        {
            matchList.addElement(str.substring(start,str.length()));
        }
            
        matchArray = new String[matchList.size()];
        matchList.copyInto(matchArray);

        return matchArray;
    }

    
    public String receiveFile()
    {
        ServerSocket ss;
        Socket socket;
        InputStream is;
        BufferedReader in;

        FileOutputStream outStream;
        byte[] buffer = new byte[150000];
        int bytesRead = 0;
        String cmd;
        String name = null;
        String[] splitCmd;

        //int filesize=6022386, current = 0, bytesRead;
        //byte [] byteArray;
        //FileOutputStream fos;
        //BufferedOutputStream bos;
        //InputStream is;
        //String path = null;
        try
        {
            ss = new ServerSocket(sport);
            socket = ss.accept();
            is = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));

            cmd = in.readLine();
            System.out.println("DEBUG cmd="+cmd);
            splitCmd = split(cmd,':');
            name = splitCmd[1];
            System.out.println("DEBUG splitCmd[0]="+splitCmd[0]);
            System.out.println("DEBUG splitCmd[1]="+splitCmd[1]);

            if (splitCmd[0].equals("SENDING_FILE"))
            {
                outStream = new FileOutputStream(name);
                int debug = 0;
                do
                {
                    bytesRead = is.read(buffer, 0, buffer.length);
                    System.out.println("DEBUG bytesRead="+bytesRead);
                    if (bytesRead >= 0)
                    {
                        System.out.println("DEBUG OK");
                        debug += bytesRead;
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
                while(bytesRead > -1);

                outStream.flush();
                outStream.close();
                System.out.println("DEBUG debug="+debug);
                println(name + " : successfully received");
                
            }
            else
            {
                err("Error on downloading file!");
            }
            socket.close();
        }
        catch (Exception e)
        {
            err(e);
        }
        
        return name;
    }


    public double sendCmd(String string) throws Exception
    {    	
    	if (socket != null && displayServer)
    	{
            if (socket.isConnected())
            {
                try
                {
                    out.println(string);
                    out.flush();
                }
                catch (Exception e)
                {
                    throw new Exception("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                }
            }
            else
            {
            	throw new Exception("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
            }
    	}
    	else
    	{
            throw new Exception("No connection to the server. Please fill in the configuration file and ensure that the server is running");
    	}
    	
    	return 0;
    }

    
    public double sendFile(File file) throws Exception
    {
        if (socket != null && displayServer)
        {
            if (socket.isConnected())
            {
                if (file.length() != 0 )
                {
                	try
                	{
                		sendCmd("::send::");
			
                		// create the file stream
                		Socket socket4File         = new Socket(IP,dport);
                		OutputStream os            = socket4File.getOutputStream();
                		FileInputStream fileStream = new FileInputStream(file);
                		PrintWriter out            = new PrintWriter(os);
			
                		// send a message before streaming the file
                		out.println("SENDING_FILE:"+file.getName());
                		out.flush();
			
                		// send the file stream
                		int step       = 150000;
                		byte[] buffer  = new byte[step];
                		long completed = 0;
                		long fileSize  = file.length();
						
                		while (fileSize - completed > (long) step)
                		{
                			fileStream.read(buffer);
                			os.write(buffer);
                			os.flush();
                			completed += step;
                		}
			
                		buffer = new byte[(int)(fileSize-completed)];
                		fileStream.read(buffer);
                		os.write(buffer);
                		os.flush();
			
                		// close the stream
                		fileStream.close();
                		socket4File.close();
			
                		println_without_server(file.getName()+" : successfully sent!");
			
                	}
                	catch (Exception e)
                	{
                		throw new Exception("An error has occurred while establishing a connection to the server. Please check your network connection and ensure that the server is running.");
                	}
                }
                else
                {
                	throw new Exception("Yout try to send an empty file.");
                }
            }
            else
            {
            	throw new Exception("An error has occurred while establishing a connection to the server. Please check your network connection and ensure that the server is running.");
            }
        }
        else
        {
        	throw new Exception("No connection to the server. Please fill in the configuration file and ensure that the server is running");
        }
        
        return 0;
    }
    
    
    public void println_without_server(String value)
    {
        try
        {
            if (displayConsole)
            {
                if (bundleName != null)
                {
                    System.out.println("[" + bundleName + "] " + value);
                }
                else
                {
                    System.out.println(value);
                }
            }
            if (displayTest&&initFile)
            {
            	bw.write(value+"\n");
            	bw.flush();
            }
        }
        catch (Exception e)
        {
            err(e);
            e.printStackTrace();
        }
    }
    
    public void println(String value)
    {
        try
        {
            if (displayConsole)
            {
                if (bundleName != null)
                {
                    System.out.println("[" + bundleName + "] " + value);
                }
                else
                {
                    System.out.println(value);
                }
            }
            
            if (displayTest&&initFile)
            {
            	if (bundleName != null)
            	{
            		bw.write("[" + bundleName + "] " + value+"\n");
            	}
            	else
            	{
            		bw.write(value+"\n");
            	}
            	bw.flush();
            }
            
            if (socket != null && displayServer)
            {
                if (socket.isConnected())
                {
                    try
                    {
                        if (bundleName != null)
                        {
                            out.println("[" + bundleName + "] " + value);
                        }
                        else
                        {
                            out.println(value);
                        }
                        out.flush();
                    }
                    catch (Exception e)
                    {
                        err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                        if (debug)
                        {
                        	err(e);e.printStackTrace();
                        }
                    }
                 }
             }
        } 
        catch (Exception e)
        {
            err(e);
            e.printStackTrace();
        }
    }
    
    
    public void println(char value)
    {
        println(String.valueOf(value));
    }
    
    
    public void println(int value)
    {
        println(String.valueOf(value));
    }
    
    
    public void println(long value)
    {
        println(String.valueOf(value));
    }
    
    
    public void println(boolean value)
    {
        println(String.valueOf(value));
    }


    public void print(String value)
    {
        try
        {
            if (displayConsole)
            {
                if (bundleName != null)
                {
                    System.out.print("[" + bundleName + "] " + value);
                }
                else
                {
                    System.out.print(value);
                }
            }
            
            if (displayTest&&initFile)
            {
                if (bundleName != null)
                {
                    bw.write("[" + bundleName + "] " + value);
                }
                else
                {
                    bw.write(value);
                }
                bw.flush();
            }
            
            if (socket != null && displayServer)
            {
                if (socket.isConnected())
                {
                    try
                    {
                        if (bundleName != null)
                        {
                            out.print("[" + bundleName + "] " + value);
                        }
                        else
                        {
                            out.print(value);
                        }
                        out.flush();
                    }
                    catch (Exception e)
                    {
                        err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                    }
                }
            }
        }
        catch (Exception e) 
        {
            err(e);
            e.printStackTrace();
        }
    }
    
    
    public void print(char value)
    {
        print(String.valueOf(value));
    }
    
    
    public void print(int value)
    {
        print(String.valueOf(value));
    }
    
    
    public void print(long value)
    {
        print(String.valueOf(value));
    }
    
    
    public void print(boolean value)
    {
        print(String.valueOf(value));
    }


    public void err(Exception e)
    {
        try
        {
            String message = e.getMessage();
            if (message != null)
            {
                err(e.getClass().getName() + ": " + message);
            }
            else
            {
                err(e.getClass().getName());
            }
        }
        catch (Exception ex)
        {
            err(ex.getMessage());
        }
    }
    
    
    public void err(String string)
    {
        try
        {
            if (displayConsole)
            {
                if (bundleName != null)
                {
                    System.err.println("[" + bundleName + "] ERROR: " + string);
                }
                else
                {
                    System.err.println("ERROR: " + string);
                }
            }
            
            if (displayTest&&initFile)
            {
                if (bundleName != null)
                {
                    bw.write("[" + bundleName + "] ::ERROR::\n");
                }
                else
                {
                    bw.write("::ERROR::\n");
                }
                bw.flush();
            }
            
            if (socket != null && displayServer)
            {
                if (socket.isConnected())
                {
                    try
                    {
                        if (bundleName != null)
                        {
                            out.println("[" + bundleName + "] ERROR: " + string);
                        }
                        else
                        {
                            out.println("ERROR: " + string);
                        }
                        out.flush();
                    }
                    catch (Exception e)
                    {
                        if (bundleName != null)
                        {
                            System.err.println("[" + bundleName + "] An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                        }
                        else
                        {
                            System.err.println("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                        }
                        if (debug)
                        {
                        	e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }


    public String receiveCmd()
    {
        String answer = null;
        try
        {
            answer = in.readLine();
        }
        catch (IOException e)
        {
            err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
            if (debug)
            {
            	err(e);
            	e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            err(e);
            e.printStackTrace();
        }
        
        return answer;
    }


    private void initFile(String name)
    {
		try
		{
			if (!outputFolder.exists())
			{
				outputFolder.mkdir();
				if (!outputFolder.isDirectory())
				{
					throw new Exception("'output' already exists and is no directory");
				}
				output = new File(outputFolder.getPath(), name);
				fw = new FileWriter(output.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				initFile=true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			err(e);
		}
    }
    
    
    private void closeFile()
    {
    	try
    	{
    		bw.close();
    		fw.close();
    	}
    	catch (Exception e)
    	{
    		err(e);
    	}
    }
    
    
    public void start(String name, String title, String description)
    {
    	synchronized (this)
    	{
    		if (displayTest)
    		{
    			initFile(name);
    		}
    		else
    		{
    			try
    			{
    				Thread.sleep(1500);
    			}
    			catch (Exception e)
    			{
    				err(e);
    			}
		
    			System.out.println("---------------------------------------------------------------------------------");
    			System.out.println(" __   __   __  ___    __   ___  __       __  ___ ___       ___  ___  __ ___  __  ");
    			System.out.println("/  \\ |__  | __  |    |__  |__  /   |  | |__|  |   |  \\ /    |  |__  |__  |  |__  ");
    			System.out.println("\\__/  __| |__| _|_    __| |___ \\__ |__| |  \\ _|_  |   |     |  |___  __| |   __| ");
    			System.out.println("");
    			System.out.println("---------------------------------------------------------------------------------");
				System.out.println("");
				System.out.println("");
				System.out.println(" -----------");
				System.out.println("| " + name + " |");
				System.out.println(" -----------");
				System.out.println(title);
				System.out.println("");
				System.out.println(" -------------");
				System.out.println("| Description |");
				System.out.println(" -------------");
				System.out.println(description);

				System.out.println("");
				System.out.println(" ---------------------");
				System.out.println("| Start of the bundle |");
				System.out.println(" ---------------------");

				try
				{
					Thread.sleep(1500);
				}
				catch (Exception e)
				{
					err(e);
				}
    		}
		}
    }

    
    private void stopFramework()
    {
    	try
    	{
    		getContext().getBundle(0).stop();
    	} 
    	catch (Exception e) 
    	{
    		err(e);
    	}
    }

    
    public void stop(boolean succeed) 
    {
    	try 
    	{
    		Thread.sleep(1500);
    	} 
    	catch (Exception e) 
    	{
    		err(e);
    	}
    	
    	if (displayTest)
	    {
    		closeFile();
    		stopFramework();
	    } 
    	else 
    	{
    		System.out.println(" -------------------");
    		System.out.println("| End of the bundle |");
    		System.out.println(" -------------------");
    	}
    	
    	// Close the socket
    	if (socket != null && displayServer)
    	{
    		if (socket.isConnected())
    		{
    			try
    			{
    				if (bundleName != null)
    				{
    					out.println("[" + bundleName + "] Exit");
    				}
    				else
    				{
    					out.println("Exit");
    				}
    				out.flush();
    				socket.close();
    			}
    			catch (IOException e)
    			{
    				err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
    				if (debug) 
    				{
    					err(e);
    					e.printStackTrace();
    				}
    			}
    			catch (Exception e)
    			{
    				err(e);
    				e.printStackTrace();
    			}
    		}
    	}
    }

    
    public void setBundleName(String bundleName)
    {
    	this.bundleName = bundleName;
    }

}
