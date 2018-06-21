package com.syb.datacloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syb.datacloud.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Admin
 */
@RestController
@RequestMapping("download")
public class DownloadFileController {

    @Autowired
    private FileService fileService;

    @RequestMapping("file")
    public String downloadFile(@RequestBody String strData){
        if (StringUtils.isBlank(strData)){
            return "传入数据为null";
        }
        JSONObject json = JSON.parseObject(strData);
        String fileId = json.getString("fileId");
        if (StringUtils.isBlank(fileId)){
            String fileName = json.getString("fileName");
            JSONObject jsonObject = fileService.downloadFileByName(fileName);
            return jsonObject.toString();
        }
        JSONObject jsonObject = fileService.downloadFile(fileId);
        if (jsonObject==null){
            return null;
        }
        return jsonObject.toString();
    }

    @RequestMapping(value = "file/html",method = RequestMethod.GET)
    public void downloadFile(String fileId, String fileName, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName",fileName);
        jsonObject.put("fileId",fileId);

        String str = downloadFile(jsonObject.toString());

        ServletOutputStream os = null;
        JSONObject json = new JSONObject();
        try {
            os = response.getOutputStream();
            json = JSON.parseObject(str);
        } catch (Exception e) {
            e.printStackTrace();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();
        }

        String name = json.getString("fileName");
        String fileBase64 = json.getString("fileBase64");
        byte[] bytes = Base64.decodeBase64(fileBase64);

        response.setCharacterEncoding("UTF-8");
        //应用程序强制下载
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(name, "UTF-8"));
        response.setContentLength(bytes.length);
        ServletOutputStream sos = response.getOutputStream();
        sos.write(bytes);
        sos.flush();
        sos.close();
    }


}
