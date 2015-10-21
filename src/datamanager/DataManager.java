package datamanager;

import java.util.List;

import org.osgi.framework.BundleContext;

import datalogger.DataLogger;

public interface DataManager {
	
	/*
	 * Periodo entre lecturas, en milisegundos
	 */
	static final int PERIOD = 20;
	
	/*
	 * Ubicaci�n del archivo donde se generan los logs
	 */
	static final String FILE_PATH = "C:\\Users\\Daniel\\Dropbox\\UPV\\DAQ\\Logs\\";
	
	/*
	 * M�todo que inicia la ejecuci�n del DataManager
	 */
	void run();
	
	/*
	 * M�todo que a�ade un Logger a la lista de Loggers del DataManager
	 */
	void addLogger(DataLogger dl);
	
	/*
	 * M�todo que elimina un Logger de la lista de Loggers del DataManager
	 */
	void deleteLogger(DataLogger dl);
	
	/*
	 * M�todo que p�ra la ejecuci�n del DataManager
	 */
	void STOP_RUNNING();
	
	/*
	 * M�todo que guarda el contexto de ejecuci�n
	 */
	void addContext(BundleContext context);
	
	/*
	 * Devuelve la lista de Loggers
	 */
	List<DataLogger> getLoggers();
	
	/* 
	 * M�todo que comprueba si un Logger ya est� en la lista de Loggers
	 * y lo devuelve en si est�, sino devuelve null
	*/
	DataLogger containsLogger(DataLogger dl);

	/*
	 * Genera el log con las lecturas y la marca de tiempo
	 */
	void outputLog(String line);

	/*
	 * Devuelve la fecha del momento actual en milisegundos a partir de 01/01/1970
	 */
	String nowString();

	/*
	 * Devuelve la fecha del momento actual en milisegundos a partir de 01/01/1970
	 */
	long nowLong();

	/*
	 * Devuelve la primera l�nea del log formateada en funci�n de los Loggers presentes
	 */
	String formatHeader(String line);

	/*
	 * Setters
	 */
	void setPeriod(int period);

	/*
	 * Getters
	 */
	int getPeriod();
}
