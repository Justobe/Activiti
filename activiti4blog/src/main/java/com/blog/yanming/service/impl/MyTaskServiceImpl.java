package com.blog.yanming.service.impl;

import com.blog.yanming.model.MyProperty;
import com.blog.yanming.model.MyTask;
import com.blog.yanming.service.MyTaskService;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanMing on 2017/7/16.
 */
@Service
public class MyTaskServiceImpl implements MyTaskService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FormService formService;

    /**
     * @Date: 16:33 2017/6/2
     * 获取正在运行中的任务 对应的流程实例
     */
    @Override
    public String getTaskProcessInstance(String taskId) {
        return runtimeService.createNativeProcessInstanceQuery().sql("select * from act_ru_task where ID_ = '"+taskId+"'").singleResult().getProcessInstanceId();
    }

    /**
     * @Date: 16:36 2017/6/2
     * 获取任务表单属性
     */
    @Override
    public List<MyProperty> getTaskFormProperty(String taskId) {
        List<MyProperty> resList = new ArrayList<>();
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        List<FormProperty> list = taskFormData.getFormProperties();
        for(FormProperty f : list){
            System.out.println(f.getName() + " " + f.getType().getName());
            resList.add(new MyProperty(f));
        }
        return resList;
    }

    /**
     * @Date: 16:37 2017/6/2
     * 获取某个用户所有的待办事务
     */
    @Override
    public List<MyTask> getTaskByUsername(String username) {
        List<MyTask> resList = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateOrAssigned(username).list();
        for(Task task : list){
            MyTask myTask = new MyTask(task);
            String processInstanceId = task.getProcessInstanceId();
            String startMan = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult().getStartUserId();
            myTask.setStartMan(startMan);
            resList.add(myTask);
        }
        return resList;
    }
}