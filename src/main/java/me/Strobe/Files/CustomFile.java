/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  org.bukkit.configuration.Configuration
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package me.Strobe.Files;

import me.Strobe.Housing;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class CustomFile
{
    private final String fileName;
    private final Housing instance = Housing.getMainThread();
    private File customConfigFile;
    private FileConfiguration customConfig;

    public CustomFile(String fileName) {
        this.fileName = fileName;
        this.customConfigFile = new File(this.instance.getDataFolder(), this.fileName + ".yml");
        this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);
    }

    public void reloadCustomConfig() {
        if (this.customConfigFile == null) {
            this.customConfigFile = new File(this.instance.getDataFolder(), this.fileName + ".yml");
        }
        this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);
        InputStreamReader defConfigStream = new InputStreamReader(this.instance.getResource(this.fileName + ".yml"));
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        this.customConfig.setDefaults(defConfig);
    }

    public FileConfiguration getCustomConfig() {
        if (this.customConfig == null) {
            this.reloadCustomConfig();
        }
        return this.customConfig;
    }

    public void saveCustomConfig() {
        if (this.customConfig == null || this.customConfigFile == null) {
            return;
        }
        try {
            this.getCustomConfig().save(this.customConfigFile);
        } catch (IOException ex) {
            this.instance.getLogger().log(Level.SEVERE, "Could not save config to " + this.customConfigFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (this.customConfigFile == null) {
            this.customConfigFile = new File(this.instance.getDataFolder(), this.fileName + ".yml");
        }
        if (!this.customConfigFile.exists()) {
            this.instance.saveResource(this.fileName + ".yml", false);
        }
    }

    String getFileName() {
        return this.fileName;
    }
}

