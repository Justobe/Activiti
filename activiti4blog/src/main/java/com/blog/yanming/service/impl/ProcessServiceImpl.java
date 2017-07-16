package com.blog.yanming.service.impl;

import com.blog.yanming.model.MyActivity;
import com.blog.yanming.model.MyProcess;
import com.blog.yanming.model.MyProperty;
import com.blog.yanming.service.ProcessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanMing on 2017/7/16.
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * @Date: 16:38 2017/6/2
     * 获取所有激活中的流程
     */
    @Override
    public List<MyProcess> getAllUnsuspendProcess() {
        List<MyProcess> resList = new ArrayList<MyProcess>();
        try {
            List<ProcessDefinition>list = repositoryService.createProcessDefinitionQuery()
                    .orderByProcessDefinitionId().asc().list();
            for (ProcessDefinition process : list) {
                //判断是否挂起
                if(!process.isSuspended()) {
                    MyProcess myProcess = new MyProcess(process);
                    resList.add(myProcess);
                }
            }
            return resList;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }



    /**
     * @Date: 16:39 2017/6/2
     * 获取指定流程id 对应所有流程实例中的 所有的活动实例
     * @param processId 流程id
     */
    @Override
    public List<MyActivity> getAllHistoricActiIns(String processId) {
        List<MyActivity> resList = new ArrayList<>();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        for (HistoricActivityInstance hai : list) {
            //只显示 任务节点 和 开始结束事件
            if (hai.getActivityType().endsWith("Task") || hai.getActivityType().endsWith("Event")){
                resList.add(new MyActivity(hai));
            }
        }
        return resList;
    }


    /**
     * @Date: 16:40 2017/6/2
     * 获取所有流程定义
     */
    @Override
    public List<MyProcess> getAllProcess() {
        List<MyProcess> resList = new ArrayList<MyProcess>();
        try {
            List<ProcessDefinition>list = repositoryService.createProcessDefinitionQuery()
                    .orderByProcessDefinitionId().asc().list();
            for (ProcessDefinition process : list) {
                MyProcess myProcess = new MyProcess(process);
                resList.add(myProcess);
            }
            return resList;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @Date: 16:41 2017/6/2
     * 获取流程定义图片
     */
    @Override
    public BufferedImage getProcessImg(String processId) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processId).singleResult();
            String deployId = processDefinition.getDeploymentId();
            //获取流程定义的图片资源名
            String ImgResource = processDefinition.getDiagramResourceName();
            //根据部署id、资源名 查询图片二进制流
            InputStream imageStream = repositoryService.getResourceAsStream(deployId, ImgResource);
            BufferedImage bufferedImage = ImageIO.read(imageStream);
            return bufferedImage;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Date: 16:47 2017/6/2
     * 获取流程XML
     */
    @Override
    public String getProcessXML(String processId) {
        String resXML = "";
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processId).singleResult();
            String deployId = processDefinition.getDeploymentId();
            //获取 xml资源文件名
            String XMLResource = processDefinition.getResourceName();
            InputStream resourceAsStream = repositoryService
                    .getResourceAsStream(deployId, XMLResource);
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] bpmnByte = new byte[1024];
            int len = -1;
            while ((len = resourceAsStream.read(bpmnByte, 0, 1024)) != -1) {
                outSteam.write(bpmnByte, 0, len);
            }
            //首先把String转换为ISO-8859-1，然后在前端用UTF-8显示
            resXML =  new String(outSteam.toString().getBytes("UTF-8"),"ISO-8859-1");
            return resXML;
        }catch (Exception e){
            e.printStackTrace();
            return resXML;
        }
    }

    /**
     * @Date: 16:48 2017/6/2
     * 删除流程部署（定义）
     */
    @Override
    public boolean deleteProcess(String deployId) {
        try{
            repositoryService.deleteDeployment(deployId,true);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @Date: 16:48 2017/6/2
     * 更改流程状态
     * -1：错误 1：激活 2：挂起
     */
    @Override
    public String changeProcessStatus(String processId) {
        final String suspend = "2";
        final String unsuspend = "1";
        final String errorStatus = "-1";
        try{
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processId).singleResult();
            //激活流程 1
            if(processDefinition.isSuspended()){

                repositoryService.activateProcessDefinitionById(processId);
                return unsuspend;
            }
            //挂起流程 2
            else{
                // 此处为按ID挂起，同时挂起所有与流程相关,立刻挂起
                repositoryService.suspendProcessDefinitionById(processId);
                return suspend;
            }
        }catch (Exception e){
            e.printStackTrace();
            return errorStatus;
        }
    }

    /**
     * @Date: 16:49 2017/6/2
     * 流程转换成模型
     */
    @Override
    public void convertToModel(String processId) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processId).singleResult();
            InputStream bpmnStream = repositoryService
                    .getResourceAsStream(processDefinition.getDeploymentId(),
                            processDefinition.getResourceName());
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xmlStreamReader);
            BpmnJsonConverter converter = new BpmnJsonConverter();
            ObjectNode modelNode = converter.convertToJson(bpmnModel);
            Model model = repositoryService.newModel();
            model.setKey(processDefinition.getKey());
            model.setName(processDefinition.getResourceName());
            model.setCategory(processDefinition.getDeploymentId());
            ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME,
                    processDefinition.getName());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION,
                    processDefinition.getDescription());
            model.setMetaInfo(modelObjectNode.toString());
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(),
                    modelNode.toString().getBytes("UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * @Date: 16:51 2017/6/2
     * 获取指定流程进度追踪图
     */
    @Override
    public BufferedImage getProgressImg(String processInstanceId) {
        BufferedImage bufferedImage = null;
        //获取流程实例对象和流程定义实体对象来进行查询工作
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
        //获取历史活动实例列表，用于后续确定高亮的线
        List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        //高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);
        //高亮节点id集合
        List<String> highLighted = new ArrayList<String>();
        List<Execution> exeList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        CharSequence charSequence = "Task";
        for (Execution exe : exeList) {
            String type = historyService.createNativeHistoricActivityInstanceQuery().sql("select * from act_hi_actinst where ACT_ID_ ='"+exe.getActivityId()+"'").list().get(0).getActivityType();
            if(type.contains(charSequence)){
                highLighted.add(exe.getActivityId());
            }
        }
        //获取模型
        BpmnModel bpmnModel = repositoryService
                .getBpmnModel(processInstance.getProcessDefinitionId());
        //获取流程图片生成器
        DefaultProcessDiagramGenerator dpdg = new DefaultProcessDiagramGenerator();
        //绘制图片
        InputStream input = dpdg.generateDiagram(bpmnModel, "png", highLighted,highLightedFlows,"宋体","宋体",null,1.0);        try{
            bufferedImage= ImageIO.read(input);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bufferedImage;
    }


    /**
     * @Date: 16:51 2017/6/2
     * 获取历史活动 每个节点的 表单属性
     */
    @Override
    public List<MyProperty> getHisActiDetail(String processId,String activityInsId) {
        List<MyProperty> resList = new ArrayList<>();
        List<HistoricDetail> historicDetailList = historyService
                .createHistoricDetailQuery().processInstanceId(
                        processId).activityInstanceId(activityInsId).orderByFormPropertyId().asc().list();
        if (historicDetailList != null && historicDetailList.size() > 0) {
            for (HistoricDetail historicDetail:historicDetailList) {
                HistoricVariableUpdate variable = (HistoricVariableUpdate) historicDetail;

                System.out.println(variable.getVariableName() + variable.getValue());
                resList.add(new MyProperty(variable.getVariableName(),variable.getVariableTypeName(),variable.getValue().toString()));
            }
        }
        return resList;
    }

    /**
     * @Date: 16:55 2017/6/2
     * 获取需要高亮的线id
     */
    private List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size(); i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
            if ((i + 1) >= historicActivityInstances.size()) {
                break;
            }
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);// 后续第二个节点
                if (activityImpl2.getStartTime().getTime()-
                        activityImpl1.getStartTime().getTime()<100) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);

                } else {
                    String type = historyService.createNativeHistoricActivityInstanceQuery().sql("select * from act_hi_actinst where ACT_ID_ ='"+activityImpl2.getActivityId()+"'").list().get(0).getActivityType();
                    if(historicActivityInstances.get(i).getEndTime() != null && type.contains("parallel")){
                        ActivityImpl sameActivityImpl2 = processDefinitionEntity
                                .findActivity(activityImpl2.getActivityId());
                        sameStartTimeNodes.add(sameActivityImpl2);
                    }
                    // 有不相同跳出循环
                    break;
                }
            }
            if(historicActivityInstances.get(i).getEndTime() != null){
                List<PvmTransition> pvmTransitions = activityImpl
                        .getOutgoingTransitions();// 取出节点的所有出去的线
                for (PvmTransition pvmTransition : pvmTransitions) {
                    // 对所有的线进行遍历
                    ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                            .getDestination();
                    // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                    if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                        highFlows.add(pvmTransition.getId());
                    }
                }
            }
        }
        return highFlows;
    }


}
