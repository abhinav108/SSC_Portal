package com.example.smartsuitcase.ssc_portal;
import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class connectionClass {
    String ip = "199.79.62.22";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "DhabaDB";
    String un = "DhabaSA";
    String password = "Abc123$*";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" +ip+ ";" + "databaseName="+db+ ";user=" +un+ ";password=" + password +";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}

