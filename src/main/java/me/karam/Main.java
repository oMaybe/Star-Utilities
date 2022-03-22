package me.karam;

import lombok.Getter;
import me.karam.config.DataSource;
import me.karam.listener.MainListener;
import me.karam.listener.MessageListener;
import me.karam.modules.ModMailManager;
import me.karam.profile.ProfileManager;
import me.karam.slash.commands.CommandManager;
import me.karam.utils.Settings;
import me.karam.utils.Severity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.awt.Color;

public class Main {

    @Getter
    private static Main instance = null;

    public static JDA jda;

    @Getter
    private JDABuilder builder;
    private CommandManager commandManager;

    private ModMailManager modMailManager;
    @Getter
    private ProfileManager profileManager;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(0);

    public Main() throws LoginException, InterruptedException {
        instance = this;

        if (!Settings.loadFromConfig()){
            shutdown();
            return;
        }

        log(Severity.INFO, "Loading main discord bot..");
        //DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(Settings.TOKEN);
        builder = JDABuilder.createDefault(Settings.TOKEN);
        builder.setActivity(Activity.watching("Star Galaxy"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setRawEventsEnabled(true);
        builder.addEventListeners(new MessageListener(), new MainListener(), commandManager = new CommandManager());

        jda = builder.build();
        jda.awaitReady();

        log(Severity.INFO, "Loading database...");
        try {
            //DataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log(Severity.INFO, "Loading mod mail...");
        modMailManager = new ModMailManager();
        log(Severity.INFO, "Loading profiles...");
        profileManager = new ProfileManager();
        log("Loading galaxy changing role..");
        startGalaxy();

        consoleListener();
    }

    public static void main(String[] args){
        try {
            new Main();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startGalaxy(){

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cancel();
                //jda.getGuildById("954271232067530782").getRoleById("955311110649675887").getManager().setColor(new Color(0, random(50, 150), random(50, 150))).queue();
            }
        }, 0, 1000L);
    }

    private int random(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void consoleListener(){
        new Thread(() -> {
            System.out.println("Command: ");
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try{
                while ((line = reader.readLine()) != null){
                    if (line.equalsIgnoreCase("exit") && jda != null){
                        log(Severity.INFO, "Okay... saving and shutting down bot...");
                        Thread.sleep(2000);
                        shutdown();
                    }else if (line.equalsIgnoreCase("list")){
                        log(Severity.INFO, profileManager.getProfiles().toString());
                    }else if (line.equalsIgnoreCase("restart")){
                        log(Severity.INFO, "Restarting...");
                        shutdown();
                        Thread.sleep(1000);
                        main(null);
                    }else{
                        log(Severity.INFO, "Unfortunately I could not understand what command you were trying to do.");
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public void shutdown(){
        Settings.saveToConfig();
        builder.setStatus(OnlineStatus.OFFLINE);
        jda.shutdown();
        System.exit(0);
    }

    public void log(me.karam.utils.Severity level, Object message){
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + me.karam.utils.Color.ANSI_RESET + message);
    }

    public void log(Object message){
        Severity level = Severity.INFO;
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + me.karam.utils.Color.ANSI_RESET + message);
    }
}
