package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;
import org.apache.avro.data.Json;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.ArrayUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class fileProcess {

    private String entityFile = "./data/entityAndRelationData/entity.txt";
    private String propertyFile = "./data/entityAndRelationData/property.txt";
    private String relationFile = "./data/entityAndRelationData/relation.txt";
    private String wordFile = "./data/entityAndRelationData/word.txt";

    Map<String,Set<String>> Words = new HashMap<>();
    public void writeSameWordsFile(){
        File file = new File(propertyFile);
    }
    public void entityWord(){
        String filePath = entityFile;
        File file = new File(filePath);
        loadWords();
        //System.out.println(Words);
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String add;
                while ((add = bufferedReader.readLine()) != null) {
                    Map map = JSONArray.parseObject(add);
                    if(!Words.containsKey(((Map)map.get("entity")).get("standardName"))) {
                        Words.put((String) ((Map)map.get("entity")).get("standardName"),new HashSet<>());
                    }

                    for(Object word : (List<Object>)((Map)((Map)map.get("entity")).get("aliases")).get("tag")){
                        // 属性标签同名词
                        // entity里的aliases里的tag是同名词 对应entity里的name
                        Words.get(((Map)map.get("entity")).get("standardName")).add(word.toString());
                    }
                    List<Object> properties = (List<Object>) map.get("property");
                    for(Object property : properties){
                        if(!Words.containsKey(((Map)property).get("standardName"))) {
                            Words.put(((Map)property).get("standardName").toString(),new HashSet<>());
                        }
                        for(Object word : ((List<Object>)((Map)((Map) property).get("aliases")).get("tag"))){
                            Words.get(((Map)property).get("standardName")).add(word.toString());
                        }
                    }
                }
                writeWords();
                //System.out.println(Words);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void relationWord(){
        String filePath = relationFile;
        File file = new File(filePath);
        loadWords();
        //System.out.println(Words);
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String add;
                while ((add = bufferedReader.readLine()) != null) {
                    Map map = JSONArray.parseObject(add);
                    if(!Words.containsKey(map.get("standardName"))) {
                        Words.put((String) map.get("standardName"),new HashSet<>());
                    }
                    for(Object word : (List<Object>)((Map)(map).get("aliases")).get("tag")){
                        // 属性标签同名词
                        // entity里的aliases里的tag是同名词 对应entity里的name
                        Words.get((map).get("standardName")).add(word.toString());
                    }
                }
                writeWords();
                //System.out.println(Words);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void propertyWord(){
        // 读property.txt 默认保存每个entity的name 写到实体词词库里 用到hanlp自定义词库里
        String filePath = propertyFile;
        File file = new File(filePath);

        //System.out.println(Words);
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String add;
                StringBuilder stringBuilder = new StringBuilder();
                while ((add = bufferedReader.readLine()) != null) {
                    stringBuilder.append(add);
                }
                Map<String, Map<String,List<Map>>> entity = (Map<String, Map<String, List<Map>>>) JSONArray.parse(stringBuilder.toString());
                Map<String,List<String>> write = new HashMap<>();
                for(String propertyKey:entity.keySet()){
                    Map<String,List<Map>> properties = entity.get(propertyKey);
                    System.out.println(properties);
                    for(String property : properties.keySet()){
                        System.out.println(property);
                        String fileOutPath;
                        if(property.equals("name"))
                            fileOutPath = propertyKey;
                        else
                            fileOutPath = propertyKey+"-"+property;
                        List<String> out = new ArrayList<>();
                        for(Map map : properties.get(property)){
                            Map aliases = (Map) map.get("aliases");
                            if(!out.contains(map.get("standardName")))
                                out.add((String) map.get("standardName"));
                            List<String> tags = (List<String>) aliases.get("tag");
                            for(String tag : tags){
                                if(!out.contains(tag))
                                    out.add(tag);
                            }
                        }
                        writeDictionary(fileOutPath,out);
                    }

                }
                //System.out.println(write);
                //writeWords();
                //System.out.println(Words);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void writeDictionary(String fileOutPath,List<String> out){
        File outFile = new File("./data/dictionary/"+fileOutPath+".txt");
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(outFile));
            for(String out1 : out){
                wr.write(out1+"\n");
            }
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public static void zip(String sourceFileName, HttpServletResponse response){
//        ZipOutputStream out = null;
//        BufferedOutputStream bos = null;
//        try {
//            //将zip以流的形式输出到前台
//            response.setHeader("content-type", "application/octet-stream");
//            response.setCharacterEncoding("utf-8");
//            response.setHeader("Content-disposition",
//                    "attachment;filename=" + new String("fileZip".getBytes("gbk"), "iso8859-1")+".zip");
//            // 设置浏览器响应头对应的Content-disposition
//            //参数中 testZip 为压缩包文件名，尾部的.zip 为文件后缀
//            out = new ZipOutputStream(response.getOutputStream());
//            //创建缓冲输出流
//            bos = new BufferedOutputStream(out);
//            File sourceFile = new File(sourceFileName);
//            //调用压缩函数
//            compress(out, bos, sourceFile, sourceFile.getName());
//            out.flush();
//        } catch (Exception e) {
//            //log.error("ZIP压缩异常："+e.getMessage(),e);
//        } finally {
//            //IOCloseUtils.ioClose(bos,out);
//        }
//    }
//
//
//    public static void compress(ZipOutputStream out, BufferedOutputStream bos, File sourceFile, String base){
//        FileInputStream fos = null;
//        BufferedInputStream bis = null;
//        System.out.println(sourceFile.getName());
//        try {
//            //如果路径为目录（文件夹）
//            if (sourceFile.isDirectory()) {
//                //取出文件夹中的文件（或子文件夹）
//                File[] flist = sourceFile.listFiles();
//                if (flist.length == 0) {//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
//                    out.putNextEntry(new ZipEntry(base + "/"));
//                } else {//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
//                    for (int i = 0; i < flist.length; i++) {
//                        compress(out, bos, flist[i], base + "/" + flist[i].getName());
//                    }
//                }
//            } else {//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
//                out.putNextEntry(new ZipEntry(base));
//                fos = new FileInputStream(sourceFile);
//                bis = new BufferedInputStream(fos);
//
//                int tag;
//                //将源文件写入到zip文件中
//                while ((tag = bis.read()) != -1) {
//                    out.write(tag);
//                }
//
//                bis.close();
//                fos.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//    }
    public static void zip(String dir, String zipFile) throws Exception {
        zip(dir, zipFile, false);
    }

    /**
     * 打包
     *
     * @param dir            要打包的目录
     * @param zipFile        打包后的文件路径
     * @param includeBaseDir 是否包括最外层目录
     * @throws Exception
     */
    public static void zip(String dir, String zipFile, boolean includeBaseDir) throws Exception {
        if (zipFile.startsWith(dir)) {
            throw new RuntimeException("打包生成的文件不能在打包目录中");
        }
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File fileDir = new File(dir);
            String baseDir = "";
            if (includeBaseDir) {
                baseDir = fileDir.getName();
            }
            compress(out, fileDir, baseDir);
        }
    }

    public static void compress(ZipOutputStream out, File sourceFile, String base) throws Exception {
        if(sourceFile.getName().equals("loadTempData"))
            return;
        if (sourceFile.isDirectory()) {
            base = base.length() == 0 ? "" : base + File.separator;
            File[] files = sourceFile.listFiles();
            if (ArrayUtils.isEmpty(files)) {
                // todo 打包空目录
                // out.putNextEntry(new ZipEntry(base));
                return;
            }
            for (File file : files) {
                compress(out, file, base + file.getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            try (FileInputStream in = new FileInputStream(sourceFile)) {
                IOUtils.copy(in, out);
            } catch (Exception e) {
                throw new RuntimeException("打包异常: " + e.getMessage());
            }
        }
    }


    public static void main(String[] args) throws Exception {
        fileProcess fileProcess = new fileProcess();
        //fileProcess.propertyWord();
        fileProcess.entityWord();
        fileProcess.relationWord();

    }
}
