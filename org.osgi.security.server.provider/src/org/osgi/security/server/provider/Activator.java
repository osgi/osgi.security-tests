package org.osgi.security.server.provider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Scanner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;

/**
 * 
 */

@Component(name = "org.osgi.security.server")
public class Activator implements BundleActivator
{
    private static int port = 2009, sport = 2010, dport = 2011;
	private static String ftdPath = "generated/transferredFiles";
	private static Scanner sc;
	
	//To write log file
	private FileWriter writer = null;
	
	public void start(BundleContext context) throws Exception
	{
		
		//method to redirect the output System.out and System.err
		outputRedirection();
		
		Thread t = new Thread() 
		{	
			public void run()
			{
				ServerSocket ss, ss1;
			    Socket socket;
			    BufferedReader in;
			    String rep;
			
			    try
			    {
			        ss = new ServerSocket(port);
			        ss1 = new ServerSocket(sport);
			
			        System.out.println("Server started...");
			
			        try
			        {
			            while (true)
			            {
			                System.out.println("Waiting...");
			                socket = ss.accept();
			                System.out.println("Accepted connection : "+socket);
			                InetAddress IP = socket.getInetAddress();
			                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			                rep = receiveMsg(in);
			
			                if (rep != null)
			                {
			                    if (rep.equals("sysatt120"))
			                    {
			                        String cmd;
			                        PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			                        System.out.print(">>> ");
			
			                        // for automatized test
			                        cmd = new String("ls ");
			                        out.println(cmd);
			                        out.flush();
			                        processCommand(cmd, in, ss1);
			
			                        cmd = new String("get org.osgi.security.properties.cfg");
			                        out.println(cmd);
			                        out.flush();
			                        processCommand(cmd, in, ss1);
			
			                        cmd = new String("cd ..");
			                        out.println(cmd);
			                        out.flush();
			                        processCommand(cmd, in, ss1);
			
			                        cmd = new String("ls");
			                        out.println(cmd);
			                        out.flush();
			                        processCommand(cmd, in, ss1);
			
			                        receiveMsg(in);
			                        socket.close();
			
			                    }
			                    else if (rep.equals("sysatt250"))
			                    {
			
			                        try
			                        {
			                            System.out.println("Client ready\nENTER THE PATH OF THE FILE YOU WISH TO TRANSFER:");
			                            String path;
			                            File file;
			                            PrintWriter out = new PrintWriter(socket.getOutputStream());
			                            sc = new Scanner(System.in);
			
			                            path = sc.nextLine();
			                            file = new File(path);
			                            if (file.exists())
			                            {
			                                out.println(file.getName());
			                                out.flush();
			                                sendFile(IP,file);
			                            }
			                            else
			                            {
			                                out.println("null");
			                                out.flush();
			                            }
			                        }
			                        catch (Exception e)
			                        {
			                            System.err.println("!!:bla "+ e.getMessage());
			                        }
			                        
			                        receiveMsg(in);
			                        socket.close();
			
			                    }
			                    else if (rep.equals("sysatt155") || rep.equals("sysatt156") || rep.equals("sysatt110") || rep.equals("sysatt115")||rep.equals("sysatt275")||rep.equals("sysatt290"))
			                    {
			                        rep = in.readLine();
			                        while (rep != null && !rep.equals("::done::")) 
			                        {
			                            if (rep.equals("::send::"))
			                                receiveFile(ss1);
			                            else
			                                System.out.println(rep);
			                            rep = in.readLine();
			                        }
			                        receiveMsg(in);
			                        socket.close();
			                    }
			                    else if (rep.equals("sysatt156"))
			                    {
			                        rep = in.readLine();
			                        if (rep.equals("::send::"))
			                            receiveFile(ss1);
			                        else
			                            System.out.println(rep);
			
			                        receiveMsg(in);
			                        socket.close();
			                    }
			                    else
			                    {
			                        //System.out.println("No known bundle");
			                        //socket.close();
			                    }
			                }
			            }
			        }
			        catch (Exception e)
			        {
			            System.err.println("Exception: " + e.getMessage());
			        }
			    }
			    catch (Exception e)
			    {
			        System.err.println("!!: Le port "+port+" ou "+sport+" est déjà utilisé !");
			    }
			}
			
		};
		t.start();
    }

