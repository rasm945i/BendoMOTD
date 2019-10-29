package dk.rasmusbendix.simplemotd.players;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class SavedPlayer {

    private String address;
    private UUID uuid;

    public SavedPlayer(String address, UUID uuid) {
        this.address  = address.replace(".", "_");
        this.uuid     = uuid;
    }

    public String getUsername() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if(offlinePlayer == null)
            return "guest";

        return offlinePlayer.getName();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getAddress() {
        return address;
    }

}
