package com.system.LockManage.bean;

import java.sql.Timestamp;


public class OperationLog {
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getOperation_time() {
		return operation_time;
	}

	public void setOperation_time(Timestamp operation_time) {
		this.operation_time = operation_time;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getBind_operator() {
		return bind_operator;
	}

	public void setBind_operator(String bind_operator) {
		this.bind_operator = bind_operator;
	}

	public byte getOperation_type() {
		return operation_type;
	}

	public void setOperation_type(byte operation_type) {
		this.operation_type = operation_type;
	}

	public String getKey_name() {
		return key_name;
	}

	public void setKey_name(String key_name) {
		this.key_name = key_name;
	}

	public String getLock_name() {
		return lock_name;
	}

	public void setLock_name(String lock_name) {
		this.lock_name = lock_name;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public byte getStatus_() {
		return status_;
	}

	public void setStatus_(byte status_) {
		this.status_ = status_;
	}

	public Timestamp getUpload_time() {
		return upload_time;
	}

	public void setUpload_time(Timestamp upload_time) {
		this.upload_time = upload_time;
	}


    private int id;
	

    private Timestamp operation_time;
     

    private String operator;


    private String bind_operator;


    private byte operation_type;
    

    private String key_name;


    private String lock_name;


    private String district;

    private byte status_;

    private Timestamp upload_time;
}
