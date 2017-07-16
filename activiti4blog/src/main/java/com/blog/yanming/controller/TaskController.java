package com.blog.yanming.controller;

import com.blog.yanming.service.MyTaskService;
import net.sf.json.JSONArray;
import org.activiti.engine.FormService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YanMing on 2017/7/16.
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private MyTaskService myTaskService;
    @Autowired
    private FormService formService;
    @Autowired
    private TaskService taskService;

    /**
     * @Date: 15:42 2017/6/18
     * 根据用户名获取任务（出现在我的待办中）
     */
    @RequestMapping(value = "/getTaskByUsername",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getTaskByUsername(@RequestParam("username")String username) throws Exception{
        username = new String(username.getBytes("ISO-8859-1"),"UTF-8");
        return JSONArray.fromObject(myTaskService.getTaskByUsername(username));
    }

    /**
     * @Date: 15:44 2017/6/16
     * 根据任务id获取任务表单的属性
     */
    @RequestMapping(value = "getCmnt",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getTaskFormProperty(@RequestParam("taskId") String taskId) throws UnsupportedEncodingException {
        taskId = new String(taskId.getBytes("ISO-8859-1"),"UTF-8");
        return JSONArray.fromObject(myTaskService.getTaskFormProperty(taskId));
    }

    /**
     * @Date: 15:53 2017/6/16
     * 根据任务id 完成某个任务
     *
     */
    @RequestMapping(value = "completeTask",method = RequestMethod.GET)
    @ResponseBody
    public String completeTask(HttpServletRequest request) throws UnsupportedEncodingException{
        try {
            //获取任务id
            String taskId = request.getParameter("taskId");
            //查询该任务的表单属性
            FormData formData = formService.getTaskFormData(taskId);
            Map<String, Object> variables = new HashMap<>();
            //获取表单数据的属性集合
            List<FormProperty> cmnt = formData.getFormProperties();
            for (FormProperty property : cmnt) { //遍历属性集合所有属性

                String pName = property.getName();
                pName = new String(pName.getBytes("UTF-8"),"ISO-8859-1");
                String pVar = request.getParameter(pName); //在request获取属性值
                if(pVar == null){ //如果request中没有该值
                    return "wrong"; // 返回错误状态
                }else {
                    pName = new String(pName.getBytes("ISO-8859-1"), "UTF-8");
                    pVar = new String(pVar.getBytes("ISO-8859-1"), "UTF-8");
                    variables.put(pName, pVar);
                }
            }
            taskService.complete(taskId, variables); // 完成任务
            return "right";
        }catch (Exception e){
            e.printStackTrace();
            return "wrong";
        }
    }
}