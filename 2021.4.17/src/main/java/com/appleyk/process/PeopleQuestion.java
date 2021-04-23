package com.appleyk.process;

import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

public class PeopleQuestion {
    private static List<Term> terms;
    private static Map<String,String> EntityMap;
    private static Map<Integer,Integer> PeopleQuestionMap = new HashMap<>();
    private static Map<Set<String>,Integer> dicts = new HashMap<>();
    private static int year = -1;
    static {
        Set<String> dict = new HashSet<>();
        dict.add("论文");
        dicts.put(new HashSet<>(dict),1);
        dict.clear();
        dict.add("单位");
        dicts.put(new HashSet<>(dict),10);
        dict.clear();
        dict.add("摘要");
        dicts.put(new HashSet<>(dict),100);
        dict.clear();
        dict.add("关键词");
        dicts.put(new HashSet<>(dict),1000);
        dict.clear();

    }
    public PeopleQuestion(List<Term> terms, Map<String,String> EntityMap){
        PeopleQuestion.terms = terms;
        PeopleQuestion.EntityMap = EntityMap;

    }
    public List<Object> getPeopleQuestion() throws Exception {
        int flag = 0;
        for(Term term: PeopleQuestion.terms){
            for(Set<String> set:dicts.keySet()){
                if(set.contains(term.word)) {
                    flag += dicts.get(set);
                }
            }
        }

        return People(flag);
    }
    public void setYear(int year){
        PeopleQuestion.year = year;}
    public List<Object> People(int flag) throws Exception {

        switch (flag){
            case 1: {
                List<Object> peopleMap = new ArrayList<>();
                peopleMap.add("name");
                if(year == -1)
                    peopleMap.add(SQL.sql(String.format("select * from test where authors like '%s%s%s'","%", EntityMap.get("person"),"%")));
                else
                    peopleMap.add( SQL.sql(String.format("select * from test where authors like '%s%s%s' and year =%s" ,"%", EntityMap.get("person"),"%",year)));
                year = -1;
                return peopleMap;
            }
            case 10:
            case 11: {
                List<Object> peopleMap = new ArrayList<>();
                peopleMap.add("unit");
                peopleMap.add(SQL.sql(String.format("select *  from test where authors like '%s%s%s'","%", EntityMap.get("person"),"%")));
                return peopleMap;
            }
            case 101: {
                List<Object> peopleMap = new ArrayList<>();
                peopleMap.add("abstract");
                if(year == -1)
                    peopleMap.add(SQL.sql(String.format("select * from test where authors like '%s%s%s'","%", EntityMap.get("person"),"%")));
                else
                    peopleMap.add(SQL.sql(String.format("select * from test where authors like '%s%s%s' and year =%s" ,"%", EntityMap.get("person"),"%",year)));
                year = -1;
                return peopleMap;
            }
            case 1001: {
                List<Object> peopleMap = new ArrayList<>();
                peopleMap.add("keywords");
                if(year == -1)
                    peopleMap.add(SQL.sql(String.format("select * from test where authors like '%s%s%s'","%", EntityMap.get("person"),"%")));
                else
                    peopleMap.add(SQL.sql(String.format("select * from test where authors like '%s%s%s' and year =%s" ,"%", EntityMap.get("person"),"%",year)));
                year = -1;
                return peopleMap;
            }
            default:
                return new ArrayList<>();
        }
    }
}
