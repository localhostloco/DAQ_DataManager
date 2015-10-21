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
	// Activación del módulo
	public void start(BundleContext context) {
		try {
			System.out.println("Starting DataManager bundle");

			// Objeto que se publica en el servicio
			dm = new DataManagerImpl();
			dm.addContext(context);

			// Hilo de ejecución del DataManager
			thread = new Thread((DataManagerImpl) dm);

			// Registro del servicio
			registration = context.registerService(DataManager.class, dm, null);

			// Obtención de los servicios publicados de la interfaz DataLogger
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
			
			// Inicio del hilo de ejecución del DataManager
			thread.start();
			
		// Excepciones
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	// Detenimiento de la ejecución
	public void stop(BundleContext context) {
		try {
			registration.unregister();
			thread.interrupt();
			dm.STOP_RUNNING();
			dm = null;
			
		// Excepciones
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
