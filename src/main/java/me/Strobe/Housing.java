package me.Strobe;

import com.mewin.WGRegionEvents.WGRegionEventsPlugin;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.EntryFlag;
import me.Strobe.Files.CustomFile;
import me.Strobe.Listeners.HouseActions;
import me.Strobe.Utilities.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.Strobe.Utilities.Utilites.colorize;

public final class Housing extends JavaPlugin
{
    private static Housing mainThread;


    public static ConsoleCommandSender console;
    public static String pTag;

    private Economy econ;
    private WorldGuardPlugin WG;
    private WorldEditPlugin WE;
    private FileConfiguration houseConfig, pConfig;
    private int hLim;
    private boolean plugBuycraft;
    private CustomFile hFile;

    public static HashMap<String, House> houseMap;
    public static BooleanFlag CUSTOM_HOUSING_FLAG;

    @Override
    public void onLoad(){
        FlagRegistry registry = WorldGuardPlugin.inst().getFlagRegistry();
        try{
            CUSTOM_HOUSING_FLAG = new BooleanFlag("isHouse");

            registry.register(CUSTOM_HOUSING_FLAG);
        }
        catch(FlagConflictException f){
            Flag<?> existing = registry.get("isHouse");
            if(existing instanceof BooleanFlag){
                CUSTOM_HOUSING_FLAG = (BooleanFlag) existing;
                console.sendMessage(Housing.pTag + "&7Custom flag for houses: &b\"isHouse\"&7 registered!");
            }
            else{
                console.sendMessage(Housing.pTag + "&7Custom flag for houses &cfailed &7to register, another plugin might already have the tag &b\"isHouse\"&7 registered!");
            }
        }
    }

    @Override
    public void onEnable(){
        // Plugin startup logic
        mainThread = this;
        houseMap = new HashMap<>();
        console = getServer().getConsoleSender();
        loadFiles();
        loadECO();
        loadWorldEdit();
        loadWorldGuard();
        loadListeners();
        loadCommands();
    }

    @Override
    public void onDisable(){
        // Plugin shutdown logic
        houseMap.values().forEach(House::saveHouseFile);
        hFile.saveCustomConfig();
        saveConfig();
        mainThread = this;
        Utilites.reloadHouses();
    }

    //--------------------------------Loads------------------------------//

    private void loadECO() {
        econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        if(econ == null){
            console.sendMessage(colorize("&aEconomy &7plugin was not found, disabling..."));
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else{
            console.sendMessage(colorize("&aEconomy loaded as " + econ.getName() + "!"));
        }
    }

    private void loadWorldGuard(){
        WG = WorldGuardPlugin.inst();
        if(WG == null){
            console.sendMessage(colorize("&cWorld Guard &7plugin was not found, disabling..."));
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else{
            console.sendMessage(colorize("&cWorld Guard loaded as " + WG.getName() + "!"));
        }


    }

    private void loadWorldEdit(){
        WE = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if(WE == null){
            console.sendMessage(colorize("&cWorld Edit &7plugin was not found, disabling..."));
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else{
            console.sendMessage(colorize("&cWorld Edit loaded as " + WE.getName() + "!"));
        }
    }

    private void loadFiles(){
        saveDefaultConfig();
        pConfig = getConfig();
        saveConfig();
        Messages.loadMessages(pConfig);
        Permissions.loadPerms(pConfig);
        pTag = colorize(pConfig.getString("Plugin Tag"));
        plugBuycraft = pConfig.getBoolean("plugBuycraft");
        hLim = pConfig.getInt("houseLim");
        hFile = new CustomFile("houses");
        hFile.saveDefaultConfig();
        hFile.saveCustomConfig();
        houseConfig = hFile.getCustomConfig();
        Utilites.loadHouses();
    }

    private void loadListeners(){
        Bukkit.getPluginManager().registerEvents(new HouseActions(), this);
    }

    private void loadCommands(){
        getCommand("home").setExecutor(new HouseCommands());
    }

    //==================================Utility Methods====================================//

    public static Housing getMainThread(){
        return mainThread;
    }

    public FileConfiguration getpConfig(){
        return pConfig;
    }

    public FileConfiguration getHouseConfig(){
        return houseConfig;
    }

    public CustomFile gethFile(){
        return hFile;
    }

    public WorldGuardPlugin getWG(){
        return WG;
    }

    public WorldEditPlugin getWE() {return WE;}

    public Economy getEcon(){
        return econ;
    }

    public int gethLim(){
        return hLim;
    }

    public void sethLim(int hLim){
        this.hLim = hLim;
    }

    public boolean isPlugBuycraft(){
        return plugBuycraft;
    }

    public void setPlugBuycraft(boolean plugBuycraft){
        this.plugBuycraft = plugBuycraft;
    }
}
