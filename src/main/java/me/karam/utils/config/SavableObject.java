package me.karam.utils.config;

import me.karam.modules.modmail.Ticket;

import java.util.HashMap;
import java.util.UUID;

public class SavableObject {

    public static SavableObject toSavableTickets(HashMap<UUID, Ticket> openTickets){
        HashMap tempMap = new HashMap();
        return new SavableObject();
    }
}
