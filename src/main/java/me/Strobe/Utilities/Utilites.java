package me.Strobe.Utilities;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Strobe.Files.CustomFile;
import me.Strobe.Housing;
import org.apache.commons.io.IOUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.Strobe.Housing.houseMap;
import static me.Strobe.Housing.pTag;

public class Utilites{

    private static final CustomFile H_FILE = Housing.getMainThread().gethFile();
    private static final FileConfiguration H_CONFIG = Housing.getMainThread().getpConfig();

    public static String colorize(String str) {
        if(str == null) return "";
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    // Formats large doubles into x,xxx.xx
    public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(d);
    }

    public static Location getLocfromString(String loc){
        String[] splitLoc = loc.split("-");
        String w = splitLoc[0];
        int x = Integer.parseInt(splitLoc[1]);
        int y = Integer.parseInt(splitLoc[2]);
        int z = Integer.parseInt(splitLoc[3]);
        World wor = Bukkit.getWorld(w);
        return new Location(wor , x ,y ,z);
    }

    public static String getStringFromLoc(Location loc){
        return loc.getWorld().getName() + "-" + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
    }

    public static House getHouseFromRegionName(String reg){
        for(String region : Housing.houseMap.keySet()){
            if(region.equalsIgnoreCase(reg))
                return Housing.houseMap.get(region);
        }
        return null;
    }

    public static House getHouseFromLocation(Location signLoc){
        for(House x : Housing.houseMap.values()){
            if(x.getSignLoc().equals(signLoc))
                return x;
        }
        return null;
    }

    public static House getHouseFromRegion(ProtectedRegion region){
        for(House h : Housing.houseMap.values()){
            if(region.equals(h.getHousingRegion()))
                return h;
        }
        return null;
    }

    public static int getNumHousesPlayerOwns(String name){
        int i = 0;
        String result = getUUID(name);
        if(!result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("invalid name")){
            for(House x : Housing.houseMap.values()){
                if(x.getOwnerUUID().equals(UUID.fromString(result))) i++;
            }
        }
        return i;
    }

    public static int getNumHousesPlayerOwns(UUID uuid){
        int i = 0;
        for(House x : Housing.houseMap.values()){
            if(x.getOwnerUUID().equals(uuid))
                i++;
        }
        return i;
    }

    public static ArrayList<House> getHousesPlayerIsAddedTo(String name){
        ArrayList<House> h = new ArrayList<>();
        String result = getUUID(name);
        if(!result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("invalid name")){
            for(House x : Housing.houseMap.values()){
                if(x.getMemberUUIDs().contains(UUID.fromString(result))) h.add(x);
            }
        }
        return h;
    }

    public static ArrayList<House> getHousesPlayerIsAddedTo(UUID uuid){
        ArrayList<House> h = new ArrayList<>();
        for(House x : Housing.houseMap.values()){
            if(x.getMemberUUIDs().contains(uuid))
                h.add(x);
        }
        return h;
    }

    public static ArrayList<House> getHousesPlayerOwns(String name){
        ArrayList<House> h = new ArrayList<>();
        String result = getUUID(name);
        if(!result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("invalid name")){
            for(House x : Housing.houseMap.values()){
                if(x.getOwnerUUID().equals(UUID.fromString(result)))
                    h.add(x);
            }
        }
        return h;
    }

    public static ArrayList<House> getHousesPlayerOwns(UUID uuid){
        ArrayList<House> h = new ArrayList<>();
        for(House x : Housing.houseMap.values()){
            if(x.getOwnerUUID().equals(uuid)) h.add(x);
        }
        return h;
    }

    public static boolean doesPlayerOwnHouse(UUID uuid, String region){
        House h = getHouseFromRegionName(region);
        if(h==null)
            return false;
        return h.getOwnerUUID().equals(uuid);
    }

