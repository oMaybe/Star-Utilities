package me.karam.profile;

import lombok.Getter;
import lombok.Setter;
import me.karam.modules.giveaway.Giveaway;
import me.karam.modules.modmail.Ticket;

@Setter
@Getter
public class Profile {

    private String id;
    private Ticket openedTicket;
    private String editing;

    public Profile(String id){
        this.id = id;
        loadData();
    }

    public void loadData(){
        // load from database
    }
}
