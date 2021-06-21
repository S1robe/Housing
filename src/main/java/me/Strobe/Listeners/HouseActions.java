package me.Strobe.Listeners;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.util.Events;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.handler.EntryFlag;
import com.sk89q.worldguard.session.handler.ExitFlag;
import me.Strobe.Files.CustomFile;
import me.Strobe.Housing;
import me.Strobe.Utilities.*;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

import static me.Strobe.Utilities.Utilites.*;

public class  HouseActions implements Listener{

    private final Housing mainThread = Housing.getMainThread();
    private final Economy econ = mainThread.getEcon();

    private final CustomFile H_FILE = mainThread.gethFile();
    private final FileConfiguration H_CONFIG = H_FILE.getCustomConfig();

    @EventHandler
    public void onSignInteraction(@NotNull PlayerInteractEvent e){
        if(e.getClickedBlock() != null && isSign(e.getClickedBlock())){
            Player p = e.getPlayer();
            Sign s = (Sign) e.getClickedBlock().getState();
            String[] sLines = new String[4];
            for(int i = 0; i < sLines.length; i++)
                sLines[i] = ChatColor.stripColor(s.getLine(i));

            if(sLines[0].equals(Messages.forRentTag.getText())){
                if(NumberUtils.isNumber(sLines[2].replace("$", ""))
                    && NumberUtils.isNumber(sLines[3].replace(" Days", ""))){
                        if(p.hasPermission(Permissions.housesRent.getNode())){
                            buyHouse(p, s);
                        }
                        else if (p.hasPermission(Permissions.aptRent.getNode())){
                            buyHouse(p, s);
                        }
                    }
                else {
                    p.sendMessage(colorize( Housing.pTag + Messages.errCantBuy.getText()));
                    p.sendMessage(colorize( Housing.pTag + Messages.buycraftAd.getText()));
                }
            }
            else if(sLines[0].equals(Messages.occupiedTag.getText())){
                adjustHouse(p, s);
            }
        }
    }

