package me.karam.commands.impl;

import me.karam.Main;
import me.karam.commands.SlashCommand;
import me.karam.modules.modmail.Ticket;
import me.karam.utils.config.Logs;
import me.karam.utils.events.ButtonsEvent;
import me.karam.utils.gear.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TicketCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel) {
        //if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) return;
        if (event.getName().equalsIgnoreCase("ticket")){
            String subCommand = event.getSubcommandName().toLowerCase();
            Ticket ticket = Main.getInstance().getTicketManager().getTicketByID(channel.getTopic().split("\n")[0].replace("**","").replace("Ticket ID: ", ""));
            switch (subCommand){
                case "close":
                    if (ticket == null){
                        event.reply("This ticket does not exist. You must be in a channel that matches an open ticket.").setEphemeral(true).queue();
                        return;
                    }

                    if (Main.getInstance().getTicketManager().isClosedTicket(ticket)){
                        event.reply("This ticket is already closed. Why has it not been deleted?").setEphemeral(true).queue();
                        return;
                    }

                    event.reply("This channel will be deleted in less than 1 minute").setEphemeral(true).queue();
                    if (event.getOptions().size() == 0){
                        MessageEmbed embed = Utils.createEmbed("Ticket has been closed by " + event.getMember().getAsMention(), new Color(200, 170, 0), new Date());
                        channel.sendMessageEmbeds(embed).queue();

                        channel.getParentCategory().upsertPermissionOverride(event.getMember()).setDenied(Permission.MESSAGE_SEND).queue();
                        channel.getParentCategory().upsertPermissionOverride(event.getMember()).setDenied(Permission.VIEW_CHANNEL).queueAfter(15, TimeUnit.SECONDS);

                        channel.upsertPermissionOverride(event.getMember()).setDenied(Permission.MESSAGE_SEND).queue();
                        channel.upsertPermissionOverride(event.getMember()).setDenied(Permission.VIEW_CHANNEL).queueAfter(15, TimeUnit.SECONDS);

                        channel.delete().queueAfter(30, TimeUnit.SECONDS);
                        Utils.sendPrivateMessage(ticket.getConsumer().getUser(), 15, ticket.getTranscript(), Utils.createEmbed("Your ticket has been closed by " + event.getMember().getAsMention() + ".\n You may view the transcript below.", "Ticket ID: " + ticket.getTicketID(), new Color(0, 0, 0), new Date()));

                        ticket.setCloseReason("(no reason)");
                        ticket.setCloser(event.getMember());

                        Main.getInstance().getTicketManager().closeTicket(ticket);
                        return;
                    }

                    String reason = event.getOptions().get(0).getAsString();
                    MessageEmbed embed = Utils.createEmbed("Ticket has been closed by " + event.getMember().getAsMention() + " for the reason: **" + reason + "**", new Color(200, 170, 0), new Date());
                    channel.sendMessageEmbeds(embed).queue();

                    channel.getParentCategory().upsertPermissionOverride(event.getMember()).setDenied(Permission.MESSAGE_SEND).queue();
                    channel.getParentCategory().upsertPermissionOverride(event.getMember()).setDenied(Permission.VIEW_CHANNEL).queueAfter(15, TimeUnit.SECONDS);

                    channel.upsertPermissionOverride(event.getMember()).setDenied(Permission.MESSAGE_SEND).queue();
                    channel.upsertPermissionOverride(event.getMember()).setDenied(Permission.VIEW_CHANNEL).queueAfter(15, TimeUnit.SECONDS);

                    ticket.setCloseReason(reason);
                    ticket.setCloser(event.getMember());

                    channel.delete().queueAfter(30, TimeUnit.SECONDS);
                    Main.getInstance().getTicketManager().closeTicket(ticket);
                    Utils.sendPrivateMessage(ticket.getConsumer().getUser(), 15, ticket.getTranscript(), Utils.createEmbed("Your ticket has been closed by " + event.getMember().getAsMention() + " for the reason of " + MarkdownUtil.bold(reason) + ".\n You may view the transcript below.\n", "Ticket ID: " + ticket.getTicketID(), new Color(0, 0, 0), new Date()));
                    break;
                case "lock":
                    if (ticket == null){
                        event.reply("This ticket does not exist. You must be in a channel that matches an open ticket.").setEphemeral(true).queue();
                        return;
                    }

                    if (ticket.isLocked()){
                        channel.sendMessageEmbeds(Utils.createEmbed("🔓 | This channel is now unlocked.", new Color(0, 120, 0), new Date())).queue();
                        channel.getParentCategory().upsertPermissionOverride(event.getMember()).setAllowed(Permission.MESSAGE_SEND).queue();
                        channel.upsertPermissionOverride(event.getMember()).setAllowed(Permission.MESSAGE_SEND).queue();
                    }else{
                        channel.sendMessageEmbeds(Utils.createEmbed("🔒 | This channel is now locked for review.", new Color(120, 0, 0), new Date())).queue();
                        channel.upsertPermissionOverride(event.getMember()).setDenied(Permission.MESSAGE_SEND).queue();
                        channel.getParentCategory().upsertPermissionOverride(event.getMember()).setDenied(Permission.MESSAGE_SEND).queue();
                    }

                    ticket.setLocked(!ticket.isLocked());
                    event.reply("Success.").setEphemeral(true).queue();
                    break;
                case "claim":
                    if (ticket == null){
                        event.reply("This ticket does not exist. You must be in a channel that matches an open ticket.").setEphemeral(true).queue();
                        return;
                    }

                    if (ticket.isClaimed()){
                        event.reply("This ticket is already claimed by " + event.getGuild().getMemberById(ticket.getResponder()).getAsMention()).setEphemeral(true).queue();
                        return;
                    }

                    channel.upsertPermissionOverride(event.getGuild().getRoleById("977192191107678238")).setDenied(Permission.MESSAGE_SEND).queue();

                    channel.getParentCategory().upsertPermissionOverride(event.getMember()).setAllowed(Permission.MESSAGE_SEND).queue();
                    channel.upsertPermissionOverride(event.getMember()).setAllowed(Permission.MESSAGE_SEND).queue();

                    ticket.setClaimed(true);
                    ticket.setResponder(event.getMember().getUser().getId());

                    channel.retrieveMessageById(ticket.getFirstMessageID()).queue((message) -> {
                        message.editMessageEmbeds(message.getEmbeds().get(0), Utils.createEmbed("This ticket is now claimed by " + event.getMember().getAsMention(),
                                new Color(0, 120, 120), new Date())).queue();
                    });

                    event.reply("Success.").setEphemeral(true).queue();
                    break;
            }
        }
    }

    @ButtonsEvent
    public void onButton(ButtonInteractionEvent event){
        String buttonID = event.getComponentId();
        Member member = event.getMember();
        if (event.getMessage().getEmbeds() == null || event.getMessage().getEmbeds().get(0) == null){
            return;
        }

        if (buttonID.equalsIgnoreCase("transcript")){
            String ticketID = event.getMessage().getEmbeds().get(0).getFields().get(4).getValue();

            File file = Logs.getLog(ticketID);
            if (ticketID == null || file == null){
                event.reply("There is no ticket with that ID or no logs were created for that ticket!").setEphemeral(true).queue();
                return;
            }

            event.replyFiles(FileUpload.fromData(file)).setEphemeral(true).queue();
        }
    }

}
