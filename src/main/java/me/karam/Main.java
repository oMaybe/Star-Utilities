package me.karam;

import lombok.Getter;
import me.karam.listener.CommandListener;
import me.karam.listener.MainListener;
import me.karam.slash.commands.CommandManager;
import me.karam.utils.Color;
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
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    @Getter
    private static Main instance = null;

    private ShardManager shardManager;
    private CommandManager commandManager;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(0);

    public Main() throws LoginException, InterruptedException {
        instance = this;

        if (!Settings.loadFromConfig()) shutdown();

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(Settings.TOKEN);
        builder.setActivity(Activity.watching("Star Galaxy"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setRawEventsEnabled(true);
        builder.addEventListeners(new CommandListener(), commandManager = new CommandManager());

        shardManager = builder.build();

        Objects.requireNonNull(Settings.GUILD_ID);

        consoleListener();
    }

    public static void main(String[] args){
        try {
            new Main();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void consoleListener(){
        new Thread(() -> {
            System.out.println("Command: ");
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try{
                while ((line = reader.readLine()) != null){
                    if (line.equalsIgnoreCase("exit") && shardManager != null){
                        log(Severity.INFO, "Okay... saving and shutting down bot...");
                        Thread.sleep(2000);
                        shutdown();
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public void shutdown(){
        shardManager.setStatus(OnlineStatus.OFFLINE);
        shardManager.shutdown();
        System.exit(0);
    }

    public void log(me.karam.utils.Severity level, Object message){
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + Color.ANSI_RESET + message);
    }
}
