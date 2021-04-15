package com.appleyk.service;

import com.alibaba.fastjson.*;
import org.apache.avro.data.Json;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Templates {

    private static List<String> compareList = new ArrayList<>();
    private Map<String,String> labelToWord = new HashMap<>();
    static {
        compareList.add("age");
        compareList.add("year");
    }

    public String LabelToWord(String label){
        readEntity();
        readRelation();
        return labelToWord.getOrDefault(label,"None");
    }
    public void readRelation(){
        String txtPath = "./data/entityAndRelationData/relation.txt";
        File file = new File(txtPath);

        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    Map temp = (Map) JSONArray.parse(text);
                    labelToWord.put((String)temp.get("name"),(String)temp.get("standardName"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void readEntity(){
        String txtPath = "./data/entityAndRelationData/entity.txt";
        File file = new File(txtPath);
        List<Map> list = new ArrayList<>();
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    Map temp = (Map) JSONArray.parse(text);
                    Map entity = (Map) temp.get("entity");
                    List<Map> properties = (List<Map>) temp.get("property");
                    labelToWord.put((String)entity.get("name"),(String)entity.get("standardName"));
                    for(Map property : properties){
                        labelToWord.put((String)property.get("name"),(String)property.get("standardName"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public List<List<String>> OneEntityTemplate(Map<String,Map<String,String>> map){
        Map<String,String> entityMap = map.get("entity1");
        String entity = entityMap.get("label");

        List<List<String>> templates = new ArrayList<>();
        for(String key : entityMap.keySet()){
            if(key.equals("label")||key.equals("name"))continue;
            List<String> temp = new ArrayList<>();
            temp.add(entity+"-"+key+"-查找属性");
            temp.add(entity+"的"+LabelToWord(key));
            templates.add(new ArrayList<>(temp));
            temp.clear();
            if(key.equals("name"))
                continue;
            temp.add(entity+"-"+key+"-查找实体");
            temp.add(LabelToWord(key)+"为"+key+"的"+LabelToWord(entity));
            templates.add(temp);
        }
        return templates;
    }
    public List<List<String>> OneEntityJudgeTemplate(Map<String,Map<String,String>> map){
        Map<String,String> entityMap = map.get("entity1");
        String entity = entityMap.get("label");
        List<List<String>> templates = new ArrayList<>();
        for(String key : entityMap.keySet()){
            if(key.equals("label")||key.equals("name"))continue;
            List<String> temp = new ArrayList<>();
            temp.add(entity+"-"+key+"-判断问题");
            temp.add(entity+"的"+LabelToWord(key)+"是不是"+key);
            templates.add(temp);
        }
        return templates;
    }
    public List<List<String>> OneEntityCompareTemplate(Map<String,Map<String,String>> map){
        Map<String,String> entityMap = map.get("entity1");
        String entity = entityMap.get("label");
        List<List<String>> templates = new ArrayList<>();
        for(String key : entityMap.keySet()){
            for(String label:compareList){
                if(label.equals(key)){
                    List<String> temp = new ArrayList<>();
                    temp.add(entity+"-"+key+"-比较问题");
                    temp.add(label +"大于的"+LabelToWord((entity)));
                    temp.add(label +"小于的"+LabelToWord((entity)));
                    templates.add(temp);
                    break;
                }
            }
        }
        return templates;
    }
    public List<List<String>> TwoEntityTemplate(Map map){
        Map<String,String> entity1Map = (Map<String, String>) map.get("entity1");
        Map<String,String> entity2Map = (Map<String, String>) map.get("entity2");
        String entity1 = entity1Map.get("label");
        String relation = (String) map.get("relation1");

        List<List<String>>  templates = new ArrayList<>();
        for(String key : entity2Map.keySet()){
            if(key.equals("label"))continue;
            List<String> temp = new ArrayList<>();
            temp.add(entity1+"-"+relation+"-"+entity2Map.get("label")+"-"+key+"-查找属性");
            temp.add(entity1+"的"+LabelToWord(relation)+"的"+LabelToWord(key));
            templates.add(temp);
        }
        for(String key : entity2Map.keySet()){
            if(key.equals("label")||key.equals("name"))continue;
            List<String> temp = new ArrayList<>();
//            temp.add(entity1+" "+LabelToWord(key)+" "+key+" "+LabelToWord(relation));
//            temp.add(entity1+" "+LabelToWord(relation)+" "+LabelToWord(key)+" "+key);
//            templates.add(temp);
            temp.add(entity1+"-"+relation+"-"+entity2Map.get("label")+"-"+key+"-查找实体");
            temp.add(entity1+"有哪些"+LabelToWord(relation)+"的"+LabelToWord(key)+"是"+key);
            temp.add(entity1+"有哪些"+LabelToWord(key)+"为"+key+"的"+LabelToWord(relation));
            templates.add(temp);
        }
        //System.out.println(questions);
        return templates;
    }

    public List<List<String>> TwoEntityJudgeTemplate(Map map){
        Map<String,String> entity1Map = (Map<String, String>) map.get("entity1");
        Map<String,String> entity2Map = (Map<String, String>) map.get("entity2");
        String entity1 = entity1Map.get("label");
        String entity2 = entity2Map.get("label");
        String relation = (String) map.get("relation1");
        List<List<String>> templates = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        temp.add(entity1+"-"+relation+"-"+entity2+"-name-判断问题");
        temp.add(entity1+"的"+LabelToWord(relation)+"中有没有"+entity2);
        temp.add(entity2+"是不是"+entity1+"的"+LabelToWord(relation));
        templates.add(temp);
        return templates;
    }

    public List<List<String>> TwoEntityCompareTemplate(Map map){
        Map<String,String> entity1Map = (Map<String, String>) map.get("entity1");
        Map<String,String> entity2Map = (Map<String, String>) map.get("entity2");
        String entity1 = entity1Map.get("label");
        String relation = (String) map.get("relation1");
        List<List<String>> templates = new ArrayList<>();
        List<String> questions = new ArrayList<>();
        for(String key : entity2Map.keySet()){
            for(String label:compareList){
                if(label.equals(key)){
                    List<String> temp = new ArrayList<>();
//                    temp.add(entity1+" "+LabelToWord(label) +" 大于 "+label+" "+LabelToWord((relation)));
//                    temp.add(entity1+" "+LabelToWord(relation) +" "+LabelToWord((key))+" 大于 "+label);
//                    templates.add(new ArrayList<>(temp));
//                    temp.clear();
//                    temp.add(entity1+" "+LabelToWord(relation) +" "+LabelToWord((key))+" 小于 "+label);
//                    temp.add(entity1+" "+LabelToWord(label) +" 小于 "+label+" "+LabelToWord((relation)));
                    temp.add(entity1+"-"+relation+"-"+entity2Map.get("label")+"-"+key+"-比较问题");
                    temp.add(entity1+"有哪些"+LabelToWord(label) +"大于"+label+"的"+LabelToWord((relation)));
                    temp.add(entity1+"有哪些"+LabelToWord(relation)+"的"+LabelToWord(label)+"大于"+label);
//                    templates.add(temp);
//                    temp.clear();
                    temp.add(entity1+"有哪些"+LabelToWord(label) +"小于"+label+"的"+LabelToWord((relation)));
                    temp.add(entity1+"有哪些"+LabelToWord(relation)+"的"+LabelToWord(label)+"小于"+label);
                    templates.add(temp);
                    break;
                }
            }
        }
        //System.out.println(questions);
        return templates;
    }


    public List<List<String>> ThreeEntityTemplate(Map map){
        Map<String,String> entity1Map = (Map<String, String>) map.get("entity1");
        Map<String,String> entity3Map = (Map<String, String>) map.get("entity3");
        String entity1 = entity1Map.get("label");
        String relation1 = (String) map.get("relation1");
        String relation2 = (String) map.get("relation2");
        List<List<String>>templates = new ArrayList<>();
        List<String> questions = new ArrayList<>();
        for(String key : entity3Map.keySet()){
            if(key.equals("label"))continue;
            List<String> temp = new ArrayList<>();
            temp.add(entity1+"的"+LabelToWord(relation1)+"的"+LabelToWord(relation2)+"的"+LabelToWord(key));
            templates.add(temp);
        }

        return templates;
    }

    public List<List<String>> ThreeEntityJudgeTemplate(Map map){
        Map<String,String> entity1Map = (Map<String, String>) map.get("entity1");
        Map<String,String> entity3Map = (Map<String, String>) map.get("entity3");
        String entity1 = entity1Map.get("label");
        String entity3 = entity3Map.get("label");
        String relation1 = (String) map.get("relation1");
        String relation2 = (String) map.get("relation2");
        List<List<String>>templates = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        temp.add(entity1+"的"+LabelToWord(relation1)+"的"+LabelToWord(relation2)+"中有没有"+entity3);
        temp.add(entity3+"是不是"+entity1+"的"+LabelToWord(relation1)+"的"+LabelToWord(relation2));
        templates.add(temp);
        return templates;
    }

    public static void main(String[] args){

//        System.out.println(jsonToTemplate.ThreeEntityTemplate(map));
//        System.out.println(jsonToTemplate.ThreeEntityJudgeTemplate(map));
//        System.out.println("____________two_______________");
//        System.out.println(jsonToTemplate.TwoEntityTemplate(map));
//        System.out.println(jsonToTemplate.TwoEntityJudgeTemplate(map));
//        System.out.println(jsonToTemplate.TwoEntityCompareTemplate(map));
//        System.out.println("____________one_______________");
//        System.out.println("Attribute question:"+jsonToTemplate.OneEntityTemplate(map).toString());
//        System.out.println("Judge question:"+jsonToTemplate.OneEntityJudgeTemplate(map).toString());
//        System.out.println("Compare question:"+jsonToTemplate.OneEntityCompareTemplate(map).toString());
    }





    public List<List<String>> getOneTemplates(Map map){
        List<List<String>> templates = new ArrayList<>();
        templates.addAll(OneEntityTemplate(map));
        templates.addAll(OneEntityJudgeTemplate(map));
        templates.addAll(OneEntityCompareTemplate(map));
        return templates;
    }
    public List<List<String>> getTwoTemplates(Map map){
        List<List<String>> templates = new ArrayList<>();
        templates.addAll(TwoEntityTemplate(map));
        templates.addAll(TwoEntityJudgeTemplate(map));
        templates.addAll(TwoEntityCompareTemplate(map));
        return templates;
    }
    public List<List<String>> getThreeTemplates(Map map){
        List<List<String>> templates = new ArrayList<>();
        templates.addAll(ThreeEntityTemplate(map));
        templates.addAll(ThreeEntityJudgeTemplate(map));
        return templates;
    }

    public void writeTemplates(String txtPath,List<Object> templates){
        FileOutputStream fileOutputStream = null;
        File file = new File(txtPath);
        try {
            if(file.exists()){
                //判断文件是否存在，如果不存在就新建一个txt
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            for(Object object:templates)
                fileOutputStream.write(new String(Json.toString(object)+"\n").getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Object> changeTemplates(List<Object> lists,List<Object> templates){

        if(templates.size() == 0)
            return lists;
        for(Object list : lists){
            for (int i = 0; i < templates.size(); i++) {
                if(((List<Object>)(templates.get(i))).get(0).toString().equals(((List<Object>)list).get(0).toString())){
                    templates.remove(i);
                    templates.add(i,list);
                    break;
                }
                if(i == templates.size()-1){
                    templates.add(list);
                    break;
                }
            }
        }
        return templates;
    }
    public List<Object> readTemplates(String txtPath) {
        File file = new File(txtPath);
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String text;
                List<Object> res = new ArrayList<>();
                while ((text = bufferedReader.readLine()) != null) {
                    Object list = JSONArray.parseArray(text);
                    res.add(list);
                }
                return res;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public  List<Object> OneEntity(){
        List<Object> readTemplates = readTemplates("./data/template/OneEntity.txt");
        if(readTemplates.size()!=0)
            return readTemplates;
        readGraph readGraph = new readGraph();
        List<Object> res = new ArrayList<>();
        List<List<String>> templates = readGraph.OneEntity();
        for(List<String> template : templates){
            if(template.size()<2)
                continue;
            String key = template.get(0);
            template.remove(key);
            List<Object> tmp = new ArrayList<>();
            tmp.add(key);
            tmp.add(template);
            res.add(tmp);
        }

        //System.out.println(res);
        return res;
    }

    public  List<Object> TwoEntity(){

        List<Object> readTemplates = readTemplates("./data/template/TwoEntity.txt");
        if(readTemplates.size()!=0)
            return readTemplates;

        readGraph readGraph = new readGraph();
        List<Object> res = new ArrayList<>();
        Map<String,List<String>> map = new HashMap<>();
        List<List<String>> templates = readGraph.TwoEntity();
        for(List<String> template : templates){
            if(template.size()<2)
                continue;
            String key = template.get(0);
            template.remove(key);
            List<Object> tmp = new ArrayList<>();
            tmp.add(key);
            tmp.add(template);
            res.add(tmp);
        }
        return res;
    }

    public  List<Object> ThreeEntity(){
        List<Object> readTemplates = readTemplates("./data/template/ThreeEntity.txt");
        if(readTemplates.size()!=0)
            return readTemplates;
        int threeEntityNumber = 1;
        readGraph readGraph = new readGraph();
        List<Object> list11 = new ArrayList<>();
        Map<String,List<String>> map = new HashMap<>();
        List<List<String>> templates = readGraph.OneEntity();

        for(List<String> template : templates){
            map.put("ThreeEntity-"+ threeEntityNumber,template);
            List<Object> tmp = new ArrayList<>();
            tmp.add("ThreeEntity-"+ threeEntityNumber);
            tmp.add(template);
            list11.add(tmp);
            threeEntityNumber++;
        }
        return list11;
    }
}



















