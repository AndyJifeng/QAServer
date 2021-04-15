package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.appleyk.process.Model;
import com.appleyk.process.Unification;
import com.google.gson.JsonArray;
import javafx.beans.binding.ObjectBinding;
import org.apache.avro.data.Json;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import scala.Int;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

@RestController
public class newInterface {



    public newInterface(){}

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public Map test() {
        Map map = new HashMap();
        map.put("test",false);
        return map;
    }

    @RequestMapping(value = "/hasLabelMatchingTable",method = RequestMethod.GET)
    public boolean hasLabelMatchingTable(){
        return entityTable.size()>0&&relationTable.size()>0;
    }

    List<Map> entityTable = new ArrayList<>();
    @PostMapping(value = "/insertEntityPropertyLabelTable")
    public void insertEntityPropertyLabelTable(@RequestBody String body){
        entity entity = new entity();
        entity.insertEntityPropertyLabelTable(body);
        entityTable = entity.entityTable;
    }

    @RequestMapping(value = "/loadEntityPropertyLabel",method = RequestMethod.GET)
    public String loadEntityPropertyLabel(){
        //传一个标签，返回标签对应的实体别名列表以及属性别名表
        entity entity =  new entity();
        return Json.toString(entity.readEntity());

    }

    @PostMapping("/removeEntity")
    public void removeEntity(@RequestBody String name){
        entity entity = new entity();
        entityTable = entity.readEntity();
        for(Map ent : entityTable){
            if(((Map)ent.get("entity")).get("name").equals(name)){
                entityTable.remove(ent);
                break;
            }
        }
        entity.writeEntity(entityTable);
    }




    List<Map> relationTable = new ArrayList<>();
    @RequestMapping(value = "/insertRelationLabelTable",method = RequestMethod.POST)
    public void insertRelationLabelTable(@RequestBody String body){
        relation relation = new relation();
        relation.insertRelationLabelTable(body);
        relationTable = relation.relationTable;
    }

    @RequestMapping(value = "/loadRelationLabel",method = RequestMethod.GET)
    public String loadRelationLabel(){
        relation relation = new relation();
        return Json.toString(relation.readRelation());
    }

    @PostMapping("/removeRelation")
    public void removeRelation(@RequestBody String name){
        relation relation = new relation();
        relationTable = relation.readRelation();
        for(Map rel : relationTable){
            if(rel.get("name").equals(name)){
                relationTable.remove(rel);
                break;
            }
        }
        relation.writeRelation(relationTable);
    }

    @RequestMapping(value = "/OneEntity",method = RequestMethod.GET)
    public  List<Object> OneEntity(){
        Templates templates = new Templates();
        return templates.OneEntity();
    }

    @RequestMapping(value = "/TwoEntity",method = RequestMethod.GET)
    public  List<Object> TwoEntity(){
        Templates templates = new Templates();
        return templates.TwoEntity();
    }

    @RequestMapping(value = "/ThreeEntity",method = RequestMethod.GET)
    public  List<Object> ThreeEntity(){
        Templates templates = new Templates();
        return templates.ThreeEntity();
    }

    @PostMapping (value = "/insertOneEntityTemplate")
    public void insertOneEntityTemplate(@RequestBody String body){
        System.out.println(body);
        List<Object> list = JSONArray.parseArray(body);
        Templates template = new Templates();
        List<Object> templates = template.OneEntity();
        list = template.changeTemplates(list,templates);
        template.writeTemplates("./data/template/OneEntity.txt",list);
    }

    @PostMapping (value = "/insertTwoEntityTemplate")
    public void insertTwoEntityTemplate(@RequestBody String body){
        List<Object> list = JSONArray.parseArray(body);
        Templates template = new Templates();
        List<Object> templates = template.TwoEntity();
        list = template.changeTemplates(list,templates);
        template.writeTemplates("./data/template/TwoEntity.txt",list);
    }
    @PostMapping (value = "/insertThreeEntityTemplate")
    public void insertThreeEntityTemplate(@RequestBody String body){
        //先不写
    }


    @PostMapping(value = "/alterProperties")
    public void alterProperties(@RequestBody String body){
        Map map = (Map) JSONArray.parse(body);
        property property = new property();
        String entity = (String) map.get("entity");
        String label = (String) map.get("label");
        Map body1 = (Map)map.get("body");
        System.out.println(entity+label+body);
        property.alterEntityPropertyLabelTable(entity,label,body1);
    }
    @PostMapping(value = "/loadProperties")
    public List<Map> loadProperties(@RequestBody String body){
        Map map = (Map) JSONArray.parse(body);
        property property = new property();
        String entity = (String) map.get("entity");
        String label = (String) map.get("label");
        return property.loadEntityPropertyLabelTable(entity,label);
    }


