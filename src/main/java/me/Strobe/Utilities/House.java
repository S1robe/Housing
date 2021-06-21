package me.Strobe.Utilities;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Strobe.Files.CustomFile;
import me.Strobe.Housing;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.Strobe.Utilities.Utilites.colorize;

public class House{

    private final Housing mainThread = Housing.getMainThread();
    private final CustomFile H_FILE = Housing.getMainThread().gethFile();
    private final FileConfiguration H_CONFIG = H_FILE.getCustomConfig();

    private Location signLoc;
    private Sign sign;
    private ProtectedRegion housingRegion;
    private Location spawnLoc;
    private String reigonName;
    private UUID ownerUUID;
    private double price;
    private int timeLeftInDays;
    private ArrayList<UUID> memberUUIDs;
    private long startTimeInMS;
    private int startingDays;
    private final ArrayList<Player> onlineMembers = new ArrayList<>();
    private ArrayList<UUID> blacklistedPlayers = new ArrayList<>();

    public House(Location sLoc, ProtectedRegion pr, String rName, double price, int startDays){
        this.signLoc = sLoc;
        if(sLoc !=null){
            this.sign = (Sign) sLoc.getBlock().getState();
        }
        this.housingRegion = pr;
        this.spawnLoc = sLoc;
        this.reigonName = rName;
        this.price = price;
        this.startingDays = startDays;
        createHouseSection();
    }

    public House(Location SignLoc, Location SpawnLoc, String regName, UUID ownerUUID, double price, int timeinDays, long StartMS, int startDays, ArrayList<UUID> members, ArrayList<UUID> blacklist){
        this.signLoc = SignLoc;
        if(SignLoc != null){
            this.sign = (Sign) SignLoc.getBlock().getState();
            ProtectedRegion pr = mainThread.getWG().getRegionManager(SignLoc.getWorld()).getRegion(regName);
            if(pr != null){
                this.housingRegion = pr;
            }
        }

        this.spawnLoc = SpawnLoc;
        this.reigonName = regName;
        this.ownerUUID = ownerUUID;
        this.price = price;
        this.timeLeftInDays = timeinDays;
        this.memberUUIDs = members;
        this.blacklistedPlayers = blacklist;
        this.startTimeInMS = StartMS;
        this.startingDays = startDays;
    }

    public Location getSignLoc(){
        return signLoc;
    }

    public void setSignLoc(Location signLoc){
        this.signLoc = signLoc;
    }

    public Sign getSign(){
        return sign;
    }

    public Location getSpawnLoc(){
        return spawnLoc;
    }

    public void setSpawnLoc(Location spawnLoc){
        this.spawnLoc = spawnLoc;
    }

    public String getReigonName(){
        return reigonName;
    }

    public void setReigonName(String reigonName){
        this.reigonName = reigonName;
    }

