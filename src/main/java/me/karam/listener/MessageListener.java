package me.karam.listener;

import me.karam.Main;
import me.karam.modules.giveaway.Giveaway;
import me.karam.modules.modmail.Ticket;
import me.karam.profile.Profile;
import me.karam.utils.config.MessageObject;
import me.karam.utils.events.SelectionMenu;
import me.karam.utils.gear.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class MessageListener extends ListenerAdapter {

    public static ArrayList<String> blacklisted = new ArrayList();
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            if (event.getChannel().getType() != ChannelType.TEXT){
                return;
            }

            if (event.getChannel().asTextChannel().getTopic() == null){
                return;
            }
            Ticket ticket = Main.getInstance().getTicketManager().getTicketByID(event.getChannel().asTextChannel().getTopic().split("\n")[0].replace("**","").replace("Ticket ID: ", ""));
                if (Main.getInstance().getTicketManager().hasOpenTicket(event.getAuthor().getId())) {
                    //Ticket ticket = Main.getInstance().getTicketManager().getTicket(event.getAuthor());
                    if (ticket != null && !ticket.isClosed(ticket)) {
                        ticket.addObject(new MessageObject(event.getMember(), new Date(), event.getMessage()));
                    }
                }
            //}
        }
        super.onMessageReceived(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        Member member = event.getMember();
        if (event.getName().equalsIgnoreCase("Ticket Blacklist")){
            if (!member.hasPermission(Permission.MESSAGE_MANAGE)){
                event.reply("You do not have enough permission to perform this action!").setEphemeral(true).queue();
                return;
            }

            User targetMember = event.getTarget();
            if (blacklisted.contains(targetMember.getId())){
                event.reply("That user is already blacklisted!").setEphemeral(true).queue();
                return;
            }

            blacklisted.add(targetMember.getId());
            event.reply("Successfully added the member " + targetMember.getAsMention() + " to the ticket blacklist.").setEphemeral(true).queue();
        }else if (event.getName().equalsIgnoreCase("Ticket Whitelist")){
            if (!member.hasPermission(Permission.MESSAGE_MANAGE)){
                event.reply("You do not have enough permission to perform this action!").setEphemeral(true).queue();
                return;
            }

            User targetMember = event.getTarget();
            if (!blacklisted.contains(targetMember.getId())){
                event.reply("That user is not blacklisted!").setEphemeral(true).queue();
                return;
            }

            blacklisted.remove(targetMember.getId());
            event.reply("Successfully removed the member " + targetMember.getAsMention() + " from the ticket blacklist.").setEphemeral(true).queue();
        }

        super.onUserContextInteraction(event);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        Member member = event.getMember();
        if (event.getName().equalsIgnoreCase("Giveaway Edit")){
            if (!member.hasPermission(Permission.MESSAGE_MANAGE)){
                event.reply("You do not have enough permission to perform this action!").setEphemeral(true).queue();
                return;
            }

            Profile profile = Main.getInstance().getProfileManager().getByID(member.getId());
            if (profile == null){
                event.replyEmbeds(Utils.createEmbed("Your profile is null! You cannot perform this action without a valid profile.", new Color(150, 0, 0)));
                return;
            }

            Giveaway giveaway = Main.getInstance().getGiveawayManager().getGiveawayByID(event.getTarget().getId());
            profile.setEditing(giveaway.getId());

            SelectMenu menu = SelectMenu.create("edit_giveaway")
                    .addOption("⏲ Time", "g_time")
                    .addOption("🏆 Prize", "g_prize")
                    .addOption("✒ Host", "g_host")
                    .addOption("🥇 Winner Amount", "g_winner_amount")
                    .addOption("🎢 Reroll Winner", "g_reroll")
                    //.addOption("🔢 Maximum Entries", "g_max_entries")
                    .addOption("📜 Requirements", "g_requirements")
                    .setMaxValues(1).setMinValues(1).build();

            event.reply("Please select what you would like to edit:")
                    .setActionRow(menu).setEphemeral(true).queue();
        }
        super.onMessageContextInteraction(event);
    }
}
// {"botData":{"guild_id":"954271232067530782","ticket_channel":"954271232067530785"}}