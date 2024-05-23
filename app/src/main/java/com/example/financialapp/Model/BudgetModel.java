package com.example.financialapp.Model;

public class BudgetModel {
    private String id;
    private String name;
    private long timeStampStart;
    private long timeStampEnd;
    private long period;
    private long budget;
    private long spending;
    private boolean ongoing;
    private boolean budgetOverspent;
    private boolean riskOverspending;
    private String userId;

    public BudgetModel() {

    }

    public BudgetModel(String id, String name, long timeStampStart, long period, long budget, long spending,
                       boolean ongoing, boolean budgetOverspent, boolean riskOverspending, String userId) {
        this.id = id;
        this.name = name;
        this.timeStampStart = timeStampStart;
        this.period = period;
        this.timeStampEnd = timeStampStart + period * 86400;
        this.budget = budget;
        this.spending = spending;
        this.ongoing = ongoing;
        this.budgetOverspent = budgetOverspent;
        this.riskOverspending = riskOverspending;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStampStart() {
        return timeStampStart;
    }

    public void setTimeStampStart(long timeStampStart) {
        this.timeStampStart = timeStampStart;
    }

    public long getTimeStampEnd() {
        return timeStampEnd;
    }

    public void setTimeStampEnd(long timeStampEnd) {
        this.timeStampEnd = timeStampEnd;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public long getSpending() {
        return spending;
    }

    public void setSpending(long spending) {
        this.spending = spending;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public boolean isBudgetOverspent() {
        return budgetOverspent;
    }

    public void setBudgetOverspent(boolean budgetOverspent) {
        this.budgetOverspent = budgetOverspent;
    }

    public boolean isRiskOverspending() {
        return riskOverspending;
    }

    public void setRiskOverspending(boolean riskOverspending) {
        this.riskOverspending = riskOverspending;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
