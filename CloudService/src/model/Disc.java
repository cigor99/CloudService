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
	private float capacity;
	
	private String vmName;

	
	
	public Disc(String name, DiscType t, float cap, String vm) {
		super(name);
		this.type = t;
		this.capacity = cap;
		this.vmName = vm;
	}

	public DiscType getType() {
		return type;
	}

	public void setType(DiscType type) {
		this.type = type;
	}

	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
}
