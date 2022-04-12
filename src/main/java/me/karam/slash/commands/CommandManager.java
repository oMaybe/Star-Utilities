package me.karam.slash.commands;

import com.google.gson.Gson;
import me.karam.Main;
import me.karam.slash.commands.impl.DMCommand;
import me.karam.slash.commands.impl.EmbedCommand;
import me.karam.slash.commands.impl.SettingsCommand;
import me.karam.slash.commands.impl.TicketCommand;
import me.karam.utils.BotLogger;
import me.karam.utils.Color;
import me.karam.utils.Settings;
import me.karam.utils.Severity;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class CommandManager extends ListenerAdapter {

    private static OkHttpClient client = new OkHttpClient();
    private Map<String, SlashCommand> commandMap;

    public CommandManager(){
        commandMap = new ConcurrentHashMap<>();

        commandMap.put("dm", new DMCommand());
        commandMap.put("settings", new SettingsCommand());
        commandMap.put("embeds", new EmbedCommand());
        commandMap.put("tickets", new TicketCommand());

        //register(true, "{\"name\":\"tickets\",\"description\":\"main tickets command\",\"options\":[{\"type\":1,\"name\":\"respond\",\"description\":\"respond to tickets\",\"options\":[{\"type\":3,\"name\":\"ticket_id\",\"description\":\"id of \",\"required\":true},{\"type\":3,\"name\":\"message\",\"description\":\"the message you want to send\",\"required\":true}]}]}");
        //register(false, "{\"name\":\"embeds\",\"description\":\"sends pre made embed by bot\",\"options\":[{\"type\":3,\"name\":\"embed_name\",\"description\":\"name of embed you want to send\",\"required\":true}]}");
        //register(false, "{\"name\":\"settings\",\"description\":\"changes configuration\",\"options\":[{\"type\":3,\"name\":\"setting\",\"description\":\"the setting you want to change\",\"required\":true},{\"type\":3,\"name\":\"value\",\"description\":\"new value of settings\",\"required\":true}]}");
        // TODO: dm command
        //register(false, "{\"name\":\"dm\",\"description\":\"private message a member from the bot\",\"options\":[{\"type\":9,\"name\":\"member\",\"description\":\"the member to send the message to\",\"required\":true},{\"type\":3,\"name\":\"message\",\"description\":\"the message to be sent tothe user\",\"required\":true}]}");
    }

    public void registerCommands(){
        CommandListUpdateAction commands = Main.jda.getGuildById(Long.valueOf(Settings.GUILD_ID)).updateCommands();

        commands.addCommands(Commands.slash("embeds", "send pre-made embeds by bot")
                .addOptions(new OptionData(OptionType.STRING, "embed_type", "the embed you want to send")
                        .addChoice("support", "support").setRequired(true)));

        commands.addCommands(Commands.slash("settings", "main settings command")
                .addOptions(new OptionData(OptionType.STRING, "key", "the value you want to change")
                        .addChoice("ticket_channel", "ticket_channel").setRequired(true)
                        .addChoice("guild_id", "guild_id").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "value", "the new value").setRequired(true)));

        commands.queue();
    }

    public static void register(boolean printResponse, String firstJson, String... json){
        String[] jsons = Stream.concat(Arrays.stream(new String[] {firstJson}), Arrays.stream(json)).toArray(String[]::new);

        for (String string : jsons){ // TODO: change guild id from test to main!
            Request request = new Request.Builder()
                    .url("https://discord.com/api/v8/applications/954649155198930954/guilds/954271232067530782/commands")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bot " + Settings.TOKEN)
                    .post(RequestBody.create(MediaType.parse("application/json"), string)).build();

            try(Response response = client.newCall(request).execute()){
                if (printResponse) {
                    BotLogger.log(Severity.LOW, response.code());
                    BotLogger.log(Severity.LOW, response.isSuccessful());
                    BotLogger.log(Severity.LOW, response.body());
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        SlashCommand command;

        if ((command = commandMap.get(commandName)) != null){
            command.performCommand(event, event.getMember(), event.getTextChannel());
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        for (SlashCommand command : commandMap.values()){
            //command.executeButton(event);
        }

        commandMap.values().stream().forEach(command -> {
            Class commandClass = command.getClass();
            Method[] methods = commandClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equalsIgnoreCase("onButton")){
                    try {
                        commandClass.getDeclaredMethod(method.getName(), new Class[] {ButtonInteractionEvent.class}).invoke(commandClass.newInstance(), event);
                        super.onButtonInteraction(event);
                    } catch (Exception e) {
                        BotLogger.log(Severity.HIGH, "Seems like a dangerous error was encountered. The stacktrace message was left with: " + Color.ANSI_YELLOW + e.getMessage() + Color.ANSI_WHITE);
                        BotLogger.log(Severity.HIGH, "Seems like a dangerous error was encountered. The stacktrace message was left with: " + Color.ANSI_YELLOW + e.getStackTrace() + Color.ANSI_WHITE);
                    }
                }
            }
        });

        super.onButtonInteraction(event);
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        commandMap.values().stream().forEach(command -> {
            Class commandClass = command.getClass();
            Method[] methods = commandClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equalsIgnoreCase("onSelectMenu")){
                    try {
                        commandClass.getDeclaredMethod(method.getName(), new Class[] {ButtonInteractionEvent.class}).invoke(commandClass.newInstance(), event);
                        super.onSelectMenuInteraction(event);
                    } catch (Exception e) {
                        BotLogger.log(Severity.HIGH, "Seems like a dangerous error was encountered. The stacktrace was left with: " + Color.ANSI_YELLOW + e.getMessage() + Color.ANSI_WHITE);
                    }
                }
            }
        });
        super.onSelectMenuInteraction(event);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        commandMap.values().stream().forEach(command -> {
            Class commandClass = command.getClass();
            Method[] methods = commandClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equalsIgnoreCase("onModalInteraction")){
                    try {
                        commandClass.getDeclaredMethod(method.getName(), new Class[] {ModalInteractionEvent.class}).invoke(commandClass.newInstance(), event);
                        super.onModalInteraction(event);
                    } catch (Exception e) {
                        BotLogger.log(Severity.HIGH, "Seems like a dangerous error was encountered. The stacktrace was left with: " + Color.ANSI_YELLOW + e.getMessage() + Color.ANSI_WHITE);
                    }
                }
            }
        });
        super.onModalInteraction(event);
    }
}
