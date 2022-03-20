package me.karam.slash.commands;

import me.karam.Main;
import me.karam.utils.Settings;
import me.karam.utils.Severity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class CommandManager extends ListenerAdapter {

    private static OkHttpClient client = new OkHttpClient();

    public CommandManager(){

        //register(true, );
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
    public void onRawGateway(@NotNull RawGatewayEvent event) {
        if (event.getType().equals("INTERACTION_CREATE")){
           /* System.out.println("INTERACTED");
            JSONObject interactionPackage = new JSONObject(event.getPackage().toString());
            String id = interactionPackage.getJSONObject("d").get("id").toString();
            String token = interactionPackage.getJSONObject("d").get("token").toString();
            String name = interactionPackage.getJSONObject("d").getJSONObject("data").get("name").toString();

            Guild g = Main.jda.getGuildById(interactionPackage.getJSONObject("d").getString("guild_id"));
            Member member = g.getMemberById(interactionPackage.getJSONObject("d").getJSONObject("data").getJSONObject("member").getJSONObject("user").getString("id"));

            // "{\"type\":4,\"data\":{\"content\":\"" + callback + "\"}}"
            JSONObject json = new JSONObject("{\"type\":4,\"data\":{\"content\":\"" + callback + "\"}}");

            Request request = new Request.Builder()
                    .url("https://discord.com/api/v8/interactions/" + id + "/" + token + "/callback")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bot " + Settings.TOKEN)
                    .post(RequestBody.create(MediaType.parse("application/json, charset=utf-8"), json.toString())).build();

            try(Response response = client.newCall(request).execute()){
                System.out.println(response.body());
            }catch (Exception ex){
                ex.printStackTrace();
            }*/

        }
    }
}
