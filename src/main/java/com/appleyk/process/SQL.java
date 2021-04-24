package com.appleyk.process;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SQL {

    public static final String URL = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8";
    public static final String USER = "root";
    public static final String PASSWORD = "123456";
    public static List<Map> sql(String sql) throws Exception{
        //System.out.println(sql);
        Class.forName("com.mysql.cj.jdbc.Driver");
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
                Map temp = new HashMap();
                temp.put("name",rs.getString("name"));
                temp.put("subject_code",rs.getString("subject_code"));
                temp.put("year",rs.getString("year"));
                temp.put("authors",rs.getString("authors"));
                temp.put("unit",rs.getString("unit"));
                temp.put("keywords",rs.getString("keywords"));
                temp.put("abstract",rs.getString("abstract"));
                temp.put("journal_name",rs.getString("journal_name"));
                res.add(temp);
            }
            catch (Exception exception){
            }

        }
        return res;
    }
    public static void main(String[]args) throws Exception {
        System.out.println(sql("select * from test where authors like \"%严义%\""));
    }
}
