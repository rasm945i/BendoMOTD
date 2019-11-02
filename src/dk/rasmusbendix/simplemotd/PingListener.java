package dk.rasmusbendix.simplemotd;

import dk.rasmusbendix.simplemotd.players.SavedPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.awt.image.BufferedImage;
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
        boolean returningUser = player != null;

        Motd motd = getRandomMOTD(!returningUser, getForcePersonalizedMotdIfAvailable(), getForceGuestMessageForGuests());

        e.setMotd(
                applyPlaceholders(
                        motd.getMotdAsString(),
                        e.getNumPlayers(),
                        e.getMaxPlayers(),
                        player // Display N/A if the player hasn't played before
                )
        );


        // Forcing an Icon to a returning user
        if(returningUser && !getIconForReturningUsers().equalsIgnoreCase("none")) {

            setIcon(getIconForReturningUsers(), e);

        }

        // Forcing an Icon to a new user
        else if(!returningUser && !getIconForNewUsers().equalsIgnoreCase("none")) {

            setIcon(getIconForNewUsers(), e);

        }

        // Forcing a random icon
        else if(plugin.isUsingRandomServerIcon()) {

            setIcon(plugin.getServerIconLoader().getRandomIcon(), e);

        }

    }

    private void setIcon(String iconName, ServerListPingEvent e) {

        BufferedImage icon = plugin.getServerIconLoader().getIcon(iconName);
        if(icon == null) {
            plugin.getLogger().warning("Failed to find icon named " + iconName);
            return;
        }

        setIcon(icon, e);

    }

    private void setIcon(BufferedImage icon, ServerListPingEvent e) {

        if(icon == null) {
            plugin.getLogger().warning("Failed to find icon!");
            return;
        }

        try {
            e.setServerIcon(Bukkit.loadServerIcon(icon));
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to set server icon! Icon dimensions: " + icon.getWidth() + "x" + icon.getHeight());
            ex.printStackTrace();
        }
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


    private Motd getRandomMOTD(boolean isGuest, boolean forceWithUsername, boolean forceWithoutUsername) {

        Random random = new Random();
        int value = random.nextInt(motdList.size());
        int startValue = value;
        Motd motd = motdList.get(value);

        if(!isGuest && forceWithUsername) {

            // Loop nr. 1
            while(!motd.isUsingPlayerName()) {
                value++;
                if(value >= motdList.size())
                    value = 0;
                if(value == startValue)
                    break;
                motd = motdList.get(value);
            }

        }

        else if(isGuest || forceWithoutUsername) {

            // Loop nr. 2. My brain cannot figure out how to make this a method :s
            while(motd.isUsingPlayerName()) {
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

    public boolean getForcePersonalizedMotdIfAvailable() {
        return plugin.getConfig().getBoolean("force-personalized-motd-if-available", true);
    }

    public boolean getForceGuestMessageForGuests() {
        return plugin.getConfig().getBoolean("force-guest-message-for-guests", true);
    }

    public String getIconForNewUsers() {
        return plugin.getConfig().getString("new-users-icon", "none");
    }

    public String getIconForReturningUsers() {
        return plugin.getConfig().getString("returning-users-icon", "none");
    }

    public String getGuestPlaceholder() {
        return plugin.getConfig().getString("guest-placeholder", "Guest");
    }


    private String applyPlaceholders(String s, int online, int max, SavedPlayer player) {
        OfflinePlayer op;
        String playerName;

        if(player != null) {
            op = Bukkit.getOfflinePlayer(player.getUuid());
            playerName = op.getName();
        } else {
            op = null;
            playerName = getGuestPlaceholder();
        }

        if(plugin.isUsingPlaceholderAPI()) {
            s = PlaceholderAPI.setPlaceholders(op, s);
        }

        return s
                .replace("%online%", String.valueOf(online))
                .replace("%max%", String.valueOf(max))
                .replace("%player%", playerName);
    }

}
