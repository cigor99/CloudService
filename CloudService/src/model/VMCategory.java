/**
 * 
 */
package model;

/**
 * @author Igor
 *
 */
public class VMCategory {
	// unique, mandatory
	private String name;
	// >0
	private int numCPUCores;
	// >0
	private int ramCapacity;
	private int numGPUCores;
	
	
	public VMCategory() {
		super();
	}
	public VMCategory(String name, int numCPUCores, int ramCapacity, int numGPUCores) {
		super();
		this.name = name;
		this.numCPUCores = numCPUCores;
		this.ramCapacity = ramCapacity;
		this.numGPUCores = numGPUCores;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VMCategory other = (VMCategory) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "VMCategory [name=" + name + ", numCPUCores=" + numCPUCores + ", ramCapacity=" + ramCapacity
				+ ", numGPUCores=" + numGPUCores + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumCPUCores() {
		return numCPUCores;
	}
	public void setNumCPUCores(int numCPUCores) {
		this.numCPUCores = numCPUCores;
	}
	public int getRamCapacity() {
		return ramCapacity;
	}
	public void setRamCapacity(int ramCapacity) {
		this.ramCapacity = ramCapacity;
	}
	public int getNumGPUCores() {
		return numGPUCores;
	}
	public void setNumGPUCores(int numGPUCores) {
		this.numGPUCores = numGPUCores;
	}

	
}
