package dk.rasmusbendix.simplemotd;

import dk.rasmusbendix.simplemotd.players.SavedPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PingListener implements Listener {

    private SimpleMotdPlugin plugin;
    private List<Motd> motdList;

    public PingListener(SimpleMotdPlugin plugin) {
        this.plugin = plugin;
        this.motdList = fetchMotds();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }



    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayerManager().addPlayer(e.getPlayer());
    }


    @EventHandler
    public void onPing(ServerListPingEvent e) {
        String host = e.getAddress().getHostAddress().replace(".", "_"); // Dots are used to define new sections in YML
        SavedPlayer player = plugin.getPlayerManager().getPlayer(host);

        Motd motd = getRandomMOTD(player != null);
        e.setMotd(
                applyPlaceholders(
                        motd.getMotdAsString(),
                        e.getNumPlayers(),
                        e.getMaxPlayers(),
                        player == null ? "guest" : player.getUsername() // Display N/A if the player hasn't played before
                )
        );
    }


    private List<Motd> fetchMotds() {

        List<Motd> list = new ArrayList<>();

        // Get each key and the 1-2 lines of text in the config.
        plugin.getConfig().getConfigurationSection("motd.").getKeys(false)
                .forEach(key -> list.add(new Motd(plugin.getConfig().getStringList("motd." + key))));

        return list;

    }


    private Motd getRandomMOTD() {
        return getRandomMOTD(true, false, false);
    }

    public Motd getRandomMOTD(boolean canUsePlayername) {
        return getRandomMOTD(canUsePlayername, false, false);
    }


    private Motd getRandomMOTD(boolean canUsePlayername, boolean forceWithUsername, boolean forceWithoutUsername) {

        Random random = new Random();
        int value = random.nextInt(motdList.size());
        int startValue = value;
        Motd motd = motdList.get(value);

        // TODO Refactor into a method instead of 2 identical loops
        if(forceWithoutUsername || (motd.isUsingPlayerName() && !canUsePlayername)) {

            // Get next available MOTD that doesn't use player name
            while(motd.isUsingPlayerName()) {
                value++;
                if(value >= motdList.size())
                    value = 0;

                if(value == startValue)
                    break;
                motd = motdList.get(value);
            }

        } else if(forceWithUsername && !motd.isUsingPlayerName()) {

            while(!motd.isUsingPlayerName()) {
                value++;
                if(value >= motdList.size())
                    value = 0;

                if(value == startValue)
                    break;
                motd = motdList.get(value);
            }

        }

        return motd;

    }


    private String applyPlaceholders(String s, int online, int max, String player) {
        return s
                .replace("%online%", String.valueOf(online))
                .replace("%max%", String.valueOf(max))
                .replace("%player%", player);
    }

}
