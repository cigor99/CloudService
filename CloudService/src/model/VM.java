/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Igor
 *
 */
public class VM extends Resource {
	// mandatory
	private String category;
	private int numCPUCores;
	private int ramCapacity;
	private int numGPUCores;
	
	private ArrayList<String> discs;
	private ArrayList<Activity> activityList;
	
	public VM() {
	}

	

	public VM(String name, String organisation, String category, int numCPUCores, int ramCapacity, int numGPUCores,
			ArrayList<String> discs, ArrayList<Activity> activityList) {
		super(name, organisation);
		this.category = category;
		this.numCPUCores = numCPUCores;
		this.ramCapacity = ramCapacity;
		this.numGPUCores = numGPUCores;
		this.discs = discs;
		this.activityList = activityList;
	}



	@Override
	public String toString() {
		return "VM [category=" + category + ", numCPUCores=" + numCPUCores + ", ramCapacity=" + ramCapacity
				+ ", numGPUCores=" + numGPUCores + ", discs=" + discs + ", activityList=" + activityList + "]";
	}



	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
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

	public ArrayList<String> getDiscs() {
		return discs;
	}

	public void setDiscs(ArrayList<String> discs) {
		this.discs = discs;
	}

	public ArrayList<Activity> getActivityList() {
		return activityList;
	}

	public void setActivityList(ArrayList<Activity> activityList) {
		this.activityList = activityList;
	}

	
	
}
