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
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

                event.getMessage().delete().queue();

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle(MarkdownUtil.bold("⭐ Star Galaxy Support ⭐"));
                builder.setFooter("Please read all the information above before creating a support ticket");

                builder.setDescription(MarkdownUtil.bold("\uD83D\uDD28 Appealing a Ban\n") +
                        "⁃ When appealing a ban, please be sure to follow all of the instructions in the appeal\n" +
                        "⁃ Failure to follow the instructions properly will result in your appeal getting denied\n" +
                        "⁃ You won't receive a response from our staff unless more information is required\n" +
                        "⁃ To know the outcome of your appeal, join the server and look for the following:\n" +
                        "‣ If your appeal was accepted, your ban will be either no longer be permanent or you will be unbanned\n" +
                        "‣ If your appeal was denied, you will not be unbanned\n" +
                        "⁃ Please wait two weeks before re-appealing if your appeal was denied");

                builder.addField(MarkdownUtil.bold("\uD83C\uDF88 Apply for Media Rank\n"),
                        "⁃ Must have 500+ subscribers on Youtube\n" +
                        "⁃ Must have 500+ followers on tiktok, twitter or twitch", true);

                builder.addField(MarkdownUtil.bold("\uD83D\uDC8E Claim Nitro Booster Perks\n"),
                        "• Please boost our server then create a ticket to claim your reward\n", true);

                builder.addField(MarkdownUtil.bold("⚠️ Reporting a User\n"),
                        "• You can report a Minecraft or Discord user here\n" +
                        "• Please have evidence of your report ready before you create the ticket", false);

                builder.addField(MarkdownUtil.bold("❓Other Questions or Concerns\n"),
                        "• If you need any kind of support that isn't covered above, use the Other Support button\n" +
                        "• Please be sure to explain what kind of support you need in detail", true);

                event.getTextChannel().sendMessageEmbeds(builder.build()).queue();
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
