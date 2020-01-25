/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Igor
 *
 */
public class VM extends Resource {
	// mandatory
	private VMCategory category;
	private int numCPUCores;
	private int ramCapacity;
	private int numGPUCores;
	
	private HashMap<String, Disc> discs;
	private ArrayList<Activity> activityList;
	
	public VM() {
	}
	
	public VM(String name, VMCategory cat, int numCpu, int ram, int numGpu) {
		super(name);
		this.category = cat;
		this.numCPUCores = numCpu;
		this.ramCapacity = ram;
		this.numGPUCores = numGpu;
		this.discs = new HashMap<String, Disc>();
		this.activityList = new ArrayList<Activity>();
	}

	@Override
	public String toString() {
		return "VM [category=" + category + ", numCPUCores=" + numCPUCores + ", ramCapacity=" + ramCapacity
				+ ", numGPUCores=" + numGPUCores + ", discs=" + discs + ", activityList=" + activityList + "]";
	}

	public VMCategory getCategory() {
		return category;
	}

	public void setCategory(VMCategory category) {
		this.category = category;
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

	public HashMap<String, Disc> getDiscs() {
		return discs;
	}

	public void setDiscs(HashMap<String, Disc> discs) {
		this.discs = discs;
	}

	public ArrayList<Activity> getActivityList() {
		return activityList;
	}

	public void setActivityList(ArrayList<Activity> activityList) {
		this.activityList = activityList;
	}
	
	
}
