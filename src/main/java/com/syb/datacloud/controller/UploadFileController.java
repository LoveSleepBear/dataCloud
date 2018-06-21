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
@RequestMapping("upload")
public class UploadFileController {

    @Autowired
    private FileService fileService;

    @RequestMapping("file")
    public String uploadFile(@RequestBody String strData){
        if (StringUtils.isBlank(strData)){
            return "传入数据为null";
        }
        JSONObject jsonObject = JSON.parseObject(strData);
        String fileName = jsonObject.getString("fileName");
        String fileBase64 = jsonObject.getString("fileBase64");
        if (StringUtils.isBlank(fileName)||StringUtils.isBlank(fileBase64)){
            return "请确定传入参数(fileName或fileBase64为null)-->fileName:"+fileName+"--->fileBase64:"+fileBase64;
        }
        return fileService.uploadFile(fileBase64, fileName);
    }

}
