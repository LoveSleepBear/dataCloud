package com.syb.datacloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syb.datacloud.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Admin
 */
@RestController
@RequestMapping("delete")
public class DeleteFileController {

    @Autowired
    private FileService fileService;

    /**
     *
     * @param strData
     * @return
     */
    public String deleteFile(@RequestBody String strData){
        if (StringUtils.isBlank(strData)){
            return "请输入参数";
        }

        JSONObject jsonObject = JSON.parseObject(strData);
        String fileId = jsonObject.getString("fileId");
        String fileName = jsonObject.getString("fileName");



        return strData;
    }

}
