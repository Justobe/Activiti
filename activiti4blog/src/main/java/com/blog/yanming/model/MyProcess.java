package com.blog.yanming.model;

import org.activiti.engine.repository.ProcessDefinition;

/**
 * Created by YanMing on 2017/7/16.
 */
public class MyProcess {
    private String processId;
    private String deployId;
    private String processName;
    private String processKey;
    private String processDes;
    private String processVer;
    private String processStatus;

    private final String suspend = "2";
    private final String unsuspend = "1";


    public MyProcess(){

    }

    public MyProcess(ProcessDefinition processDefinition){
        this.processId = processDefinition.getId();
        this.deployId = processDefinition.getDeploymentId();
        this.processName = processDefinition.getName();
        this.processKey = processDefinition.getKey();
        this.processDes = processDefinition.getDescription();
        this.processVer = String.valueOf(processDefinition.getVersion());
        if(!processDefinition.isSuspended()){
            this.processStatus = unsuspend;
        }
        else{
            this.processStatus =suspend;
        }
    }
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getProcessDes() {
        return processDes;
    }

    public void setProcessDes(String processDes) {
        this.processDes = processDes;
    }

    public String getProcessVer() {
        return processVer;
    }

    public void setProcessVer(String processVer) {
        this.processVer = processVer;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
}