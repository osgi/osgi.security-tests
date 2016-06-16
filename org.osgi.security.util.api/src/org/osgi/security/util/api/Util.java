package org.osgi.security.util.api;

import java.io.File;
import java.io.FilePermission;
import java.io.InputStream;
import java.lang.Exception;

/**
 * 
 */
public interface Util {
	
	/**
	 * 
	 */
	
    public String getResourcePath(String name, Class<?> bundle) throws Exception;

    public InputStream getResourcePathAsStream(String name, Class<?> bundle);

    public String[] split(String str, char limit);

    public String receiveFile();

    public void sendCmd(String string) throws Exception;

    public boolean sendFile(File file) throws Exception;

    public void println_without_server(String value);

    public void println(String value);

    public void println(char value);

    public void println(int value);
    
    public void println(long value);
    
    public void println(boolean value);

    public void print(String value);

    public void print(char value);

    public void print(int value);
    
    public void print(long value);
    
    public void print(boolean value);

    public void err(Exception e);

    public void err(String string);

    public String receiveCmd();

    public void start(boolean securityManager);

    public void stop(boolean succeed);

    public void setBundleName(String bundleName);
    
    public boolean testConnection();
    
    public boolean setSecurityManager();
    
    public boolean checkSecurityManager();
    
    /*
     * Need to have a better understand of Permissions management before use checkPermission method 
     * 
    public void checkPermissions(FilePermission perm);
	*/
}