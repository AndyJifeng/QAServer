package com.appleyk.process;

import com.alibaba.fastjson.JSONArray;
import com.appleyk.service.Templates;
import com.appleyk.service.newInterface;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.collections.ArrayStack;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Unification {

    private String wordFile = "./data/entityAndRelationData/word.txt";
    private String templateFile = "./data/template";
    static {
        new addDict();
    }
    Map<String,String> allTemplates = new HashMap<>();
    List<List<String>> sameWords = new ArrayList<>();
    List<String> labelToWord = new ArrayList<>();

    public boolean testLabel(String label){
        if(label.equals("person")||label.equals("unit")||label.equals("journal")||label.equals("paper"))
            return true;
        return false;
    }

    public void loadSameWords(){
        File file = new File(wordFile);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String word;
            while ((word = br.readLine()) != null) {
                List<String> list = (List<String>) JSONArray.parse(word);
                sameWords.add(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String segmentResult(String query){
        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        return segment.seg(query).toString();
    }

    public String getLabelToWord(String query){
        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        List<Term> terms = segment.seg(query);
        loadSameWords();
        //System.out.println(terms);
        for(Term term:terms){
            if(testLabel(term.nature.toString())) {
                labelToWord.add(term.word);
                query = query.replace(term.word, term.nature.toString());
            }
            for(List<String> compare:sameWords){
                if(compare.contains(term.word)){
                    query = query.replace(term.word,compare.get(0));
                break;
                }
            }
        }
        return query;
    }

    public String getTemplateNumber(String query){

        File baseFile = new File(templateFile);
        File[] files = baseFile.listFiles();
        query = getLabelToWord(query);
        //System.out.println(unification);

        for(File file : files){
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String word;
                while ((word = br.readLine()) != null) {
                    List<Object> list = (List<Object>) JSONArray.parse(word);
                    for(String string:(List<String>)list.get(1)){
                        allTemplates.put(string,list.get(0).toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return allTemplates.getOrDefault(query,"None");
    }

    public static void loadDict() {
        File file = new File("./test.txt");
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(file));
            String word;
            while ((word = br.readLine()) != null)
                CustomDictionary.add(word, "dict");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        //Unification unification = new Unification();
//        loadDict();
//        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
//        String string = "hello word";
//        System.out.println(segment.seg(string));
//        for(Term term : segment.seg(string)){
//            if(term.nature.toString().equals("dict")) {
//                System.out.println(term);
//            }
//        }

    }
}
