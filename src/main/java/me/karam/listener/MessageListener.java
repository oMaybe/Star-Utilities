package me.karam.listener;

import me.karam.Main;
import me.karam.modules.modmail.Ticket;
import me.karam.modules.modmail.TicketType;
import me.karam.utils.BotLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.UUID;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()){
            if (event.getMessage().getEmbeds() != null){
                //BotLogger.log(event.getMessage().getEmbeds());
            }
        }else {
            if (event.getAuthor().isBot()) return;
            if (Main.getInstance().getTicketManager().hasOpenTicket(event.getAuthor().getId())) {
                Ticket ticket = Main.getInstance().getTicketManager().getTicket(event.getAuthor());
                if (ticket != null && !ticket.isClosed(ticket)) {
                    if (ticket.isResponded()) {
                        String message = event.getMessage().getContentRaw();
                        ticket.setResponded(false);
                        ticket.setM(event.getMessage());
                        Main.getInstance().getTicketManager().receive(ticket, message);
                        return;
                    }else {
                        return;
                    }
                }
            }

            Date date = new Date();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(MarkdownUtil.bold("⭐ Star Galaxy Support ⭐"));
            /*builder.setFooter("Please read all the information above before creating a support ticket");

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

            builder.setTimestamp(date.toInstant());
            builder.setColor(new Color(190, 250, 0));

            MessageChannel channel = event.getChannel();
            channel.sendMessageEmbeds(builder.build())
                    .setActionRow(SelectMenu.create("dm_support")
                    .addOption("Media Application", "Media_App")
                    .addOption("Server Boost Perks", "Server_Boost")
                    .addOption("Ban Appeal Form", "Ban_Appeal")
                    .addOption("Report User", "Report_User")
                    .addOption("Other Question", "Other_Q")
                    .build())
                    .queue();*/

            builder.setColor(new Color(0, 0, 0));
            builder.setDescription("Ticket creation in dms are still being developed. If you want to create a ticket you must create one from the guild.");
            event.getChannel().sendMessageEmbeds(builder.build()).queue();
        }
        super.onMessageReceived(event);
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        String selectedMenu = event.getComponentId();
        if (event.isFromGuild()) return;
        if (Main.getInstance().getTicketManager().hasOpenTicket(event.getUser().getId())){
            event.reply("You already have another ticket open!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Your ticket has been created. A staff member will be with you shortly.");
        embedBuilder.setTimestamp(new Date().toInstant());
        embedBuilder.setColor(new Color(0, 150, 0));
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

        switch (selectedMenu){
            case "Media Application":
                Ticket ticket = new Ticket(UUID.randomUUID(), event.getMember().getUser().getId(), event.getMember(), TicketType.Media);
                Main.getInstance().getTicketManager().add(ticket);
            case "Server Boost Perks":
                Ticket ticket2 = new Ticket(UUID.randomUUID(), event.getMember().getUser().getId(), event.getMember(), TicketType.Nitro_Perks);
                Main.getInstance().getTicketManager().add(ticket2);
            case "Ban Appeal Form":
                Ticket ticket3 = new Ticket(UUID.randomUUID(), event.getMember().getUser().getId(), event.getMember(), TicketType.Ban_Appeal);
                Main.getInstance().getTicketManager().add(ticket3);
            case "Report User":
                Ticket ticket4 = new Ticket(UUID.randomUUID(), event.getMember().getUser().getId(), event.getMember(), TicketType.Report_User);
                Main.getInstance().getTicketManager().add(ticket4);
            case "Other Question":
                Ticket ticket5 = new Ticket(UUID.randomUUID(), event.getMember().getUser().getId(), event.getMember(), TicketType.Other);
                Main.getInstance().getTicketManager().add(ticket5);
        }
        super.onSelectMenuInteraction(event);
    }
}
// {"botData":{"guild_id":"954271232067530782","ticket_channel":"954271232067530785"}}