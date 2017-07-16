package com.blog.yanming.model;

import org.activiti.engine.history.HistoricActivityInstance;

/**
 * Created by YanMing on 2017/7/16.
 */
public class MyActivity {
    private String id = "";
    private String activityId = "";
    private String activityType = "";
    private String activityName = "";
    private String assignee ="";
    private String startTime ="";
    private String endTime ="";

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public MyActivity(){

    }
    public MyActivity(HistoricActivityInstance historicActivityInstance){
        if(historicActivityInstance.getActivityId()!=null){
            this.activityId = historicActivityInstance.getActivityId();
        }
        if(historicActivityInstance.getActivityId()!=null){
            this.id = historicActivityInstance.getId();
        }
        if(historicActivityInstance.getActivityName()!=null){
            this.activityName = historicActivityInstance.getActivityName();
        }
        if(historicActivityInstance.getAssignee()!=null){
            this.assignee = historicActivityInstance.getAssignee();
        }
        if(historicActivityInstance.getStartTime()!=null){
            this.startTime = historicActivityInstance.getStartTime().toString();
        }
        if(historicActivityInstance.getEndTime()!=null){
            this.endTime = historicActivityInstance.getEndTime().toString();
        }
        if(historicActivityInstance.getActivityType()!=null){
            this.activityType = historicActivityInstance.getActivityType();
        }
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