    @EventHandler
    public void onSignPlace(@NotNull SignChangeEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
        String[] lines = {e.getLine(0),e.getLine(1),e.getLine(2),e.getLine(3)};
        RegionManager rgMan = Housing.getMainThread().getWG().getRegionManager(b.getWorld());
        if(p.hasPermission(Permissions.houseAdmin.getNode())){
            if(e.getLine(0).equalsIgnoreCase("rent")){
                ProtectedRegion pcr = validateSign(p, lines, rgMan);
                if(pcr != null){
                    pcr.setFlag(Housing.CUSTOM_HOUSING_FLAG, true);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + pcr.getId() + "-w " +b.getWorld() + " chest-access -g NON_MEMBERS deny");
                    House newHouse = new House(b.getLocation(), pcr, pcr.getId(), Double.parseDouble(e.getLine(2)), Integer.parseInt(e.getLine(3)));
                    newHouse.saveHouseFile();
                    rgMan.addRegion(pcr);
                    e.setLine(0, ChatColor.WHITE + Messages.forRentTag.getText());
                    e.setLine(1, ChatColor.WHITE + pcr.getId());
                    e.setLine(2, ChatColor.WHITE + "$" + e.getLine(2));
                    e.setLine(3, ChatColor.WHITE + e.getLine(3) + " Days");
                    p.sendMessage(Housing.pTag + Messages.creHouse.getText().replace("%reg%", newHouse.getReigonName()));
                }
            }
        }
    }

    @EventHandler
    public void onSignBreak(@NotNull BlockBreakEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if(p.hasPermission(Permissions.houseAdmin.getNode()))
            if(Utilites.isSign(b)){
                Sign s = (Sign) b.getState();
                String[] sLines = new String[4];
                for(int i = 0; i < sLines.length; i++)
                    sLines[i] = ChatColor.stripColor(s.getLine(i));
                if(sLines[0].equalsIgnoreCase(Messages.occupiedTag.getText())){
                    Housing.houseMap.remove(sLines[1]);
                    H_CONFIG.set(sLines[1], null);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg removeowner " + sLines[1] + " all -w " + b.getWorld());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg removemember " + sLines[1] + " all -w " + b.getWorld());
                    H_FILE.saveCustomConfig();
                    p.sendMessage(Housing.pTag + Messages.remHouse.getText().replace("%reg%", sLines[1]));
                }
                else if(sLines[0].equalsIgnoreCase(Messages.forRentTag.getText())){
                    Housing.houseMap.remove(sLines[1]);
                    H_CONFIG.set(sLines[1], null);
                    H_FILE.saveCustomConfig();
                    p.sendMessage(Housing.pTag + Messages.remHouse.getText().replace("%reg%", sLines[1]));
                }
            }
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent e){
        House h = getHouseFromRegion(e.getRegion());
        if(h != null){
            if(h.getBlacklistedPlayers().contains(e.getPlayer().getUniqueId())){
                e.setCancelled(true);
                e.getPlayer().sendMessage(Housing.pTag + Messages.errBlacklisted.getText());
            }
        }
    }

    private void buyHouse(@NotNull Player p, @NotNull Sign s){
        UUID pUUID = p.getUniqueId();
        String pName = p.getName();
        World pWorld = p.getWorld();
        Location pLoc = p.getLocation();
        House h = Utilites.getHouseFromLocation(s.getLocation());
        String[] sLines = new String[4];
        for(int i = 0; i < sLines.length; i++)
            sLines[i] = ChatColor.stripColor(s.getLine(i));

        if( Utilites.getNumHousesPlayerOwns(pUUID) < mainThread.gethLim()){
            double price = Double.parseDouble(sLines[2].replace("$",""));

            if(price < econ.getBalance(p)){
                econ.withdrawPlayer(p, price);
                assert h != null;
                h.setOwnerUUID(pUUID);
                h.setStartTimeInMS(System.currentTimeMillis());
                h.setTimeLeftInDays(h.getStartingDays());
                h.setSpawnLoc(h.getSignLoc());
                h.saveHouseFile();

                s.setLine(0, Messages.occupiedTag.getText());
                s.setLine(1, ChatColor.WHITE + h.getTimeLeftDaysFormatted());
                s.setLine(2, "");
                s.setLine(3, ChatColor.WHITE + pName);
                s.update(true);

                p.playSound(pLoc, Sound.LEVEL_UP, 1f, 1.7f);
                p.playSound(pLoc, Sound.FIREWORK_TWINKLE2, 1f, 1.4f);
                p.sendMessage(colorize(Housing.pTag + Messages.buyHouse.getText().replace("%price%", "" + price)));
                p.sendMessage(colorize(Messages.priceTag.getText().replace("%price%", "" + price)));

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "region addowner "
                        + h.getReigonName() //Region name
                        + " " + pName + " -w " + pWorld);

            }
        }
    }
    private void adjustHouse(@NotNull Player p, @NotNull Sign s){
        UUID pUUID = p.getUniqueId();
        Location pLoc = p.getLocation();
        House h = Utilites.getHouseFromLocation(s.getLocation());
        assert h != null;
        if(Utilites.doesPlayerOwnHouse(pUUID, h)){
            p.openInventory(HomeGUIs.home(h));
            p.playSound(pLoc, Sound.WOOD_CLICK, 1f, 0.5f);
        }
        else if(Utilites.isPlayerAddedToHouse(pUUID, h)){
            p.openInventory(HomeGUIs.member(h));
            p.playSound(pLoc, Sound.WOOD_CLICK, 1f, 0.5f);
        }
        else{
            p.sendMessage(colorize(Housing.pTag + Messages.rentTime.getText().replace("%time%" , h.getTimeLeftFullFormatted()) ));
            p.playSound(pLoc, Sound.ORB_PICKUP, 1f, 1.5f);
        }

    }
    private ProtectedRegion validateSign(@NotNull Player p, String[] sLines, RegionManager rgMan){
        Selection sel = mainThread.getWE().getSelection(p);
        if(sel != null){
            if(!sLines[1].isEmpty()){
                if(NumberUtils.isNumber(sLines[2])){ // price
                    if(NumberUtils.isNumber(sLines[3])){// days
                        ProtectedCuboidRegion pcr = new ProtectedCuboidRegion(sLines[1], sel.getNativeMinimumPoint().toBlockVector(), sel.getNativeMaximumPoint().toBlockVector());
                        if(rgMan.getRegion(pcr.getId()) == null){ // region doesnt exist
                            p.sendMessage(Housing.pTag + colorize("New region for house: &a" + sLines[1] + " &7named: &B" + pcr.getId() + "&7 was created."));
                            return pcr;
                        }
                        else{
                            p.sendMessage(Housing.pTag + Messages.errRegAlrExist.getText().replace("%reg%", sLines[1]));
                        }
                    }
                    else{
                        p.sendMessage(Housing.pTag + Messages.errTimeNotNumber.getText());
                    }
                }
                else{
                    p.sendMessage(Housing.pTag + Messages.errPriceNotNumber.getText());
                }
            }
            else{
                p.sendMessage(Housing.pTag + Messages.errRegNoExist.getText().replace("%reg%", sLines[1]));
            }
        }
        else{
            p.sendMessage(Housing.pTag + colorize("Please make a selction with WE to create houses!"));
        }

        return null;
    }
}
