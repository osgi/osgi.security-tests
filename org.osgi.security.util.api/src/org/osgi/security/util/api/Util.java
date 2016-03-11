package org.osgi.security.util.api;

import java.io.File;
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

    public double sendCmd(String string) throws Exception;

    public double sendFile(File file) throws Exception;

    public void println_without_server(String value);

    public void println(String value);

    public void println(char value);

    public void println(int value);

    public void print(String value);

    public void print(char value);

    public void print(int value);

    public void err(Exception e);

    public void err(String string);

    public String receiveCmd();

    public void start(String name, String title, String description);

    public void stop(boolean succeed);

    public void setBundleName(String bundleName);
	
}