package com.appleyk.service;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.util.*;

public class readGraph {

    private String VertexLabel = "http://10.1.13.142:28080/graphs/hugegraph/schema/vertexlabels";
    private String EdgeLabel = "http://10.1.13.142:28080/graphs/hugegraph/schema/edgelabels";

    public List<List<String>> OneEntity() {
        List<List<String>> templates = new ArrayList<>();
        String txtPath = "./data/entityAndRelationData/entity.txt";
        File file = new File(txtPath);
        List<Map> list = new ArrayList<>();
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String text;
                while((text = bufferedReader.readLine()) != null){
                    Map temp = (Map) JSONArray.parse(text);
                    Map entity = (Map) temp.get("entity");
                    List<Map> properties = (List<Map>) temp.get("property");
                    Map entity1 = new HashMap();
                    entity1.put("label",(String)entity.get("name"));


                    for(Map property : properties){
                        entity1.put(property.get("name"),"");
                    }
                    Map<String,Map<String,String>> map = new HashMap<>();
                    map.put("entity1",entity1);
                    Templates template = new Templates();
                    templates.addAll(template.getOneTemplates(map));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return templates;
    }

    public List<List<String>> TwoEntity() {
        List<List<String>> templates = new ArrayList<>();
        String txtPath = "./data/entityAndRelationData/relation.txt";
        File file = new File(txtPath);

        List<Map> entityTable = new entity().readEntity();
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String text;
                while((text = bufferedReader.readLine()) != null){
                    Map temp = (Map) JSONArray.parse(text);
                    Map<Object,Object> map = new HashMap<>();
                    String source_name = (String)temp.get("source_name");
                    Map source = new HashMap();
                    source.put("label",source_name);
                    for(Map ent : entityTable){
                        Map label = (Map)ent.get("entity");
                        if(label.get("name").equals(source_name)){
                            List<Map> properties = (List<Map>) ent.get("property");
                            for(Map property : properties){
                                source.put(property.get("name"),"");
                            }
                            break;
                        }
                    }
                    map.put("entity1",source);
                    String target_name = (String)temp.get("target_name");
                    Map target = new HashMap();
                    target.put("label",target_name);
                    for(Map ent : entityTable){
                        Map label = (Map)ent.get("entity");
                        if(label.get("name").equals(target_name)){
                            List<Map> properties = (List<Map>) ent.get("property");
                            for(Map property : properties){
                                target.put(property.get("name"),"");
                            }
                            break;
                        }
                    }
                    map.put("entity2",target);
                    map.put("relation1",(String)temp.get("name"));

                    Templates template = new Templates();
                    System.out.println(map);
                    templates.addAll(template.getTwoTemplates(map));
                    System.out.println(map);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(templates);
        return templates;
    }
//    public List<List<String>> OneEntity() {
//        String result = HttpUtil.get(VertexLabel);
//        Map<Map, List<Map>> listObjectFir = (Map<Map, List<Map>>) JSONArray.parse(result);
//        List<Map> list = listObjectFir.get("vertexlabels");
//        List<List<String>> temp = new ArrayList<>();
//        for (Map m : list) {
//            Map<String,Map<String,String>> map = new HashMap<>();
//            Map<String,String> entity1 = new HashMap<>();
//            entity1.put("label",m.get("name").toString());
//
//            String properties = m.get("properties").toString();
//            properties = properties.replace("[", "");
//            properties = properties.replace("]", "");
//            properties = properties.replace("\"","");
//            String[] property = properties.split(",");
//            for (String p : property)
//                entity1.put(p,"");
//            map.put("entity1",entity1);
//
//            Templates templates = new Templates();
//            temp.addAll(templates.getOneTemplates(map));
//
//        }
//        return temp;
//    }



//    public List<List<String>> test(){
//        readGraph httpPost = new readGraph();
//        String result = HttpUtil.get(EdgeLabel);
//
//        Map<Map, List<Map>> listObjectFir = (Map<Map, List<Map>>) JSONArray.parse(result);
//        System.out.println(listObjectFir);
//        List<Map> list = listObjectFir.get("edgelabels");
//        List<List<String>> temp = new ArrayList<>();
//        for(Map m:list) {
//            Map<Object, Object> map = new HashMap<>();
//            Map<String,String> entity1 = httpPost.getEntityProperties(m.get("source_label").toString());
//            System.out.println(entity1);
//            String relation = m.get("name").toString();
//            Map<String,String> entity2 = httpPost.getEntityProperties(m.get("target_label").toString());
//            map.put("entity1",entity1);
//            map.put("relation1",relation);
//            map.put("entity2",entity2);
//            Templates templates = new Templates();
//            temp.addAll(templates.getTwoTemplates(map));
//        }
//        return temp;
//    }




    public Map<String, String> getEntityProperties(String label){
        String result = HttpUtil.get(VertexLabel);
        Map<Map, List<Map>> listObjectFir = (Map<Map, List<Map>>) JSONArray.parse(result);
        List<Map> list = listObjectFir.get("vertexlabels");
        Map<String, String> res = new HashMap<>();
        for (Map m : list) {
            String entity = m.get("name").toString();
            if(entity.equals(label)){
                res.put("label",label);
                String properties = m.get("properties").toString();
                properties = properties.replace("[", "");
                properties = properties.replace("]", "");
                properties = properties.replace("\"","");
                String[] property = properties.split(",");
                for(String p : property){
                    res.put(p,"");
                }
                return res;
            }
        }
        return null;
    }

    public Map<String,Object> EntityProperties(){

        String result = HttpUtil.get(VertexLabel);
        Map<Map, List<Map>> listObjectFir = (Map<Map, List<Map>>) JSONArray.parse(result);
        List<Map> list = listObjectFir.get("vertexlabels");
        if(list == null)
            return null;
        Map<String, Object> entityProperties = new HashMap<>();
        for (Map m : list) {
            String entity = m.get("name").toString();
            List<String> property = (List<String>)m.get("properties");
            entityProperties.put("name", entity);
            entityProperties.put("standardName","");
            entityProperties.put("properties",property);
        }

        return entityProperties;
    }

    public Map<String,Object> Relation(){

        String result = HttpUtil.get(EdgeLabel);
        Map<Map, List<Map>> listObjectFir = (Map<Map, List<Map>>) JSONArray.parse(result);
        List<Map> list = listObjectFir.get("edgelabels");
        if(list == null)
            return null;
        Map<String, Object> entityProperties = new HashMap<>();
        for (Map m : list) {
            String entity = m.get("name").toString();
            List<String> property = (List<String>)m.get("properties");
            entityProperties.put("name", entity);
            entityProperties.put("standardName","");
            entityProperties.put("properties",property);
        }

        return entityProperties;
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        readGraph readGraph = new readGraph();

    }


}
