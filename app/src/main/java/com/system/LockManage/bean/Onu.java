package com.system.LockManage.bean;
import java.sql.Timestamp;

public class Onu {

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

    public String getBelonger() {
        return belonger;
    }

    public void setBelonger(String belonger) {
        this.belonger = belonger;
    }

    public Timestamp getSetup_time() {
        return setup_time;
    }

    public void setSetup_time(Timestamp setup_time) {
        this.setup_time = setup_time;
    }

    public String getIn_onu() {
        return in_onu;
    }

    public void setIn_onu(String in_onu) {
        this.in_onu = in_onu;
    }

    public String getOut_onu() {
        return out_onu;
    }

    public void setOut_onu(String out_onu) {
        this.out_onu = out_onu;
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

    public int getStatus_() {
        return status_;
    }

    public void setStatus_(int status_) {
        this.status_ = status_;
    }

    public float getOccupancy_rate() {
        return occupancy_rate;
    }

    public void setOccupancy_rate(float occupancy_rate) {
        this.occupancy_rate = occupancy_rate;
    }


    private int id;


    private String name;

    private String belonger;

    private Timestamp setup_time;

    private String in_onu;

    private String out_onu;

    private float lng;

    private float lat;

    private int status_;

    private float occupancy_rate;

}
