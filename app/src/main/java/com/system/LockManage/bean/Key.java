package com.system.LockManage.bean;

import java.sql.Timestamp;

public class Key {
 
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey_name() {
		return key_name;
	}

	public void setKey_name(String key_name) {
		this.key_name = key_name;
	}

	public String getMac_addr() {
		return mac_addr;
	}

	public void setMac_addr(String mac_addr) {
		this.mac_addr = mac_addr;
	}

	public String getBelonger() {
		return belonger;
	}

	public void setBelonger(String belonger) {
		this.belonger = belonger;
	}

	public byte getStatus_() {
		return status_;
	}

	public void setStatus_(byte status_) {
		this.status_ = status_;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getRemote_scan() {
		return remote_scan;
	}

	public void setRemote_scan(String remote_scan) {
		this.remote_scan = remote_scan;
	}

	public Timestamp getStart_time() {
		return start_time;
	}

	public void setStart_time(Timestamp start_time) {
		this.start_time = start_time;
	}

	public Timestamp getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}


    private int id;
     

    private String key_name;

    private String mac_addr;


    private String belonger;


    private byte status_;


    private String notes;


    private String remote_scan;


    private Timestamp start_time;


    private Timestamp end_time;
    

    private String duty;
    

    private String phone;
    

    private byte status;

}