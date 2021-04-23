package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.data.Json;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class entity {
    private final String entityTxt = "./data/entityAndRelationData/entity.txt";
    List<Map> entityTable = new ArrayList<>();


    public void insertEntityPropertyLabelTable(String body){
        //插入一条或者多条实体、属性别名表
        if(entityTable.size() == 0) //尝试从本地文件读取
            entityTable.addAll(readEntity());
        Map<String,Object> bodyMap = (Map<String,Object>) JSONArray.parse(body);
        insertBodyToLabelTable(bodyMap);
        writeEntity(entityTable);
    }

    public void insertBodyToLabelTable(Map bodyMap){
        String name = (String)((Map)bodyMap.get("entity")).getOrDefault("name","null");
        if(name.equals("null"))
            return;
        removeEntity(name);
        entityTable.add(bodyMap);
    }
    public void removeEntity(String name){
        if(entityTable.size() == 0) //尝试从本地文件读取
            entityTable.addAll(readEntity());
        for(Map label:entityTable){
            Map entity = (Map) (label).get("entity");
            if((entity.get("name")).equals(name)){
                entityTable.remove(label);
                break;
            }
        }
    }

    public  List<Map> readEntity() {
        String txtPath = entityTxt;
        File file = new File(txtPath);
        List<Map> list = new ArrayList<>();
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    list.add((Map) JSONArray.parse(text));
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public  void writeEntity(List<Map> content){
        //写入实体别名表
        FileOutputStream fileOutputStream = null;
        File file = new File(entityTxt);
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
}
