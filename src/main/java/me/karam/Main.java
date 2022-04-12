package me.karam;

import lombok.Getter;
import me.karam.listener.MainListener;
import me.karam.listener.MessageListener;
import me.karam.modules.TicketManager;
import me.karam.profile.ProfileManager;
import me.karam.slash.commands.CommandManager;
import me.karam.utils.BotLogger;
import me.karam.utils.Settings;
import me.karam.utils.Severity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static me.karam.utils.BotLogger.log;

public class Main {

    @Getter
    private static Main instance = null;

    public static JDA jda;

    @Getter
    private JDABuilder builder;
    private CommandManager commandManager;

    @Getter
    private TicketManager ticketManager;
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
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        builder.setStatus(OnlineStatus.ONLINE);
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
        log(Severity.INFO, "Loading command manager...");
        commandManager.registerCommands();
        log(Severity.INFO, "Loading ticket manager...");
        ticketManager = new TicketManager();
        log(Severity.INFO, "Loading profiles...");
        profileManager = new ProfileManager();
        log("Loading galaxy..");
        startGalaxy();

        consoleListener();
    }

    public static void main(String[] args){
        if (args.length != 1){
            BotLogger.log(Severity.HIGH, "Please run the application through the launcher.");
            return;
        }

        if (!args[0].equalsIgnoreCase("devco")){
            BotLogger.log(Severity.HIGH, "Please run the application through the launcher.");
            return;
        }

        try {
            new Main();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGalaxy(){
        String[] messages = {"Star Galaxy", "You", jda.getGuildById(895687295284437013L).getMemberCount() + " Members"};

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if (i > messages.length)
                    i = 0;

                jda.getPresence().setActivity(Activity.watching(messages[i]));
                i++;
                //cancel();
                //jda.getGuildById("954271232067530782").getRoleById("955311110649675887").getManager().setColor(new Color(0, random(50, 150), random(50, 150))).queue();
            }
        }, 0, 30000L);
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
                        Settings.saveToConfig();
                        builder.setStatus(OnlineStatus.IDLE);
                        jda.shutdown();

                        Thread.sleep(1000);
                        main(null);
                    }else if (line.equalsIgnoreCase("save")){
                        Settings.saveToConfig();
                        ticketManager.saveTickets();

                        log(Severity.INFO, "Saving...");
                    }else if (line.equalsIgnoreCase("modify")){
                        log(Severity.INFO, "What would you like to modify?");
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
        ticketManager.saveTickets();

        builder.setStatus(OnlineStatus.OFFLINE);
        jda.shutdown();
        System.exit(0);
    }
}
