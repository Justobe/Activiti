package com.blog.yanming.model;

import org.activiti.engine.repository.Model;

/**
 * Created by YanMing on 2017/7/15.
 */
public class MyModel {
    private String modelId;
    private String modelKey;
    private String modelName;
    private String modelVersion;
    private String modelCtime;
    private String modelLMtime;
    private String modelMetaData;

    public MyModel(){

    }
    public MyModel(Model model){
        this.modelId = model.getId();
        this.modelKey = model.getKey();
        this.modelName = model.getName();
        this.modelVersion = model.getVersion().toString();
        this.modelCtime = model.getCreateTime().toString();
        this.modelLMtime=model.getLastUpdateTime().toString();
        this.modelMetaData = model.getMetaInfo();

    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelKey() {
        return modelKey;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelCtime() {
        return modelCtime;
    }

    public void setModelCtime(String modelCtime) {
        this.modelCtime = modelCtime;
    }

    public String getModelLMtime() {
        return modelLMtime;
    }

    public void setModelLMtime(String modelLMtime) {
        this.modelLMtime = modelLMtime;
    }

    public String getModelMetaData() {
        return modelMetaData;
    }

    public void setModelMetaData(String modelMetaData) {
        this.modelMetaData = modelMetaData;
    }
}