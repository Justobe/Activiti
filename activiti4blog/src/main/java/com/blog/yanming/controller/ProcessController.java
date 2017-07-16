package com.blog.yanming.controller;

import com.blog.yanming.entity.User;
import com.blog.yanming.model.MyHistoricProc;
import com.blog.yanming.model.MyProperty;
import com.blog.yanming.service.MyTaskService;
import com.blog.yanming.service.ProcessService;
import com.blog.yanming.utils.SpringSessionUtil;
import net.sf.json.JSONArray;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanMing on 2017/7/16.
 */
@Controller
@RequestMapping("/process")
public class ProcessController {

    @Autowired
    private ProcessService processService;
    @Autowired
    private FormService formService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private MyTaskService myTaskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * @Date: 14:18 2017/6/16
     * 获取所有流程定义
     */
    @RequestMapping(value = "/getAllProcess",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getAllProcess(){

        return JSONArray.fromObject(processService.getAllProcess());
    }

    /**
     * @Date: 14:23 2017/6/16
     * 获取指定流程定义XML
     * @param request 前端传回请求
     */
    @RequestMapping(value = "/getProcessXML",method = RequestMethod.GET)
    @ResponseBody
    public String getProcessXML(HttpServletRequest request) throws Exception{
        //在String中添加<xmp>，在html中显示xml标签
        String resXML = "<xmp>";
        String processId = request.getParameter("processId");
        //对流程id进行转码
        processId = new String(processId.getBytes("ISO-8859-1"),"UTF-8");
        resXML = resXML + processService.getProcessXML(processId) + "</xmp>";
        return resXML;
    }

    /**
     * @Date: 14:24 2017/6/16
     * 获取指定流程定义Img
     * @param request 前端传回请求
     */
    @RequestMapping(value = "/getProcessImg",method = RequestMethod.GET)
    @ResponseBody
    public String getProcessImg(HttpSession session, HttpServletRequest request/*, HttpServletResponse response*/) throws UnsupportedEncodingException {
        //获取request中的processId，进行转码为UTF-8
        String processId = request.getParameter("processId");
        processId = new String(processId.getBytes("ISO-8859-1"),"UTF-8");
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processId).singleResult();
        String processName =processDefinition.getName();
        String processDeploy = processDefinition.getDeploymentId();
        try {
            //设置response编码和内容类型
            //response.setCharacterEncoding("UTF-8");
            //response.setContentType("image/png");
            //获取流通图片bufferImage
            String fileName = "";
            String fileName_url = "";
            User tmpUser = SpringSessionUtil.getSession(session);
            if(tmpUser!=null) {
                //fileName = "D://tempory-def//" +tmpUser.getUsername()+"-"+ processDeploy + ".png";
                String rootContext = System.getProperty("web.root");
                fileName = rootContext + "/static/process_images/" + tmpUser.getUsername()+"-"+ processDeploy + ".png";
                System.out.println(fileName);
                File file = new File(fileName);
                BufferedImage bufferedImage = processService.getProcessImg(processId);
                //写入response中
                ImageIO.write(bufferedImage, "PNG", file);
                //fileName_url = "http://localhost:8080/activiti3.0/static/process_images/" + tmpUser.getUsername()+"-"+ processDeploy + ".png";
                fileName_url = request.getContextPath() + "/static/process_images/" + tmpUser.getUsername()+"-"+ processDeploy + ".png";
            }
            return fileName_url;
        }catch (Exception e){
            e.printStackTrace();
            return "wrong";
        }
    }

    /**
     * @Date: 14:31 2017/6/16
     * 删除流程定义
     * @param deployId 流程部署Id
     */
    @RequestMapping(value = "/deleteProcess",method = RequestMethod.GET)
    @ResponseBody
    public String deleteProcess(@RequestParam("deployId")String deployId) throws Exception{
        deployId = new String(deployId.getBytes("ISO-8859-1"),"UTF-8");
        return processService.deleteProcess(deployId)?"right":"wrong";
    }

