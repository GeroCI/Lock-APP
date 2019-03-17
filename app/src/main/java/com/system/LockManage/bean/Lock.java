package com.system.LockManage.bean;


public class Lock {
 
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

	public String getMac_addr() {
		return mac_addr;
	}

	public void setMac_addr(String mac_addr) {
		this.mac_addr = mac_addr;
	}

	public byte getSignal_value() {
		return signal_value;
	}

	public void setSignal_value(byte signal_value) {
		this.signal_value = signal_value;
	}

	public int getDevice_key() {
		return device_key;
	}
	
	public void setDevice_key(int device_key) {
		this.device_key = device_key;
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

	public String getInstallation_site() {
		return installation_site;
	}

	public void setInstallation_site(String installation_site) {
		this.installation_site = installation_site;
	}

	public String getDetail_addr() {
		return detail_addr;
	}

	public void setDetail_addr(String detail_addr) {
		this.detail_addr = detail_addr;
	}

	public float getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}


    private int id;
     

    private String lock_name;


    private String mac_addr;


    private byte signal_value;


    private int device_key;
    

    private String belonger;
    

    private byte status_;


    private String installation_site;


    private String detail_addr;
    

    private float lng;
    

    private float lat;
    

    private String notes;

}