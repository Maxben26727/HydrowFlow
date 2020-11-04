package com.max.hydro_flow;

public class area_data {
    private String areaID;
    private String areaName;

    public area_data(){}

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public area_data(String areaID, String areaName) {
        this.areaName = areaName;
        this.areaID=areaID;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

}