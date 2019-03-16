package com.system.LockManage.bean;

import java.sql.Timestamp;

public class Warning {

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLock_name() {
		return lock_name;
	}

	public void setLock_name(String lock_name) {
		this.lock_name = lock_name;
	}

	public String getWarning_message() {
		return warning_message;
	}

	public void setWarning_message(String warning_message) {
		this.warning_message = warning_message;
	}

	public Timestamp getWarning_time() {
		return warning_time;
	}

	public void setWarning_time(Timestamp warning_time) {
		this.warning_time = warning_time;
	}


    private int id;

    private String lock_name;

    private String warning_message;

    private Timestamp warning_time;
}
