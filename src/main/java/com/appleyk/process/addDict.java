package com.appleyk.process;

import com.alibaba.fastjson.JSONArray;
import com.appleyk.service.property;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import org.apache.avro.data.Json;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class addDict {


    private String entityFile = "./data/entityAndRelationData/entity.txt";
    private String propertyFile = "./data/entityAndRelationData/property.txt";
    private String relationFile = "./data/entityAndRelationData/relation.txt";
    private String wordFile = "./data/entityAndRelationData/word.txt";

    Map<String,Set<String>> Words = new HashMap<>();
    public addDict(){
        String dataPath = "./data/dictionary/";
        File file = new File(dataPath);
        for(File listFile : file.listFiles()){
            loadDict(dataPath,listFile.getName());
        }
    }
    public void loadWords(){
        File file = new File(wordFile);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String word;
            while ((word = br.readLine()) != null) {
                List<String> list = (List<String>) JSONArray.parse(word);
                Set<String> set = new HashSet<>();
                for (int i = 1; i < list.size(); i++) {
                    set.add(list.get(i));
                }
                Words.put(list.get(0),set);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeWords(){
        File file = new File(wordFile);
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(file));
            for(String key : Words.keySet()){
                List<String> list = new ArrayList<>();
                list.add(key);
                Iterator<String> iterator = Words.get(key).iterator();
                while (iterator.hasNext()){
                    String add = iterator.next();
                    list.add(add);
                }
                wr.write(Json.toString(list)+"\n");
            }
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  void loadDict(String filePath,String fileName) {
        File file = new File(filePath+fileName);
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(file));
            String word;
            while ((word = br.readLine()) != null)
                CustomDictionary.add(word, fileName.substring(0,fileName.length()-4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void propertyWord(){
          //读property.txt 保存里面的同名词
//        String filePath = propertyFile;
//        File file = new File(filePath);
//        loadWords();
//        //System.out.println(Words);
//        if (file.isFile() && file.exists()) {
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String add;
//                StringBuilder stringBuilder = new StringBuilder();
//                while ((add = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(add);
//                }
//                Map<String, Map<String,List<Map>>> entity = (Map<String, Map<String, List<Map>>>) JSONArray.parse(stringBuilder.toString());
//                for(String propertyKey:entity.keySet()){
//                    Map<String,List<Map>> properties = entity.get(propertyKey);
//                    for(String specificPropertyKey : properties.keySet()){
//                        List<Map> specificPropertyList = properties.get(specificPropertyKey);
//                        for(Map specificProperty : specificPropertyList){
//                            System.out.println(specificProperty.get("name"));
//                            String name = String.valueOf(specificProperty.get("name"));
//                            if(!Words.containsKey(name)){
//                                Words.put(name,new HashSet<>());
//                            }
//                            List<String> aliases = (List<String>) ((Map)specificProperty.get("aliases")).get("tag");
//                            for(String aliase : aliases){
//                                Words.get(name).add(aliase);
//                            }
//                        }
//                    }
//                }
//                writeWords();
//                //System.out.println(Words);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static void main(String[] args) throws IOException {

        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        addDict addDict = new addDict();
        String string = "高保真印刷技术探秘的陈杰的杭州电子科技大学信息工程学院";
        System.out.println(segment.seg(string));

    }

}
