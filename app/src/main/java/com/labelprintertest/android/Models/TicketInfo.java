package com.labelprintertest.android.Models;

public class TicketInfo {
    private TicketModel model;
    private int num;
    private TicketType type;

    public TicketInfo () {

    }

    public void setModel(TicketModel val) {
        model = val;
    }
    public TicketModel getModel() {
        return model;
    }

    public void setNum(int val) {
        num = val;
    }
    public int getNum() {
        return num;
    }

    public void setType(TicketType val) {
        type = val;
    }
    public TicketType getType() {
        return type;
    }
}
