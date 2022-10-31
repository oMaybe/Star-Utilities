package me.karam.commands;

import me.karam.Main;
import me.karam.commands.impl.SettingsCommand;
import me.karam.commands.impl.EmbedCommand;
import me.karam.commands.impl.GiveawayCommand;
import me.karam.commands.impl.TicketCommand;
import me.karam.utils.info.BotLogger;
import me.karam.utils.info.Color;
import me.karam.utils.Settings;
import me.karam.utils.info.Severity;
import me.karam.utils.events.ButtonsEvent;
import me.karam.utils.events.SelectionMenu;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

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

        commandMap.put("settings", new SettingsCommand());
        commandMap.put("embeds", new EmbedCommand());
        commandMap.put("giveaway", new GiveawayCommand());

        // Non-command
        commandMap.put("ticket", new TicketCommand());
    }

    public void registerCommands(){
        CommandListUpdateAction commands = Main.jda.getGuildById(Long.parseLong(Settings.GUILD_ID)).updateCommands();

        commands.addCommands(Commands.slash("embeds", "send pre-made embeds by bot")
                .addOptions(new OptionData(OptionType.STRING, "embed_type", "the embed you want to send")
                        .addChoice("support", "support").setRequired(true)));

        commands.addCommands(Commands.slash("settings", "main settings command")
                .addOptions(new OptionData(OptionType.STRING, "key", "the value you want to change")
                        .addChoice("ticket_channel", "ticket_channel").setRequired(true)
                        .addChoice("guild_id", "guild_id").setRequired(true)
                        .addChoice("ticket_category", "ticket_category_id").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "value", "the new value").setRequired(true)));

        commands.addCommands(Commands.slash("ticket", "manages tickets")
                .addSubcommands(new SubcommandData("close", "closes an existing ticket")
                        .addOptions(new OptionData(OptionType.STRING, "reason", "reason for ticket closure (not required)")))
                .addSubcommands(new SubcommandData("lock", "locks an open ticket from the user"))
                .addSubcommands(new SubcommandData("claim", "Claims a ticket")));

        commands.addCommands(Commands.slash("giveaway", "giveaway admin command")
                        .addSubcommands(new SubcommandData("create", "creates a new giveaway")
                .addOptions(new OptionData(OptionType.STRING, "prize", "the prize of the giveaway").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "duration", "the duration of the giveaway. (i.e. 3d, 30m, 3h)").setRequired(true))
                .addOptions(new OptionData(OptionType.INTEGER, "winners", "amount of winners").setMinValue(1).setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "requirements", "requirements to enter giveaway").setRequired(false))));
                //.addOptions(new OptionData(OptionType.INTEGER, "max_e", "maximum amount of enteries").setRequired(false))));


        commands.addCommands(
                Commands.context(Command.Type.USER, "Ticket Blacklist"),
                Commands.context(Command.Type.USER, "Ticket Whitelist"),
                Commands.context(Command.Type.MESSAGE, "Giveaway Edit")
        );

        commands.queue();
    }

    @Deprecated
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
            command.performCommand(event, event.getMember(), event.getChannel().asTextChannel());
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        commandMap.values().stream().forEach(command -> {
            Class commandClass = command.getClass();
            Method[] methods = commandClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.isAnnotationPresent(ButtonsEvent.class)){
                    try {
                        commandClass.getDeclaredMethod(method.getName(), ButtonInteractionEvent.class).invoke(commandClass.newInstance(), event);
                        super.onButtonInteraction(event);
                    } catch (Exception e) {
                        BotLogger.log(Severity.HIGH, "Seems like a dangerous error was encountered. The stacktrace message was left with: " + Color.ANSI_YELLOW + e.getMessage() + Color.ANSI_WHITE);
                        e.printStackTrace();
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
                if (method.isAnnotationPresent(SelectionMenu.class)){ //method.getName().equalsIgnoreCase("onSelectMenu")
                    try {
                        commandClass.getDeclaredMethod(method.getName(), SelectMenuInteractionEvent.class).invoke(commandClass.newInstance(), event);
                        super.onSelectMenuInteraction(event);
                    } catch (Exception e) {
                        BotLogger.log(Severity.HIGH, "Seems like a dangerous error was encountered. The stacktrace was left with: " + Color.ANSI_YELLOW + e.getMessage() + Color.ANSI_WHITE);
                        e.printStackTrace();
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
                if (method.getName().equalsIgnoreCase("onModal")){
                    try {
                        commandClass.getDeclaredMethod(method.getName(), new Class[] {ModalInteractionEvent.class}).invoke(commandClass.newInstance(), event);
                        super.onModalInteraction(event);
                    } catch (Exception e) {
                        BotLogger.log(Severity.HIGH, "Seems like a dangerous error was encountered. The stacktrace was left with: " + Color.ANSI_YELLOW + e.getMessage() + Color.ANSI_WHITE);
                        e.printStackTrace();
                    }
                }
            }
        });
        super.onModalInteraction(event);
    }
}
