package datamanager.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import datalogger.DataLogger;
import datamanager.DataManager;

public class Activator implements BundleActivator {

	private static DataManager dm;
	private Thread thread;
	private ServiceRegistration registration;

	@Override
	public void start(BundleContext context) {
		try {
			dm = new DataManagerImpl();
			System.out.println("Starting DataManager bundle");
			dm.addContext(context);
			thread = new Thread((DataManagerImpl) dm);
			registration = context.registerService(DataManager.class, dm, null);
			ServiceReference[] ref = context.getServiceReferences(
					DataLogger.class.getName(), null);
			if (ref != null) {
				for (ServiceReference sr : ref) {
					DataLogger dl = (DataLogger) context.getService(sr);
					if (dl != null) {
						dm.addLogger(dl);
						System.out.println("Service added: "
								+ dl.getDriverName());
					}
				}
			}
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(BundleContext context) {
		try {
			registration.unregister();
			thread.interrupt();
			dm.STOP_RUNNING();
			dm = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
