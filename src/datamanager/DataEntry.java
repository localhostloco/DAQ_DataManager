package datamanager;

public class DataEntry {

	private Double value;
	private Long timestamp;
	
	public DataEntry(double value, Long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}
	
	public Double getValue() {
		return value;
	}	
	
	public void setTimestamp(long l) {
		timestamp = l;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
}
