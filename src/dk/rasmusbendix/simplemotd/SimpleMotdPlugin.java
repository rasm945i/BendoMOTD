package dk.rasmusbendix.simplemotd;

import dk.rasmusbendix.simplemotd.players.PlayerManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SimpleMotdPlugin extends JavaPlugin {

    // Compiled with Spigot 1.8.8

    private File configFile;
    private YamlConfiguration playerFile;

    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        getLogger().info("Prepare for epic MOTDs by rasm945i from https://en.rasmusbendix.dk");
        saveDefaultConfig();
        createPlayerFile();

        playerManager = new PlayerManager(this);

        new PingListener(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("No more of those nice MOTDs :(");
        playerManager.writeValues();
        savePlayerFile();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public YamlConfiguration getPlayerFile() {
        return playerFile;
    }

    private void savePlayerFile() {
        try {
            playerFile.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPlayerFile() {
        configFile = new File(getDataFolder(), "players.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("players.yml", false);
        }

        playerFile = new YamlConfiguration();
        try {
            playerFile.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
