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
	 * Ubicación del archivo donde se generan los logs
	 */
	static final String FILE_PATH = "C:\\Users\\Daniel\\Dropbox\\UPV\\DAQ\\Logs\\";
	
	/*
	 * Método que inicia la ejecución del DataManager
	 */
	void run();
	
	/*
	 * Método que añade un Logger a la lista de Loggers del DataManager
	 */
	void addLogger(DataLogger dl);
	
	/*
	 * Método que elimina un Logger de la lista de Loggers del DataManager
	 */
	void deleteLogger(DataLogger dl);
	
	/*
	 * Método que pára la ejecución del DataManager
	 */
	void STOP_RUNNING();
	
	/*
	 * Método que guarda el contexto de ejecución
	 */
	void addContext(BundleContext context);
	
	/*
	 * Devuelve la lista de Loggers
	 */
	List<DataLogger> getLoggers();
	
	/* 
	 * Método que comprueba si un Logger ya está en la lista de Loggers
	 * y lo devuelve en si está, sino devuelve null
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
	 * Devuelve la primera línea del log formateada en función de los Loggers presentes
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
