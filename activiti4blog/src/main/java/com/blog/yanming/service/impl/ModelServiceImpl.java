package com.blog.yanming.service.impl;

import com.blog.yanming.model.MyModel;
import com.blog.yanming.service.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.json.JSONArray;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanMing on 2017/7/15.
 */
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * @Date: 16:26 2017/6/2
     * 获取所有模型
     */
    @Override
    public JSONArray getAllModule() {
        List<MyModel> resList = new ArrayList<MyModel>();
        JSONArray jsonArray = null;
        try {
            //获取所有模型列表
            List<Model>list = repositoryService.createModelQuery().orderByCreateTime()
                    .desc().list();
            for (Model model : list) {
                //转换为前端显示MyModel
                MyModel myModel = new MyModel(model);
                resList.add(myModel);
            }
            jsonArray = JSONArray.fromObject(resList);
            return jsonArray;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return jsonArray;
        }
    }

    /**
     * @Date: 16:28 2017/6/2
     * 部署模型
     */
    @Override
    public boolean deployModel(String modelId){
        //System.out.println(modelId);
        try {
            //获取指定模型id的模型
            Model modelData = repositoryService.getModel(modelId);
            //解析模型
            ObjectNode modelNode = (ObjectNode) new ObjectMapper()
                    .readTree(repositoryService.getModelEditorSource(modelData.getId()));
            byte [] bpmnBytes = null;
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            String processName = modelData.getName() + ".bpmn20.xml";
            //部署
            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName,new String(bpmnBytes))
                    .deploy();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * @Date: 16:31 2017/6/2
     * 导出模型定义
     */
    @Override
    public String exportModel(String modelId) {
        String res = "";
        try {
            Model modelData = repositoryService.getModel(modelId);
            ObjectNode modelNode = (ObjectNode) new ObjectMapper()
                    .readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            byte [] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            byte [] bytes = new byte [1024];
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int len = -1;
            while ((len = in.read(bytes,0,1024))!=-1){
                outStream.write(bytes,0,len);
            }
            res = new String(outStream.toString().getBytes("UTF-8"),"ISO-8859-1");
            return res;
        }catch (Exception e){
            e.printStackTrace();
            return res;
        }
    }

    /**
     * @Date: 16:31 2017/6/2
     * 删除模型
     */
    @Override
    public boolean deleteModel(String modelId) {
        try {
            repositoryService.deleteModel(modelId);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}