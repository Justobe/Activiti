package com.blog.yanming.service;

import com.blog.yanming.model.MyProperty;
import com.blog.yanming.model.MyTask;

import java.util.List;

/**
 * Created by YanMing on 2017/7/16.
 */
public interface MyTaskService {
    List<MyTask> getTaskByUsername(String username);
    List<MyProperty> getTaskFormProperty(String taskId);
    String getTaskProcessInstance(String taskId);
}
