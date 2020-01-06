package model;

import java.util.HashMap;

public class VMCategories {
	private HashMap<String, VMCategory> vmCategories;

	public VMCategories() {
		super();
	}
	
	public VMCategories(String path) {
		
	}
	public HashMap<String, VMCategory> getVmCategories() {
		return vmCategories;
	}

	public void setVmCategories(HashMap<String, VMCategory> vmCategories) {
		this.vmCategories = vmCategories;
	}
	
}