    @PostMapping(value = "/hugeGraph/insertHugeVertexLabels")
    public void insertHugeVertexLabels(HttpServletRequest request, HttpServletResponse response){
        //读取传过来的文件 转成自己的格式

        hugeGraphInsert hugeGraphInsert = new hugeGraphInsert();
        try {
            Collection<Part> parts = request.getParts();
            List<String> responseName = new ArrayList<>();
            for (Part part : parts) {
                if(part.getSubmittedFileName() != null){
                    hugeGraphInsert.insertHugeVertexLabels(part.getInputStream(),part.getSubmittedFileName());
                }
            }
            Map<String,Object> responseMap = new HashMap<String,Object>();
            responseMap.put("name", responseName);
            String jsonResponse = JSONObject.toJSONString(responseMap);
            sendResponse(jsonResponse,response);
        }
        catch (Throwable ignored) {
        }
    }

    @PostMapping("/templateMatch")
    public List<Object> templateMatch(@RequestBody String body){
        Unification unification = new Unification();
        System.out.println(body);
        Map map = (Map) JSONArray.parse(body);
        List<Object> res = new ArrayList<>();
        res.add(unification.segmentResult((String) map.get("question")));
        res.add(unification.getLabelToWord((String) map.get("question")));
        res.add(unification.getTemplateNumber((String) map.get("question")));
        return res;
    }
    @PostMapping(value = "/hugeGraph/insertHugeEdgeLabels")
    public void insertHugeEdgeLabels(HttpServletRequest request, HttpServletResponse response){
        //读取传过来的文件 转成自己的格式

        hugeGraphInsert hugeGraphInsert = new hugeGraphInsert();
        try {
            Collection<Part> parts = request.getParts();
            List<String> responseName = new ArrayList<>();
            for (Part part : parts) {
                if(part.getSubmittedFileName() != null){
                    hugeGraphInsert.insertHugeEdgeLabels(part.getInputStream(),part.getSubmittedFileName());
                }
            }
            Map<String,Object> responseMap = new HashMap<String,Object>();
            responseMap.put("name", responseName);
            String jsonResponse = JSONObject.toJSONString(responseMap);
            sendResponse(jsonResponse,response);
        }
        catch (Throwable ignored) {
        }
    }

    @PostMapping(value = "/hugeGraph/insertHugeVertices")
    public void insertHugeVertices(HttpServletRequest request, HttpServletResponse response){
        //读取传过来的文件 转成自己的格式

        hugeGraphInsert hugeGraphInsert = new hugeGraphInsert();
        try {
            Collection<Part> parts = request.getParts();
            List<String> responseName = new ArrayList<>();
            for (Part part : parts) {
                if(part.getSubmittedFileName() != null){

                    hugeGraphInsert.insertHugeVertices(part.getInputStream(),part.getSubmittedFileName());
                }
            }
            Map<String,Object> responseMap = new HashMap<String,Object>();
            responseMap.put("name", responseName);
            String jsonResponse = JSONObject.toJSONString(responseMap);
            sendResponse(jsonResponse,response);
        }
        catch (Throwable ignored) {
        }
    }


    @PostMapping(value = "/relationDatabaseWritePropertiesFile")
    public void writePropertiesFile(@RequestBody String body) throws IOException {
        //读取传过来的文件 转成自己的格式
        Map map = (Map) JSONArray.parse(body);
        property property = new property();
        String fileName = (String) map.get("fileName");
        String entity = (String) map.get("entity");
        System.out.print(body);
        property.jsonWrite("./data/loadTempData/"+fileName,entity);
    }

