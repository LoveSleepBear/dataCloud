package com.syb.datacloud.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Admin
 */
@Service
public class FileService {


    @Value("${msg.file.path}")
    private String msgFilePath;

    @Value("${file.save.path}")
    private String fileSavePath;

    /**
     * 读取文件参数
     */
    public JSONArray readFileMessage() {
        byte[] bs;
        String msg = null;
        try {
            File file = new File(msgFilePath);
            if (!file.isFile()){
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(msgFilePath);
            bs = IOUtils.toByteArray(fis);
            fis.read(bs);
            fis.close();
            if (bs == null) {
                return null;
            }

            msg = new String(bs, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(msg)) {
            return new JSONArray();
        }
        return JSONArray.parseArray(msg);
    }

    /**
     * 根据文件id查询文件
     *
     * @param fileId
     * @return
     */
    public JSONObject searchFile(String fileId) {
        JSONArray jsonArray = readFileMessage();
        return searchFile(fileId, jsonArray);
    }

    /**
     * 根据文件id查询文件
     *
     * @param fileId    文件id
     * @param jsonArray 文件json
     * @return
     */
    public JSONObject searchFile(String fileId, JSONArray jsonArray) {
        if (StringUtils.isEmpty(fileId)) {
            return null;
        }
        for (Object o : jsonArray) {
            JSONObject json = (JSONObject) o;
            if (fileId.equals(json.getString("fileId"))) {
                return json;
            }
        }
        return null;
    }

    /**
     * 根据文件名搜索文件
     * 只返回第一个文件
     * @param fileName 文件名
     * @return
     */
    public JSONObject searchFileByName(String fileName) {
        JSONArray jsonArray = readFileMessage();
        return searchFileByName(fileName,jsonArray);
    }


    /**
     * 根据文件名搜索文件
     * 只返回第一个文件
     * @param fileName 文件名
     * @param jsonArray 文件内容json格式
     * @return
     */
    public JSONObject searchFileByName(String fileName, JSONArray jsonArray) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        for (Object o : jsonArray) {
            JSONObject json = (JSONObject) o;
            if (fileName.contains(json.getString("fileName"))) {
                return json;
            }
        }
        return null;
    }


    /**
     * 根据文件id下载文件
     *
     * @param fileId
     * @return
     */
    public JSONObject downloadFile(String fileId) {
        JSONObject jsonObject = searchFile(fileId);
        if (jsonObject == null) {
            return null;
        }

        byte[] bytes = new byte[0];
        try {
            FileInputStream fis = new FileInputStream(jsonObject.getString("filePath"));
            bytes = IOUtils.toByteArray(fis);
            fis.read(bytes);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytes == null) {
            return null;
        }
        String fileBase64 = Base64.encodeBase64String(bytes);
        String fileName = jsonObject.getString("fileName");
        JSONObject json = new JSONObject();
        json.put("fileBase64",fileBase64);
        json.put("fileName",fileName);
        return json;
    }

    /**
     * 根据文件名下载文件
     * @param fileId
     * @return
     */
    public JSONObject downloadFileByName(String fileId) {
        JSONObject jsonObject = searchFileByName(fileId);
        return downloadFile(jsonObject.getString("fileId"));
    }


    /**
     * 上传文件
     * @param fileBase64 文件base64
     * @param fileName 文件名称
     * @return file
     */
    public String uploadFile(String fileBase64, String fileName) {
        String fileId = getFileId();
        JSONArray jsonArray = readFileMessage();
        JSONObject jsonObject = searchFile(fileId, jsonArray);
        if (jsonObject == null) {
            JSONObject json = new JSONObject();
            json.put("fileName",fileName);
            byte[] bytes = Base64.decodeBase64(fileBase64);
            // create file path
            String newFileId = getFileId();
            json.put("fileId",newFileId);
            ArrayList<String> list = getStringArray(fileId, 8);
            String filePath = fileSavePath;
            for (String s : list) {
                filePath = filePath+File.separator+s;
            }
            File file = new File(filePath);
            if (!file.exists()){
                file.mkdirs();
            }
            filePath = filePath+File.separator+fileName;
            try {
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(bytes);
                fos.flush();
                fos.close();
                json.put("filePath",filePath);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStr = format.format(new Date());
                json.put("createTime",timeStr);
                // 文件写入文件信息中
                FileOutputStream fosMsg = new FileOutputStream(msgFilePath);
                jsonArray.add(json);
                fosMsg.write(jsonArray.toJSONString().getBytes("UTF-8"));
                fosMsg.flush();
                fosMsg.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return newFileId;
        }else{
            return null;
        }
    }

    /**
     * 指定间隔数分割字符串
     * @param str
     * @param spitNum
     * @return
     */
    private ArrayList<String> getStringArray(String str, int spitNum){
        if (StringUtils.isBlank(str)){
            return null;
        }
        ArrayList<String> list = new ArrayList<>((str.length()/spitNum)+1);
        for (int i = 0; i < str.length(); i=i+spitNum) {
            String substring ;
            if (i+spitNum<=str.length()){
                substring = str.substring(i, i + spitNum);
            }else{
                substring = str.substring(i,str.length());
            }

            list.add(substring);
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
//        FileService fileService = new FileService();
//        ArrayList<String> list = fileService.getStringArray(fileService.getFileId(), 8);
//        String s = Arrays.toString(list.toArray());
//        System.err.println(s);
//        FileInputStream fis = new FileInputStream("C:\\Users\\Admin\\Desktop\\dataCloud.zip");
//        byte[] bytes = IOUtils.toByteArray(fis);
//        String s = Base64.encodeBase64String(bytes);
//        System.err.println(s);
//        FileOutputStream fos = new FileOutputStream("F:\\ideaWorkSpace\\test.docx");
//        fos.write(Base64.decodeBase64("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"));
//        fos.flush();
//        fos.close();


    }

    /**
     * 生成文件
     * uuid
     *
     * @return
     */
    private String getFileId() {
        return UUID.randomUUID().toString().replace("-", "");
    }


}
