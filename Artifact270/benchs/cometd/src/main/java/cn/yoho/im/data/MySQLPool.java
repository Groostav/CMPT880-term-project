package cn.yoho.im.data;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MySQLPool {

    private String url;

    private String username;

    private String password;

    private String driverClassName;

    /**
     * 连接池的大小，也就是连接池中有多少个数据库连接。
     */
    private int minPoolSize = 1;
    private int maxPoolSize = 10;
    
    private ComboPooledDataSource cpds;

    private static MySQLPool instance = null;

    /**
     * 私有的构造方法，禁止外部创建本类的对象，要想获得本类的对象，通过<code>getIstance</code>方法。
     * 使用了设计模式中的单子模式。
     */
    private MySQLPool() {
        init();
    }

    /**
     * 连接池初始化方法，读取属性文件的内容 建立连接池中的初始连接
     */
    private void init() {
        
        readConfig();
        cpds = new ComboPooledDataSource(); 
        try {
            cpds.setDriverClass(driverClassName);
            cpds.setJdbcUrl(url); 
            cpds.setUser(username); 
            cpds.setPassword(password); 
            cpds.setMinPoolSize(minPoolSize);
            cpds.setMaxPoolSize(maxPoolSize);
        } catch (PropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
         
    }

    /**
     * 返回连接到连接池中
     */
    public synchronized void release(Connection conn) {
        
    }
    
    /**
     * 返回当前连接池的一个对象
     */
    public static MySQLPool getInstance() {
        if (instance == null) {
            instance = new MySQLPool();
        }
        return instance;
    }

    /**
     * 返回连接池中的一个数据库连接
     */
    public synchronized Connection getConnection() { 
        try {
            return cpds.getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取设置连接池的属性文件
     */
    private void readConfig() {
        try {

            Properties props = new Properties();
            props.load(MySQLPool.class.getResourceAsStream("MySQLPool.properties"));
            this.driverClassName = props.getProperty("driverClassName");
            this.username = props.getProperty("username");
            this.password = props.getProperty("password");
            this.url = props.getProperty("url");
            this.minPoolSize = Integer.parseInt(props.getProperty("minPoolSize"));
            this.maxPoolSize = Integer.parseInt(props.getProperty("maxPoolSize"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("读取属性文件出错. ");        
        }
    }
    
    public static void main(String[] args) {
        MySQLPool pool = MySQLPool.getInstance();
        Connection conn = pool.getConnection();
        String statement = "select * from chat_history";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(statement);
            while (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("id is " + id);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
