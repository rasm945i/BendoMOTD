package dk.rasmusbendix.simplemotd.players;

import dk.rasmusbendix.simplemotd.SimpleMotdPlugin;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class PlayerManager {

    private HashSet<SavedPlayer> players;
    private SimpleMotdPlugin plugin;

    public PlayerManager(SimpleMotdPlugin plugin) {
        this.plugin = plugin;
        players = new HashSet<>();
        load();
    }

    private void load() {

        for(String host : plugin.getPlayerFile().getKeys(false)) {
            addPlayer(
                    new SavedPlayer(
                            host,
                            UUID.fromString(plugin.getPlayerFile().getString(host))
                    )
            );
        }

    }

    public void writeValues() {

        for(SavedPlayer player : players) {
            savePlayer(player);
        }

    }

    public SavedPlayer getPlayer(String hostAddress) {
        for(SavedPlayer player : players) {
            if(player.getAddress().contentEquals(hostAddress))
                return player;
        }
        return null; // Address not found
    }

    public void addPlayer(Player player) {
        addPlayer(
            new SavedPlayer(
                player.getAddress().getAddress().getHostAddress(),
                player.getUniqueId()
            )
        );
    }

    public void addPlayer(SavedPlayer player) {
        players.add(player);
    }

    public void savePlayer(SavedPlayer player) {
        plugin.getPlayerFile().set(player.getAddress(), player.getUuid().toString());
    }

}
