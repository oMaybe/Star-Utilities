package me.karam.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.isFromGuild()){
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args[0].equalsIgnoreCase("support")){

            }
        }else {

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Your message has been sent!");
            embedBuilder.setTimestamp(date.toInstant());

            MessageChannel channel = event.getChannel();
            channel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
