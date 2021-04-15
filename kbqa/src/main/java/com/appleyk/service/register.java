package com.appleyk.service;

import com.alibaba.fastjson.JSONArray;
import org.apache.avro.data.Json;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class register {

    public static final String URL = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8";
    public static final String USER = "root";
    public static final String PASSWORD = "123456";
    public static final String jdbc = "com.mysql.cj.jdbc.Driver";
    @PostMapping("/passWord")
    public void passWord(@RequestBody String body) throws Exception {
        Map map = (Map) JSONArray.parse(body);
        String passWard = (String)map.get("passWard");
        String userName = (String)map.get("userName");
        String sql = String.format("select * from user where name = '%s' and passward = '%s'",userName,passWard);
        System.out.println(sql(sql));
    }

    @PostMapping("/login")
    public void login(@RequestBody String body) throws Exception {
        Map map = (Map) JSONArray.parse(body);
        String passWard = (String)map.get("passWard");
        String userName = (String)map.get("userName");
        String sql = String.format("insert into user (id,name,passward) values (null,%s,%s)",userName,passWard);
        try {
            Class.forName(jdbc);//向DriverManager注册自己
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);//与数据库建立连接
            PreparedStatement pst = con.prepareStatement(sql);//用来执行SQL语句查询，对sql语句进行预编译处理
            pst.executeUpdate();//解释在下
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {//执行与数据库建立连接需要抛出SQL异常
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\zjk\\Desktop\\test.csv");
        FileInputStream input = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String text = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(text+"\n");
        while((text = bufferedReader.readLine()) != null){
            //stringBuilder.append(String.format("paper%s,author,author%s,ACTED_IN\n",text,text));
            stringBuilder.append("author"+text+",Author\n");
        }
        reader.close();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(stringBuilder.toString().getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }
    public static String sql(String sql) throws Exception{
        //System.out.println(sql);
        Class.forName(jdbc);
        //2. 获得数据库连接
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        //3.操作数据库，实现增删改查
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        //如果有数据，rs.next()返回true
        System.out.println(sql);
        List<Map> res = new ArrayList<>();
        while (rs.next()) {
            try{
                return rs.getString("id");
            }
            catch (Exception exception){
            }
        }
        return "null";
    }
}
