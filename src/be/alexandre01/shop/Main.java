package be.alexandre01.shop;

import be.alexandre01.shop.api.ShopAPI;
import be.alexandre01.shop.commands.ShopCommand;
import be.alexandre01.shop.configs.ConfigReader;
import be.alexandre01.shop.configs.YamlUtils;
import be.alexandre01.shop.listeners.InventoryListener;
import be.alexandre01.shop.objects.ShopItem;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;


public class Main extends JavaPlugin {
    YamlUtils base;
    public static Main instance;
    private ConfigReader configReader;

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private HashMap<Player,Long> eventSell = new HashMap<>();
    private HashMap<Player,Integer> sell_amount = new HashMap<>();
    private HashMap<Player,ShopItem> lastShopItem = new HashMap<>();

    @Override
    public void onEnable() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEEE:HH:mm:ss", Locale.ENGLISH);
        System.out.println(format.format(now));
        instance = this;
        configReader = new ConfigReader(this);
    getCommand("shop").setExecutor(new ShopCommand());
    getServer().getPluginManager().registerEvents(new InventoryListener(configReader), this);
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
     //   setupChat();
        getEconomy();
    }

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }
    public void setBase(YamlUtils base) {
        this.base = base;
    }

    public ConfigReader getConfigReader() {
        return configReader;
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;

    }
 /*   public  boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }*/

    public  boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }



    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public boolean isLegacy(){
        String a = Main.instance.getServer().getClass().getPackage().getName();
        String version = a.substring(a.lastIndexOf('.') + 1);
        System.out.println(version);
        switch (version){
            case "v1_7_R1" : return false;
            case "v1_7_R2" : return false;
            case "v1_7_R3": return false;
            case "v1_7_R4": return false;
            case "v1_8_R1": return false;
            case "v1_8_R2": return false;
            case "v1_8_R3": return false;
            case "v1_9_R1": return false;
            case "v1_9_R2": return false;
            case "v1_10_R1": return false;
            case "v1_11_R1": return false;
            case "v1_12_R1": return false;
            default:return true;
        }

    }

    public HashMap<Player, Long> getEventSell() {
        return eventSell;
    }

    public HashMap<Player, Integer> getSell_amount() {
        return sell_amount;
    }

    public HashMap<Player, ShopItem> getLastShopItem() {
        return lastShopItem;
    }
}

