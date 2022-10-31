package me.karam.utils.config;

import net.dv8tion.jda.api.entities.Member;

import java.io.File;

public class Logs {

    public static File getLog(String ticketID){
        File folder = new File(System.getProperty("user.dir") + "/transcripts/");
        if (!folder.exists()){
            folder.mkdir();
        }

        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles){
            //TODO fix
            if (file.isFile() && file.getName().equalsIgnoreCase(ticketID + ".txt")){
                return file;
            }
        }
        return null;
    }
}
