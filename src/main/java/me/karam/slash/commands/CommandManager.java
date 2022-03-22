package me.karam.slash.commands;

import com.google.gson.Gson;
import me.karam.Main;
import me.karam.slash.commands.impl.DMCommand;
import me.karam.slash.commands.impl.EmbedCommand;
import me.karam.slash.commands.impl.SettingsCommand;
import me.karam.utils.Settings;
import me.karam.utils.Severity;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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

        register(false, "{\"name\":\"embeds\",\"description\":\"sends pre made embed by bot\",\"options\":[{\"type\":3,\"name\":\"embed_name\",\"description\":\"name of embed you want to send\",\"required\":true}]}");
        register(false, "{\"name\":\"settings\",\"description\":\"changes configuration\",\"options\":[{\"type\":3,\"name\":\"setting\",\"description\":\"the setting you want to change\",\"required\":true},{\"type\":3,\"name\":\"value\",\"description\":\"new value of settings\",\"required\":true}]}");
        // TODO: dm command
        register(false, "{\"name\":\"dm\",\"description\":\"private message a member from the bot\",\"options\":[{\"type\":9,\"name\":\"member\",\"description\":\"the member to send the message to\",\"required\":true},{\"type\":3,\"name\":\"message\",\"description\":\"the message to be sent tothe user\",\"required\":true}]}");
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
                    Main.getInstance().log(Severity.LOW, response.code());
                    Main.getInstance().log(Severity.LOW, response.isSuccessful());
                    Main.getInstance().log(Severity.LOW, response.body());
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        String commandName = event.getName();
        SlashCommand command;

        if ((command = commandMap.get(commandName)) != null){
            command.performCommand(event, event.getMember(), event.getTextChannel());
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        for (SlashCommand command : commandMap.values()){
            Method[] methods = command.getClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equalsIgnoreCase("onButton")){
                    try {
                        command.getClass().getDeclaredMethod(methods[i].getName(), new Class[] {ButtonClickEvent.class}).invoke(command, event);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    //checkClass.getDeclaredMethod(method.getName(), new Class[] {WrappedEvent.class}).invoke(check, wrappedEvent);
                }
            }
        }
    }
}
