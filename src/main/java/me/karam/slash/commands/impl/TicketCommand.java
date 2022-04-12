package me.karam.slash.commands.impl;

import me.karam.Main;
import me.karam.modules.modmail.Ticket;
import me.karam.slash.commands.SlashCommand;
import me.karam.utils.BotLogger;
import me.karam.utils.Settings;
import me.karam.utils.Severity;
import me.karam.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import okhttp3.*;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel) {

    }

    public void onButton(ButtonInteractionEvent event){
        String buttonID = event.getComponentId();
        Member member = event.getMember();
        //BotLogger.log(buttonID);
        if (event.getMessage().getEmbeds() == null || event.getMessage().getEmbeds().get(0) == null){
            return;
        }

        MessageEmbed embed = event.getMessage().getEmbeds().get(0);
        if (!embed.getFooter().getText().contains("Ticket ID: ")) return;
        String ticketID = embed.getFooter().getText().replace("Ticket ID: ", "");
        Ticket ticket = Main.getInstance().getTicketManager().getTicketByUUID(UUID.fromString(ticketID));
        if (ticket == null){
            event.reply("That ticket is already closed or does not exist!").setEphemeral(true).queue();
            return;
        }

        if (buttonID.equalsIgnoreCase("respond")){
            TextInput respond = TextInput.create("response_input", "Message to member", TextInputStyle.PARAGRAPH)
                    .setMinLength(2)
                    .setMaxLength(1024)
                    .setPlaceholder("message here..")
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("response", "Respond to member")
                    .addActionRows(ActionRow.of(respond))
                    .build();

            ticket.setResponder(event.getUser().getId());
            event.replyModal(modal).queue();
        }else if (buttonID.equalsIgnoreCase("close_no_reason")){
            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setAuthor(ticket.getConsumer().getUser().getAsTag(), null, ticket.getConsumer().getAvatarUrl());
            embedBuilder.setTitle("Ticket Closed.");
            embedBuilder.setDescription("This ticket has been closed with no reason.");
            embedBuilder.setFooter("Closed");
            embedBuilder.setTimestamp(new Date().toInstant());
            embedBuilder.setColor(new Color(0, 0, 0));

            ticket.setResponder(event.getUser().getId());
            if (ticket.getM() != null)
                ticket.getM().addReaction("✅").queue();

            EmbedBuilder consumerTicket = new EmbedBuilder();
            consumerTicket.setAuthor("Support Team", null, event.getJDA().getSelfUser().getAvatarUrl());
            consumerTicket.setColor(new Color(0, 0, 0));
            consumerTicket.setDescription("This ticket has now been closed. If you send another message, it will create a new ticket.");
            consumerTicket.setFooter("Ticket Closed");
            consumerTicket.setTimestamp(new Date().toInstant());

            event.replyEmbeds(embedBuilder.build()).queue();
            Utils.sendPrivateMessage(ticket.getConsumer().getUser(), consumerTicket.build());

            Main.getInstance().getTicketManager().closeTicket(ticket);

            // TODO:

        }else if (buttonID.equalsIgnoreCase("close_reason")){
            TextInput respond = TextInput.create("ticket_close", "Message to Close ticket", TextInputStyle.PARAGRAPH)
                    .setMinLength(2)
                    .setMaxLength(1024)
                    .setPlaceholder("message here..")
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("ticket_close_m", "Respond to member")
                    .addActionRows(ActionRow.of(respond))
                    .build();

            ticket.setResponder(event.getUser().getId());
            event.replyModal(modal).queue();
        }
    }

    public void onModalInteraction(ModalInteractionEvent event) {
        try {
            if (event.getModalId().equalsIgnoreCase("response")) {
                String buttonID = event.getValues().stream().findAny().get().getId();
                BotLogger.log(buttonID);
                if (buttonID.equalsIgnoreCase("response_input")) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();

                    Ticket ticket = Main.getInstance().getTicketManager().getTicketByResponder(event.getUser().getId());
                    if (ticket == null) {
                        event.reply("Something that shouldn't go wrong went wrong. Contact developer if problem persists!").setEphemeral(true).queue();
                        return;
                    }

                    embedBuilder.setAuthor("Support Team", null, event.getJDA().getSelfUser().getAvatarUrl());
                    embedBuilder.addField(ticket.getType().name(), ticket.getConsumer().getAsMention(), true);
                    embedBuilder.addField("Response", event.getValue("response_input").getAsString(), false);
                    embedBuilder.setColor(new Color(200, 200, 0));
                    embedBuilder.setFooter("Ticket ID: " + ticket.getTicketID());
                    embedBuilder.setTimestamp(new Date().toInstant());

                    // TODO: send ticket
                    Main.getInstance().getTicketManager().send(ticket, event.getValue("response_input").getAsString());
                    event.replyEmbeds(embedBuilder.build()).queue();
                } else if (event.getModalId().equalsIgnoreCase("ticket_close_m")) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();

                    Ticket ticket = Main.getInstance().getTicketManager().getTicketByResponder(event.getUser().getId());
                    if (ticket == null) {
                        event.reply("Something that shouldn't go wrong went wrong. Contact developer if problem persists!").setEphemeral(true).queue();
                        return;
                    }

                    embedBuilder.setAuthor("Support Team", "", event.getJDA().getSelfUser().getAvatarUrl());
                    embedBuilder.addField(ticket.getType().name() + " Closure", ticket.getConsumer().getAsMention(), true);
                    embedBuilder.addField("Response", event.getValue("response_input").getAsString(), false);
                    embedBuilder.setColor(new Color(200, 0, 0));
                    embedBuilder.setFooter("Ticket ID: " + ticket.getTicketID());
                    embedBuilder.setTimestamp(new Date().toInstant());

                    // TODO: send it

                    event.replyEmbeds(embedBuilder.build()).queue();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
