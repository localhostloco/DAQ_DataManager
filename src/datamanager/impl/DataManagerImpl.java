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

	private List<DataLogger> loggers;
	private boolean stop;
	private boolean first;
	private boolean formatFileHeader;
	private boolean createNewFile;
	private boolean noLoggersMessage;
	private int counter;
	private int period;
	private ServiceTracker tracker;
	private BundleContext context;
	private String currentTimestamp;

	public DataManagerImpl() {
		// Initialization of variables
		stop = false;
		createNewFile = false;
		noLoggersMessage = false;
		first = true;
		loggers = new ArrayList<DataLogger>();
		counter = 0;
		currentTimestamp = today();
		period = PERIOD;
	}

	public void run() {

		System.out.println("<DataManager Bundle> " + new SimpleDateFormat("HH:mm:ss").format(Calendar
				.getInstance().getTime()));
		while (!stop && !Thread.currentThread().isInterrupted()) {
			File f = new File(FILE_PATH + currentTimestamp + "-"
					+ loggers.size() + ".txt");
			if (f.exists()) {
				formatFileHeader = false;
			} else {
				formatFileHeader = true;
			}
			String line = "";
			if (first) {
				first = false;
				LoggersTrackerCustomizer customizer = new LoggersTrackerCustomizer(
						context);
				customizer.setDm(this);
				tracker = new ServiceTracker<>(context,
						DataLogger.class.getName(), customizer);
				tracker.open();

			}
			if (formatFileHeader)
				line = formatHeader(line);
			if (!loggers.isEmpty()) {
				try {
					long millis = System.currentTimeMillis();
					line += millis + "|";
					boolean firstData = true;
					for (DataLogger dl : loggers) {
						if (dl.isActivated()) {
							dl.readValue();
							DataEntry de = new DataEntry(dl.getCurrentValue(),
									millis);
							if (firstData) {
								line += de.getValue();
								firstData = false;
							} else {
								line += "|" + de.getValue();
							}
						} else {
							if (firstData) {
								line += "null";
								firstData = false;
							} else {
								line += "|" + "null";
							}
						}
					}
					outputLog(line);
					if (!currentTimestamp.equals(today())) {
						currentTimestamp = today();
						formatFileHeader = true;
					}
					try {
						Thread.sleep(period);
					} catch (InterruptedException e) {

					}

					// ***** Exceptions *****
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				if (noLoggersMessage) {
					System.out.println("<DataManager Bundle> " + "There are no loggers online.");
					noLoggersMessage = true;
				}
			}
		}
	}

	// ***** The following methods are used by the Activator *****

	public void addLogger(DataLogger dl) {
		DataLogger d = containsLogger(dl);
		if (d == null) {
			dl.activate();
			loggers.add(dl);
			createNewFile = true;
			formatFileHeader = true;
		} else {
			d.activate();
		}
	}

	public void addContext(BundleContext context) {
		this.context = context;
	}

	public void deleteLogger(DataLogger dl) {
		// if (loggers.contains(dl))
		// loggers.remove(dl);
	}

	public void STOP_RUNNING() {
		stop = true;
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

	public String today() {
		return new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance()
				.getTime());
	}

	public String formatHeader(String line) {
		formatFileHeader = false;
		createNewFile = false;
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