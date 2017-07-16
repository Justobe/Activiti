package com.blog.yanming.model;

import org.activiti.engine.task.Task;

/**
 * Created by YanMing on 2017/7/16.
 */
public class MyTask {
    private String taskId;
    private String taskName;
    private String startTime;
    private String startMan;

    public MyTask(){

    }
    public MyTask(Task task){
        this.taskId = task.getId();
        this.startTime = task.getCreateTime().toString();
        this.taskName = task.getProcessDefinitionId() +"-->"+ task.getName();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartMan() {
        return startMan;
    }

    public void setStartMan(String startMan) {
        this.startMan = startMan;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
