package com.blog.yanming.model;

import org.activiti.engine.form.FormProperty;

/**
 * Created by YanMing on 2017/7/16.
 */
public class MyProperty {
    private String fieldName;
    private String fieldType;
    private String fieldValue;

    public MyProperty(){

    }
    public MyProperty(String fieldName,String fieldType){
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
    public MyProperty(String fieldName,String fieldType,String fieldValue){
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldValue = fieldValue;
    }
    public MyProperty(FormProperty property){
        this.fieldName = property.getName();
        this.fieldType = property.getType().getName();
        this.fieldValue = property.getValue();
    }
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}