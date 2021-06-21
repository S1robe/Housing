package me.Strobe.Utilities;

import com.google.common.collect.ImmutableList;
import me.Strobe.Housing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeGUIs
{
    public static Inventory homes(Player p){
        ArrayList<ItemStack> houseItemOwned = new ArrayList<>();
        ArrayList<ItemStack> houseItemAddedTo = new ArrayList<>();
        Utilites.getHousesPlayerOwns(p.getUniqueId()).forEach(house -> houseItemOwned.add(house.houseItem()));
        Utilites.getHousesPlayerIsAddedTo(p.getUniqueId()).forEach(house -> houseItemAddedTo.add(house.memHouseItem(p)));
        int size = houseItemAddedTo.size() + houseItemOwned.size();
        Inventory inv ;

        if(size <=9)
            inv = Bukkit.createInventory(p, 9, Utilites.colorize("&9Select a home to travel to..."));
        else if(size <=18)
            inv = Bukkit.createInventory(p, 18, Utilites.colorize("&9Select a home to travel to..."));
        else if(size <=27)
            inv = Bukkit.createInventory(p, 27, Utilites.colorize("&9Select a home to travel to..."));
        else if(size <=36)
            inv = Bukkit.createInventory(p, 36, Utilites.colorize("&9Select a home to travel to..."));
        else if(size <=45)
            inv = Bukkit.createInventory(p, 45, Utilites.colorize("&9Select a home to travel to..."));
        else if(size <=54)
            inv = Bukkit.createInventory(p, 54, Utilites.colorize("&9Select a home to travel to..."));
        else
            inv = Bukkit.createInventory(p, 63, Utilites.colorize("&9Select a home to travel to..."));

        Utilites.setMiddle(inv, divider());
        if(size == 0){
            inv.setItem(0, noHouse());
            inv.setItem(5, noMemberOf());
        }
        else if(houseItemAddedTo.size() == 0){
            inv.setItem(5, noMemberOf());
            Utilites.setLeft(inv, filler(), houseItemOwned);
        }
        else if (houseItemOwned.size() == 0){
            inv.setItem(0, noHouse());
            Utilites.setRight(inv, filler(), houseItemAddedTo);
        }
        else{
            Utilites.setLeft(inv, filler(), houseItemOwned);
            Utilites.setRight(inv, filler(), houseItemAddedTo);
        }
        return inv;
    }

    public static Inventory home(House h){
        OfflinePlayer p = Bukkit.getOfflinePlayer(h.getOwnerUUID());
        Inventory inv = Bukkit.createInventory(null, 9, "&7Your house: &9" + h.getReigonName());
        Utilites.fill(inv, filler());
        inv.setItem(0, Utilites.createItem(Material.POTION, 1, (byte) 8195, "&6Travel Home"));
        inv.setItem(1, Utilites.createItem(Material.POTION, 1, (byte) 8235, "&aExtend Your Stay"));
        inv.setItem(2, Utilites.createItem(Material.POTION, 1, (byte) 8229, "&cLeave Home"));
        inv.setItem(4, Utilites.createItem(Material.POTION, 1, (byte) 8264, "&eEdit Members"));
        inv.setItem(7, Utilites.getSkullOf(p.getName()));
        return inv;
    }

    public static Inventory member(House h){
        OfflinePlayer o = Bukkit.getOfflinePlayer(h.getOwnerUUID());
        Inventory inv = Bukkit.createInventory(null, 9,"&a"+ o.getName() + "&7'&as &7House: " + h.getReigonName());
        Utilites.fill(inv, filler());
        inv.setItem(1, Utilites.createItem(Material.POTION, 1, (byte) 8195, "&6Travel Home"));
        inv.setItem(4, Utilites.createItem(Material.POTION, 1, (byte) 8235, "&aExtend Your Stay"));
        inv.setItem(7, Utilites.createItem(Material.POTION, 1, (byte) 8229, "&cLeave Home"));
        return inv;
    }

    public static Inventory memGUI(House h){
        int numMems = h.getMemberUUIDs().size();
        Inventory inv;
        if(numMems <=9)
            inv = Bukkit.createInventory(null, 18, "&9Members of your house");
        else if(numMems <=18)
            inv = Bukkit.createInventory(null, 27, "&9Members of your house");
        else if(numMems <=27)
            inv = Bukkit.createInventory(null, 36, "&9Members of your house");
        else if(numMems <=36)
            inv = Bukkit.createInventory(null, 45, "&9Members of your house");
        else if(numMems <=45)
            inv = Bukkit.createInventory(null, 54, "&9Members of your house");
        else
            inv = Bukkit.createInventory(null, 64, "&9Members of your house");
        Utilites.fill(inv, filler());
        inv.setItem(4, Utilites.createItem(Material.POTION, 1, (byte) 8201, "&cBack"));
        for(int s = 9; s < inv.getSize(); s++){
            inv.setItem(s, Utilites.getSkullOf(h.getMemberUUIDs().get(s - 9)));
        }
        return inv;
    }




    public static ItemStack filler(){
        return Utilites.createItem(Material.STAINED_GLASS_PANE, 1, 15, "&9&l???");
    }

    public static ItemStack divider(){
        return Utilites.createItem(Material.STICK, 1, 0, "&a<&m---&r &a&nHouses you Own&r &8: &6&nHouses you're added to&r &6&m---&r&6>");
    }

    public static ItemStack noHouse(){
        return Utilites.createItem(Material.TRIPWIRE_HOOK, 1, 0, "&9You dont own any houses!");
    }
    public static ItemStack noMemberOf(){
        return Utilites.createItem(Material.TRIPWIRE_HOOK, 1, 0, "&9You're not added to any houses!");
    }


}
