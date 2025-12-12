package com.example.electricitybillcalculator2;

public class BillModel {
    private int id;
    private String month;
    private double unit;
    private double totalCharges;
    private int rebate;
    private double finalCost;

    // Constructor kosong
    public BillModel() {}

    // Constructor dengan parameter
    public BillModel(String month, double unit, double totalCharges, int rebate, double finalCost) {
        this.month = month;
        this.unit = unit;
        this.totalCharges = totalCharges;
        this.rebate = rebate;
        this.finalCost = finalCost;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public double getUnit() { return unit; }
    public void setUnit(double unit) { this.unit = unit; }

    public double getTotalCharges() { return totalCharges; }
    public void setTotalCharges(double totalCharges) { this.totalCharges = totalCharges; }

    public int getRebate() { return rebate; }
    public void setRebate(int rebate) { this.rebate = rebate; }

    public double getFinalCost() { return finalCost; }
    public void setFinalCost(double finalCost) { this.finalCost = finalCost; }
}
