package com.system.LockManage.bean;

import java.sql.Timestamp;


public class Authorization {
 

    private int id;
    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getLock_name() {
		return lock_name;
	}

	public void setLock_name(String lock_name) {
		this.lock_name = lock_name;
	}
	
    public int getLock_id() {
		return lock_id;
	}

	public void setLock_id(int lock_id) {
		this.lock_id = lock_id;
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


    private String name;
    

    private Timestamp create_time;
    

    private byte type;
    

    private String staff;
     

    private String lock_name;


    private int lock_id;


    private Timestamp start_time;


    private Timestamp end_time;

}