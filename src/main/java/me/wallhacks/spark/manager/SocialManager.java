package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.SessionUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocialManager {

    private ArrayList<UUID> friends = new ArrayList<>();

    public void clearFriends() {
        friends.clear();
    }
    public List<UUID> getFriends() {
        return friends;
    }
    public List<String> getFriendsNames() {
        ArrayList<String> l = new ArrayList<>();
        for (UUID s : getFriends())
            l.add(SessionUtils.getname(s));
        return l;
    }


    public void addFriend(UUID uuid){
        if(!friends.contains(uuid))
            friends.add(uuid);
    }
    public void removeFriend(UUID uuid){
        if(getFriends().contains(uuid))
            getFriends().remove(uuid);
    }

    public void addFriend(String name){
        UUID uuid = SessionUtils.getid(name);
        addFriend(uuid);
    }
    public void removeFriend(String name){
        UUID uuid = SessionUtils.getid(name);
        removeFriend(uuid);
    }


    public boolean isFriend(UUID uuid) {
        for (UUID s : getFriends()) {
            if (uuid.equals(s)) return true;
        }
        return false;
    }
    public boolean isFriend(String name) {
        for (UUID s : getFriends()) {
            if (name.equalsIgnoreCase(SessionUtils.getname(s))) return true;
        }
        return false;
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(player.getGameProfile().getId());
    }



    String getFriendsFile() {
        String base = Spark.ParentPath.getAbsolutePath() + "\\socials\\";
        return base + "friends.sex";
    }


    public void LoadFriends() {
        try {
            String s = FileUtil.read(getFriendsFile());
            if (s != null) {
                String[] List = s.split("\n");
                Spark.socialManager.clearFriends();
                for (String var : List) {
                    if (var != "") {
                        UUID uuid = UUID.fromString(var);
                        Spark.socialManager.addFriend(uuid);
                    }
                }

            }
        } catch (Exception e) {
            Spark.logger.info("Failed to load friends");
            e.printStackTrace();
        }
    }

    public void SaveFriends() {
        try {
            ArrayList<String> lines = new ArrayList<String>();

            String content = "";
            for (UUID e : Spark.socialManager.getFriends())
                content = content + e.toString() + "\n";

            FileUtil.write(getFriendsFile(), content);


        } catch (Exception e) {
            Spark.logger.info("Failed to save friends");
            e.printStackTrace();
        }
    }

}
