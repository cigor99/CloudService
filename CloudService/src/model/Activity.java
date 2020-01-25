/**
 * 
 */
package model;

import java.util.Date;

/**
 * @author Ksenija
 *
 */
public class Activity {
	private Date onTime;
	private Date offTime;
	
	public Activity() {
		super();
	}
	public Activity(Date onTime, Date offTime) {
		super();
		this.onTime = onTime;
		this.offTime = offTime;
	}
	public Date getOnTime() {
		return onTime;
	}
	public void setOnTime(Date onTime) {
		this.onTime = onTime;
	}
	public Date getOffTime() {
		return offTime;
	}
	public void setOffTime(Date offTime) {
		this.offTime = offTime;
	}
	
	
}
