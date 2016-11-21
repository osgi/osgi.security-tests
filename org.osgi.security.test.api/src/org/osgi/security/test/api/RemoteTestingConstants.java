package org.osgi.security.test.api;

public interface RemoteTestingConstants {

	/**
	 * Command to run a test, arguments:
	 * 
	 * <ol>
	 *   <li>String - Test Bundle symbolic name</li>
	 *   <li>String - Test Bundle version</li>
	 *   <li>String - Test class name</li>
	 *   <li>String - Test method name</li>
	 * </ol>
	 */
	public static final String RUN_TEST = "runTest";
	
	/**
	 * Command to get a bundle, arguments:
	 *
	 * <ol>
	 *   <li>String - Bundle symbolic name</li>
	 *   <li>String - Bundle version</li>
	 * </ol>
	 * 
	 */
	public static final String GET_BUNDLE = "getBundle";
	
	/**
	 * Sending a Bundle, arguments:
	 *
	 * <ol>
	 *   <li>String - Bundle symbolic name</li>
	 *   <li>String - Bundle version</li>
	 *   <li>int - Size of the bundle</li>
	 *   <li>bytes - The raw bundle bytes</li>
	 * </ol>
	 */
	public static final String BUNDLE = "bundle";
	
	/**
	 * Report the success of a requested test, no arguments
	 */
	public static final String SUCCESS = "success";
	
	/**
	 * Report the failure of a requested test, arguments:
	 *
	 * <ol>
	 *   <li>String - Failure message</li>
	 * </ol>
	 */
	public static final String FAIL = "fail";
	
	/**
	 * A command indicating that a block of tests is complete and tidy-up may occur, no arguments
	 */
	public static final String END = "end";
	
}
