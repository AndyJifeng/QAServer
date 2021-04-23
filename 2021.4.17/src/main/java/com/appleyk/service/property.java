package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.data.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;


public class property {
    private final String propertiesTxt = "./data/entityAndRelationData/property.txt";
    Map<String, Map<String,List<Map>>> propertiesTable = new HashMap<>();


    public void alterEntityPropertyLabelTable(String entity,String property,Map bodyMap){
        //插入一条或者多条实体、属性别名表
        if(propertiesTable.size() == 0) //尝试从本地文件读取
            loadEntities();
        Map<String,List<Map>> properties = propertiesTable.get(entity);
        List<Map> list = properties.get(property);
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            if(map.get("name").equals(bodyMap.get("name"))){
                list.remove(map);
                list.add(i,bodyMap);
                break;
            }
        }
        writeProperties(propertiesTxt,propertiesTable);
    }

    public List<Map> loadEntityPropertyLabelTable(String entity, String property){
        //返回具体属性表
        if(propertiesTable.size() == 0) //尝试从本地文件读取
            loadEntities();
        if(propertiesTable.size() == 0)
            return new ArrayList<>();
        Map<String,List<Map>> properties = propertiesTable.getOrDefault(entity,null);
        if(properties == null)
            return new ArrayList<>();
        return properties.getOrDefault(property,new ArrayList<>());
    }

    public String insertFile(InputStream inputStream,String name) throws Exception{
        InputStream input = inputStream;
        String fileFullPath = "./data/loadTempData/"+name;
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(fileFullPath);
            int size = 0;
            byte[] buffer = new byte[1024*1024];
            while ((size = input.read(buffer,0,1024*1024)) != -1) {
                fos.write(buffer, 0, size);
            }
            fos.close();
            return name;

        } catch (IOException ignored) {
            return "false";
        } finally{
            if(input != null){
                input.close();
            }
            if(fos != null){
                fos.close();
            }
        }

    }



    public void jsonWrite(String filePath,String fileName) throws IOException {
        //导入mysql生成文件解析过程
        File file = new File(filePath);
        //System.out.println(filePath);
        //System.out.println(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder text = new StringBuilder();
                String add;
                while ((add = bufferedReader.readLine()) != null) {
                    text.append(add);
                }
                Map records = (Map)JSONArray.parse(text.toString());
                List<Map> jsonData = (List<Map>) records.get("RECORDS");
                if (jsonData.size() == 0)
                    return;
                Map<String,List<Map>> addProperties = new HashMap<>();
                Set<String> inMap = new HashSet<>();
                for (Map map : jsonData) {
                    for (Object key : map.keySet()) {
                        Map temp = new HashMap();
                        String name = key.toString();
                        String standardName = (String) map.get(key);
                        if(inMap.contains(standardName))
                            continue;
                        inMap.add(standardName);
                        temp.put("standardName", standardName);
                        temp.put("name", standardName);
                        Map aliases = new HashMap();
                        aliases.put("isEditable", false);
                        aliases.put("tag", new ArrayList<>());
                        temp.put("aliases", aliases);
                        if(addProperties.containsKey(name)) {
                            addProperties.getOrDefault(name, new ArrayList<>()).add(temp);
                        }
                        else{
                            List<Map> properties = new ArrayList<>();
                            properties.add(temp);
                            addProperties.put(name,properties);
                        }
                    }
                }
                loadEntities();
                propertiesTable.put(fileName, addProperties);
                writeProperties(propertiesTxt,propertiesTable);
                //System.out.println(propertiesTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public  void writeProperties(String txtPath, Map<String, Map<String,List<Map>>> content){
        FileOutputStream fileOutputStream = null;
        File file = new File(txtPath);
        try {
            if(!file.exists()){
                //判断文件是否存在，如果不存在就新建一个txt
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(Json.toString(content).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadEntities(){
        File file = new File(propertiesTxt);
        try {
            if(!file.exists()){
                //判断文件是否存在，如果不存在就新建一个txt
                file.createNewFile();
            }
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String text = null;
            StringBuilder stringBuilder = new StringBuilder();
            while((text = bufferedReader.readLine()) != null){
                stringBuilder.append(text);
            }
            propertiesTable =  (Map<String, Map<String,List<Map>>>) JSONArray.parse(stringBuilder.toString());
            if(propertiesTable == null)
                propertiesTable = new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        property property = new property();
        property.loadEntities();
        System.out.println(property.propertiesTable.get("paper"));
    }
}
