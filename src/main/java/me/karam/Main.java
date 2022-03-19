package me.karam;

import me.karam.utils.Settings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class Main {

    public static Main instance = null;

    public static JDA jda;
    private JDABuilder jdaBuilder;

    public Main() throws LoginException {
        Settings.loadFromConfig();

        jdaBuilder = JDABuilder.createDefault(Settings.TOKEN);
        jdaBuilder.setActivity(Activity.watching("Star Galaxy"));
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        jda = jdaBuilder.build();
    }

    public static void main(String[] args){
        try {
            new Main();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
