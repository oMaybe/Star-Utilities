package me.karam.utils;

import me.karam.modules.modmail.Ticket;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class SavableObject {

    public static SavableObject toSavableTickets(HashMap<UUID, Ticket> openTickets){
        HashMap tempMap = new HashMap();
        return new SavableObject();
    }
}
