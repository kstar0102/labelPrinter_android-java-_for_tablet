package com.labelprintertest.android.Models;

public class TicketType {
    private String type;
    private String name;
    private int order;
    private String isShown; //表示区分

    public TicketType () {

    }

    public void setType(String val) {
        type = val;
    }
    public String getType() {
        return type;
    }

    public void setName(String val) {
        name = val;
    }
    public String getName() {
        return name;
    }

    public void setOrder(int val) {
        order = val;
    }
    public int getOrder() {
        return order;
    }

    public void setIsShown(String val) {
        isShown = val;
    }
    public String getIsShown() {
        return isShown;
    }
}
