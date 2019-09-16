package org.apache.servicecomb.samples.practise.houserush.customer.manage;
import java.sql.*;


public class Utils {
  // JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  static final String DB_URL = "jdbc:mysql://172.17.0.128:3306/customer?characterEncoding=utf8&useSSL=false";

  //  Database credentials
  static final String USER = "root";
  static final String PASS = "123456";


 public static void updateCustomersBySql(int newCid ,int oldCid) {
    Connection conn = null;
    Statement stmt = null;
    try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      String sql ="UPDATE customers set id="+newCid+" where id="+oldCid;
      System.out.println(sql);
      stmt = conn.createStatement();

      stmt.executeUpdate(sql);

    }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
    }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
    }finally{
      //finally block used to close resources
      try{
        if(stmt!=null)
          conn.close();
      }catch(SQLException se){
      }// do nothing
      try{
        if(conn!=null)
          conn.close();
      }catch(SQLException se){
        se.printStackTrace();
      }//end finally try
    }//end try
    System.out.println("Goodbye!");
  }//end main
}


