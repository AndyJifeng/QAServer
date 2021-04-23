package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;
import org.apache.avro.data.Json;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class test {

    Map<String,String> templatesID = new HashMap<>();
    public void t(){
        File f = new File(this.getClass().getResource("").getPath());
        System.out.println(f);

    }
    public static void main(String[] args){

    }

    public void setTemplates(String fileName){
        String txtPath = "./data/template/"+fileName;
        File file = new File(txtPath);
        List<Map> list = new ArrayList<>();
        HashMap<Integer,Integer> map = new HashMap<>();

        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String text;
                while((text = bufferedReader.readLine()) != null){
                    List<Object> template = JSONArray.parseArray(text);
                    List<Object> templates = JSONArray.parseArray(template.get(1).toString());
                    System.out.println(templates.get(0));
                    for(Object add:templates)
                        templatesID.put((String)add,(String)template.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}


