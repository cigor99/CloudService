package model;

import java.util.HashMap;

public class BillWrap {

	private HashMap<String, Double> discBills;
	private HashMap<String, Double> vmBills;
	private double totalBill;
	
	public BillWrap() {
		discBills = new HashMap<String, Double>();
		vmBills = new HashMap<String, Double>();
		setTotalBill(0);
	}
	public BillWrap(HashMap<String, Double> discBills, HashMap<String, Double> vmBills) {
		super();
		this.discBills = discBills;
		this.vmBills = vmBills;
	}
	
	@Override
	public String toString() {
		return "BillWrap [discBills=" + discBills + ", vmBills=" + vmBills + "]";
	}
	
	public HashMap<String, Double> getDiscBills() {
		return discBills;
	}
	public void setDiscBills(HashMap<String, Double> discBills) {
		this.discBills = discBills;
	}
	public HashMap<String, Double> getVmBills() {
		return vmBills;
	}
	public void setVmBills(HashMap<String, Double> vmBills) {
		this.vmBills = vmBills;
	}
	public double getTotalBill() {
		return totalBill;
	}
	public void setTotalBill(double totalBill) {
		this.totalBill = totalBill;
	}
	
	
}
