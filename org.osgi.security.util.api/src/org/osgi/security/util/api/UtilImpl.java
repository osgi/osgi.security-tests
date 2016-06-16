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
import java.io.FilePermission;
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

	private Boolean bool;
	private SecurityManager sm;
	private Object securityContext = null;

	
    static BundleContext getContext()
    {
        return bundleContext;
    }

    
    public UtilImpl(BundleContext context, boolean displayBox, boolean displayServer, String serverIP, int serverPort,
			int serverDPort, int serverSPort, boolean automatedTesting) throws Exception
    {
		// TODO Auto-generated constructor stub   	
        if (automatedTesting) {
            this.displayConsole = false;
            this.displayTest    = true;
        } else {
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
        if (displayServer && IP != null && port != 0) {
            try {
                socket = new Socket(IP,port);
            } catch (UnknownHostException e) {
                throw new Exception("The hostname specified in the file 'com.sogetiht.otb.properties.cfg' cannot be resolved to a network address");
            } catch(IOException e) {
                throw new Exception("An error has occurred while establishing a connection to the server. Please check your network connection and ensure that the server is running.");
            }

            if (socket != null) {
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader (socket.getInputStream()));
            } else {
                System.out.println("The socket is null or not connected.");
            }
        } else {
            if (displayServer) {
                System.out.println("The server IP and/or port are not specified in the file 'com.sogetiht.otb.properties.cfg'.");
            }
        }
    }

    
    @Override
    public String getResourcePath(String name, Class<?> bundle) throws Exception
    {
    	// TODO Auto-generated method stub
    	throw new Exception("Not implemented because of IS2T Framework");
    }

    
    @Override
    public InputStream getResourcePathAsStream(String name, Class<?> bundle)
    {
    	// TODO Auto-generated method stub
    	System.out.println("bundle.getResourceAsStream : " + name);
        return bundle.getResourceAsStream("/META-INF/resources/" + name);
    }


    @Override
    public String[] split(String str, char limit)
    {
    	// TODO Auto-generated method stub
    	Vector<String> matchList = new Vector<String>();
        String[] matchArray;
        int start=0;

        for (int end=0; end<str.length(); end++) {
            if (str.charAt(end)==limit) {
                matchList.addElement(str.substring(start,end));
                start=end+1;
            }
        }
        
        if (start!=str.length()+1) {
            matchList.addElement(str.substring(start,str.length()));
        }
            
        matchArray = new String[matchList.size()];
        matchList.copyInto(matchArray);

        return matchArray;
    }

    
    @Override
    public String receiveFile()
    {
    	// TODO Auto-generated method stub
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

        try {
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

            if (splitCmd[0].equals("SENDING_FILE")) {
                outStream = new FileOutputStream(name);
                int debug = 0;
                
                do {
                    bytesRead = is.read(buffer, 0, buffer.length);
                    System.out.println("DEBUG bytesRead="+bytesRead);
                    
                    if (bytesRead >= 0) {
                        System.out.println("DEBUG OK");
                        debug += bytesRead;
                        outStream.write(buffer, 0, bytesRead);
                    }
                } while(bytesRead > -1);

                outStream.flush();
                outStream.close();
                System.out.println("DEBUG debug="+debug);
                println(name + " : successfully received");               
            } else {
                err("Error on downloading file!");
            }
            socket.close();
        } catch (Exception e) {
            err(e);
        }
        
        return name;
    }


    @Override
    public void sendCmd(String string) throws Exception
    {    	
    	// TODO Auto-generated method stub
    	if (socket != null && displayServer) {
            if (socket.isConnected()) {
                try {
                    out.println(string);
                    out.flush();
                } catch (Exception e) {
                    throw new Exception("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                }
            } else {
            	throw new Exception("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
            }
    	} else {
            throw new Exception("No connection to the server. Please fill in the configuration file and ensure that the server is running");
    	}
    	
    }

    
    @Override
    public boolean sendFile(File file) throws Exception
    {
    	// TODO Auto-generated method stub
    	bool = false;
    	
    	if (socket != null && displayServer) {
            if (socket.isConnected()) {
            	try {
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
					
            		while (fileSize - completed > (long) step) {
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
            		
            		bool = true;			
            	} catch (Exception e) {
            		throw new Exception("An error has occurred while establishing a connection to the server. Please check your network connection and ensure that the server is running.");
            	}
            } else {
            	throw new Exception("An error has occurred while establishing a connection to the server. Please check your network connection and ensure that the server is running.");
            }
        } else {
        	throw new Exception("No connection to the server. Please fill in the configuration file and ensure that the server is running");
        }
        
		return bool;
    }
    
   
    @Override
    public void println_without_server(String value)
    {
    	// TODO Auto-generated method stub
    	try {
            if (displayConsole) {
                if (bundleName != null) {
                    System.out.println("[" + bundleName + "] " + value);
                } else {
                    System.out.println(value);
                }
            }
            
            if (displayTest&&initFile) {
            	bw.write(value+"\n");
            	bw.flush();
            }
        } catch (Exception e) {
            err(e);
            e.printStackTrace();
        }
    }

    
    @Override
    public void println(String value)
    {
    	// TODO Auto-generated method stub
    	try {
            if (displayConsole) {
                if (bundleName != null) {
                    System.out.println("[" + bundleName + "] " + value);
                } else {
                    System.out.println(value);
                }
            }
            
            if (displayTest&&initFile) {
            	if (bundleName != null) {
            		bw.write("[" + bundleName + "] " + value+"\n");
            	} else {
            		bw.write(value+"\n");
            	}
            	bw.flush();
            }
            
            if (socket != null && displayServer) {
                if (socket.isConnected()) {
                    try {
                        if (bundleName != null) {
                            out.println("[" + bundleName + "] " + value);
                        } else {
                            out.println(value);
                        }
                        out.flush();
                    } catch (Exception e) {
                        err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                        if (debug) {
                        	err(e);e.printStackTrace();
                        }
                    }
                 }
             }
        } catch (Exception e) {
            err(e);
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void println(char value)
    {
    	// TODO Auto-generated method stub
    	println(String.valueOf(value));
    }
    
    
    @Override
    public void println(int value)
    {
    	// TODO Auto-generated method stub
    	println(String.valueOf(value));
    }
    
    
    @Override
    public void println(long value)
    {
    	// TODO Auto-generated method stub
    	println(String.valueOf(value));
    }
    
    
    @Override
    public void println(boolean value)
    {
    	// TODO Auto-generated method stub
    	println(String.valueOf(value));
    }


    @Override
    public void print(String value)
    {
    	// TODO Auto-generated method stub
    	try {
            if (displayConsole) {
                if (bundleName != null) {
                    System.out.print("[" + bundleName + "] " + value);
                } else {
                    System.out.print(value);
                }
            }
            
            if (displayTest&&initFile) {
                if (bundleName != null) {
                    bw.write("[" + bundleName + "] " + value);
                } else {
                    bw.write(value);
                }
                bw.flush();
            }
            
            if (socket != null && displayServer) {
                if (socket.isConnected()) {
                    try {
                        if (bundleName != null) {
                            out.print("[" + bundleName + "] " + value);
                        } else {
                            out.print(value);
                        }
                        out.flush();
                    } catch (Exception e) {
                        err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                    }
                }
            }
        } catch (Exception e) {
            err(e);
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void print(char value)
    {
    	// TODO Auto-generated method stub
    	print(String.valueOf(value));
    }
    
    
    @Override
    public void print(int value)
    {
    	// TODO Auto-generated method stub
    	print(String.valueOf(value));
    }
    
    
    @Override
    public void print(long value)
    {
    	// TODO Auto-generated method stub
    	print(String.valueOf(value));
    }
    
    
    @Override
    public void print(boolean value)
    {
    	// TODO Auto-generated method stub
    	print(String.valueOf(value));
    }


    @Override
    public void err(Exception e)
    {
    	// TODO Auto-generated method stub
    	try {
            String message = e.getMessage();
            if (message != null) {
                err(e.getClass().getName() + ": " + message);
            } else {
                err(e.getClass().getName());
            }
        } catch (Exception ex) {
            err(ex.getMessage());
        }
    }
    
    
    @Override
    public void err(String string)
    {
    	// TODO Auto-generated method stub
    	try {
            if (displayConsole) {
                if (bundleName != null) {
                    System.err.println("[" + bundleName + "] ERROR: " + string);
                } else {
                    System.err.println("ERROR: " + string);
                }
            }
            
            if (displayTest&&initFile) {
                if (bundleName != null) {
                    bw.write("[" + bundleName + "] ::ERROR::\n");
                } else {
                    bw.write("::ERROR::\n");
                }
                bw.flush();
            }
            
            if (socket != null && displayServer) {
                if (socket.isConnected()) {
                    try {
                        if (bundleName != null) {
                            out.println("[" + bundleName + "] ERROR: " + string);
                        } else {
                            out.println("ERROR: " + string);
                        }
                        out.flush();
                    } catch (Exception e) {
                        if (bundleName != null) {
                            System.err.println("[" + bundleName + "] An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                        } else {
                            System.err.println("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
                        }
                        
                        if (debug) {
                        	e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }


    @Override
    public String receiveCmd()
    {
    	// TODO Auto-generated method stub
    	String answer = null;
        try {
            answer = in.readLine();
        } catch (IOException e) {
            err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
            if (debug) {
            	err(e);
            	e.printStackTrace();
            }
        } catch (Exception e) {
            err(e);
            e.printStackTrace();
        }
        
        return answer;
    }


    private void initFile(String name)
    {
		try {
			if (!outputFolder.exists()) {
				outputFolder.mkdir();
				if (!outputFolder.isDirectory()) {
					throw new Exception("'output' already exists and is no directory");
				}
				output = new File(outputFolder.getPath(), name);
				fw = new FileWriter(output.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				initFile=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			err(e);
		}
    }
    
    
    private void closeFile()
    {
    	try {
    		bw.close();
    		fw.close();
    	} catch (Exception e) {
    		err(e);
    	}
    }
    
    
    @Override
    public void start(boolean securityManager)
    {
    	// TODO Auto-generated method stub
    	synchronized (this) {
    		if (securityManager) {
    			this.setSecurityManager();
    		} else {
    			try {
    				Thread.sleep(1500);
    			} catch (Exception e) {
    				err(e);
    			}
    		}
		}
    }


    private void stopFramework()
    {
    	try {
    		getContext().getBundle(0).stop();
    	} catch (Exception e) {
    		err(e);
    	}
    }


    @Override
    public void stop(boolean succeed) 
    {
    	// TODO Auto-generated method stub
    	try  {
    		Thread.sleep(1500);
    	} catch (Exception e) {
    		err(e);
    	}
    	
    	if (displayTest) {
    		closeFile();
    		stopFramework();
    	}
    	
    	// Close the socket
    	if (socket != null && displayServer) {
    		if (socket.isConnected()) {
    			try {
    				if (bundleName != null) {
    					out.println("[" + bundleName + "] Exit");
    				} else {
    					out.println("Exit");
    				}
    				out.flush();
    				socket.close();
    			} catch (IOException e) {
    				err("An error has occurred while establishing a connection to the server.Please check your network connection and ensure that the server is running.");
    				if (debug)  {
    					err(e);
    					e.printStackTrace();
    				}
    			} catch (Exception e) {
    				err(e);
    				e.printStackTrace();
    			}
    		}
    	}
    }

    
    @Override
    public void setBundleName(String bundleName)
    {
    	// TODO Auto-generated method stub
    	this.bundleName = bundleName;
    }
    
    
	@Override
	public boolean testConnection()
	{
		// TODO Auto-generated method stub
		if (socket != null && socket.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	public boolean checkSecurityManager()
	{
		// TODO Auto-generated method stub
		System.setProperty("java.security.policy", System.getProperty("user.dir") + "/" + "org.osgi.security.permissions.cfg");
		sm = System.getSecurityManager();	
		if (sm != null) {
			securityContext = sm.getSecurityContext();
			return true;
		} else {
			System.out.println("\n[WARNING] No SecurityManager is installed on the framework.\n");
			return false; 
		}
	}

	
	/*
	 * Need to have a better understand of Permissions management before use checkPermission method 
	 * 
	@Override
	public void checkPermissions(FilePermission perm)
	{
		// TODO Auto-generated method stub		
		if (this.checkSecurityManager()) {
			sm.checkPermission(perm, securityContext);
		}
	}
	*/

	@Override
	public boolean setSecurityManager()
	{
		// TODO Auto-generated method stub
		System.setSecurityManager(new SecurityManager());
		return false;
	}
}
