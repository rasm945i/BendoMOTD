package dk.rasmusbendix.simplemotd.icon;

import dk.rasmusbendix.simplemotd.SimpleMotdPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ServerIconLoader {

    private SimpleMotdPlugin plugin;
    private HashMap<String, BufferedImage> icons;
    private Random random;

    public ServerIconLoader(SimpleMotdPlugin plugin) {
        this.plugin      = plugin;
        this.icons       = new HashMap<>();
        this.random      = new Random();
        loadImages();
    }

    public void loadImages() {

        // Load images from data folder

        File imageFolder = new File(plugin.getDataFolder() + "" + File.separatorChar + "icons");
        if(!imageFolder.exists())
            imageFolder.mkdirs();


        File[] files = imageFolder.listFiles();
        if(files == null) {
            plugin.getLogger().warning("Failed to list files in 'icons' folder. It may be empty!");
            return;
        }


        for(File file : files) {
            try {
                icons.put(file.getName(), ImageIO.read(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(String str : icons.keySet()) {
            System.out.println("Filename : " + str);
        }

    }

    public BufferedImage getIcon(String name) {
        return icons.getOrDefault(name, null);
    }

    public BufferedImage getRandomIcon() {
        if(icons.isEmpty())
            return null;

        List<String> keys = new ArrayList<>(icons.keySet());
        return icons.get(keys.get(random.nextInt(keys.size())));
    }

}
