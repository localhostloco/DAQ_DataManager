package datamanager.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import datalogger.DataLogger;
import datamanager.DataEntry;
import datamanager.DataManager;
import datamanager.tracker.LoggersTrackerCustomizer;

public class DataManagerImpl implements DataManager, Runnable {

	// Lista de Loggers
	private List<DataLogger> loggers;

	// Control del estado del DataManager
	private boolean stop;

	// Control del listener de los servicios de la interfaz DataLogger
	private boolean first;

	// Control del formateo del log
	private boolean formatFileHeader;

	// Control de mensajes de informaci�n
	private boolean noLoggersMessage;

	// Periodo local del DataManager
	private int period;

	// Tracker de servicios de la interfaz DataLogger
	private ServiceTracker tracker;

	// Contexto de la ejecuci�n
	private BundleContext context;

	// Marca de tiempo actual en milisegundos desde 01/01/1970
	private long currentTimestamp;

	// Constructor
	public DataManagerImpl() {
		stop = false;
		noLoggersMessage = false;
		first = true;
		loggers = new ArrayList<DataLogger>();
		currentTimestamp = nowLong();
		period = PERIOD;
	}

	public void run() {

		System.out.println("<DataManager Bundle> "
				+ new SimpleDateFormat("HH:mm:ss").format(Calendar
						.getInstance().getTime()));
		
		// Bucle de ejecuci�n
		while (!stop && !Thread.currentThread().isInterrupted()) {
			
			// Creaci�n del archivo de log
			File f = new File(FILE_PATH + currentTimestamp + "-"
					+ loggers.size() + ".txt");
			if (f.exists()) {
				formatFileHeader = false;
			} else {
				formatFileHeader = true;
				System.out.println(currentTimestamp);
			}
			
			String line = "";
			
			// Gesti�n del inicio del tracker
			if (first) {
				first = false;
				LoggersTrackerCustomizer customizer = new LoggersTrackerCustomizer(
						context);
				customizer.setDm(this);
				tracker = new ServiceTracker<>(context,
						DataLogger.class.getName(), customizer);
				tracker.open();
			}
			
			// Gesti�n de la cabecera del log
			if (formatFileHeader)
				line = formatHeader(line);
			
			// Si hay al menos un Logger en la lista
			if (!loggers.isEmpty()) {
				try {
					// Marca de tiempo relativa
					long millis = System.currentTimeMillis() - currentTimestamp;
					
					line += millis + "|";
					boolean firstData = true;
					
					// Lectura de Loggers
					for (DataLogger dl : loggers) {
						if (dl.isActivated()) {
							dl.readValue();
							
							// Creaci�n de la variable lectura
							DataEntry de = new DataEntry(dl.getCurrentValue(),
									millis);
							
							// Gesti�n de escritura del log para Loggers activados
							if (firstData) {
								line += de.getValue();
								firstData = false;
							} else {
								line += "|" + de.getValue();
							}
						
							// Gesti�n de escritura del log para Loggers desactivados
						} else {
							if (firstData) {
								line += "null";
								firstData = false;
							} else {
								line += "|" + "null";
							}
						}
					}
					
					// Escritura del log
					outputLog(line);
					
					// Gesti�n de l�mite de lecturas
					/*
					 * Posible mejora -> atributo
					 */
					if (currentTimestamp - nowLong() > 86400000) {
						currentTimestamp = nowLong();
						formatFileHeader = true;
					}
					
					// Pausa entre lecturas
					try {
						Thread.sleep(period);
					} catch (InterruptedException e) {

					}

					// ***** Excepciones *****
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			// Mensaje de informaci�n
			} else {
				if (noLoggersMessage) {
					System.out.println("<DataManager Bundle> "
							+ "There are no loggers online.");
				}
			}
		}
	}

	public void addLogger(DataLogger dl) {
		
		// Control de la presencia de un Logger
		DataLogger d = containsLogger(dl);
		
		// Si no est� en la lista, se le a�ade a la lista
		if (d == null) {
			dl.activate();
			loggers.add(dl);
			formatFileHeader = true;
			
		// Sino se activa su funcionamiento
		} else {
			d.activate();
		}
	}

	public void addContext(BundleContext context) {
		this.context = context;
	}

	// Eliminaci�n de un Logger de la lista
	/*
	 * Posible mejora -> comprobar necesidad del m�todo
	 */
	public void deleteLogger(DataLogger dl) {
		// if (loggers.contains(dl))
		// loggers.remove(dl);
	}

	public void STOP_RUNNING() {
		stop = true;
		
		// Detenimiento de todos los Loggers
		for (DataLogger dlogger : loggers) {
			try {
				dlogger.stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tracker.close();
	}

	public List<DataLogger> getLoggers() {
		return loggers;
	}

	public DataLogger containsLogger(DataLogger dl) {
		for (DataLogger dlogger : loggers) {
			
			// La comprobaci�n se hace en funci�n del nombre del Logger
			if (dlogger.getDriverName().equals(dl.getDriverName())) {
				return dlogger;
			}
		}
		return null;
	}

	public void outputLog(String line) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(FILE_PATH + currentTimestamp + "-"
						+ loggers.size() + ".txt", true)))) {
			out.println(line);
		} catch (IOException e) {
		}
	}

	public String nowString() {
		return new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance()
				.getTime());
	}

	public long nowLong() {
		return System.currentTimeMillis();
	}

	public String formatHeader(String line) {
		formatFileHeader = false;
		boolean firstLogger = true;
		for (DataLogger datalogger : loggers) {
			if (firstLogger) {
				firstLogger = false;
				line += datalogger.getDriverName();
			} else {
				line += "|" + datalogger.getDriverName();
			}
		}
		line += "\n";
		return line;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getPeriod() {
		return period;
	}
}