package be.alexandre01.shop.objects;

import be.alexandre01.shop.Main;
import be.alexandre01.shop.configs.YamlUtils;
import be.alexandre01.shop.enums.SoundReceiver;
import be.alexandre01.shop.gui.OrderGUI;
import be.alexandre01.shop.utils.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopMenu {
    private String name;
    private int invSlot;
    private String startevent;
    private String buymessage;
    private String sellmessage;
    private String notamountmessage;
    private String notenoughtitem;
    private String endevent;
    private String invTitle;
    private int eventtimer;
    private boolean useAsyncTimer;
    private boolean lastSlotUsed = false;
    private HashMap<String,ShopItem> itemHM;
    private ArrayList<ShopItem> shopItems;
    private YamlUtils yml;
    private boolean hasMultiplePage = false;
    private HashMap<Integer,ArrayList<ShopItem>> modOnOpen = new HashMap<>();
    private HashMap<Integer, Inventory> inventoryByPage;
    private HashMap<Integer, ShopItem> index;
    private HashMap<Player, Integer> actualPage;
    private HashMap<Player, OrderGUI> playerOrderGUI;
    public ShopMenu(String name){
        this.name = name;
        shopItems = new ArrayList<>();
        itemHM = new HashMap<>();
        actualPage = new HashMap<>();
        index = new HashMap<>();
        inventoryByPage = new HashMap<>();
        playerOrderGUI = new HashMap<>();
        yml = new YamlUtils(Main.instance,name+".yml",FileConfig.getPath("/inventories/"+name+".yml"));
        readShopFile();
    }

    public int getInventorySlot() {
        return invSlot;
    }

    public String getInventoryTitle() {
        return invTitle;
    }

    public HashMap<String, ShopItem> getItemHashMap() {
        return itemHM;
    }
    public ShopItem getShopItem(String key){
        return itemHM.get(key);
    }
    public YamlUtils getYml() {
        return yml;
    }

    public Inventory getInventory(int page,Player player) {
        Inventory clone = Bukkit.createInventory(null,invSlot,invTitle);
        clone.setContents(inventoryByPage.get(page).getContents().clone());
        if(modOnOpen.containsKey(page)){
            for(ShopItem shopItem : modOnOpen.get(page)){
                ItemStack it = shopItem.toItemStack();
                ItemMeta itm = it.getItemMeta();
                ArrayList<String> s = new ArrayList<>();
                for (String lores : shopItem.getDefaultLore()){
                    s.add(lores.replaceAll("%sell_price%",""+shopItem.getSellPrice(player)).replaceAll("%player_balance%",""+Main.getEconomy().getBalance(player)).replaceAll("&","§"));
                }
                itm.setLore(s);

                itm.setDisplayName(shopItem.getName().replaceAll("%sell_price%",""+shopItem.getSellPrice(player)).replaceAll("&","§").replaceAll("%player_balance%",""+Main.getEconomy().getBalance(player)));
                it.setItemMeta(itm);
                for (int slot : shopItem.getSlots()) {
                    clone.setItem(slot- page*invSlot,it);

                }
            }
        }


        return clone;
    }
    public Inventory getInventory(int page) {
        Inventory clone = Bukkit.createInventory(null,invSlot,invTitle);
        clone.setContents(inventoryByPage.get(page).getContents().clone());
        return inventoryByPage.get(page);
    }
    public void openInventory(Player player){
        player.openInventory(getInventory(getActualPage(player),player));
    }
    public void openInventory(Player player,int page){

        player.openInventory(getInventory(page,player));
        actualPage.put(player,page);
    }
    public int getActualPage(Player player){
        if(!actualPage.containsKey(player)){
            actualPage.put(player,0);
        }
        return actualPage.get(player);
    }
    public ShopItem getItemBySlot(int slot,int page){
        return index.get(slot+page*invSlot);
    }
    public ShopItem getItemBySlot(int slot){
        return index.get(slot);
    }

    public boolean hasItemInSlot(int slot,int page){
        return index.containsKey(slot+page*invSlot);
    }
    public boolean hasItemInSlot(int slot){
        return index.containsKey(slot);
    }
    private void readShopFile(){
        //CONFIG
        invSlot = yml.getConfig().getInt("config.inventoryslot");
        startevent = yml.getConfig().getString("config.start_price_event").replaceAll("&","§");;
        notamountmessage = yml.getConfig().getString("config.not_amout_message").replaceAll("&","§");;
        buymessage = yml.getConfig().getString("config.buy_message").replaceAll("&","§");;
        sellmessage = yml.getConfig().getString("config.sell_message").replaceAll("&","§");;
        notenoughtitem = yml.getConfig().getString("config.not_enought_item").replaceAll("&","§");;
        endevent = yml.getConfig().getString("config.endevent").replaceAll("&","§");;
        eventtimer = yml.getConfig().getInt("config.eventtimer");
        invTitle = yml.getConfig().getString("config.inventorytitle").replaceAll("&","§");
        if(yml.getConfig().contains("config.runTimerAsyncOnEvent")){
            useAsyncTimer = yml.getConfig().getBoolean("config.runTimerAsyncOnEvent");
        }

        //INVENTORIES

        for(String key : yml.getConfig().getConfigurationSection("items").getKeys(false)){
            //READ ITEM - ADD TO LIST - ADD TO INVENTORY
            ShopItem shopItem = getSectionItem(key);
            shopItems.add(shopItem);
            itemHM.put(key,shopItem);
            for (int slot : shopItem.getSlots()) {
                int page = roundUp(slot);
                if(yml.getConfig().contains("items."+key+"."+"Force-update")){
                    if(yml.getConfig().getBoolean("items."+key+"."+"Force-update")){

                        if(!modOnOpen.containsKey(page)){
                            modOnOpen.put(page,new ArrayList<>());
                        }
                        ArrayList<ShopItem> s = modOnOpen.get(page);
                        s.add(shopItem);
                        modOnOpen.put(page,s);
                    }
                }
                if(invSlot == slot+1){
                  lastSlotUsed = true;
                }else {
                    if(page > 0){
                        hasMultiplePage = true;


                    }

                    if(!inventoryByPage.containsKey(page)){
                        Inventory inv = Bukkit.createInventory(null, invSlot,invTitle);
                        inventoryByPage.put(page,inv);
                    }
                    Inventory inv = inventoryByPage.get(page);
                    inv.setItem(slot- page*invSlot,shopItem.toItemStack());
                    index.put(slot,shopItem);
                }
                }

            }


    }
    private ShopItem getSectionItem(String key){
        String path = "items."+key+".";
        ShopItem shopItem;
        //READ BASE FILE



        if(yml.getConfig().contains(path+"Slots")){
            shopItem = new ShopItem(this,yml.getConfig().getIntegerList(path+"Slots"));
        }else {
            shopItem = new ShopItem(this,yml.getConfig().getInt(path+"Slot"));
        }

        if(yml.getConfig().contains(path+"Buy")){
            shopItem.setBuyEnable(true);
            shopItem.setBuyPrice(yml.getConfig().getDouble(path+"Buy.price"));
            if(yml.getConfig().contains(path+"Buy.commands"))
            {
                shopItem.setCommands(new ArrayList<>(yml.getConfig().getStringList(path + "Buy.commands")));
            }
            if(yml.getConfig().contains(path+"Buy.orderPanel")){
                shopItem.setCanBuyMultipleTimes(yml.getConfig().getBoolean(path+"Buy.orderPanel"));
            }
            if(yml.getConfig().contains(path+"Buy.give_current_item")){
                shopItem.setGiveCurrentItem(yml.getConfig().getBoolean(path+"Buy.give_current_item"));
            }
        }
        if(yml.getConfig().contains(path+"Sell")){
            shopItem.setSellEnable(true);
            shopItem.setSellPrice(yml.getConfig().getDouble(path+"Sell.price"));
            if(yml.getConfig().contains(path+"Sell.orderPanel")){
                shopItem.setCanSellMultipleTimes(yml.getConfig().getBoolean(path+"Sell.orderPanel"));
            }
            if(yml.getConfig().contains(path+"Sell.price_multiplier_up")){
                shopItem.setSellMultiplierUp(yml.getConfig().getDouble(path+"Sell.price_multiplier_up"));
            }
            if(yml.getConfig().contains(path+"Sell.price_multiplier_down")){
                shopItem.setSellMultiplierDown(yml.getConfig().getDouble(path+"Sell.price_multiplier_down"));
            }
            if(yml.getConfig().contains(path+"Sell.price_event")){
                shopItem.setPriceEvent(yml.getConfig().getInt(path+"Sell.price_event"));
            }
        }
        if(yml.getConfig().contains(path+"Name")){
            shopItem.setName(yml.getConfig().getString(path+"Name").replaceAll("&","§").replaceAll("%sell_price%", String.valueOf(shopItem.getSellPrice())).replaceAll("%buy_price%",String.valueOf(shopItem.getBuyPrice())));
        }
        if(yml.getConfig().contains(path+"Material")){
            shopItem.setMaterial(yml.getConfig().getString(path+"Material"));
        }
        if(yml.getConfig().contains(path+"Enchant")){
            shopItem.addEnchant(yml.getConfig().getString(path+"Enchant"));
        }
        if(yml.getConfig().contains(path+"Enchants")){
            yml.getConfig().getStringList(path+"Enchant").forEach(shopItem::addEnchant);
        }
        if(yml.getConfig().contains(path+"AddItemFlag")){
            shopItem.addItemFlag(ItemFlag.valueOf(yml.getConfig().getString(path+"AddItemFlag").toUpperCase()));
        }
        if(yml.getConfig().contains(path+"AddItemFlags")){
            for (String itf : yml.getConfig().getStringList(path+"AddItemFlag"))
                shopItem.addItemFlag(ItemFlag.valueOf(yml.getConfig().getString(path+"AddItemFlag").toUpperCase()));
        }
        if(yml.getConfig().contains(path+"Lore")){
            List<String> list = new ArrayList<>();
            List<String> unChanged = new ArrayList<>();
            for (String lore : yml.getConfig().getStringList(path+"Lore")){
                unChanged.add(lore);
                list.add(lore.replaceAll("&","§").replaceAll("%sell_price%", String.valueOf(shopItem.getSellPrice())).replaceAll("%buy_price%",String.valueOf(shopItem.getBuyPrice())));
            }
            shopItem.setDefaultLore(unChanged);
            shopItem.setLore(list);
        }
        if(yml.getConfig().contains(path+"toPage")){
            shopItem.setToPage(yml.getConfig().getInt(path+"toPage"));
        }
        if(yml.getConfig().contains(path+"PlaySound")){
            shopItem.setVolume((float) yml.getConfig().getDouble(path+"PlaySound.volume"));
            shopItem.setPitch((float) yml.getConfig().getDouble(path+"PlaySound.pitch"));
            shopItem.setSound(yml.getConfig().getString(path+"PlaySound.sound"));
            shopItem.setSoundReceiver(SoundReceiver.valueOf(yml.getConfig().getString(path+"PlaySound.receiver").toUpperCase()));
        }

        return shopItem;
    }

    public HashMap<Player, OrderGUI> getPlayerOrderGUI() {
        return playerOrderGUI;
    }

    public String getName() {
        return name;
    }


    public ArrayList<ShopItem> getShopItems() {
        return shopItems;
    }

    public String getStartevent() {
        return startevent;
    }

    public String getBuymessage() {
        return buymessage;
    }

    public String getSellmessage() {
        return sellmessage;
    }

    public String getNotamountmessage() {
        return notamountmessage;
    }

    public String getNotenoughtitem() {
        return notenoughtitem;
    }

    public String getEndevent() {
        return endevent;
    }

    public int getEventtimer() {
        return eventtimer;
    }
    private int roundUp(int num) {
        return num/invSlot;
    }

    public void setActualPage(Player player, Integer actualPage) {
        this.actualPage.put(player, actualPage);
    }

    public boolean isUseAsyncTimer() {
        return useAsyncTimer;
    }
}