    public static boolean doesPlayerOwnHouse(String name, String region){
        House h = getHouseFromRegionName(region);
        if(h==null)
            return false;
        String result = getUUID(name);
        if(!result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("invalid name")){
            return h.getOwnerUUID().equals(UUID.fromString(result));
        }
        return false;
    }

    public static boolean doesPlayerOwnHouse(UUID uuid, House house){
        return house.getOwnerUUID().equals(uuid);
    }

    public static boolean doesPlayerOwnHouse(String name, House house){
        String result = getUUID(name);
        if(!result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("invalid name")){
            return house.getOwnerUUID().equals(UUID.fromString(result));
        }
        return false;
    }

    public static boolean isPlayerAddedToHouse(UUID uuid, String region){
        House h = getHouseFromRegionName(region);
        if(h==null)
            return false;
        return h.getMemberUUIDs().contains(uuid);
    }

    public static boolean isPlayerAddedToHouse(String name, String region){
        House h = getHouseFromRegionName(region);
        if(h==null)
            return false;
        String result = getUUID(name);
        if(!result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("invalid name")){
            return h.getMemberUUIDs().contains(UUID.fromString(result));
        }
        return false;
    }

    public static boolean isPlayerAddedToHouse(UUID uuid, House house){
        return house.getMemberUUIDs().contains(uuid);
    }

    public static boolean isPlayerAddedToHouse(String name, House house){
        String result = getUUID(name);
        if(!result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("invalid name")){
            return house.getMemberUUIDs().contains(UUID.fromString(result));
        }
        return false;
    }


    public static ItemStack createSkull(UUID uuid, String name, String... lore) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1 , (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(Bukkit.getOfflinePlayer(uuid).getName());
        ArrayList<String> list = new ArrayList<String>();
        String status;
        meta.setDisplayName(colorize(name));
        if(Bukkit.getPlayer(uuid) != null) {
            status = "&a&lONLINE";
        } else {
            status = "&c&lOFFLINE";
        }
        for(String l : lore)
            list.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', l.replace("%status%", status)));
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isSign(Block b){
        Material m = b.getType();
        return m.equals(Material.SIGN) || m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN);
    }

    // Plays a spigot effect at the the players given location
    public static void playBlockEffect(Player p, Location loc, int blockID) {
        p.getWorld().spigot().playEffect(loc, Effect.STEP_SOUND, blockID, 0, (float) 0, (float) 0, (float) 0, (float) 0.01, 5, 10);
    }

    public static void reloadHouses(){
        Housing.houseMap.values().forEach(House::saveHouseFile);
        H_FILE.saveCustomConfig();
        Housing.houseMap.clear();
        loadHouses();

    }

    public static void loadHouses(){
        if(H_CONFIG.getKeys(false).size() != 0){
            for(String region : H_CONFIG.getKeys(false)){
                Location dLoc = Utilites.getLocfromString(H_CONFIG.getString(region+".Location"));
                Location uLoc = Utilites.getLocfromString(H_CONFIG.getString(region+".User-Set-Location"));
                double price = H_CONFIG.getInt(region+".Price");
                long startTime = H_CONFIG.getLong(region+".Start-Time-MS");
                String Owner = H_CONFIG.getString(region+".Owner");
                int defaultTime = H_CONFIG.getInt(region+".PurchaseTime-Days");
                int timeLeft = H_CONFIG.getInt(region+".TimeLeft-Days");

                ArrayList<UUID> membs = new ArrayList<>();
                ArrayList<UUID> blcklist = new ArrayList<>();
                H_CONFIG.getStringList(region+".Members").forEach(uuid -> {
                    membs.add(UUID.fromString(uuid));
                });
                H_CONFIG.getStringList(region+"Blacklist").forEach(uuid -> {
                    blcklist.add(UUID.fromString(uuid));
                });
                House h = new House(dLoc, uLoc, region, UUID.fromString(Owner), price, timeLeft,startTime,defaultTime, membs, blcklist);
                Housing.houseMap.put(region, h);
            }
            Housing.console.sendMessage(colorize(pTag + "&chousing.yml file loaded! List houses with: " /*Insert command*/));
        }
        else
            Housing.console.sendMessage(colorize(pTag + "&chousing.yml &7is empty, no houses have been made: &eSkipping"));

        Housing.houseMap.values().forEach(House::saveHouseFile);
        H_FILE.saveCustomConfig();
    }

