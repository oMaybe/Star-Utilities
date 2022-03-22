package me.karam.profile;

import me.karam.Main;
import me.karam.utils.Settings;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.DefaultShardManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import java.util.Collection;
import java.util.HashMap;

public class ProfileManager {

    private HashMap<String, Profile> profileHashMap;

    public ProfileManager(){
        profileHashMap = new HashMap<>();
        Main.jda.getGuildById("954271232067530782").getMembers()
                .stream()
                .filter(member -> !member.getUser().isBot())
                .forEach(member -> add(member.getUser()));
    }

    public void add(User user){
        if (!exists(user.getId())) {
            profileHashMap.put(user.getId(), new Profile(user.getId()));
        }
    }

    public void add(String id){
        if (!exists(id)) {
            profileHashMap.put(id, new Profile(id));
        }
    }

    public Profile getByID(String id){
        return exists(id) ? profileHashMap.get(id) : null;
    }

    public Profile getByMention(IMentionable mentionable){
        return exists(mentionable.getId()) ? getByID(mentionable.getId()) : null;
    }

    public Collection<Profile> getProfiles() { return profileHashMap.values(); }

    public boolean exists(String id){
        return profileHashMap.containsKey(id);
    }

    public void remove(String id) {
        if (exists(id)) profileHashMap.remove(id);
    }
}
