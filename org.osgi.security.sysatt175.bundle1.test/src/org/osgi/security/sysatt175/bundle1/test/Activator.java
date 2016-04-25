package org.osgi.security.sysatt175.bundle1.test;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.junit.Assert;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import org.osgi.security.sysatt175.common.api.HelloWorldService;
import org.osgi.security.util.api.Util;

public class Activator implements BundleActivator
{
	private static BundleContext bundleContext;
	private static ServiceReference<?> service;
	private Util util;
	private boolean succeed = false;
	private ServiceRegistration<?> registration = null;
	private Hashtable<String,String> dict = new Hashtable<String,String>();
	private int key = 0;
	private String filter;
	private String filter1;
	private ServiceListener listener;
	private ServiceListener serviceListener;
	
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
	
	
	static ServiceReference<?> getService()
    {
		return service;
    }
	

	public void start(BundleContext context) throws Exception
	{
		Activator.bundleContext = context;
	}
	
	
	@Test
    public void testSysatt175() throws Exception
    {
		service = getContext().getServiceReference(Util.class.getName());
		
		if (service != null)
		{
			serviceProcessing();
		}
		else
		{
			listener = new ServiceListener()
			{
				public void serviceChanged(ServiceEvent e)
				{
					service = e.getServiceReference();				
					switch (e.getType())
					{
						case ServiceEvent.REGISTERED:
							try 
							{
								serviceProcessing();
							} 
							catch (Exception e1)
							{				
								e1.printStackTrace();
							}
							break;
							
						default:
							// Nothing
							break;
					}
				}
			};

			filter = "(" + Constants.OBJECTCLASS + "="
					+ Util.class.getName() + ")";
			getContext().addServiceListener(listener, filter);
		}
		
		while (key < 50000)
		{
			new Thread();
			Thread.sleep(1);
		}	
	}

	
	private void serviceProcessing() throws Exception
	{
		util = (Util) getContext().getService(getService());
		util.start("sysatt175", "Deadlock", "Exploitation de deadlock par souscription de services mutuellement dépendant");
		try
		{
			filter1 = "(&(servicenumber=bundle2.test)(servicegroup=org.osgi.security.sysatt175))";
			serviceListener = new ServiceListener()
			{
				public void serviceChanged(ServiceEvent e)
				{
					switch (e.getType())
					{
						case ServiceEvent.REGISTERED:
							new Thread() {
				                  public void run() {
										try
										{
											Thread.sleep(3000);
										}
										catch (Exception e1)
										{
											e1.printStackTrace();
										}
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
									stop(getContext());
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
				getContext().addServiceListener(serviceListener, filter1);
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
	
	
	public void stop(BundleContext context) throws Exception
	{
		registration.unregister();
		Activator.bundleContext = null;
		succeed = true;
		Assert.assertTrue("Test passed", succeed);
		util.stop(succeed);
	}
}
