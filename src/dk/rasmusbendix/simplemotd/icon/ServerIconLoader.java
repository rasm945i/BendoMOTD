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
    private ArrayList<BufferedImage> base64Icons;
    private Random random;

    public ServerIconLoader(SimpleMotdPlugin plugin) {
        this.plugin      = plugin;
        this.base64Icons = new ArrayList<>();
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
                base64Icons.add(ImageIO.read(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public BufferedImage getRandomIcon() {
        if(base64Icons.isEmpty())
            return null;
        return base64Icons.get(random.nextInt(base64Icons.size()));
    }

}
