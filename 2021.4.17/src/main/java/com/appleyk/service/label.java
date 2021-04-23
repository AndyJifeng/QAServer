//package com.appleyk.service;
//
//import com.alibaba.fastjson.JSONArray;
//import org.apache.avro.data.Json;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class label {
//    private final String labelTxt = "./label.txt";
//
//
//    List<Map> labelTable = new ArrayList<>();
//    Map<String,String> word = new HashMap<>();
//    Map<String,String> propertyToChinese = new HashMap<>();
//    Map<String,String> labelToChinese = new HashMap<>();
//
//    //@RequestMapping(value = "/insertEntityPropertyLabelTable",method = RequestMethod.POST)
//    public void insertEntityPropertyLabelTable(String body){
//        //插入一条或者多条实体、属性别名表
//        if(labelTable.size() == 0) //尝试从本地文件读取
//            labelTable.addAll(readLabel());
//        Map<String,Object> bodyMap = (Map<String,Object>) JSONArray.parse(body);
//        Map entity = (Map)bodyMap.get("entity");
//        String labelStandardName = (String)entity.get("standardName");
//        labelToChinese.put((String)entity.get("name"),labelStandardName);
//        for(Map property : (List<Map>)bodyMap.get("property")){
//            String propertyStandardName = (String)property.get("standardName");
//            propertyToChinese.put((String)property.get("name"),propertyStandardName);
//        }
//        getLabelWord(bodyMap);
//        insertBodyToLabelTable(bodyMap);
//        System.out.println(propertyToChinese);
//        System.out.println(labelToChinese);
//        writeLabel(labelTxt,labelTable);
//    }
//    public void getLabelWord(Map bodyMap){
//        Map entity = (Map)bodyMap.get("entity");
//        if(entity == null)
//            return;
//        String labelStandardName = (String)entity.get("standardName");
//        if(labelStandardName == null)
//            return;
//        word.put(labelStandardName,labelStandardName);
//        List<String> labelAliases = (List<String>) entity.get("aliases");
//        for(String labelAliase : labelAliases){
//            word.put(labelAliase,labelStandardName);
//        }
//        for(Map property : (List<Map>)bodyMap.get("property")){
//            String propertyStandardName = (String)property.get("standardName");
//            propertyToChinese.put((String)property.get("name"),propertyStandardName);
//            word.put(propertyStandardName,propertyStandardName);
//            List<String> propertyAliases = (List<String>) property.get("aliases");
//            for(String propertyAliase : propertyAliases) {
//                word.put(propertyAliase, propertyStandardName);
//            }
//        }
//    }
//    public void insertBodyToLabelTable(Map bodyMap){
//        String name = (String)((Map)bodyMap.get("entity")).getOrDefault("name","null");
//        for(Map label:labelTable){
//            Map entity = (Map) (label).get("entity");
//            if((entity.get("name")).equals(name)){
//                labelTable.remove(label);
//                labelTable.add(bodyMap);
//                return;
//            }
//        }
//        labelTable.add(bodyMap);
//    }
//
//    public  List<Map> readLabel() {
//        String txtPath = labelTxt;
//        File file = new File(txtPath);
//        List<Map> list = new ArrayList<>();
//        if(file.isFile() && file.exists()){
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                StringBuffer sb = new StringBuffer();
//                String text = null;
//                while((text = bufferedReader.readLine()) != null){
//                    getLabelWord((Map) JSONArray.parse(text));
//                    list.add((Map) JSONArray.parse(text));
//                }
//                return list;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return new ArrayList<>();
//    }
//
//    public  void writeLabel(String txtPath, List<Map> content){
//        //写入实体别名表
//        FileOutputStream fileOutputStream = null;
//        File file = new File(txtPath);
//        try {
//            if(file.exists()){
//                //判断文件是否存在，如果不存在就新建一个txt
//                file.createNewFile();
//            }
//            fileOutputStream = new FileOutputStream(file);
//            for(Map map:content)
//                fileOutputStream.write(new String(Json.toString(map)+"\n").getBytes());
//            fileOutputStream.flush();
//            fileOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
