package datamanager;

import java.util.List;

import org.osgi.framework.BundleContext;

import datalogger.DataLogger;

public interface DataManager {
	
	static final int PERIOD = 17; // 20 milliseconds
	
	static final String FILE_PATH = "C:\\Users\\Daniel\\Dropbox\\UPV\\DAQ\\Logs\\";
	
	void run();
	
	void addLogger(DataLogger dl);
	
	void deleteLogger(DataLogger dl);
	
	void STOP_RUNNING();
	
	void addContext(BundleContext context);
	
	List<DataLogger> getLoggers();
	
	DataLogger containsLogger(DataLogger dl);
	
	void outputLog(String line);
	
	String today();
	
	String formatHeader(String line);
	
	void setPeriod(int period);
	
	int getPeriod();
}
