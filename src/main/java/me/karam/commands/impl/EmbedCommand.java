package me.karam.commands.impl;

import me.karam.Main;
import me.karam.commands.SlashCommand;
import me.karam.listener.MessageListener;
import me.karam.modules.modmail.Ticket;
import me.karam.modules.modmail.TicketType;
import me.karam.utils.*;
import me.karam.utils.config.Logs;
import me.karam.utils.events.SelectionMenu;
import me.karam.utils.gear.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EmbedCommand implements SlashCommand {
    public void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        if (event.getOptions().get(0).getAsString().equalsIgnoreCase("support")) {
            EmbedBuilder builder = new EmbedBuilder();
            /*builder.setTitle(MarkdownUtil.bold("⭐ Wolfverse Support ⭐"));
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
                            "• Please be sure to explain what kind of support you need in detail", true);*/

            builder.setAuthor("Wolfverse Support Team", null, event.getJDA().getSelfUser().getAvatarUrl());
            builder.setDescription("`1` General Questions\n" +
                    "Choose this option for any general questions you may have towards the developers, or staff team.\n" +
                    "\n" +
                    "`2` Staff/User Reports\n" +
                    "Use this whenever you want to make our moderators aware of severely bad behaviour by server members. Be prepared to provide the User ID and a screenshot.\n" +
                    "\n" +
                    "`3` Server Giveaways\n" +
                    "Use this category if you want to sponsor a giveaway. Please use the \"Administrators\" category for hosting nitro giveaways instead.\n" +
                    "\n" +
                    "`4` Partnership\n" +
                    "Choose this option if you have a server and want to partner with us. Be sure to read the requirements before requesting partnership.\n" +
                    "\n" +
                    "`5` Appeal\n" +
                    "Choose this option if you feel like a punishment that was given to you was unfair or biased. A head moderator or above will handle your request, so please be patient.\n" +
                    "\n" +
                    "`6` Administrators\n" +
                    "Anything that is not meant for our moderators, for example reporting a staff member, or hosting nitro giveaways. Please be patient as an admin may not be available to assist you instantly.");

            event.getChannel().sendMessageEmbeds(builder.build()).setComponents(ActionRow.of(
                    SelectMenu
                            .create("support")
                            .addOption("❓ General Question", "question")
                            .addOption("⚡ Staff/User report", "user_report")
                            .addOption("🎉 Server Giveaways", "giveaways")
                            .addOption("📚 Partnership", "partner")
                            .addOption("🔨 Appeal", "appeal")
                            .addOption("\uD83D\uDED1 Administrators", "admin")
                            .setMinValues(1)
                            .setMaxValues(1)
                            .build()
            )).queue();

            /*event.getChannel().sendMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
                        Button.success("question", "❓ General Question"),
                        Button.danger("user_report", "⚡ Staff/User report"),
                        Button.primary("giveaways", "🎉 Server Giveaways")),
                    ActionRow.of(
                        Button.success("partner", "📚 Partnership"),
                        Button.danger("appeal", "🔨 Appeal"),
                        Button.primary("admin", "🛡 Administrators")
            )).queue();*/
            event.reply("okay.").setEphemeral(true).queue();
        }else{
            event.reply("I'm sorry but the only choices you have is 'support'.").setEphemeral(true).queue();
        }
    }

    @SelectionMenu
    public void testing(SelectMenuInteractionEvent event){
       String id = event.getValues().get(0);
       Member member = event.getMember();
        if (id.equalsIgnoreCase("question") || id.equalsIgnoreCase("user_report") ||
                id.equalsIgnoreCase("giveaways") || id.equalsIgnoreCase("partner") ||
                id.equalsIgnoreCase("appeal") || id.equalsIgnoreCase("admin")) {
            if (MessageListener.blacklisted.contains(member.getId())){
                event.reply("You are currently blacklisted from tickets. You may not create a ticket.").setEphemeral(true).queue();
                return;
            }

            if (Main.getInstance().getTicketManager().hasOpenTicket(member.getUser().getId())) {
                event.reply("You already have another ticket open!").setEphemeral(true).queue();
                return;
            }

            String tempID = Utils.generate(9).toLowerCase();
            if (Logs.getLog(tempID) != null){
                tempID = Utils.generate(9).toLowerCase();
            }

            Category category = event.getGuild().getCategoryById(Settings.TICKET_CATEGORY);
            TextChannel channel = category.createTextChannel(member.getUser().getAsTag()).complete();

            channel.upsertPermissionOverride(event.getMember()).setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();
            channel.upsertPermissionOverride(event.getGuild().getRoleById("977192191107678238")).setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Wolfverse Support", null, event.getJDA().getSelfUser().getAvatarUrl());
            builder.setDescription("The staff team are on their way to assist you. Please wait until a staff member responds to you. Use as much detail as you can in order to maximize support capacity.");
            builder.setTimestamp(new Date().toInstant());
            builder.setFooter(TicketType.from(id));

            Ticket ticket = new Ticket(tempID, member.getUser().getId(), member, TicketType.to(id));
            Main.getInstance().getTicketManager().add(ticket);

            channel.sendMessageEmbeds(builder.build()).queue(message -> {
                ticket.setFirstMessageID(message.getId());
                channel.sendMessage("@here").queue(d -> {
                    d.delete().queueAfter(2, TimeUnit.SECONDS);
                });
            });

            channel.getManager().setTopic(MarkdownUtil.bold("Ticket ID: " + tempID)).queue();

            ticket.setChannel(channel);
            event.reply("Your ticket has been created in: " + channel.getAsMention()).setEphemeral(true).queue();
        }
    }

}