    /**
     * @Date: 14:41 2017/6/16
     * 更改流程状态
     * 1：激活 2：挂起
     */
    @RequestMapping(value = "/changeStatus",method = RequestMethod.GET)
    @ResponseBody
    public String changeProcessStatus(@RequestParam("processId")String processId) throws Exception{
        processId = new String(processId.getBytes("ISO-8859-1"),"UTF-8");
        return processService.changeProcessStatus(processId);
    }

    /**
     * @Date: 14:42 2017/6/16
     * 将流程转换为模型
     */
    @RequestMapping(value = "/convertToModel",method = RequestMethod.GET)
    @ResponseBody
    public String convertToModel(@RequestParam("processId")String processId)
            throws UnsupportedEncodingException,XMLStreamException {
        try {
            processId = new String(processId.getBytes("ISO-8859-1"), "UTF-8");
            processService.convertToModel(processId);
            return "right";
        }catch (Exception e){
            e.printStackTrace();
            return "wrong";
        }
    }

    /**
     * @Date: 14:43 2017/6/16
     * 获取流程属性
     */
    //未使用
    @RequestMapping(value = "/getProperty",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getProperty(@RequestParam("processId")String processId) throws UnsupportedEncodingException
    {

        processId = new String(processId.getBytes("ISO-8859-1"),"UTF-8");
        List<MyProperty> resList = new ArrayList<>();
        //获取流程开始节点的属性
        StartFormData startFormData = formService.getStartFormData(processId);
        List<FormProperty> list = startFormData.getFormProperties();
        for(FormProperty f : list){
            System.out.println(f.getName() + " " + f.getType().getName());
            resList.add(new MyProperty(f.getName(),f.getType().getName()));
        }
        return JSONArray.fromObject(resList);

    }

    /**
     * @Date: 14:45 2017/6/16
     * 获取所有激活流程
     */
    @RequestMapping(value = "/getUnsuspendProcess",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getUnsusProcess(){
        return JSONArray.fromObject(processService.getAllUnsuspendProcess());
    }

    /**
     * @Date: 14:46 2017/6/16
     * 获取指定任务 所属流程实例 中的各个活动任务节点具体信息（不包括表单属性）
     */
    @RequestMapping(value = "/getDetail",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDetail(HttpServletRequest request) throws UnsupportedEncodingException{
        String taskId = request.getParameter("taskId");
        taskId = new String(taskId.getBytes("ISO-8859-1"),"UTF-8");
        String processId = myTaskService.getTaskProcessInstance(taskId);
        return JSONArray.fromObject(processService.getAllHistoricActiIns(processId));
    }

    /**
     * @Date: 14:53 2017/6/16
     *  获取某一历史活动节点表单属性
     */
    @RequestMapping(value = "/getHisDetail",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getHisDetail(HttpServletRequest request) throws UnsupportedEncodingException{
        //获取任务节点id
        String taskId = request.getParameter("taskId");
        //获取活动节点id
        String activityInsId = request.getParameter("activityInsId");
        taskId = new String(taskId.getBytes("ISO-8859-1"),"UTF-8");
        activityInsId = new String(activityInsId.getBytes("ISO-8859-1"),"UTF-8");
        //获取流程实例节点id
        String processId = myTaskService.getTaskProcessInstance(taskId);
        return JSONArray.fromObject(processService.getHisActiDetail(processId,activityInsId));
    }

    /**
     * @Date: 15:35 2017/6/16
     * 获取获取某用户所有历史流程实例集合，包括已完结和正在处理的
     * @param username 用户名
     */
    @RequestMapping(value = "/getHisProc",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getHisProc(@RequestParam("username")String username) throws UnsupportedEncodingException{
        username = new String(username.getBytes("ISO-8859-1"),"UTF-8");
        //获取某用户所有历史流程实例集合
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().startedBy(username).list();
        List<MyHistoricProc> resList = new ArrayList<>();
        for (HistoricProcessInstance hpi : list) {
            resList.add(new MyHistoricProc(hpi));
        }
        return JSONArray.fromObject(resList);
    }

    /**
     * @Date: 15:36 2017/6/16
     * 发起一个流程实例
     */
    @RequestMapping(value = "/startProcess",method = RequestMethod.GET)
    @ResponseBody
    public String startProcess(HttpServletRequest request) throws UnsupportedEncodingException{
        try {
            //获取流程发起人
            String startMan = request.getParameter("startMan");
            //获取流程Id
            String processId = request.getParameter("processId");
            startMan = new String(startMan.getBytes("ISO-8859-1"),"UTF-8");
            processId = new String(processId.getBytes("ISO-8859-1"),"UTF-8");
            //设置流程定义中的applyUserId的值
            identityService.setAuthenticatedUserId(startMan);
            //发起流程实例
            runtimeService.startProcessInstanceById(processId);
            return "right";
        }catch (Exception e){
            return "wrong";
        }
    }

    /**
     * @Date: 15:37 2017/6/16
     * 根据任务节点Id获取流程进度追踪图
     */
    @RequestMapping(value = "/getProgressImg",method = RequestMethod.GET)
    @ResponseBody
    public String getProgress(HttpSession session,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{

        String taskId = request.getParameter("taskId");
        taskId = new String(taskId.getBytes("ISO-8859-1"),"UTF-8");
        String processInstanceId = myTaskService.getTaskProcessInstance(taskId);
        BufferedImage bufferedImage = processService.getProgressImg(processInstanceId);
        try {
            //设置response编码和内容类型
            //response.setCharacterEncoding("UTF-8");
            //response.setContentType("image/png");
            String fileName = "";
            String fileName_url = "";
            User tmpUser = SpringSessionUtil.getSession(session);
            if(tmpUser!=null) {
                //fileName = "D://tempory-prg//" +tmpUser.getUsername()+"-"+ processInstanceId + "-" + taskId + ".png";
                //fileName = "E://activiti30//src//main//webapp//static//process_images//" +tmpUser.getUsername()+"-"+ processInstanceId + "-" + taskId + ".png";
                String rootContext = System.getProperty("web.root");
                fileName = rootContext + "//static//process_images//"+tmpUser.getUsername()+"-"+ processInstanceId + "-" + taskId + ".png";
                System.out.println(fileName);
                File file = new File(fileName);
                ImageIO.write(bufferedImage, "PNG", file);
                fileName_url = request.getContextPath() + "/static/process_images/" + tmpUser.getUsername()+"-"+ processInstanceId + "-" + taskId + ".png";

            }
            return fileName_url;
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * @Date: 15:39 2017/6/16
     * 根据历史流程实例Id获取进度追踪图
     *
     */
    @RequestMapping(value = "/getHisProgressImg",method = RequestMethod.GET)
    @ResponseBody
    public String getHisProgress(HttpSession session,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{

        String processInstanceId = request.getParameter("processId");
        processInstanceId = new String(processInstanceId.getBytes("ISO-8859-1"),"UTF-8");
        BufferedImage bufferedImage = processService.getProgressImg(processInstanceId);
        try {
            //response.setCharacterEncoding("UTF-8");
            //response.setContentType("image/png");
            String fileName = "";
            String fileName_url = "";
            User tmpUser = SpringSessionUtil.getSession(session);
            if(tmpUser!=null) {
                //fileName = "D://tempory-his//"+tmpUser.getUsername()+ "-" + "his-" + processInstanceId + ".png";
                //fileName = "E://activiti30//src//main//webapp//static//process_images//"+tmpUser.getUsername()+ "-" + "his-" + processInstanceId + ".png";
                String rootContext = System.getProperty("web.root");
                fileName = rootContext+ "//static//process_images//"+tmpUser.getUsername()+ "-" + "his-" + processInstanceId + ".png";
                System.out.println(fileName);
                File file = new File(fileName);
                ImageIO.write(bufferedImage, "PNG", file);
                fileName_url = request.getContextPath() + "/static/process_images/" + tmpUser.getUsername()+ "-" + "his-" + processInstanceId + ".png";
            }

            return fileName_url;
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }




}
