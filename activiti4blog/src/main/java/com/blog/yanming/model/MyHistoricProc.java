package com.blog.yanming.model;

import org.activiti.engine.history.HistoricProcessInstance;

/**
 * Created by YanMing on 2017/7/16.
 */
public class MyHistoricProc {
    private String hisProcId;
    private String hisProcName;
    private String hisProcStartTime;
    private String hisProcStatus;

    public MyHistoricProc(){

    }


    public MyHistoricProc(HistoricProcessInstance historicProcessInstance){
        this.hisProcId = historicProcessInstance.getId();
        this.hisProcStartTime = historicProcessInstance.getStartTime().toString();
        this.hisProcName = historicProcessInstance.getProcessDefinitionId();
        if(historicProcessInstance.getEndTime()!=null){
            this.hisProcStatus = "1";
        }else{
            this.hisProcStatus = "0";
        }
    }

    public String getHisProcId() {
        return hisProcId;
    }

    public void setHisProcId(String hisProcId) {
        this.hisProcId = hisProcId;
    }

    public String getHisProcStartTime() {
        return hisProcStartTime;
    }

    public void setHisProcStartTime(String hisProcStartTime) {
        this.hisProcStartTime = hisProcStartTime;
    }

    public String getHisProcStatus() {
        return hisProcStatus;
    }

    public void setHisProcStatus(String hisProcStatus) {
        this.hisProcStatus = hisProcStatus;
    }

    public String getHisProcName() {
        return hisProcName;
    }

    public void setHisProcName(String hisProcName) {
        this.hisProcName = hisProcName;
    }
}
