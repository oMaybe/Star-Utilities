package me.karam.config;

import me.karam.utils.info.BotLogger;
import me.karam.utils.info.Color;
import me.karam.utils.info.Severity;

import java.io.File;
import java.sql.*;

public class DataSource {

    private static Connection connection;
    private static String url;

    static {
        try{
            final File dbFile = new File(System.getProperty("user.dir") + File.separator + "database" + File.separator + "main.db");

            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    BotLogger.log(Severity.INFO, "Created database file.");
                }else{
                    BotLogger.log(Severity.HIGH, "Failed to create database file! While this is a big problem and most modules wont work, the bot can still work on minimal." );
                }
            }
        }catch (Exception ex){
            BotLogger.log(Severity.HIGH, "It seems that the bot has caught a database error. Contact the developer with the message: " + Color.ANSI_CYAN + ex.getMessage());
        }

       url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "database" + File.separator + "main.db";

        try {
            ResultSet test = getConnection().createStatement().executeQuery("SELECT *");
            while (test.next()){
                System.out.println(test.first());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception{
        return DriverManager.getConnection(url);
    }
}
