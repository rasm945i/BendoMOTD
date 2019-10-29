package dk.rasmusbendix.simplemotd;

import org.bukkit.ChatColor;

import java.util.List;

public class Motd {

    private List<String> motd;
    private boolean usingPlayerName;

    public Motd(List<String> motd) {
        this.motd = motd;
        this.usingPlayerName = false;

        // Check if any of the variables should get replaced with the players name.
        for(String s : motd) {
            if(s.contains("%player%")) {
                this.usingPlayerName = true;
                break;
            }
        }

    }

    public List<String> getMotd() {
        return motd;
    }

    public boolean isUsingPlayerName() {
        return usingPlayerName;
    }

    public String getMotdAsString() {

        StringBuilder builder = new StringBuilder();
        boolean doNewLine = false;

        for(String s : motd) {

            // Don't make a new line before the first line
            if(doNewLine) {
                builder.append("\n");
            } else {
                doNewLine = true;
            }

            builder.append(ChatColor.translateAlternateColorCodes('&', s));

        }

        return builder.toString();

    }

    @Override
    public String toString() {
        return this.getMotdAsString();
    }

}
