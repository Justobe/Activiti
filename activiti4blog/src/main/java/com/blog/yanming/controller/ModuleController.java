package com.blog.yanming.controller;

import com.blog.yanming.service.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.json.JSONArray;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * Created by YanMing on 2017/7/15.
 */


/**
 * Created by YanMing on 2017/6/8.
 */
/**
 * @Date: 13:33 2017/6/6
 * 实现关于module管理相关操作功能
 */
@Controller
@RequestMapping("/model")
public class ModuleController {
    private Logger logger = LoggerFactory.getLogger(ModuleController.class);


    @Autowired
    private ModelService modelService;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * @Date: 13:41 2017/6/15
     * 创建模型功能
     * @param name 模型名称
     * @param key 模型key值
     * @param description 模型描述
     *
     */
    @RequestMapping(value = "create")
    public void create(@RequestParam("name")String name,
                       @RequestParam("key")String key,
                       @RequestParam("description")String description,
                       HttpServletRequest request, HttpServletResponse response) {
        try{
            //将前端 ISO-8859-1编码转码为 UTF-8
            name = new String(name.getBytes("ISO-8859-1"),"UTF-8");
            key = new String(key.getBytes("ISO-8859-1"),"UTF-8");
            description = new String(description.getBytes("ISO-8859-1"),"UTF-8");
            //创建一个activiti bpmn模板对象
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id","canvas");
            editorNode.put("resourceId","canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset",stencilSetNode); //为模型绑定参数
            Model modelData = repositoryService.newModel(); //创建模型对象

            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME,name);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION,1);
            description = StringUtils.defaultString(description);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION,description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(name);
            modelData.setKey(StringUtils.defaultString(key));

            repositoryService.saveModel(modelData);  //保存模型模型
            repositoryService.addModelEditorSource(modelData.getId(),editorNode.toString().getBytes("utf-8"));
            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());//跳转到模型设计界面
        }catch (Exception e){
            logger.error("创建模型失败",e);
        }
    }

    /**
     * @Date: 13:44 2017/6/15
     * 获取所ACTI_RE_MODEL表所有模型信息
     *
     */
    @RequestMapping(value = "/getAllModel",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getAllModel(){
        return modelService.getAllModule();
    }

    /**
     * @Date: 13:45 2017/6/15
     * 根据指定模型Id、部署模型
     * 返回状态字符串
     *
     */
    @RequestMapping(value = "/deployModel",method = RequestMethod.POST)
    @ResponseBody
    public String deployModel(@RequestParam("modelId") String modelId) throws UnsupportedEncodingException{
        //前端modelId转码为UTF-8
        modelId = new String(modelId.getBytes("ISO-8859-1"),"UTF-8");
        return modelService.deployModel(modelId)?"right":"wrong";
    }

    /**
     * @Date: 13:47 2017/6/15
     * 根据制定模型Id、导出模型XML文件，显示在新的HTML中
     *
     */
    @RequestMapping(value = "/exportModel",method = RequestMethod.GET)
    @ResponseBody
    public String exportModel(HttpServletRequest request) throws UnsupportedEncodingException{
        //获取modelId并转码
        String modelId = request.getParameter("modelId");
        modelId = new String(modelId.getBytes("ISO-8859-1"),"UTF-8");
        String res = "<xmp>";
        res =  res + modelService.exportModel(modelId) + "</xmp>";
        return res;
    }

    /**
     * @Date: 13:48 2017/6/15
     * 根据制定模型Id、删除模型
     *
     */
    @RequestMapping(value = "/deleteModel",method = RequestMethod.POST)
    @ResponseBody
    public String deleteModel(@RequestParam("modelId") String modelId)throws UnsupportedEncodingException{
        modelId = new String(modelId.getBytes("ISO-8859-1"),"UTF-8");
        return modelService.deleteModel(modelId)?"right":"wrong";
    }

}
