package com.syb.datacloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.syb.datacloud.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Admin
 */
@RestController
@RequestMapping("msg")
public class MessageController {

    @Autowired
    private FileService fileService;

    private static String ALL_MSG = "all";

    @RequestMapping("file")
    public String getMessage(@RequestBody String strData) {
        if (StringUtils.isBlank(strData)) {
            return "传入数据为null";
        }
        JSONObject jsonObject = JSON.parseObject(strData);
        String fileId = jsonObject.getString("fileId");
        String fileName = jsonObject.getString("fileName");
        if (StringUtils.isNotBlank(fileId)) {
            if (ALL_MSG.equalsIgnoreCase(fileId)) {
                JSONArray jsonArray = fileService.readFileMessage();
                if (jsonArray == null) {
                    return null;
                }
                return jsonArray.toString();
            }
            JSONObject json = fileService.searchFile(fileId);
            if (json == null) {
                return null;
            }
            return json.toString();
        } else if (StringUtils.isNotBlank(fileName)) {
            JSONObject json = fileService.searchFileByName(fileName);
            if (json == null) {
                return null;
            }
            return json.toString();
        } else {
            return "错误的数据-->" + strData;
        }
    }

    @RequestMapping(value = "file/html", method = RequestMethod.GET)
    public String getMessage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileId", "all");
        return getMessage(jsonObject.toString());
    }

}
