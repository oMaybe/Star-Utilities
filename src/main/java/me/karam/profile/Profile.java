package me.karam.profile;

import me.karam.Main;
import me.karam.modules.modmail.Ticket;
import me.karam.utils.Severity;

import java.io.File;
import java.io.IOException;

public class Profile {

    private String id;
    private Ticket openedTicket;

    public Profile(String id){
        this.id = id;
        loadData();
    }

    public void loadData(){

        // load from database
    }
}
