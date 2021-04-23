package com.appleyk.process;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model {

    static Map<String,String> EntityMap = new HashMap<>();
    static List<Term> terms = new ArrayList<>();
    static{
        new addDict();
    }

    public int GetQuestionNum(){
        int flag = 0;
        List<Term> left = new ArrayList<>();

        for (Term term : terms) {
            if(term.nature.toString().equals("person")) {
                flag = flag+1;
                EntityMap.put("person",term.word);
                continue;
            }
            if(term.nature.toString().equals("paper")) {
                flag = flag+10;
                EntityMap.put("paper",term.word);
                continue;
            }
            if(term.nature.toString().equals("unit")) {
                flag = flag+100;
                EntityMap.put("unit",term.word);
                continue;
            }
            left.add(term);
        }
        terms = left;

        return flag;
    }


    public List<Object> question(int flag,String query) throws Exception {
        switch (flag){
            case 1: {
                System.out.println("人+属性");
                PeopleQuestion peopleQuestion = new PeopleQuestion(terms, EntityMap);
                String pattern = "(.*)(\\d{4})(.*)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(query);
                if(m.find())  peopleQuestion.setYear(Integer.parseInt(m.group(2)));
                return peopleQuestion.getPeopleQuestion();
            }
            case 10:{ //查找论文的某个属性（暂不支持）
                System.out.println("论文+属性");
                PaperQuestion paperQuestion = new PaperQuestion(terms,EntityMap);
                return paperQuestion.getPaperQuestion();
            }
//            case 100:{
//                System.out.println("单位+属性");
//                UnitQuestion unitQuestion = new UnitQuestion(terms,EntityMap);
//                return unitQuestion.getUnitQuestion();
//            }
            default:
                return new ArrayList<>();
        }
    }
    public List<Object> search(String query) throws Exception {
        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        terms = segment.seg(query);
        System.out.println(terms);
        int flag = GetQuestionNum();
        List<Object> res = new ArrayList<>(question(flag,query));
        EntityMap.clear();
        return res;
    }
}
