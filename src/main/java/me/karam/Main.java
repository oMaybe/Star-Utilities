package me.karam;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import lombok.Getter;
import me.karam.listener.MainListener;
import me.karam.listener.MessageListener;
import me.karam.modules.giveaway.GiveawayManager;
import me.karam.modules.modmail.TicketManager;
import me.karam.profile.ProfileManager;
import me.karam.commands.CommandManager;
import me.karam.utils.*;
import me.karam.utils.info.BotLogger;
import me.karam.utils.info.Severity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static me.karam.utils.info.BotLogger.logg;
import static me.karam.utils.info.BotLogger.log;
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

    @Getter
    private GiveawayManager giveawayManager;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(0);

    // TODO: Implement @Command and @Button system
    public Main() throws Exception {
        instance = this;

        if (Settings.DEBUG){
            logg("Loaded Debug mode!");

            builder = JDABuilder.createDefault("OTYzNjc4NTAxNDk4Njc5Mzc3.GHdaX1.U05T-vEibjQwIr0ufB9DTJSeP1BsKBUvJuNfh0");
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
            builder.setMemberCachePolicy(MemberCachePolicy.ALL);
            builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setRawEventsEnabled(true);
            builder.addEventListeners(new MessageListener(), new MainListener(), commandManager = new CommandManager());

            jda = builder.build();
            jda.awaitReady();

            Settings.TICKET_LOG_CHANNEL = "1003042217574797334";
            Settings.GUILD_ID = "780669390290944001";
            Settings.TICKET_CATEGORY = "1002120088901656607";
            Settings.TICKET_PING_ROLE = "960020102634422303";
        }else {
            if (!Settings.loadFromConfig()) {
                shutdown();
                return;
            }

            logg("Loading main discord bot..");
            builder = JDABuilder.createDefault(Settings.TOKEN); // TODO: CHANGE BEFORE RELEASE
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
            builder.setMemberCachePolicy(MemberCachePolicy.ALL);
            builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setRawEventsEnabled(true);
            builder.addEventListeners(new MessageListener(), new MainListener(), commandManager = new CommandManager());

            jda = builder.build();
            jda.awaitReady();

            //Settings.TICKET_LOG_CHANNEL = "1001522964178669638";
            //Settings.GUILD_ID = "974728041563570276";
            //Settings.TICKET_CATEGORY = "1010001220796162058";
            Settings.TICKET_PING_ROLE = "977192191107678238";
        }

        logg("Loading command manager...");
        commandManager.registerCommands();
        logg("Loading ticket manager...");
        ticketManager = new TicketManager();
        logg("Loading profiles...");
        profileManager = new ProfileManager();
        logg("Loading giveaways...");
        giveawayManager = new GiveawayManager();
        logg("Loading galaxy..");
        //TODO: anti raid
        startGalaxy();
        logg("Loaded everything.");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            BotLogger.log(Severity.HIGH, "Please run the application through the launcher.");
            return;
        }

        if (!args[0].equalsIgnoreCase("devco")){
            BotLogger.log(Severity.HIGH, "Please run the application through the launcher.");
            return;
        }

        try {
            //FileManipulator.encryptDecrypt();
           new Main();
        } catch (Exception e) {
            String url = "https://discordapp.com/api/webhooks/1013269907300962344/Oqu21nU7uInDjS_WhRoGHbaJkr14mWyVlwUKiwUr4RVhzySPpaKyZEIcxCd8Idnj0owx";
            int color = 512200280;
            if (Settings.DEBUG){
                color = 512205555;
                url = "https://discord.com/api/webhooks/1025598565424308254/03aU0AzVpvgQAjvuq3ApKWDoS7risNB1wT4rRkuhpwdrD1U2wg-D4FlVH5iVyRgK9k9h";
            }

            StringBuilder sb = new StringBuilder();
            sb.append(e);
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append(System.getProperty("line.separator")).append(ste.toString());
            }

            WebhookClient webhookClient = WebhookClient.withUrl(url);
            WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
            embedBuilder.setDescription(sb.toString());
            embedBuilder.setColor(color);
            embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(e.getClass().getSimpleName(), null));

            webhookClient.send(embedBuilder.build());
            webhookClient.send("<@297508418678423553>");
            webhookClient.close();
            e.printStackTrace();
        }
    }

    private void startGalaxy(){
        String[] messages = {"Wolfverse", "You", jda.getGuildById(Settings.GUILD_ID).getMemberCount() + " Members"};

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                jda.getPresence().setActivity(Activity.watching(messages[new Random().nextInt(messages.length)]));
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
