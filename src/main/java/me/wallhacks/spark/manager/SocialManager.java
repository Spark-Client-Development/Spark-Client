package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.socials.playerLists.PlayerListItem;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.SessionUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocialManager {

    private ArrayList<SocialEntry> friends = new ArrayList<>();

    public void clearFriends() {
        friends.clear();
    }
    public List<SocialEntry> getFriends() {
        return friends;
    }
    public List<String> getFriendsNames() {
        ArrayList<String> l = new ArrayList<>();
        for (SocialEntry s : getFriends())
            l.add(s.getName());
        return l;
    }


    public void addFriend(SocialEntry entry){
        if(!friends.contains(entry))
            friends.add(entry);
    }



    public void removeFriend(UUID uuid){
        if(getFriends().contains(uuid))
            getFriends().remove(uuid);
    }
    public void removeFriend(String name){
        if(getFriends().contains(name))
            getFriends().remove(name);
    }
    public void removeFriend(SocialEntry name){
        if(getFriends().contains(name))
            getFriends().remove(name);
    }





    public void addFriend(String name){
        UUID id = SessionUtils.fromString(name);
        if(id != null)
            addFriend(new UUIDSocial(id));
        else
            addFriend(new INGSocial(name));
    }



    public boolean isFriend(SocialEntry entry) {
        return getFriends().contains(entry);
    }
    public boolean isFriend(String name) {
        return getFriends().contains(name);
    }

    public boolean isFriend(EntityPlayer player) {
        for (SocialEntry s : getFriends()) {
            if (s.isEntityPlayer(player)) return true;
        }
        return false;
    }



    String getFriendsFile() {
        String base = Spark.ParentPath.getAbsolutePath() + ""+System.getProperty("file.separator")+"socials"+System.getProperty("file.separator")+"";
        return base + "friends.sex";
    }


    public void LoadFriends() {
        try {
            String s = FileUtil.read(getFriendsFile());
            if (s != null) {
                String[] List = s.split("\n");
                Spark.socialManager.clearFriends();
                for (String var : List) {
                    String[] parts = var.split("/");
                    if (parts.length == 2) {
                        if(parts[0] == "ING")
                            Spark.socialManager.addFriend(parts[1]);
                        else
                            Spark.socialManager.addFriend(new UUIDSocial(UUID.fromString(parts[1])));


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
            for (SocialEntry e : Spark.socialManager.getFriends())
            {
                if(e instanceof INGSocial)
                    content = content + "ING/" + e.getName() + "\n";
                else
                    content = content + "UUID/" + e.getUUID() + "\n";
            }

            FileUtil.write(getFriendsFile(), content);


        } catch (Exception e) {
            Spark.logger.info("Failed to save friends");
            e.printStackTrace();
        }
    }






    public static abstract class SocialEntry<T> {
        public T getIdentifier() {
            return identifier;
        }

        public SocialEntry(T identifier)
        {
            this.identifier = identifier;
        }

        T identifier;

        public abstract boolean isEntityPlayer(EntityPlayer player);
        public abstract String getName();
        public abstract UUID getUUID();

        @Override
        public boolean equals(Object o)
        {
            if (o == this)
                return true;
            if ((o instanceof SocialEntry))
            {
                SocialEntry other = (SocialEntry)o;
                if(other.getName() == null)
                    return false;
                return other.getName().equalsIgnoreCase(getName());
            }
            if(o instanceof String)
                return ((String)o).equalsIgnoreCase(getName());
            if(o instanceof UUID)
                return ((UUID)o).equals(getUUID());


            return false;
        }
        @Override
        public int hashCode() {
            return getName().hashCode();
        }
    }
    public static class INGSocial extends SocialEntry<String> {

        public INGSocial(String identifier) {
            super(identifier.toLowerCase());
        }

        @Override
        public boolean isEntityPlayer(EntityPlayer player) {
            if(player == null || player.getGameProfile() == null)
                return false;
            return player.getGameProfile().getName().equalsIgnoreCase(getIdentifier());
        }

        @Override
        public String getName() {
            return getIdentifier();
        }

        @Override
        public UUID getUUID() {
            return SessionUtils.getid(getIdentifier());
        }
    }
    public static class UUIDSocial extends SocialEntry<UUID> {

        public UUIDSocial(UUID identifier) {
            super(identifier);
        }

        @Override
        public boolean isEntityPlayer(EntityPlayer player) {
            if(player == null || player.getGameProfile() == null)
                return false;
            return player.getGameProfile().getId().equals(getIdentifier());
        }

        @Override
        public String getName() {
            return SessionUtils.getname(getIdentifier());
        }

        @Override
        public UUID getUUID() {
            return getIdentifier();
        }
    }


    public static SocialEntry getSocialFromNetworkPlayerInfo(NetworkPlayerInfo info)
    {
        if(info.getGameProfile() != null)
        {
            if(info.getGameProfile().getId() != null && SessionUtils.getname(info.getGameProfile().getId()) != null)
                return new UUIDSocial(info.getGameProfile().getId());
            return new INGSocial(info.getGameProfile().getName());
        }
        return null;

    }

}
