package be.alexandre01.shop.api;

import be.alexandre01.shop.Main;
import be.alexandre01.shop.configs.ConfigReader;
import be.alexandre01.shop.objects.ShopMenu;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;


public class ShopAPI {
    public static Economy getEconomy(){
        return Main.getEconomy();
    }
    public static Chat getChat(){
        return Main.getChat();
    }
    public static Permission getPermission(){
        return Main.getPermissions();
    }
    public static ShopMenu getShopMenu(String name){
        return Main.instance.getConfigReader().getShopMenu(name);
    }
    public static ConfigReader getMainMenu(){
        return Main.instance.getConfigReader();
    }
}
