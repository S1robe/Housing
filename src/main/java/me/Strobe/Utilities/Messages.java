package me.Strobe.Utilities;

import me.Strobe.Housing;
import org.bukkit.configuration.file.FileConfiguration;

import static me.Strobe.Utilities.Utilites.colorize;

public enum Messages{

    buycraftAd("&7Upgrade your rank today to unlock access to rent houses and more using /buy!", "buycraftAd"),
    errCantExt("&7You &ccannot &7extend your rent further.", "errCantExt"),
    errCantBuy("&7You &cdo not &7have permission to rent houses.", "errCantBuy"),
    errRegNoExist("&7The house &b%reg%&7 &cdoesn't&7 exist!", "errRegNoExist"),
    errTimeNotNumber("&7The &blast value &7must be the &atime &7in &adays &7the region is &asold &7after.", "errTimeNotNumber"),
    errPriceNotNumber("&7The &b3rd value &7must be the &aprice &7the region is &asold \n&7for per time quantity specified by the next line.", "errPriceNotNumber"),
    errPlyrNotExist("&7The player &b%plr% &7does not exist.", "errPlyrNotExist"),
    errRegAlrExist("&7The house &b%reg%&7 already exists!", "errRegAlrExist"),
    errNotInRegion("&7You are not within the house &b%reg%&7.", "errNotInRegion"),
    errNotOwner("&7You are not the owner of the house &b%reg%&7.", "errNotOwner"),
    errBlacklisted("&7You may not enter this house!", "errBlacklisted"),
    errAlrBlacklisted("&7The &b%plr%&7 is &aalready&7 blacklisted from the house &b%reg%&7.", "errAlrBlacklisted"),
    errNotBlacklisted("&7The &b%plr%&7 is &anot&7 blacklisted from the house &b%reg%&7.", "errAlrBlacklisted"),
    errNotMem("&7The &b%plr%&7 is &anot&7 a member of this house.", "errNotMem"),
    errAlrMem("&7The &b%plr%&7 is &aalready&7 a member of this house.", "errAlrMem"),

    rentTime("&7This house will expire in: &a%time%&7.", "rentTime"),
    forRentTag("&fFOR RENT", "forRentTag"),
    occupiedTag("&fOCCUPIED", "occupiedTag"),

    blPlr("&7The player &b%plr%&7 has been blacklisted from the house &b%reg%&7.", "blPlr"),
    unBlPlr("&7The player &b%plr%&7 has been unblacklisted from the house &b%reg%&7.", "unBlPlr"),

    forceSetOwner("&7You have set the &b%reg%&7's owner to &b%plr%&7", "forceSetOwner"),
    errAlreadyOwner("&7The player &b%plr%&7 is already the &b%reg%&7's owner.", "errAlreadyOwner"),
    playerSetOwner("&7You have been assigned the house &b%reg%&7.", "playerSetOwner"),

    forceSetSpawn("&7You have force set &b%reg%'s spawn to &a%x%&7, &a%y%&7, &a%z%&7.", "forceSetSpawn"),
    playerSetSpawn("&7The house &b%reg%&7's spawn to &a%x%&7, &a%y%&7, &a%z%&7.", "playerSetSpawn"),

    forceAddPlayer("&7The player &b%plr%&7 has been force added to your house &b%reg%&7.", "forceAddPlayer"),
    forceAddPlayerAdmin("&7The player &b%plr%&7 has been force added to the region &b%reg%&7.", "forceAddPlayerAdmin"),
    errPlayerAlreadyAdded("&7The player &b%plr%&7, is already added to the region &b%reg%&7.", "errPlayerAlreadyAdded"),

    forceRemPlayer("&7The player &b%plr%&7 has been force removed from your house &b%reg%&7.", "forceRemPlayer"),
    forceRemPlayerAdmin("&7The player &b%plr%&7 has been force removed from the region &b%reg%&7.", "forceRemPlayerAdmin"),
    errPlrNotAdded("&7The player &b%plr%&7, is not added to the region &b%reg%&7.", "errPlrNotAdded"),

    forceAddTimePlayer("&7Your house, &b%reg%&7, had its time left in days has been updated to: &a%time%", "forceAddTimePlayer"),
    forceAddTimeAdmin("&7The house &b%reg%&7, had its time left in days has been updated to: &a%time%", "forceAddTimeAdmin"),

    forceChangePrice("&7The house &b%reg%&7 is now worth &a$%price%&7 for a period of &b%time%&7.","forceChangePrice"),
    playerChangePrice("&7Your house &b%reg%&7 is now worth &a$%price%&7 for a period of &b%time%&7.","playerChangePrice"),

    refundToPlayer("&7You &7have been refunded &a%refund%&7 because your house was force unrented.", "refundToPlayer"),
    refundToAdmin("&b%plr% &7has been refunded &a%refund%&7.", "refundPlayer"),

    priceTag("&c&l- $%price%", "priceTag"),
    sellHouse("&7Your house has been &asold&7!", "sellHouse"),
    buyHouse("&7You've &abought &7this lovely house for &a%price%&7!", "buyHouse"),
    addToHouse("&7You've &aadded &b%player% &7to your house!", "addToHouse"),
    removeFromHouse("&7You've &cremoved &b%plr% &7from house &b%reg%&7!", "removeFromHouse"),
    extRent("&7You've extended your rent by %inc% %unit%&7(s)!", "extRent"),
    permBuy("&7You've &lPerminantly &a&lBought &7this house for &a%price%&7!", "permBuy"),
    remHouse("&7You've &cremoved&7 the house &b%reg%&7.", "remHouse"),
    creHouse("&7You've &acreated&7 the house &b%reg%&7.", "creHouse");

    private String text;
    private final String path;

    Messages(String text, String path){
        this.text = text;
        this.path = path;
    }

    public String getText(){
        return colorize(this.text);
    }

    public static void loadMessages(FileConfiguration f){
        for(Messages x : Messages.values()){
            x.text = f.getString(x.path);
        }
        Housing.console.sendMessage(colorize(Housing.pTag + "Messages &aLoaded&7!"));
    }

}
