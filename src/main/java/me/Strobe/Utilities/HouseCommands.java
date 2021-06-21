package me.Strobe.Utilities;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.domains.GroupDomain;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Strobe.Files.CustomFile;
import me.Strobe.Housing;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static me.Strobe.Utilities.Utilites.colorize;
import static me.Strobe.Utilities.Utilites.getHouseFromRegionName;
//import net.slim.cubes.CCUtils;

public class HouseCommands implements CommandExecutor {

    private final Housing mainThread = Housing.getMainThread();
    private final CustomFile H_FILE = mainThread.gethFile();

    //this is only for /home command
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize(Housing.pTag + "You &Cmust&7 be a player to use this command!"));
            return false;
        }
        //Commands, /home. home sethome, home blacklist
        // Admins: /force unrent, setspawnloc, setowner, add member
        Player p = (Player) sender;
        int length = args.length;
        if(p.hasPermission(Permissions.houseAdmin.getNode())){
            if(length >= 2){
                House h = Utilites.getHouseFromRegionName(args[1]);
                if(h != null){
                    OfflinePlayer o = Bukkit.getOfflinePlayer(h.getOwnerUUID());
                    Sign s = h.getSign();
                    ProtectedRegion pr = h.getHousingRegion();
                    ArrayList<UUID> blacklist = h.getBlacklistedPlayers();
                    ArrayList<UUID> members = h.getMemberUUIDs();
                    UUID owner = h.getOwnerUUID();
                    switch(args[0]){
                        case "funrent": // home funrent %reg%
                        {
                            double refund = (h.getPrice() / h.getStartingDays()) * h.getTimeLeftInDays();
                            h.setOwnerUUID(null);
                            mainThread.getEcon().depositPlayer(o, refund);
                            h.saveHouseFile();
                            s.setLine(0, Messages.forRentTag.getText());
                            s.setLine(1, h.getReigonName());
                            s.setLine(2, h.getStartingDays() + " Days");
                            s.setLine(3, "$" + h.getPrice());
                            s.update();
                            p.sendMessage(Housing.pTag + Messages.refundToAdmin.getText().replace("%plr%", o.getName()).replace("%refund%", "" + refund));
                            h.sendOwnerAMessage(Housing.pTag + Messages.refundToPlayer.getText().replace("%refund%", "" + refund));
                            break;
                        }
                        case "fsetspawn": // home fsetspawn %reg%
                        {
                            int[] xyz  = {p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()};
                            h.setSpawnLoc(p.getLocation());
                            h.saveHouseFile();
                            p.sendMessage(Housing.pTag + Messages.forceSetSpawn.getText().replace("%reg%", h.getReigonName())
                                                                                             .replace("%x%", ""+ xyz[0])
                                                                                             .replace("%y%", ""+ xyz[1] )
                                                                                             .replace("%z%", ""+ xyz[2]) );
                            h.sendOwnerAMessage(Housing.pTag + Messages.playerSetSpawn.getText().replace("%reg%", h.getReigonName())
                                    .replace("%x%", ""+ xyz[0])
                                    .replace("%y%", ""+ xyz[1] )
                                    .replace("%z%", ""+ xyz[2]));
                            h.sendMembersAMessage(Housing.pTag + Messages.playerSetSpawn.getText().replace("%reg%", h.getReigonName())
                                    .replace("%x%", ""+ xyz[0])
                                    .replace("%y%", ""+ xyz[1] )
                                    .replace("%z%", ""+ xyz[2]));
                            break;
                        }
                        case "fsetowner": // home fsetowner %reg% %name%
                        {
                            Player plr = Bukkit.getPlayer(args[2]);
                            if(plr != null){
                                if(owner.equals(plr.getUniqueId())){
                                    p.sendMessage(Housing.pTag + Messages.errAlreadyOwner.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                    return true;
                                }
                                h.setOwnerUUID(plr.getUniqueId());
                                h.setSpawnLoc(h.getSignLoc());
                                h.setTimeLeftInDays(h.getStartingDays());
                                h.setStartTimeInMS(System.currentTimeMillis());
                                h.getOnlineMembers().remove(plr);
                                members.remove(plr.getUniqueId());
                                h.saveHouseFile();
                                s.setLine(0, Messages.occupiedTag.getText());
                                s.setLine(1, h.getTimeLeftDaysFormatted());
                                s.setLine(2, "");
                                s.setLine(3, plr.getName());
                                s.update();
                                h.sendOwnerAMessage(Housing.pTag + Messages.playerSetOwner.getText().replace("%reg%", h.getReigonName()));
                                p.sendMessage(Housing.pTag + Messages.forceSetOwner.getText().replace("%reg%", h.getReigonName()).replace("%plr%", args[2]));
                            }
                            else{
                                String uuid = Utilites.getUUID(args[2]);
                                if(!uuid.equalsIgnoreCase("error") && !uuid.equalsIgnoreCase("invalid name")){
                                    if(owner.equals(UUID.fromString(uuid))){
                                        p.sendMessage(Housing.pTag + Messages.errAlreadyOwner.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                        return true;
                                    }
                                    OfflinePlayer o1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                    h.setOwnerUUID(o1.getUniqueId());
                                    h.setSpawnLoc(h.getSignLoc());
                                    h.setTimeLeftInDays(h.getStartingDays());
                                    h.setStartTimeInMS(System.currentTimeMillis());
                                    h.getOnlineMembers().remove(o1.getPlayer());
                                    members.remove(o1.getUniqueId());
                                    h.saveHouseFile();
                                    s.setLine(0, Messages.occupiedTag.getText());
                                    s.setLine(1, h.getTimeLeftDaysFormatted());
                                    s.setLine(2, "");
                                    s.setLine(3, o1.getName());
                                    s.update();
                                    h.sendOwnerAMessage(Housing.pTag + Messages.playerSetOwner.getText().replace("%reg%", h.getReigonName()));
                                    p.sendMessage(Housing.pTag + Messages.forceSetOwner.getText().replace("%reg%", h.getReigonName()).replace("%plr%", args[2]));
                                }
                                else{
                                    p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                }
                            }
                            break;
                        }
                        case "faddmem": //home faddmem %reg% %name%
                        {
                            Player plr2 = Bukkit.getPlayer(args[2]);
                            if(plr2 != null){
                                if(members.contains(plr2.getUniqueId())){
                                    p.sendMessage(Housing.pTag + Messages.errPlayerAlreadyAdded.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                    return true;
                                }
                                members.add(plr2.getUniqueId());
                                h.getOnlineMembers().add(plr2);
                                h.sendOwnerAMessage(Housing.pTag + Messages.forceAddPlayer.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                h.sendMembersAMessage(Housing.pTag + Messages.forceAddPlayer.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                p.sendMessage(Housing.pTag + Messages.forceAddPlayerAdmin.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                            }
                            else{
                                String uuid2 = Utilites.getUUID(args[2]);
                                if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                    if(members.contains(UUID.fromString(uuid2))){
                                        p.sendMessage(Housing.pTag + Messages.errPlayerAlreadyAdded.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                        return true;
                                    }
                                    OfflinePlayer o2 = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                    members.add(UUID.fromString(uuid2));
                                    h.getOnlineMembers().add(o2.getPlayer());
                                    h.sendOwnerAMessage(Housing.pTag + Messages.forceAddPlayer.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    h.sendMembersAMessage(Housing.pTag + Messages.forceAddPlayer.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    p.sendMessage(Housing.pTag + Messages.forceAddPlayerAdmin.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                }
                                else{
                                    p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                }
                            }
                            break;
                        }
                        case "fremmem":
                        {
                            Player plr2 = Bukkit.getPlayer(args[2]);
                            if(plr2 != null){
                                if(!members.contains(plr2.getUniqueId())){
                                    p.sendMessage(Housing.pTag + Messages.errPlrNotAdded.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                    return true;
                                }
                                members.remove(plr2.getUniqueId());
                                h.getOnlineMembers().add(plr2);
                                h.sendOwnerAMessage(Housing.pTag + Messages.forceRemPlayer.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                h.sendMembersAMessage(Housing.pTag + Messages.forceRemPlayer.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                p.sendMessage(Housing.pTag + Messages.forceRemPlayerAdmin.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                            }
                            else{
                                String uuid2 = Utilites.getUUID(args[2]);
                                if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                    if(!members.contains(UUID.fromString(uuid2))){
                                        p.sendMessage(Housing.pTag + Messages.errPlrNotAdded.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                        return true;
                                    }
                                    OfflinePlayer o2 = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                    members.add(UUID.fromString(uuid2));
                                    h.getOnlineMembers().add(o2.getPlayer());
                                    h.sendOwnerAMessage(Housing.pTag + Messages.forceRemPlayer.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    h.sendMembersAMessage(Housing.pTag + Messages.forceRemPlayer.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    p.sendMessage(Housing.pTag + Messages.forceRemPlayerAdmin.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                }
                                else{
                                    p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                }
                            }
                            break;
                        }
                        case "fblacklist": //home faddmem %reg% %name%
                        {
                            Player plr2 = Bukkit.getPlayer(args[2]);
                            if(plr2 != null){
                                if(blacklist.contains(plr2.getUniqueId())){
                                    p.sendMessage(Housing.pTag + Messages.errAlrBlacklisted.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                    return true;
                                }
                                members.add(plr2.getUniqueId());
                                h.getOnlineMembers().add(plr2);
                                h.sendOwnerAMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                h.sendMembersAMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                p.sendMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                            }
                            else{
                                String uuid2 = Utilites.getUUID(args[2]);
                                if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                    if(members.contains(UUID.fromString(uuid2))){
                                        p.sendMessage(Housing.pTag + Messages.errAlrBlacklisted.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                        return true;
                                    }
                                    OfflinePlayer o2 = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                    members.add(UUID.fromString(uuid2));
                                    h.getOnlineMembers().add(o2.getPlayer());
                                    h.sendOwnerAMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    h.sendMembersAMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    p.sendMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                }
                                else{
                                    p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                }
                            }
                            break;
                        }
                        case "funblacklist":
                        {
                            Player plr2 = Bukkit.getPlayer(args[2]);
                            if(plr2 != null){
                                if(!blacklist.contains(plr2.getUniqueId())){
                                    p.sendMessage(Housing.pTag + Messages.errNotBlacklisted.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                    return true;
                                }
                                members.remove(plr2.getUniqueId());
                                h.getOnlineMembers().add(plr2);
                                h.sendOwnerAMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                h.sendMembersAMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                                p.sendMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", plr2.getName()).replace("%reg%", h.getReigonName()));
                            }
                            else{
                                String uuid2 = Utilites.getUUID(args[2]);
                                if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                    if(!members.contains(UUID.fromString(uuid2))){
                                        p.sendMessage(Housing.pTag + Messages.errNotBlacklisted.getText().replace("%plr%", args[2]).replace("%reg%", h.getReigonName()));
                                        return true;
                                    }
                                    OfflinePlayer o2 = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                    members.add(UUID.fromString(uuid2));
                                    h.getOnlineMembers().add(o2.getPlayer());
                                    h.sendOwnerAMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    h.sendMembersAMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                    p.sendMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", o2.getName()).replace("%reg%", h.getReigonName()));
                                }
                                else{
                                    p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                }
                            }
                            break;
                        }
                        case "fsetdaysleft": //home fsetdaysleft %reg% %time%
                        {
                            if(NumberUtils.isNumber(args[2])){
                                h.setTimeLeftInDays(Integer.parseInt(args[2]));
                                h.saveHouseFile();
                                s.setLine(1, h.getTimeLeftDaysFormatted());
                                s.update();
                                h.sendMembersAMessage(Housing.pTag + Messages.forceAddTimePlayer.getText().replace("%reg%", h.getReigonName()).replace("%time%", args[2]));
                                p.sendMessage(Housing.pTag + Messages.forceAddTimeAdmin.getText().replace("%reg%", h.getReigonName()).replace("%time%", args[2]));
                            }
                            else{
                                p.sendMessage(Housing.pTag + Messages.errTimeNotNumber.getText());
                            }
                            break;
                        }
                        case "fsetprice": // home fsetprice %reg% %price%
                        {
                            if(NumberUtils.isNumber(args[2])){
                                h.setPrice(Integer.parseInt(args[2]));
                                h.saveHouseFile();
                                h.sendMembersAMessage(Housing.pTag + Messages.playerChangePrice.getText().replace("%reg%", h.getReigonName()).replace("%price%", args[2]));
                                p.sendMessage(Housing.pTag + Messages.forceChangePrice.getText().replace("%reg%", h.getReigonName()).replace("%time%", args[2]));
                            }
                            else{
                                p.sendMessage(Housing.pTag + Messages.errPriceNotNumber.getText());
                            }
                            break;
                        }
                        case "destroy": // home destroy %reg%
                        {
                            h.destroyHouseAndFileSection();
                            break;
                        }
                        case "reload":  // home reload %reg% or all for all
                        {
                            if("all".equals(args[1])){
                                Utilites.reloadHouses();
                            }
                            else{
                                h.reloadHouse();
                            }
                            break;
                        }
                        case "list":
                        {
                            if(!Housing.houseMap.isEmpty()){
                                p.sendMessage(colorize("-------------------") + Housing.pTag + "-------------------");
                                p.sendMessage();
                                p.sendMessage(colorize("&7Houses shown in &aGreen&7 are availible and ones in &cRed&7 are taken."));
                                p.sendMessage();
                                Housing.houseMap.values().forEach(ho -> {
                                    if(h.getOwnerUUID() == null){
                                        p.sendMessage(colorize("&7Name: &a" + ho.getReigonName() + " &7Location: &a" + Utilites.getStringFromLoc(ho.getSignLoc())));
                                    }
                                    else{
                                        p.sendMessage(colorize("&7Name: &c" + ho.getReigonName()+  " &7Owner: &c"+Bukkit.getOfflinePlayer(ho.getOwnerUUID()).getName() +  "&7Location: &c" + Utilites.getStringFromLoc(ho.getSignLoc())));
                                    }
                                });
                            }
                            else{
                                p.sendMessage(Housing.pTag + "&7There are no houses to show.");
                            }
                            break;
                        }
                    }
                }
                else{
                    p.sendMessage(Housing.pTag + Messages.errRegNoExist.getText().replace("%reg%", args[1]));
                }
            }
        }
        else{
            switch(length){
                case 0: //home
                {
                    p.openInventory(HomeGUIs.homes(p));
                    break;
                }
                case 2: //home sethome %reg%
                {
                    RegionManager rgMan = mainThread.getWG().getRegionManager(p.getWorld());
                    House h = Utilites.getHouseFromRegionName(args[1]);
                    if(h != null){
                        ProtectedRegion pr = h.getHousingRegion();
                        if(args[0].equalsIgnoreCase("sethome")){
                            if(pr != null){
                                if(h.getOwnerUUID().equals(p.getUniqueId())){
                                    if(rgMan.getApplicableRegions(p.getLocation()).getRegions().contains(pr)){
                                        h.setSpawnLoc(p.getLocation());
                                        h.saveHouseFile();
                                    }
                                    else{
                                        h.sendOwnerAMessage(Housing.pTag + Messages.errNotInRegion.getText().replace("%reg%", args[1]));
                                    }
                                }
                                else{
                                    p.sendMessage(Housing.pTag + Messages.errNotOwner.getText().replace("%reg%", args[1]));
                                }
                            }
                            else{
                                p.sendMessage(Housing.pTag + Messages.errRegNoExist.getText().replace("%reg%" , args[1]));
                            }
                        }
                    }
                    break;
                }
                case 3: //home blacklist %reg% %plr%
                {
                    House h2 = Utilites.getHouseFromRegionName(args[1]);
                    if(h2 != null){
                        switch(args[0]){
                            case "blacklist":
                            {
                                ArrayList<UUID> blacklist = h2.getBlacklistedPlayers();
                                Player plr = Bukkit.getPlayer(args[2]);
                                if(plr != null){
                                    if(!blacklist.contains(plr.getUniqueId())){
                                        h2.getBlacklistedPlayers().add(plr.getUniqueId());
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", plr.getName()).replace("%reg%", h2.getReigonName()));
                                    }
                                    else{
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.errAlrBlacklisted.getText().replace("%plr%", plr.getName()));
                                    }
                                }
                                else{
                                    String uuid2 = Utilites.getUUID(args[2]);
                                    if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                        OfflinePlayer o = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                        UUID uuid = o.getUniqueId();
                                        if(!blacklist.contains(uuid)){
                                            h2.getBlacklistedPlayers().add(uuid);
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.blPlr.getText().replace("%plr%", o.getName()).replace("%reg%", h2.getReigonName()));
                                        }
                                        else{
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.errAlrBlacklisted.getText().replace("%plr%", o.getName()));
                                        }
                                    }
                                    else{
                                        p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                    }
                                }
                                break;
                            }
                            case "unblacklist":
                            {

                                ArrayList<UUID> blacklist = h2.getBlacklistedPlayers();
                                Player plr = Bukkit.getPlayer(args[2]);
                                if(plr != null){
                                    if(blacklist.contains(plr.getUniqueId())){
                                        h2.getBlacklistedPlayers().remove(plr.getUniqueId());
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", plr.getName()).replace("%reg%", h2.getReigonName()));
                                    }
                                    else{
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.errNotBlacklisted.getText().replace("%plr%", plr.getName()));
                                    }
                                }
                                else{
                                    String uuid2 = Utilites.getUUID(args[2]);
                                    if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                        OfflinePlayer o = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                        UUID uuid = o.getUniqueId();
                                        if(blacklist.contains(uuid)){
                                            h2.getBlacklistedPlayers().remove(uuid);
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.unBlPlr.getText().replace("%plr%", o.getName()).replace("%reg%", h2.getReigonName()));
                                        }
                                        else{
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.errNotBlacklisted.getText().replace("%plr%", o.getName()));
                                        }
                                    }
                                    else{
                                        p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                    }
                                }
                                break;
                            }
                            case "removemember":
                            {
                                ArrayList<UUID> mems = h2.getMemberUUIDs();
                                Player plr = Bukkit.getPlayer(args[2]);
                                if(plr != null){
                                    if(mems.contains(plr.getUniqueId())){
                                        h2.getMemberUUIDs().remove(plr.getUniqueId());
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.removeFromHouse.getText().replace("%plr%", plr.getName()).replace("%reg%", h2.getReigonName()));
                                    }
                                    else{
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.errNotMem.getText().replace("%plr%", plr.getName()));
                                    }
                                }
                                else{
                                    String uuid2 = Utilites.getUUID(args[2]);
                                    if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                        OfflinePlayer o = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                        UUID uuid = o.getUniqueId();
                                        if(mems.contains(uuid)){
                                            h2.getMemberUUIDs().remove(uuid);
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.removeFromHouse.getText().replace("%plr%", o.getName()).replace("%reg%", h2.getReigonName()));
                                        }
                                        else{
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.errNotMem.getText().replace("%plr%", o.getName()));
                                        }
                                    }
                                    else{
                                        p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                    }
                                }
                                break;
                            }
                            case "addmember":
                            {
                                ArrayList<UUID> mems = h2.getMemberUUIDs();
                                Player plr = Bukkit.getPlayer(args[2]);
                                if(plr != null){
                                    if(!mems.contains(plr.getUniqueId())){
                                        h2.getMemberUUIDs().add(plr.getUniqueId());
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.addToHouse.getText().replace("%plr%", plr.getName()).replace("%reg%", h2.getReigonName()));
                                    }
                                    else{
                                        h2.sendOwnerAMessage(Housing.pTag + Messages.errAlrMem.getText().replace("%plr%", plr.getName()));
                                    }
                                }
                                else{
                                    String uuid2 = Utilites.getUUID(args[2]);
                                    if(!uuid2.equalsIgnoreCase("error") && !uuid2.equalsIgnoreCase("invalid name")){
                                        OfflinePlayer o = Bukkit.getOfflinePlayer(UUID.fromString(uuid2));
                                        UUID uuid = o.getUniqueId();
                                        if(!mems.contains(uuid)){
                                            h2.getMemberUUIDs().remove(uuid);
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.addToHouse.getText().replace("%plr%", o.getName()).replace("%reg%", h2.getReigonName()));
                                        }
                                        else{
                                            h2.sendOwnerAMessage(Housing.pTag + Messages.errAlrMem.getText().replace("%plr%", o.getName()));
                                        }
                                    }
                                    else{
                                        p.sendMessage(Housing.pTag + Messages.errPlyrNotExist.getText().replace("%plr%", args[2]));
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        //home sethome
        //home
        //home blacklist %plr%/*

        return true;
    }
}

