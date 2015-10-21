package datamanager;

public class DataEntry {

	// Valor de la lectura
	private Double value;
	
	// Marca de tiempo en milisegundos desde 01/01/1970
	private Long timestamp;
	
	// Constructor
	public DataEntry(double value, Long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}

	// Getters
	public Double getValue() {
		return value;
	}	
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	// Setters
	public void setTimestamp(long l) {
		timestamp = l;
	}
}
