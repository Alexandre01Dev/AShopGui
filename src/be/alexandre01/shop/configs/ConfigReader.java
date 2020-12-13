package be.alexandre01.shop.configs;

import be.alexandre01.shop.Main;
import be.alexandre01.shop.calendar.DateContainer;
import be.alexandre01.shop.calendar.TimersManager;
import be.alexandre01.shop.enums.SoundReceiver;
import be.alexandre01.shop.objects.MainItem;
import be.alexandre01.shop.objects.OrderData;
import be.alexandre01.shop.objects.ShopMenu;
import be.alexandre01.shop.utils.FileConfig;
import be.alexandre01.shop.utils.HeadDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigReader {
    Main m;
    private int invSlot;
    private String invTitle;
    private HashMap<String,MainItem> itemHM;
    private HashMap<ShopMenu, String> shopMenuHM;
    private HashMap<Integer, MainItem> index;
    private HeadDatabase headDatabase;

    private OrderData orderData;
    private HashMap<String, Inventory> shopInventories;
    private HashMap<Inventory, ShopMenu> inventoryShopMenus;
    private HashMap<Player,ShopMenu> currentShopMenu;
    private Inventory inv;
    YamlUtils base;
    YamlUtils eventsYML;
    public ConfigReader(Main main){
        this.m = main;
        index = new HashMap<>();
        base = new YamlUtils(m, "base.yml");
        eventsYML = new YamlUtils(m,"events.yml");
        itemHM = new HashMap<>();
        shopMenuHM = new HashMap<>();
        currentShopMenu = new HashMap<>();
        shopInventories = new HashMap<>();
        inventoryShopMenus = new HashMap<>();
        Main.instance.setBase(base);
        if(!FileConfig.contains(main.getDataFolder()+"inventories")){
            FileConfig.createDir(main.getDataFolder()+"/inventories");
            //INITIALIZE DEFAULTS FILE
            new YamlUtils(main,"blocs.yml",FileConfig.getPath("/inventories/blocs.yml"));
        }
        headDatabase = new HeadDatabase();
        orderData = new OrderData(headDatabase);
        readMainFile();
        readEventsFile();
        System.out.println("finish");
        if(!TimersManager.index.isEmpty() && eventsYML.getConfig().getBoolean("enable")){
            new TimersManager(ChatColor.translateAlternateColorCodes('&',eventsYML.getConfig().getString("message")));
        }
    }
    private void readEventsFile(){
        System.out.println(eventsYML.getConfig());
        System.out.println(eventsYML.getConfig().getKeys(true));
        for (String key : eventsYML.getConfig().getConfigurationSection("cancellingEvents").getKeys(false)){
           for (String time : eventsYML.getConfig().getStringList("cancellingEvents."+key)){
            if(!time.split(":")[0].matches("[0-9]+|-\\d+")){
                m.getLogger().severe("Characters in file events.yml is invalid");
                m.getPluginLoader().disablePlugin(m);
            }
            int hour = Integer.parseInt(time.split(":")[0]);
            if(!time.split(":")[1].matches("[0-9]+|-\\d+")){
                m.getLogger().severe("Characters in file events.yml is invalid");
                m.getPluginLoader().disablePlugin(m);
            }
            int minute = Integer.parseInt(time.split(":")[1]);

            new DateContainer(key,hour,minute);
        }
        }
    }
    private void readMainFile(){
        //CONFIG
        invSlot = base.getConfig().getInt("config.inventoryslot");
        invTitle = base.getConfig().getString("config.inventorytitle").replaceAll("&","§");
        //INVENTORIES
        inv = Bukkit.createInventory(null, invSlot,invTitle);
        for(String key : base.getConfig().getConfigurationSection("inventories").getKeys(false)){
            //READ ITEM - ADD TO LIST - ADD TO INVENTORY
            MainItem mainItem = getSectionItem(key);
            itemHM.put(key,mainItem);
            for (int slot : mainItem.getSlots()) {
                inv.setItem(slot,mainItem.toItemStack());
                index.put(slot,mainItem);
            }
            //READ SECTION AND PUT IN HASHMAP
            if(mainItem.getRedirection() != null){
                ShopMenu shopMenu = new ShopMenu(mainItem.getRedirection());
                shopMenuHM.put(shopMenu,key);
                Inventory inv = shopMenu.getInventory(0);
                shopInventories.put(shopMenu.getInventoryTitle(), inv);
                inventoryShopMenus.put(inv,shopMenu);
            }

        }
        Inventory inv = Bukkit.createInventory(null, invSlot,invTitle);


    }
    public ShopMenu getShopMenu(String key){
        for (ShopMenu shopMenu : shopMenuHM.keySet()){
            if(shopMenu.getName().equals(key)){
                return shopMenu;
            }
        }

        return null;
    }
    private MainItem getSectionItem(String key){
        String path = "inventories."+key+".";
        MainItem mainItem;
        //READ BASE FILE
        if(base.getConfig().contains(path+"Slots")){
            mainItem = new MainItem(base.getConfig().getIntegerList(path+"Slots"));
        }else {
            mainItem = new MainItem(base.getConfig().getInt(path+"Slot"));
        }
        if(base.getConfig().contains(path+"Name")){
            mainItem.setName(base.getConfig().getString(path+"Name").replaceAll("&","§"));
        }
        if(base.getConfig().contains(path+"Material")){
            mainItem.setMaterial(base.getConfig().getString(path+"Material"));
        }
        if(base.getConfig().contains(path+"Redirection")){
            mainItem.setRedirection(base.getConfig().getString(path+"Redirection"));

        }
        if(base.getConfig().contains(path+"Enchant")){
            mainItem.addEnchant(base.getConfig().getString(path+"Enchant"));
        }
        if(base.getConfig().contains(path+"Enchants")){
            base.getConfig().getStringList(path+"Enchant").forEach(mainItem::addEnchant);
        }
        if(base.getConfig().contains(path+"AddItemFlag")){
            mainItem.addItemFlag(ItemFlag.valueOf(base.getConfig().getString(path+"AddItemFlag").toUpperCase()));
        }
        if(base.getConfig().contains(path+"AddItemFlags")){
            for (String itf : base.getConfig().getStringList(path+"AddItemFlag"))
                mainItem.addItemFlag(ItemFlag.valueOf(base.getConfig().getString(path+"AddItemFlag").toUpperCase()));
        }
        if(base.getConfig().contains(path+"Lore")){
            List<String> list = new ArrayList<>();
            for (String lore :   base.getConfig().getStringList(path+"Lore")){
                list.add(lore.replaceAll("&","§"));
            }
           mainItem.setLore(list);
        }

        if(base.getConfig().contains(path+"PlaySound")){
            mainItem.setVolume((float) base.getConfig().getDouble(path+"PlaySound.volume"));
            mainItem.setPitch((float) base.getConfig().getDouble(path+"PlaySound.pitch"));
            mainItem.setSound(base.getConfig().getString(path+"PlaySound.sound"));
            mainItem.setSoundReceiver(SoundReceiver.valueOf(base.getConfig().getString(path+"PlaySound.receiver").toUpperCase()));
        }
        return mainItem;
    }

    public ShopMenu getShopMenuByInventory(Inventory inventory) {
        return inventoryShopMenus.get(inventory);
    }

    public HashMap<Integer, MainItem> getIndex() {
        return index;
    }
    public boolean hasItemInSlot(int slot){
        return index.containsKey(slot);
    }
    public MainItem getItemBySlot(int slot){
     return index.get(slot);
    }

    public int getInvSlot(){
        return invSlot;
    }

    public HashMap<String, MainItem> getItemHashMap() {
        return itemHM;
    }
    public MainItem getMainItem(String key){
        return itemHM.get(key);
    }
    public Inventory getMainInventory() {
        Inventory clone = Bukkit.createInventory(null,invSlot,invTitle);
        clone.setContents(inv.getContents().clone());
        return clone; //JUSTE UN TEST POUR ESSAYER D'OPTI EN CAS DE PROBLEME JUSTE METTRE return inv (ESSAYER DE COPIER L'INVENTAIRE EN CAS DE SPAM );
    }
    public void reloadConfig(){
        index = new HashMap<>();
        base = new YamlUtils(m, "base.yml");
        itemHM = new HashMap<>();
        shopMenuHM = new HashMap<>();
        currentShopMenu = new HashMap<>();
        shopInventories = new HashMap<>();
        inventoryShopMenus = new HashMap<>();
        readMainFile();
    }

    public String getTitle() {
        return invTitle;
    }

    public OrderData getOrderData() {
        return orderData;
    }
    public HeadDatabase getHeadDatabase() {
        return headDatabase;
    }
    public HashMap<String, Inventory> getShopInventories() {
        return shopInventories;
    }

    public HashMap<Player, ShopMenu> getCurrentShopMenu() {
        return currentShopMenu;
    }

}
