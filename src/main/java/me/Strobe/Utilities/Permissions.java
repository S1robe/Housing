package me.Strobe.Utilities;

import me.Strobe.Housing;
import org.bukkit.configuration.file.FileConfiguration;

import static me.Strobe.Utilities.Utilites.colorize;

public enum Permissions{

    housesRent("houses.rent", "houseRentPerm"),
    aptRent("apt.rent", "aptRentPerm"),
    playerLim("houses.add.", "playerAddLimit"),
    houseAdmin("houses.admin", "houseAdmin");


    private String node;
    private String path;

    Permissions(String node, String path){
        this.node = node;
        this.path = path;
    }

    public String getNode(){
        return node;
    }

    public static void loadPerms(FileConfiguration f){
        for(Permissions x : Permissions.values()){
            x.node = f.getString(x.path);
        }
        Housing.console.sendMessage(colorize(Housing.pTag + "Permissions &aLoaded&7!"));
    }
}
