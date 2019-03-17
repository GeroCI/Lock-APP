package com.system.LockManage.bean;

import java.sql.Timestamp;

//@Entity
//@Table(name = "user_info")
public class User {

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte getAuthority() {
		return authority;
	}

	public void setAuthority(byte authority) {
		this.authority = authority;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getAdmin_areas() {
		return admin_areas;
	}

	public void setAdmin_areas(String admin_areas) {
		this.admin_areas = admin_areas;
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

	public byte getStatus_() {
		return status_;
	}

	public void setStatus_(byte status_) {
		this.status_ = status_;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return username + ", " + password;
	}

    private String username;

    private String name;

    private String password;

    private byte authority;

    private String department;

    private String admin_areas;

    private String remote_scan;

    private Timestamp start_time;

    private Timestamp end_time;

    private String duty;

    private String phone;

    private byte status_;

}