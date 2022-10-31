package me.karam.modules.modmail;

import lombok.Getter;
import lombok.Setter;
import me.karam.Main;
import me.karam.utils.config.MessageObject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Ticket {

    private String ticketID;
    private String id;
    private Member consumer;
    private TicketType type;
    private List<Message> embedsSent;
    private String responder;
    private boolean locked;
    private boolean claimed;
    private Message m;
    private String firstMessageID;
    private String closeReason;
    private Member closer;
    private ArrayList<MessageObject> contents;
    private TextChannel channel;

    private File transcript;
    public Ticket(String ticketID, String id, Member consumer, TicketType type) {
        this.ticketID = ticketID;
        this.id = id;
        this.consumer = consumer;
        this.type = type;
        this.contents = new ArrayList<>();
    }

    public void addObject(MessageObject mO){
        contents.add(mO);
    }
    public static boolean isClosed(Ticket ticket){
        return Main.getInstance().getTicketManager().isClosedTicket(ticket);
    }

    public String getCloseReason() {
        return closeReason;
    }
}