    public UUID getOwnerUUID(){
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID){
        this.ownerUUID = ownerUUID;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public int getTimeLeftInDays(){
        return timeLeftInDays;
    }

    public void setTimeLeftInDays(int timeLeftInDays){
        this.timeLeftInDays = timeLeftInDays;
    }

    public ArrayList<UUID> getMemberUUIDs(){
        return memberUUIDs;
    }

    public void setMemberUUIDs(ArrayList<UUID> memberUUIDs){
        this.memberUUIDs = memberUUIDs;
    }

    public long getStartTimeInMS(){
        return startTimeInMS;
    }

    public void setStartTimeInMS(long startTimeInMS){
        this.startTimeInMS = startTimeInMS;
    }

    public int getStartingDays(){
        return startingDays;
    }

    public void setStartingDays(int startingDays){
        this.startingDays = startingDays;
    }

    public ArrayList<Player> getOnlineMembers(){
        return this.onlineMembers;
    }

    public ProtectedRegion getHousingRegion(){
        return housingRegion;
    }

    public ArrayList<UUID> getBlacklistedPlayers(){
        return blacklistedPlayers;
    }

    public String getTimeLeftFullFormatted(){
        long remainingTime = this.getTimeLeftInDays() * 86400000 + this.startTimeInMS - System.currentTimeMillis();
        int days = (int) TimeUnit.MILLISECONDS.toDays(remainingTime);
        long remainingTimeMS1 = remainingTime - TimeUnit.DAYS.toMillis(days); // days in ms
        int Hours = (int) TimeUnit.MILLISECONDS.toHours(remainingTimeMS1); // num hours remaining
        long remainingTimeMS2 = remainingTimeMS1 - TimeUnit.HOURS.toMillis(Hours); // hours in ms
        int Minutes = (int) TimeUnit.MILLISECONDS.toMinutes(remainingTimeMS2); // num minutes remaining
        long remainingTimeMS3 = remainingTimeMS2 - TimeUnit.MINUTES.toMillis(Minutes); // time remaining in ms
        int Seconds = (int) TimeUnit.MILLISECONDS.toSeconds(remainingTimeMS3);


        StringBuilder sb = new StringBuilder();
        if (days > 1) {
            sb.append("&a").append(days).append(" &7Days, ");
        }
        else if (days == 1) {
            sb.append("&a").append(days).append(" &7Day, ");
        }
        if(Hours > 1){
            sb.append("&a").append(Hours).append(" &7Hours, ");
        }
        else if(Hours == 1){
            sb.append("&a").append(Hours).append(" &7Hour, ");
        }
        if(Minutes > 1){
            sb.append("&a").append(Minutes).append(" &7Minutes, ");
        }
        else if(Minutes == 1){
            sb.append("&a").append(Minutes).append(" &7Minute, ");
        }
        if(Seconds > 1){
            sb.append("&a").append(Seconds).append(" &7Seconds ");
        }
        else if(Seconds == 1){
            sb.append("&a").append(Seconds).append(" &7Second ");
        }
        return  sb.toString().trim();
    }

    public String getTimeLeftDaysFormatted(){
        if(this.timeLeftInDays > 1){
            return this.timeLeftInDays + "+ Days";
        }
        else
            return this.timeLeftInDays + " Day";
    }

    public ItemStack houseItem(){
        ArrayList<OfflinePlayer> mems = new ArrayList<>();
        this.memberUUIDs.forEach(m -> {mems.add(Bukkit.getOfflinePlayer(m));});
        ItemStack houseItem =  Utilites.createItem(Material.TRIPWIRE_HOOK, 1, (byte) 0, true, this.reigonName,
                "&9Time Left&7: " + getTimeLeftFullFormatted(),
                "&9Price of your house&7: &a" + this.price,
                "",
                "&9Members: ");
        mems.forEach(m -> {
            if(m.isOnline()){
                Utilites.appendToLore(houseItem, "&a" + m.getName());
            }
            else
                Utilites.appendToLore(houseItem, "&8" + m.getName());
        });

        return houseItem;
    }

    public ItemStack memHouseItem( Player p){
        ArrayList<OfflinePlayer> mems = new ArrayList<>();
        this.memberUUIDs.forEach(m -> {mems.add(Bukkit.getOfflinePlayer(m));});
        ItemStack houseItem =  Utilites.createItem(Material.TRIPWIRE_HOOK, 1, (byte) 0, true, this.reigonName,
                "&9Time Left&7: " + getTimeLeftFullFormatted(),
                "&9Price of the house&7: &a" + this.price,
                "",
                "&9Members: ");
        OfflinePlayer o = Bukkit.getOfflinePlayer(this.ownerUUID);
        if(o.isOnline())
            Utilites.insertLoreLine(houseItem, 0, "&9Owner&7: &a" + o.getName());
        else
            Utilites.insertLoreLine(houseItem, 0, "&9Owner&7: &8" + o.getName());
        mems.remove(p);
        mems.forEach(m -> {
            if(m.isOnline()){
                Utilites.appendToLore(houseItem, "&a" + m.getName());
            }
            else
                Utilites.appendToLore(houseItem, "&8" + m.getName());
        });

        return houseItem;
    }

    private boolean createHouseSection(){
        if(!H_CONFIG.isConfigurationSection(this.reigonName)){
            H_CONFIG.createSection(this.reigonName);
            H_CONFIG.createSection(this.reigonName+".Owner");
            H_CONFIG.createSection(this.reigonName+".Start-Time-MS");
            H_CONFIG.createSection(this.reigonName+".Location");
            H_CONFIG.createSection(this.reigonName+".User-Set-Location");
            H_CONFIG.createSection(this.reigonName+".Price");
            H_CONFIG.createSection(this.reigonName+".PurchaseTime-Days");
            H_CONFIG.createSection(this.reigonName+".TimeLeft-Days");
            H_CONFIG.createSection(this.reigonName+".Members");
            H_CONFIG.createSection(this.reigonName+".Blacklist");
            saveEmptyHouse();
            return true;
        }
        else
            return false;
    }

    private void saveEmptyHouse(){
        String dLoc = Utilites.getStringFromLoc(this.signLoc);
        H_CONFIG.set(this.reigonName+".Owner","none");
        H_CONFIG.set(this.reigonName+".Start-Time-MS", 0);
        H_CONFIG.set(this.reigonName+".Location", dLoc);
        H_CONFIG.set(this.reigonName+".User-Set-Location", dLoc);
        H_CONFIG.set(this.reigonName+".Price", this.price);
        H_CONFIG.set(this.reigonName+".PurchaseTime-Days", this.startingDays);
        H_CONFIG.set(this.reigonName+".TimeLeft-Days", 0);
        H_CONFIG.set(this.reigonName+".Members", new ArrayList<>());
        H_CONFIG.set(this.reigonName+".Blacklist", new ArrayList<>());
        H_FILE.saveCustomConfig();
    }

    public void saveHouseFile(){
        if(H_CONFIG.isConfigurationSection(reigonName)){
            if(this.ownerUUID == null){
                saveEmptyHouse();
            }
            else{
                H_CONFIG.set(this.reigonName + ".Owner", this.ownerUUID.toString());
                H_CONFIG.set(this.reigonName + ".Start-Time-MS", this.startTimeInMS);
                H_CONFIG.set(this.reigonName + ".Location", Utilites.getStringFromLoc(this.signLoc));
                H_CONFIG.set(this.reigonName + ".User-Set-Location", Utilites.getStringFromLoc(this.spawnLoc));
                H_CONFIG.set(this.reigonName + ".Price", this.price);
                H_CONFIG.set(this.reigonName + ".PurchaseTime-Days", this.startingDays);
                H_CONFIG.set(this.reigonName + ".TimeLeft-Days", this.timeLeftInDays);

                ArrayList<String> temp = new ArrayList<>();
                ArrayList<String> temp2 = new ArrayList<>();
                this.memberUUIDs.forEach(uuid -> temp.add(uuid.toString()));
                this.blacklistedPlayers.forEach(uuid -> temp2.add(uuid.toString()));
                H_CONFIG.set(this.reigonName + ".Members", temp);
                H_CONFIG.set(this.reigonName + ".Blacklist", temp2);
                H_FILE.saveCustomConfig();
            }
            Housing.console.sendMessage(colorize(Housing.pTag + "&cRegion: &e" + this.reigonName + " &7has been saved."));
        }
        else{
            Housing.console.sendMessage(colorize(Housing.pTag + "&cReigon: &e" + this.reigonName + " &7does not exist yet, Creating default entry instead." ));
            if(createHouseSection()){
                Housing.console.sendMessage(colorize(Housing.pTag + "&cDefault config for: &e" + this.reigonName + " &7has been saved."));
            }
            else{
                Housing.console.sendMessage(colorize(Housing.pTag + "&cRegion: &e" + this.reigonName + " &7failed to save"));
            }
        }

    }

    public void reloadHouse(){
        Housing.houseMap.remove(this.reigonName);
        Location dLoc = Utilites.getLocfromString(H_CONFIG.getString(this.reigonName+".Location"));
        Location uLoc = Utilites.getLocfromString(H_CONFIG.getString(this.reigonName+".User-Set-Location"));
        double price = H_CONFIG.getInt(this.reigonName+".Price");
        long startTime = H_CONFIG.getLong(this.reigonName+".Start-Time-MS");
        String Owner = H_CONFIG.getString(this.reigonName+".Owner");
        int defaultTime = H_CONFIG.getInt(this.reigonName+".PurchaseTime-Days");
        int timeLeft = H_CONFIG.getInt(this.reigonName + ".TimeLeft-Days");

        ArrayList<UUID> membs = new ArrayList<>();
        ArrayList<UUID> blcklist = new ArrayList<>();
        H_CONFIG.getStringList(this.reigonName+".Members").forEach(uuid -> {
            membs.add(UUID.fromString(uuid));
        });
        H_CONFIG.getStringList(this.reigonName+"Blacklist").forEach(uuid -> {
            blcklist.add(UUID.fromString(uuid));
        });

        this.signLoc = dLoc;
        this.sign = (Sign) dLoc.getBlock().getState();
        ProtectedRegion pr = mainThread.getWG().getRegionManager(dLoc.getWorld()).getRegion(this.reigonName);
        if(pr != null){
            this.housingRegion = pr;
        }
        this.spawnLoc = uLoc;
        this.ownerUUID = UUID.fromString(Owner);
        this.price = price;
        this.timeLeftInDays = timeLeft;
        this.memberUUIDs = membs;
        this.blacklistedPlayers = blcklist;
        this.startTimeInMS = startTime;
        this.startingDays = defaultTime;

        Housing.houseMap.put(this.reigonName, this);
    }

    public void destroyHouseAndFileSection(){
        String region = this.reigonName;
        this.reigonName = null;
        Housing.houseMap.remove(region);
        saveHouseFile();
        RegionManager rgMan = Housing.getMainThread().getWG().getRegionManager(this.signLoc.getWorld());
        rgMan.removeRegion(region);
        try{
            rgMan.saveChanges();
        } catch(StorageException e){
            Housing.console.sendMessage(Housing.pTag + e.getMessage());
        }
    }

    public void sendMembersAMessage(String message){
        this.onlineMembers.forEach(player -> player.sendMessage(message));
    }

    public void sendOwnerAMessage(String message){
        OfflinePlayer o = Bukkit.getOfflinePlayer(this.ownerUUID);
        if(o.isOnline()){
            o.getPlayer().sendMessage(message);
        }
    }

}
