package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.data.Json;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class relation {

    private final String relationTxt = "./data/entityAndRelationData/relation.txt";


    List<Map> relationTable = new ArrayList<>();
    public void insertRelationLabelTable(String body){
        //修改relation表
        if(relationTable.size()==0)
            relationTable.addAll(readRelation());
        Map bodyMap = (Map) JSONArray.parse(body);
        insertBodyToRelationTable(bodyMap);
        //System.out.println(relationTable);
        writeRelation(relationTable);
    }

    public void insertBodyToRelationTable(Map bodyMap){
        for(Map relation : relationTable){
            if(relation.get("name").equals(bodyMap.get("name"))){
                relationTable.remove(relation);
                break;
            }
        }
        relationTable.add(bodyMap);
    }

    public  void writeRelation(List<Map> content){
        FileOutputStream fileOutputStream = null;
        File file = new File(relationTxt);
        try {
            if(!file.exists()){
                //判断文件是否存在，如果不存在就新建一个txt
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            for(Map map:content)
                fileOutputStream.write(new String(Json.toString(map)+"\n").getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  List<Map> readRelation() {
        String txtPath = relationTxt;
        File file = new File(txtPath);
        List<Map>  list = new ArrayList<>();
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String text = null;
                while((text = bufferedReader.readLine()) != null){
//                    getRelationWord((Map)JSONArray.parse(text));
                    list.add((Map) JSONArray.parse(text));
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

}
