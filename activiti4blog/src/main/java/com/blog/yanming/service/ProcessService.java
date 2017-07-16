package com.blog.yanming.service;

import com.blog.yanming.model.MyActivity;
import com.blog.yanming.model.MyProcess;
import com.blog.yanming.model.MyProperty;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by YanMing on 2017/7/16.
 */
public interface ProcessService {
    List<MyProcess> getAllProcess();
    List<MyProcess> getAllUnsuspendProcess();
    String getProcessXML(String processId);
    BufferedImage getProcessImg(String processId);
    boolean deleteProcess(String deployId);
    String changeProcessStatus(String processId);
    void convertToModel(String processId);
    List<MyActivity> getAllHistoricActiIns(String processId);
    BufferedImage getProgressImg(String processId);
    List<MyProperty> getHisActiDetail(String processId, String activityInsId);
}
