package datamanager.tracker;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import datalogger.DataLogger;
import datamanager.DataManager;

public class LoggersTrackerCustomizer implements ServiceTrackerCustomizer {

	private final BundleContext context;
	private static DataManager dm;

	public LoggersTrackerCustomizer(BundleContext context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		DataLogger dl = (DataLogger) context.getService(reference);
		DataLogger d = getDm().containsLogger(dl); 
		if (d == null) {
			getDm().addLogger(dl);
			return dl;
		}
		d.activate();
		return d;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub
		DataLogger dl = (DataLogger) service;
		try {
			dl.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("<DataManager Bundle> " + "Service removed: " + dl.getDriverName());
	}

	public static DataManager getDm() {
		return dm;
	}

	public static void setDm(DataManager dm) {
		LoggersTrackerCustomizer.dm = dm;
	}

}