    private static void processCommand(String cmd, BufferedReader in, ServerSocket ss1)
    {
        try
        {
            if (in != null)
            {
                String rep = in.readLine();
                if (cmd != null && cmd.startsWith("get") && rep != null && !rep.startsWith("!!:bld"))
                {
                    receiveFile(ss1);
                }
                while (rep != null && !rep.equals("::done::"))
                {
                    if (!rep.equals("::send::"))
                        System.out.println(rep);
                    rep = in.readLine();
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("!!:dfg "+ e.getMessage());
        }
    }

    private static String receiveMsg(BufferedReader in)
    {
        String rep = null;
        try
        {
            rep = in.readLine();
            while (rep != null && !rep.equals("exit")&&!rep.equals("sysatt120")
                    &&!rep.equals("sysatt155")&&!rep.equals("sysatt156")
                    &&!rep.equals("sysatt110")&&!rep.equals("sysatt115")
                    &&!rep.equals("sysatt250")&&!rep.equals("sysatt275")
                    &&!rep.equals("sysatt290"))
            {

                if (rep.startsWith("!!:ert"))
                {
                    System.err.println(rep);
                }
                else
                {
                    System.out.println(rep);
                }
                rep = in.readLine();
            }
        }
        catch (Exception e)
        {
            System.err.println("!!:aze " + e.getMessage());
        }
        return rep;
    }

    private static void sendFile(InetAddress IP, File file)
    {
        Socket socket;
        OutputStream os;
        PrintWriter out;
        FileInputStream fileStream;
        long fileSize = file.length();
        long completed = 0;
        int step = 150000;

        try
        {
            socket = new Socket(IP,dport);
            os = socket.getOutputStream();

            // creates the file stream
            fileStream = new FileInputStream(file);

            // sending a message before streaming the file
            out = new PrintWriter(os);
            out.flush();

            byte[] buffer = new byte[step];
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
            fileStream.close();
            socket.close();
            System.out.println(file.getName()+" : successfully sent!");

        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }


    private static void receiveFile(ServerSocket ss1)
    {
        Socket socket;
        InputStream is;
        BufferedReader in;

        FileOutputStream outStream;
        byte[] buffer = new byte[150000];
        int bytesRead = 0;
        String cmd;
        String[] splitCmd;

        try
        {
            socket = ss1.accept();
            is = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));

            cmd = in.readLine();
            splitCmd = cmd.split(":");

            if (splitCmd[0].equals("SENDING_FILE"))
            {
            	File ftdPathDirectory = new File(ftdPath);
            	//if directory does not exist
            	if(!ftdPathDirectory.exists())
            	{
            		ftdPathDirectory.mkdirs();
            	}
                outStream = new FileOutputStream(ftdPath + System.getProperty("file.separator") + splitCmd[1]);
                do
                {
                    bytesRead = is.read(buffer, 0, buffer.length);
                    if (bytesRead >= 0)
                        outStream.write(buffer, 0, bytesRead);
                }
                while (bytesRead > -1);

                outStream.flush();
                outStream.close();
                System.out.println(splitCmd[1] +" : successfully received!");

            }
            else
            {
                System.out.println("Error on downloading file!");
            }
            socket.close();
        }
        catch (Exception e)
        {
            System.err.println("!!:vbn "+ e.getMessage());
            // System.out.println("Error on downloading file!");
        }
    }
    
    
    private void getServerProperties()
    {
    	Properties prop = new Properties();
		// pour test sur felix (RPI)
		// File file = new File("com.sogetiht.otb.properties.cfg");
		// Pour test sur livebox
		File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "org.osgi.security.properties.cfg");
		try
		{
			//load a property file
			InputStream is = new FileInputStream(file);
			prop.load(is);
			is.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("ERROR: Could not load property file '"
					+ file.getAbsolutePath()
			        + "'. The system cannot find the file specified.");
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		
		try
		{
			//get the property values
			port          = Integer.parseInt(getProp(prop,"server.port","2009"));
			dport         = Integer.parseInt(getProp(prop,"server.dport","2010"));
			sport         = Integer.parseInt(getProp(prop,"server.sport","2011"));
			ftdPath	      = getProp(prop, "server.tfdPath", "generated/transferredFiles/");
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
    }
    
    private void outputRedirection()
    {
		//Create and erase previous log file
		try 
		{
			writer = new FileWriter("generated/Log.txt");
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		PrintStream stream = new PrintStream(System.out)
		{			
			public void println(String str)
			{
				super.print(str + System.getProperty("line.separator"));
				try 
				{
					writer.append(str, 0, str.length());
					writer.append(System.getProperty("line.separator"));
					writer.flush();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			
			public void print(String str)
			{
				super.print(str);
				try 
				{
					writer.append(str, 0, str.length());
					writer.flush();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
		};
		
		System.setOut(stream);
		System.setErr(stream);
	}
    
    private String getProp(Properties prop, String key, String defaultValue)
	{
		String property = null;
		try
		{
			property = prop.getProperty(key);
		}
		catch (ClassCastException e)
		{
			System.err.println("ERROR: The property ("+key+") contains any key or value that isn't a string. The default property ("+defaultValue+") is set");
		}
   
		if (property == null || property.equals(""))
		{
			property = defaultValue;
		}
		
		return property;
	}
    

	@Override
	public void stop(BundleContext context) throws Exception
	{
		// TODO Auto-generated method stub
	}
}