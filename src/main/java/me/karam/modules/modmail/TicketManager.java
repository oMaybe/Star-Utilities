package me.karam.modules.modmail;

import me.karam.Main;
import me.karam.config.Config;
import me.karam.modules.modmail.Ticket;
import me.karam.utils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.awt.Color;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class TicketManager {

    private Config config;
    private HashMap<UUID, Ticket> openTickets;
    private final HashMap<String, Ticket> closedTickets;

    public TicketManager(){
        this.config = new Config(new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "tickets.json"));
        openTickets = new HashMap<>();
        closedTickets = new HashMap<>();
        loadExistingTickets();
    }

    public void loadExistingTickets(){
        //BotLogger.log(config.getRawData("openedTickets"));
        //BotLogger.log(config.getRawData("closedTickets"));
    }

    public void saveTickets(){
        //HashMap

        // Bucket
        config.insert("openedTickets", SavableObject.toSavableTickets(openTickets));
        config.insert("closedTickets", closedTickets);
        config.save();
    }

    public void add(Ticket ticket){
        openTickets.put(ticket.getTicketID(), ticket);
        if (Settings.TICKET_LOG_CHANNEL == null){
            BotLogger.log(Severity.HIGH, "Failed to create ticket because ticket channel doesn't exist!");
            return;
        }
        TextChannel channel = ticket.getConsumer().getJDA().getGuildById(Settings.GUILD_ID).getTextChannelById(Settings.TICKET_LOG_CHANNEL);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(ticket.getConsumer().getUser().getAsTag(), null, ticket.getConsumer().getEffectiveAvatarUrl());
        builder.setTitle("New Ticket"); //
        //builder.setThumbnail(ticket.getConsumer().getEffectiveAvatarUrl());
        builder.addField(MarkdownUtil.bold("Ticket Creation."), ticket.getConsumer().getAsMention() + " created a " + ticket.getType().name().replace("_", " ") + " ticket.", false);
        builder.setColor(new Color(0, 40, 200));
        builder.setFooter("Ticket ID: " + ticket.getTicketID());
        builder.setTimestamp(new Date().toInstant());

        channel.sendMessageEmbeds(builder.build())
                .setActionRows(ActionRow.of(
                        Button.danger("close_no_reason", "🔒 Close"),
                        Button.primary("respond", "🖋️ Respond")
                )).queue();

        // TODO: ticket.setEmbedsSent();
    }

    public void send(Ticket ticket, String message){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor("Support Team", null, Main.jda.getSelfUser().getAvatarUrl());
        embedBuilder.setDescription(message);
        embedBuilder.addField("Responder", Main.jda.getUserById(ticket.getResponder()).getAsTag(), fa);
        embedBuilder.setFooter("Response");
        embedBuilder.setTimestamp(new Date().toInstant());
        embedBuilder.setColor(new Color(200, 200, 0));

        ticket.setResponded(true);
        if (ticket.getM() != null)
            ticket.getM().addReaction("✅").queue();

        Utils.sendPrivateMessage(ticket.getConsumer().getUser(), embedBuilder.build());
    }

    public void receive(Ticket ticket, String message){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(ticket.getConsumer().getUser().getAsTag(), null, ticket.getConsumer().getUser().getAvatarUrl());
        embedBuilder.addField("Response", message, false);
        embedBuilder.setColor(new Color(50, 200, 0));
        embedBuilder.setFooter("Ticket ID: " + ticket.getTicketID());
        embedBuilder.setTimestamp(new Date().toInstant());

        TextChannel channel = ticket.getConsumer().getJDA().getGuildById(Settings.GUILD_ID).getTextChannelById(Settings.TICKET_LOG_CHANNEL);
        channel.sendMessageEmbeds(embedBuilder.build()).setActionRows(ActionRow.of(
                Button.danger("close_no_reason", "🔒 Close"),
                Button.primary("respond", "🖋️ Respond")
            )).queue();
    }

    public void remove(Ticket ticket){
        if (openTickets.containsKey(ticket.getTicketID())) openTickets.remove(ticket.getTicketID());
    }

    public Ticket getTicket(User user){
        return openTickets.values().stream().filter(ticket -> ticket.getConsumer().getUser().getId().equals(user.getId())).findFirst().orElse(null);
    }

    public Ticket getTicketByResponder(String id){
        return openTickets.values().stream().filter(ticket -> ticket.getResponder().equals(id)).findFirst().orElse(null);
    }

    public Ticket getTicketByUUID(UUID ticketID){

        return openTickets.values().stream().filter(ticket -> ticket.getTicketID().equals(ticketID)).findFirst().orElse(null);
    }

    public boolean hasOpenTicket(String id){
        return openTickets.values().stream().filter(ticket -> ticket.getId().equals(id)).findAny().isPresent();
    }

    public boolean isClosedTicket(Ticket ticket){
        return closedTickets.containsKey(ticket.getTicketID());
    }

    public void closeTicket(Ticket ticket) {
        openTickets.remove(ticket.getTicketID());
        closedTickets.put(ticket.getId(), ticket);
    }
}

