package me.karam.utils.gear;

import me.karam.utils.config.MessageObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.Color;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static MessageEmbed PERMISSION = createEmbed("You do not have enough permission!", new Color(135, 0, 14));
    public static MessageEmbed NULL_EMBED = createEmbed("Could not find that giveaway! It may have ended or have been deleted.", new Color(135, 0, 14));

    public static void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(content).queue();
        });
    }

    public static String generate(int count) {
        SecureRandom random = new SecureRandom();
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        byte[] buffer = new byte[count];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }
    public static void sendPrivateMessage(User user, MessageEmbed content) {
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessageEmbeds(content).queue();
        });
    }

    public static void sendPrivateMessage(User user, int delay, File file, MessageEmbed content) {
        user.openPrivateChannel().queueAfter(delay, TimeUnit.SECONDS, (channel) ->
        {
            channel.sendMessageEmbeds(content).queue();
            channel.sendFiles(FileUpload.fromData(file)).queue();
        });
    }

    public static void createLogs(ArrayList<MessageObject> messageObjects){
        /*Request request = new Request.Builder()
                .post(RequestBody.create(MessageObject.format(messageObjects).getBytes()))
                .addHeader("Content-Type", ContentType.TEXT);*/
    }
    public static MessageEmbed createEmbed(String description, Color color){
        return new EmbedBuilder().setDescription(description).setColor(color).build();
    }

    public static MessageEmbed createEmbed(String description, Color color, Date date){
        return new EmbedBuilder().setDescription(description).setColor(color).setFooter("Task Completed").setTimestamp(date.toInstant()).build();
    }

    public static MessageEmbed createEmbed(String description, String footer, Color color, Date date){
        return new EmbedBuilder().setDescription(description).setColor(color).setFooter("Task Completed").setFooter(footer).setTimestamp(date.toInstant()).build();
    }
}
