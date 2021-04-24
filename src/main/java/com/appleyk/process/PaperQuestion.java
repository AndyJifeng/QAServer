package com.appleyk.process;

import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

public class PaperQuestion {
    private static List<Term> terms;
    private static Map<String,String> EntityMap;
    private static Map<Integer,Integer> PaperQuestionMap = new HashMap<>();
    private static Map<Set<String>,Integer> dicts = new HashMap<>();

    static {
        Set<String> dict = new HashSet<>();
        dict.add("期刊");
        dicts.put(new HashSet<>(dict),1);
        dict.clear();

        dict.add("主题");
        dicts.put(new HashSet<>(dict),10);
        dict.clear();

        dict.add("摘要");
        dicts.put(new HashSet<>(dict),100);
        dict.clear();

        dict.add("关键词");
        dicts.put(new HashSet<>(dict),1000);
        dict.clear();

        dict.add("单位");
        dicts.put(new HashSet<>(dict),10000);
        dict.clear();

        dict.add("年份");
        dicts.put(new HashSet<>(dict),100000);
        dict.clear();

        dict.add("作者");
        dicts.put(new HashSet<>(dict),1000000);
        dict.clear();

        PaperQuestionMap.put(1,0);
        PaperQuestionMap.put(10,1);
        PaperQuestionMap.put(100,2);
        PaperQuestionMap.put(1000,3);
        PaperQuestionMap.put(10000,4);
        PaperQuestionMap.put(100000,5);
        PaperQuestionMap.put(1000000,6);
    }
    public PaperQuestion(List<Term> terms, Map<String,String> EntityMap){
        PaperQuestion.terms = terms;
        PaperQuestion.EntityMap = EntityMap;
    }
    public List<Object> getPaperQuestion() throws Exception {
        int flag = 0;
        for(Term term: PaperQuestion.terms){
            for(Set<String> set:dicts.keySet()){
                if(set.contains(term.word)) {
                    flag += dicts.get(set);
                }
            }
        }
        return Paper(flag);
    }

    public List<Object> Paper(int flag) throws Exception {
        String papername = EntityMap.get("paper");

        flag = PaperQuestionMap.get(flag)==null?-1:PaperQuestionMap.get(flag);
        switch (flag){
            case 0:{
                List<Object> paperMap = new ArrayList<>();
                paperMap.add("journal_name");
                paperMap.add(SQL.sql(String.format("select * from test where name = '%s'",papername)));
                return paperMap;
            }
            case 1:{
                List<Object> paperMap = new ArrayList<>();
                paperMap.add("subject_code");
                paperMap.add(SQL.sql(String.format("select * from test where name = '%s'",papername)));
                return paperMap;
            }
            case 2:{
                List<Object> paperMap = new ArrayList<>();
                paperMap.add("abstract");
                paperMap.add(SQL.sql(String.format("select * from test where name = '%s'",papername)));
                return paperMap;
            }
            case 3:{
                List<Object> paperMap = new ArrayList<>();
                paperMap.add("keywords");
                paperMap.add(SQL.sql(String.format("select * from test where name = '%s'",papername)));
                return paperMap;
            }
            case 4:{
                List<Object> paperMap = new ArrayList<>();
                paperMap.add("unit");
                paperMap.add(SQL.sql(String.format("select * from test where name = '%s'",papername)));
                return paperMap;
            }
            case 5:{
                List<Object> paperMap = new ArrayList<>();
                paperMap.add("year");
                paperMap.add(SQL.sql(String.format("select * from test where name = '%s'",papername)));
                return paperMap;

            }
            case 6:{
                List<Object> paperMap = new ArrayList<>();
                paperMap.add("authors");
                paperMap.add(SQL.sql(String.format("select * from test where name = '%s'",papername)));
                return paperMap;

            }
            default:
                return null;
        }
    }
}
