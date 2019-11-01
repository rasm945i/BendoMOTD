package dk.rasmusbendix.simplemotd.icon;

import dk.rasmusbendix.simplemotd.SimpleMotdPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ServerIconLoader {

    private SimpleMotdPlugin plugin;
    private ArrayList<BufferedImage> icons;
    private Random random;

    public ServerIconLoader(SimpleMotdPlugin plugin) {
        this.plugin      = plugin;
        this.icons = new ArrayList<>();
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
                icons.add(ImageIO.read(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public BufferedImage getRandomIcon() {
        if(icons.isEmpty())
            return null;
        return icons.get(random.nextInt(icons.size()));
    }

}