    @PostMapping(value = "/relationDatabaseInsertPropertyFile")
    //传文件 保存
    public void insertEntityFile(HttpServletRequest request, HttpServletResponse response) throws Exception{
        property property = new property();
        //property.insertFile(request,response,uploadfile1);

        try {
            Collection<Part> parts = request.getParts();
            List<String> responseName = new ArrayList<>();
            for (Part part : parts) {
                if(part.getSubmittedFileName() != null){
                    String name = property.insertFile(part.getInputStream(),part.getSubmittedFileName());
                    responseName.add(name);
                }
            }
            Map<String,Object> responseMap = new HashMap<String,Object>();
            responseMap.put("name", responseName);
            String jsonResponse = JSONObject.toJSONString(responseMap);
            sendResponse(jsonResponse,response);
        }
        catch (Throwable ignored) {
        }

    }
    private void sendResponse(String responseString,HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(responseString);
            pw.flush();
        } finally {
            IOUtils.closeQuietly(pw);
        }
    }


    @PostMapping(value = "/answer")
    public List<Object> answer(@RequestBody String body) throws Exception {
        System.out.println(body);
        return  new Model().search(body);
    }

    @RequestMapping("/GetFile")
    public void getFile(HttpServletRequest request , HttpServletResponse response) throws IOException {

    }

    @GetMapping(value = "/dictionaryFile")
    public void dictionaryFile(HttpServletRequest request,HttpServletResponse response) throws IOException {
        ServletOutputStream out = null;
        FileInputStream ips = null;
        try {
            fileProcess.zip("./data/dictionary/","./dictionary.zip",true);
            File file = new File("./dictionary.zip");
            //获取到文字 数据库里对应的附件名字加上老的文件名字：filename 截取到后面的文件类型 例：txt  组成一个新的文件名字：newFileName
           if(!file.exists()) {
                //如果文件不存在就跳出
                return;
            }
            fileProcess fileProcess = new fileProcess();
            fileProcess.propertyWord();
            fileProcess.entityWord();
            fileProcess.relationWord();
            response.setContentType("multipart/form-data");
            //为文件重新设置名字，采用数据库内存储的文件名称
            response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(file.getName().getBytes("UTF-8"),"ISO8859-1") + "\"");
            out = response.getOutputStream();
            //读取文件流
            ips = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((len = ips.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
                ips.close();
            } catch (IOException e) {
                System.out.println("关闭流出现异常");
                e.printStackTrace();
            }
        }
        return ;
    }

    @GetMapping(value = "/entityFile")
    public void entityFile(HttpServletRequest request,HttpServletResponse response) throws IOException {
        ServletOutputStream out = null;
        FileInputStream ips = null;
        try {
            fileProcess.zip("./data/entityAndRelationData/","./entity.zip",true);
            File file = new File("./entity.zip");
            //获取到文字 数据库里对应的附件名字加上老的文件名字：filename 截取到后面的文件类型 例：txt  组成一个新的文件名字：newFileName
            if(!file.exists()) {
                //如果文件不存在就跳出
                return;
            }
            fileProcess fileProcess = new fileProcess();
            fileProcess.propertyWord();
            fileProcess.entityWord();
            fileProcess.relationWord();
            response.setContentType("multipart/form-data");
            //为文件重新设置名字，采用数据库内存储的文件名称
            response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(file.getName().getBytes("UTF-8"),"ISO8859-1") + "\"");
            out = response.getOutputStream();
            ips = new FileInputStream(file);
            //读取文件流
            int len = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((len = ips.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            out.flush();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
                ips.close();
            } catch (IOException e) {
                System.out.println("关闭流出现异常");
                e.printStackTrace();
            }
        }
        return ;
    }

    @GetMapping(value = "/templateFile")
    public void templateFile(HttpServletRequest request,HttpServletResponse response) throws IOException {
        ServletOutputStream out = null;
        FileInputStream ips = null;
        try {
            fileProcess.zip("./data/template/","./template.zip",true);
            File file = new File("./template.zip");
            String fileName=file.getName();
            //获取到文字 数据库里对应的附件名字加上老的文件名字：filename 截取到后面的文件类型 例：txt  组成一个新的文件名字：newFileName
            if(!file.exists()) {
                //如果文件不存在就跳出
                return;
            }
            response.setContentType("multipart/form-data");
            //为文件重新设置名字，采用数据库内存储的文件名称
            response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(file.getName().getBytes("UTF-8"),"ISO8859-1") + "\"");
            out = response.getOutputStream();
            ips = new FileInputStream(file);
            //读取文件流
            int len = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((len = ips.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            out.flush();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
                ips.close();
            } catch (IOException e) {
                System.out.println("关闭流出现异常");
                e.printStackTrace();
            }
        }
        return ;
    }


    public static void main(String[] args){
//        String s = "[{\"label\":{\"name\": \"8\",\"standard\":\"2\"},\"property\":{\"name\": \"80\",\"standard\":\"22\"}}]";
//        List<Map> listObjectFir = (List<Map>) JSONArray.parse(s);
        newInterface t = new newInterface();
//        System.out.println(listObjectFir);

    }
}
