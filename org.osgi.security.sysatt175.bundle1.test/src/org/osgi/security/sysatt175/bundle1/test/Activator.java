package org.osgi.security.sysatt175.bundle1.test;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.junit.Assert;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;

import org.osgi.security.sysatt175.common.api.HelloWorldService;
import org.osgi.security.util.api.Util;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
	private static BundleContext bundleContext;
	private ServiceTracker serviceRef;
	private Util util;
	private boolean succeed = false;
	private ServiceRegistration<?> registration = null;
	private Hashtable<String,String> dict = new Hashtable<String,String>();
	private int key = 0;
	private String filter;
	private ServiceListener utilListener;
	
	private Dictionary<String, String> getDictionary()
	{ 
		if (dict.isEmpty())
		{
			dict.put("servicegroup", "org.osgi.security.sysatt175");
			dict.put("servicenumber", "bundle1.test");
			dict.put("key", Integer.toString(key));
			key++;
		}  
		return (Dictionary<String, String>)dict;
	}


	static BundleContext getContext()
	{
		return bundleContext;
	}

	public void start(BundleContext context) throws Exception
	{
		Activator.bundleContext = context;
	}
	
	
	@Test
    public void testSysatt175() throws Exception
    {
		serviceRef = new ServiceTracker(getContext(), Util.class.getName(), null);
  		serviceRef.open();
  		util = (Util) serviceRef.waitForService(30000); 
  		Assert.assertNotNull(util);
  		serviceProcessing();
  	
		while (key < 50000)
		{
			Thread.sleep(1);
		}
  	
	}

	
	private void serviceProcessing() throws Exception
	{
		util.start("sysatt175", "Deadlock", "Exploit resource exhaustion by mutually dependant service subscriptions");
		try
		{
			filter = "(&(servicenumber=bundle2.test)(servicegroup=org.osgi.security.sysatt175))";
			utilListener = new ServiceListener()
			{
				public void serviceChanged(ServiceEvent e)
				{
					switch (e.getType())
					{
						case ServiceEvent.REGISTERED:
							new Thread() {
				                  public void run() {
										dict.clear();
										getDictionary();
										util.println("----------------------------------------------------------------------");
										util.println("Bundle1: new key generated.");
										util.println("----------------------------------------------------------------------");
										registration.setProperties(dict);
					                }
								}.start();
							break;
	      
						case ServiceEvent.MODIFIED:
							if (key > 50000) 
							{
								try
								{
									unregisteredService();
								} 
								catch (Exception e1)
								{
									e1.printStackTrace();
								}
							} 
							else 
							{
								dict.clear();
								getDictionary();
								util.println("Bundle1: Service of " + e.getServiceReference().getBundle().getSymbolicName() + " changed (New key = " + dict.get("key")+").");
								util.println("----------------------------------------------------------------------");
								registration.setProperties(dict);
							}
							break;
							
						case ServiceEvent.UNREGISTERING:
							getContext().ungetService(e.getServiceReference());
							break;
	     
						case ServiceEvent.MODIFIED_ENDMATCH:
							getContext().ungetService(e.getServiceReference());
							break;
						
						default:
							// Nothing
							break;
					}
				}
			};
	
			try
			{
				getContext().addServiceListener(utilListener, filter);
			}
			catch (InvalidSyntaxException e1)
			{
				e1.printStackTrace();
			}
			registration = getContext().registerService(HelloWorldService.class.getName(), new HelloWorldServiceImpl(), getDictionary());
		} 
		catch (Exception e)
		{
			util.err(e);
		}	
	} 
	
	private void unregisteredService()
	{
		registration.unregister();
		succeed = true;
		Assert.assertTrue("Test failed", succeed);
		util.stop(succeed);
	}
	
	
	public void stop(BundleContext context) throws Exception
	{
		Activator.bundleContext = null;
	}
}
