package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class hugeGraphInsert {


    Map<String, Map<String,List<Map>>> propertiesTable = new HashMap<>();
    public void insertHugeVertices(InputStream inputStream, String name) throws Exception {
        //具体属性导入
        String filePath = "./data/loadTempData/"+name;
        File file = new File(filePath);
        makeFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        insertFile(inputStream,name);

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
                loadEntities();
                List<Map> hugeGraphVertices = new ArrayList<>();
                for(String text_split : text.toString().split("]}")) {
                    List<Map> vertices = new ArrayList<>();
                    try{
                        Map records = (Map) JSONArray.parse(text_split+"]}");
                        vertices = (List<Map>) records.get("vertices");
                    }catch (Exception e){
                        System.out.println(text_split);
                    }

                    //Map<String, Map<String,List<Map>>> propertiesTable = new HashMap<>();
                    for (Map object : vertices) {
                        Map properties = (Map) object.get("properties");
                        String entityLabel = (String) object.get("label");
                        for (Object property : properties.keySet()) {
                            Map appendVertices = new HashMap();
                            appendVertices.put("name", properties.get(property));
                            appendVertices.put("standardName", properties.get(property));
                            Map aliases = new HashMap();
                            aliases.put("isEditable", false);
                            aliases.put("tag", new ArrayList<>());
                            appendVertices.put("aliases", aliases);

                            if (propertiesTable.containsKey(entityLabel)) {
                                Map inPropertyTable = propertiesTable.get(entityLabel);
                                if (!inPropertyTable.containsKey(property)) {
                                    inPropertyTable.put(property, new ArrayList<>());
                                }

                                propertiesTable.get(entityLabel).get(property).add(appendVertices);
                            } else {
                                propertiesTable.put(entityLabel, new HashMap<>());
                                propertiesTable.get(entityLabel).put((String) property, new ArrayList<>());
                                propertiesTable.get(entityLabel).get((String) property).add(appendVertices);
                            }
                        }
                    }
                }
                property property = new property();
                property.writeProperties("./data/entityAndRelationData/property.txt",propertiesTable);
                //System.out.println(propertiesTable);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insertHugeEdgeLabels(InputStream inputStream, String name) throws Exception {
        //读取传过来的文件 转成自己的格式
        String filePath = "./data/loadTempData/"+name;
        File file = new File(filePath);
        makeFile();
        if (!file.exists()) {

            file.createNewFile();
        }
        insertFile(inputStream,name);

        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                //String text = null;
                StringBuilder text = new StringBuilder();
                String add;
                while ((add = bufferedReader.readLine()) != null) {
                    text.append(add);
                }
                List<Map> hugeGraphEdge = new ArrayList<>();
                for(String text_split : text.toString().split("\n")) {
                    Map records = (Map) JSONArray.parse(text_split);
                    List<Map> edgelabels = (List<Map>) records.get("edgelabels");
                    for (Map object : edgelabels) {
                        System.out.println(object);
                        Map appendEdge = new HashMap();
                        appendEdge.put("name", object.get("name"));
                        appendEdge.put("standardName", "");
                        Map aliases = new HashMap();
                        aliases.put("isEditable", false);
                        aliases.put("tag", new ArrayList<>());
                        appendEdge.put("aliases", aliases);
                        appendEdge.put("source_name", object.get("source_label"));
                        appendEdge.put("target_name", object.get("target_label"));

                        hugeGraphEdge.add(appendEdge);
                    }
                }
                System.out.println(hugeGraphEdge);
                relation relation = new relation();
                relation.writeRelation(hugeGraphEdge);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void makeFile() throws IOException {
        File file = new File("./data/entityAndRelationData/");
        if(!file.exists())
            file.mkdirs();
        file = new File("./data/loadTempData/");
        if(!file.exists())
            file.mkdirs();
        file = new File("./data/template/");
        if(!file.exists())
            file.mkdirs();
        file = new File("./data/entityAndRelationData/property.txt");
        if(!file.exists())
            file.createNewFile();
        file = new File("./data/entityAndRelationData/entity.txt");
        if(!file.exists())
            file.createNewFile();
        file = new File("./data/entityAndRelationData/relation.txt");
        if(!file.exists())
            file.createNewFile();
        file = new File("./data/template/ThreeEntity.txt");
        if(!file.exists())
            file.createNewFile();
        file = new File("./data/template/TwoEntity.txt");
        if(!file.exists())
            file.createNewFile();
        file = new File("./data/template/OneEntity.txt");
        if(!file.exists())
            file.createNewFile();
    }
    public void insertHugeVertexLabels(InputStream inputStream, String name) throws Exception {
        //读取传过来的文件 转成自己的格式

        String filePath = "./data/loadTempData/"+name;
        File file = new File(filePath);
        makeFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        insertFile(inputStream,name);
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                //String text = null;
                StringBuilder text = new StringBuilder();
                String add;
                while ((add = bufferedReader.readLine()) != null) {
                    text.append(add);
                }
                List<Map> hugeGraphVertex = new ArrayList<>();
                Map records = (Map) JSONArray.parse(text.toString());
                List<Map> vertexlabels = (List<Map>) records.get("vertexlabels");
                for(Map object: vertexlabels){
                    Map append = new HashMap();   //List<Map> 中的一个Map
                    Map appendEntity = new HashMap(); //Map 中的Entity
                    appendEntity.put("name",object.get("name"));
                    appendEntity.put("standardName","");
                    Map aliases = new HashMap();
                    aliases.put("isEditable", false);
                    aliases.put("tag", new ArrayList<>());
                    appendEntity.put("aliases",aliases);

//                    System.out.println(appendEntity);
//                    appendProperty.put("aliases",aliases);
                    List<Map> appendPropertyList = new ArrayList<>();
                    List<String> objectPropertyList = (List<String>) object.get("properties");
                    for(String objectProperty : objectPropertyList){
                        Map appendProperty = new HashMap();
                        appendProperty.put("aliases",aliases);
                        appendProperty.put("name",objectProperty);
                        appendProperty.put("standardName","");
                        appendPropertyList.add(appendProperty);
                    }
                   // System.out.println(appendPropertyList);
                    append.put("entity",appendEntity);
                    append.put("property",appendPropertyList);
                    hugeGraphVertex.add(append);
                }
                entity entity= new entity();//写入entity
                entity.writeEntity(hugeGraphVertex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public String insertFile(InputStream inputStream,String name) throws Exception{
        InputStream input = inputStream;
        String fileFullPath = "./data/loadTempData/"+name;

        OutputStream fos = null;
        try {
            fos = new FileOutputStream(fileFullPath);
            File file = new File(fileFullPath);
            if(!file.exists()){
                makeFile();
                file.createNewFile();
            }
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


    public void loadEntities() {
        File file = new File("./data/entityAndRelationData/property.txt");
        try {
            if (!file.exists()) {
                //判断文件是否存在，如果不存在就新建一个txt
                file.mkdirs();
                file.createNewFile();
            }
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String text = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((text = bufferedReader.readLine()) != null) {
                stringBuilder.append(text);
            }
            propertiesTable = (Map<String, Map<String, List<Map>>>) JSONArray.parse(stringBuilder.toString());
            if (propertiesTable == null)
                propertiesTable = new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        String filePath = "./data/loadTempData/vertices3";
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder text = new StringBuilder();
        String add;
        while ((add = bufferedReader.readLine()) != null) {
            text.append(add);
        }

        for(String string:text.toString().split("]}")){
            System.out.println(JSONArray.parse(string+"]}"));
            break;
        }
    }
}
