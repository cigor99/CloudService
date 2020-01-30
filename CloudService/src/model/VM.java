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
	private Organisation organisation;
	private VMCategory category;
	private int numCPUCores;
	private int ramCapacity;
	private int numGPUCores;
	
	private HashMap<String, Disc> discs;
	private ArrayList<Activity> activityList;
	
	public VM() {
	}
	
	public VM(String name, Organisation organisation, VMCategory category, int numCPUCores, int ramCapacity,
			int numGPUCores, HashMap<String, Disc> discs, ArrayList<Activity> activityList) {
		super(name);
		this.organisation = organisation;
		this.category = category;
		this.numCPUCores = numCPUCores;
		this.ramCapacity = ramCapacity;
		this.numGPUCores = numGPUCores;
		this.discs = discs;
		this.activityList = activityList;
	}

	@Override
	public String toString() {
		return "VM [organisation=" + organisation + ", category=" + category + ", numCPUCores=" + numCPUCores
				+ ", ramCapacity=" + ramCapacity + ", numGPUCores=" + numGPUCores + ", discs=" + discs
				+ ", activityList=" + activityList + "]";
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

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
}
