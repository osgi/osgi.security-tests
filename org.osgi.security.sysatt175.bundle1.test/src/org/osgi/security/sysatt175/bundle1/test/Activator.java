package org.osgi.security.sysatt175.bundle1.test;

import java.util.Hashtable;
import java.util.Dictionary;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.security.sysatt175.common.api.HelloWorldService;
import org.osgi.security.util.api.Util;

public class Activator implements BundleActivator
{
	private static BundleContext bundleContext;
	private Util util;
	private ServiceRegistration<?> registration = null;
	private Hashtable<String,String> dict = new Hashtable<String,String>();
	private int key = 0;


	private Dictionary<String, String> getDictionary()
	{ 
		if (dict.isEmpty())
		{
			dict.put("servicegroup", "com.sogetiht.otb.sysatt175");
			dict.put("servicenumber", "bundle1");
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

		ServiceReference<?> service = getContext().getServiceReference(
				Util.class.getName());
		if (service != null)
		{
			main(service);
		}
		else
		{
			ServiceListener listener = new ServiceListener()
			{
				public void serviceChanged(ServiceEvent e)
				{
					ServiceReference<?> service = e.getServiceReference();				
					switch (e.getType())
					{
						case ServiceEvent.REGISTERED:
							main(service);
							break;
							
						default:
							// Nothing
							break;
					}
				}
			};

			String filter = "(" + Constants.OBJECTCLASS + "="
					+ Util.class.getName() + ")";
			getContext().addServiceListener(listener, filter);
		}
	}


	private void main (ServiceReference<?> service)
	{
		Object serv = getContext().getService(service);
		util = (Util) serv;
		if (util != null)
		{
			util.start("sysatt175", "Deadlock", "Exploitation de deadlock par souscription de services mutuellement dépendant");
			try
			{
				String filter1 = "(&(servicenumber=bundle2)(servicegroup=com.sogetiht.otb.sysatt175))";
				ServiceListener serviceListener = new ServiceListener()
				{
					public void serviceChanged(ServiceEvent e)
					{
						switch (e.getType())
						{
							case ServiceEvent.REGISTERED:
								new Thread()
								{
									public void run()
									{
										try
										{
											Thread.sleep(3000);
										}
										catch (Exception e)
										{
											
										}
										dict.clear();
										getDictionary();
										util.println("----------------------------------------------------------------------");
										util.println("Bundle1: new key generated.");
										util.println("----------------------------------------------------------------------");
										registration.setProperties(dict);
									}
								}
								.start();
								break;
              
							case ServiceEvent.MODIFIED:
								dict.clear();
								getDictionary();
								util.println("Bundle1: Service of " + e.getServiceReference().getBundle().getSymbolicName() + " changed (New key = " + dict.get("key")+").");
								util.println("----------------------------------------------------------------------");
								registration.setProperties(dict);
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

				getContext().addServiceListener(serviceListener, filter1);
				registration = getContext().registerService(HelloWorldService.class.getName(), new HelloWorldServiceImpl(), getDictionary());

			} 
			catch (Exception e)
			{
				util.err(e);
			}
			//util.stop(succeed);
		}
		else
		{
			System.err.println("Service not available. Please install the package com.sogetiht.otb.util.jar");
		}
	}


	public void stop(BundleContext context) throws Exception
	{
		registration.unregister();
		Activator.bundleContext = null;
	}
}
