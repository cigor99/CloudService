/**
 * 
 */
package model;

/**
 * @author Igor
 *
 */
public class Disc extends Resource {
	// mandatory
	private DiscType type;
	private int capacity;
	private String vmName;

	

	public Disc() {
		super();
	}

	

	public Disc(String name, String organisation, DiscType type, int capacity, String vmName) {
		super(name, organisation);
		this.type = type;
		this.capacity = capacity;
		this.vmName = vmName;
	}



	@Override
	public String toString() {
		return "Disc [type=" + type + ", capacity=" + capacity + ", vmName=" + vmName + "]";
	}




	public DiscType getType() {
		return type;
	}



	public void setType(DiscType type) {
		this.type = type;
	}



	public int getCapacity() {
		return capacity;
	}



	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}



	public String getVmName() {
		return vmName;
	}



	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	
}
