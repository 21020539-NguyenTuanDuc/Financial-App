package com.example.financialapp.Model;

import java.io.Serializable;

public class GoalModel implements Serializable {
    private String id;
    private String name;
    private long target;
    private long saved;
    private boolean goalAchievedNoti;
    private boolean reached;
    private String userId;

    public GoalModel() {
    }

    public GoalModel(String id, String name, long target, long saved, boolean goalAchievedNoti, boolean reached, String userId) {
        this.id = id;
        this.name = name;
        this.target = target;
        this.saved = saved;
        this.goalAchievedNoti = goalAchievedNoti;
        this.reached = reached;
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

    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
    }

    public long getSaved() {
        return saved;
    }

    public void setSaved(long saved) {
        this.saved = saved;
    }

    public boolean isGoalAchievedNoti() {
        return goalAchievedNoti;
    }

    public void setGoalAchievedNoti(boolean goalAchievedNoti) {
        this.goalAchievedNoti = goalAchievedNoti;
    }

    public boolean isReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