    // creates an inventory of size rows * 9, null owner, and no title,
    public static Inventory createInventory(int rows) {
        return Bukkit.createInventory(null, rows * 9);
    }

    // returns a custom inventory with default size 27 ( 3 rows ) with name 'inventory name'
    public static Inventory createInventory(String inventoryName) {
        return Bukkit.createInventory(null, 27, colorize(inventoryName));
    }

    // Creates an inventory with name, and rows 'rows'
    public static Inventory createInventory(String inventoryName, int rows) {
        return Bukkit.createInventory(null, rows * 9, colorize(inventoryName));
    }

    // Fills all slots of a premade inventory.
    // Best to call first then overwrite slots. with custom items.
    public static void fill(Inventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, itemStack);
        }
    }

    // creates a new ItemStack of type 'material': must be of Material.*
    public static ItemStack createItem(Material material) {
        return new ItemStack(material);
    }

    // returns a quantity of itemstack 'm',
    // EX createItem( new ItemStack(Material.STONE), 3) would set the new itemstack's
    // amount to 3 and would return that reference to the called position.
    public static ItemStack createItem(ItemStack m, int amount) {
        m.setAmount(amount);
        ItemMeta meta = m.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', m.getItemMeta().getDisplayName()));
        ArrayList<String> list = new ArrayList<String>();
        for (String l : m.getItemMeta().getLore())
            list.add(ChatColor.translateAlternateColorCodes('&', l));
        meta.setLore(list);
        m.setItemMeta(meta);
        return m;
    }

    // essentially does the same thing as abouve but creates a new itemstack in the process
    public static ItemStack createItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    // Returns a new Named Itemstack.
    public static ItemStack createItem(Material material, String itemName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // More specific way to spawn in items, uses the dataValue
    // Ex Redrose:4 <data value, also called damage value, is the type parameter specification
    public static ItemStack createItem(Material material, int amount, int dataValue) {
        return new ItemStack(material, amount, (short) dataValue);
    }

    // Returns new Itemstack that has custom material, name, and lore
    public static ItemStack createItem(Material material, String itemName, List<String> itemLore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<String> newLore = new ArrayList<>();
        for (String str : itemLore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // Returns new itemstack with material, amount, name, and lore custom
    public static ItemStack createItem(Material material, int amount, String itemName, List<String> itemLore) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<String> newLore = new ArrayList<>();
        for (String str : itemLore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // Returns new itemstack with material, amount, data, and name custom
    public static ItemStack createItem(Material material, int amount, int dataValue, String itemName) {
        ItemStack itemStack = new ItemStack(material, amount, (short) dataValue);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // creates a new itemstack very specific allows for amount, type, data
    // enchanted, name and lore to be changed
    public static ItemStack createItem(Material m, int amount, byte data, boolean enchanted, String name, String... lore){
        ItemStack item = new ItemStack(m, amount, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(name));
        if(enchanted) {
            meta.addEnchant(Enchantment.getByName("DURABILITY"), 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        ArrayList<String> list = new ArrayList<String>();
        for(String l : lore)
            list.add(colorize(l));
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }

    // Returns a new Itemstack with material, amount, data, item, and lore custom
    public static ItemStack createItem(Material material, int amount, int dataValue, String itemName, List<String> itemLore) {
        ItemStack itemStack = new ItemStack(material, amount, (short) dataValue);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<String> newLore = new ArrayList<>();
        for (String str : itemLore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setLore(ItemStack itemStack, List<String> newLore) {
        ArrayList<String> itemLore = new ArrayList<>();
        for (String str : newLore) {
            itemLore.add(colorize(str));
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack appendToLore(ItemStack itemStack, String... addedLines){
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemStack.getItemMeta().getLore();
        for (String str : addedLines) {
            itemLore.add(colorize(str));
        }
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setLoreLine(ItemStack itemStack, int line, String newLine){
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemStack.getItemMeta().getLore();
        itemLore.set(line, colorize(newLine));
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack insertLoreLine(ItemStack itemStack, int line, String insertedLine){
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemStack.getItemMeta().getLore();
        itemLore.add(line, colorize(insertedLine));
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack removeLoreLine(ItemStack itemStack, int line){
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemStack.getItemMeta().getLore();
        itemLore.remove(line);
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack removeLoreLine(ItemStack itemStack, String exactText){
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemStack.getItemMeta().getLore();
        itemLore.remove(colorize(exactText));
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    //
    public static ItemStack createItem(Material m, int amount, byte data, String name, String... lore){
        ItemStack item = new ItemStack(m, amount, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        ArrayList<String> list = new ArrayList<>();
        for(String l : lore)
            list.add(ChatColor.translateAlternateColorCodes('&', l));
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }

    //returns the skull of other people
    public static ItemStack getSkullOf(String otherOwners) {
        ItemStack memberSkull = Utilites.createItem(Material.SKULL_ITEM, 1, 3,
                "&6" + Bukkit.getOfflinePlayer(UUID.fromString(otherOwners)).getPlayer().getDisplayName());
        SkullMeta memberMeta = (SkullMeta) memberSkull.getItemMeta();
        memberMeta.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(otherOwners)).getName());
        memberSkull.setItemMeta(memberMeta);
        return memberSkull;
    }

    public static ItemStack getSkullOf(UUID otherOwners) {
        ItemStack memberSkull = Utilites.createItem(Material.SKULL_ITEM, 1, 3,
                "&6" + Bukkit.getOfflinePlayer(otherOwners).getPlayer().getDisplayName());
        SkullMeta memberMeta = (SkullMeta) memberSkull.getItemMeta();
        memberMeta.setOwner(Bukkit.getOfflinePlayer(otherOwners).getName());
        memberSkull.setItemMeta(memberMeta);
        return memberSkull;
    }


    // overwrites the middle column of each row of a given inventory.
    public static void setMiddle(Inventory inventory, ItemStack itemStack){
        for(int i = 4; i < inventory.getSize(); i = i + 9){
            inventory.setItem(i, itemStack);
        }
    }

    public static void setLeft(Inventory inv, ItemStack filler, ArrayList<ItemStack> items){
        short loops = (short) (inv.getSize()/9);
        for(int l = 0; l < loops; l++){
            for(int i = 0, s = 9 * l; s < 9*l + 4; s++, i++ ){
                if(i >= items.size())
                    inv.setItem(i, filler);
                else
                    inv.setItem(i, items.get(i));
            }
        }
    }

    public static void setRight(Inventory inv, ItemStack filler, ArrayList<ItemStack> items){
        short loops = (short) (inv.getSize()/9);
        for(int l = 0; l < loops; l++){
            for(int i = 0, s = 5+ 9 * l; s < 9*l + 9; s++, i++ ){
                if(i >= items.size())
                    inv.setItem(i, filler);
                else
                    inv.setItem(i, items.get(i));
            }
        }
    }

    // Returns the UUID string from Mojangs UUID servers to help fight fake names
    public static String getUUID(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
        try {
            String UUIDJson = IOUtils.toString(new URL(url));
            if(UUIDJson.isEmpty()) return "invalid name";
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            String result = UUIDObject.get("id").toString();
            StringBuilder sb = new StringBuilder();
            sb.append(result, 0, 7).append('-')
                    .append(result, 7, 12).append('-')
                    .append(result, 12, 16).append('-')
                    .append(result, 16, 20).append('-')
                    .append(result, 20, result.length());
            return sb.toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return "error";
    }

}