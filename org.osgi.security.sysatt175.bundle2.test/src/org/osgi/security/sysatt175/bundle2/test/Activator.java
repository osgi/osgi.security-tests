package org.osgi.security.sysatt175.bundle2.test;

import java.util.Dictionary;
import java.util.Hashtable;

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
	private boolean succeed;
	private ServiceRegistration<?> registration;
	private Hashtable<String,String> dict = new Hashtable<String,String>();
	private int key = 0;
	private String filter;
	private ServiceListener utilListener;

	
	private Dictionary<String, String> getDictionary()
	{ 
		if (dict.isEmpty())
		{
			dict.put("servicegroup", "org.osgi.security.sysatt175");
			dict.put("servicenumber", "bundle2.test");
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
		serviceTracking();
	}
	
	private void serviceTracking() throws InterruptedException, InvalidSyntaxException{
		serviceRef = new ServiceTracker(getContext(), Util.class.getName(), null);
  		serviceRef.open();
  		util = (Util) serviceRef.waitForService(30000);
  		if(util != null){
  	  		serviceProcessing();
  		}
	}
	
	   
	private void serviceProcessing()
	{
		try
		{
			filter = "(&(servicenumber=bundle1.test)(servicegroup=org.osgi.security.sysatt175))";
			registration = getContext().registerService(HelloWorldService.class.getName(), new HelloWorldServiceImpl(), getDictionary());
			utilListener = new ServiceListener()
			{
				public void serviceChanged(ServiceEvent e)
				{
					switch (e.getType())
					{
						case ServiceEvent.REGISTERED:
							new Thread() {
				                  public void run() {
				                	  	getContext().getServiceReference(HelloWorldService.class.getName());
										dict.clear();
										getDictionary();
										util.println("----------------------------------------------------------------------");
										util.println("Bundle2: new key generated.");
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
				                util.println("Bundle2: Service of " + e.getServiceReference().getBundle().getSymbolicName() + " changed (New key = " + dict.get("key") + ")");
				                util.println("----------------------------------------------------------------------");
				                
				                if (key % 20 == 0)
				                {
				                	new Thread() {
				                		public void run() {
				                			registration.setProperties(dict);
				                		}
				                	}.start();
				                }
				                else
				                {
				                	registration.setProperties(dict);		          
				                }
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
		util.stop(succeed);
	}
	

	public void stop(BundleContext context) throws Exception
	{
		Activator.bundleContext = null;
	}
}
