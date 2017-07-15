package com.blog.yanming.service;

import net.sf.json.JSONArray;

/**
 * Created by YanMing on 2017/7/15.
 */
public interface ModelService {

    JSONArray getAllModule();
    boolean deployModel(String modelId);
    String exportModel(String modelId);
    boolean deleteModel(String modelId);
}
