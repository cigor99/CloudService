/**
 * 
 */
package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Ksenija
 *
 */
public class Activity {
	private static DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private String onTime;
	private String offTime;
	
	public Activity() {
		super();
		this.offTime = "";
		this.onTime = "";
	}
	public Activity(String onTime, String offTime) {
		super();
		this.onTime = onTime;
		this.offTime = offTime;
	}
	public String getOnTime() {
		return onTime;
	}
	public void setOnTime(String onTime) {
		this.onTime = onTime;
	}
	public String getOffTime() {
		return offTime;
	}
	public void setOffTime(String offTime) {
		this.offTime = offTime;
	}
	
	
}
