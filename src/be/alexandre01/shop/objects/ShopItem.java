package be.alexandre01.shop.objects;

import be.alexandre01.shop.Main;
import be.alexandre01.shop.configs.ConfigReader;
import be.alexandre01.shop.enums.OrderType;
import be.alexandre01.shop.enums.SoundReceiver;
import be.alexandre01.shop.gui.OrderGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShopItem {
    private int slot = -1;
    private List<Integer> slots;
    private String name = "";
    private Material mat = Material.GLASS;
    private Enchantment enchant;
    private ItemStack itemStack = new ItemStack(mat);
    private List<String> lore;
    private ItemMeta itemMeta = itemStack.getItemMeta();
    private ArrayList<ItemFlag> itemFlags;
    private Byte data = (byte)0;
    private String redirection;
    private HashMap<Player,Integer> sell_price;
    private float volume;
    private boolean canBuyMultipleTimes = false;
    private boolean canSellMultipleTimes = false;
    private float pitch;
    private boolean buyEnable = false;
    private boolean sellEnable = false;
    private boolean giveCurrentItem = false;
    private int priceEvent = -1;
    private List<String> commands = null;
    private int toPage = -1;
    private List<String> defaultLore;
    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean isGiveCurrentItem() {
        return giveCurrentItem;
    }

    public void setGiveCurrentItem(boolean giveCurrentItem) {
        this.giveCurrentItem = giveCurrentItem;
    }

    private Sound sound;
    private double sellPrice = -1;
    private double sellMultiplierUp;
    private double sellMultiplierDown;
    private double buyPrice = -1;
    private double buyMultiplier;
    private SoundReceiver soundReceiver = SoundReceiver.NONE;
    private ShopMenu shopMenu;
    public ShopItem(ShopMenu shopMenu ,int slot){
        this.slot = slot;
        this.shopMenu = shopMenu;
        this.sell_price = new HashMap<>();
        itemFlags = new ArrayList<>();

    }
    public ShopItem(ShopMenu shopMenu ,List<Integer> slots){
        this.slots = slots;
        this.shopMenu = shopMenu;
        this.sell_price = new HashMap<>();
        itemFlags = new ArrayList<>();

    }

    public boolean isSellable(){
        if(sellPrice != -1) return true;
        return false;
    }
    public boolean isBuyable(){
        if(buyPrice != -1) return true;
        return false;
    }

    public int getToPage() {
        return toPage;
    }

    public void setToPage(int toPage) {
        this.toPage = toPage;
    }

    public List<String> getDefaultLore() {
        return defaultLore;
    }

    public void setDefaultLore(List<String> defaultLore) {
        this.defaultLore = defaultLore;
    }

    public void setRedirection(String redirection) {
        this.redirection = redirection;
    }

    public ItemStack toItemStack(){
        return itemStack.clone();
    }
    public void setToInventory(Inventory inv){
        inv.setItem(slot,itemStack);
    }
    //SETTER
    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setData(Byte data) {
        MaterialData materialData = itemStack.getData();
        materialData.setData(data);
        this.data = data;
        itemStack.setData(materialData);
    }
    public void addItemFlag(ItemFlag itemFlag){
        itemFlags.add(itemFlag);
        itemMeta.addItemFlags(itemFlag);
        updateItemMeta();
    }

    public void setBuyEnable(boolean buyEnable) {
        this.buyEnable = buyEnable;
    }

    public void setSellEnable(boolean sellEnable) {
        this.sellEnable = sellEnable;
    }

    public void setName(String name) {
        this.name = name;
        itemMeta.setDisplayName(name);
        updateItemMeta();
    }

    public void setMaterial(String mat) {
        boolean isId ;
        try{
            Integer.parseInt(mat);
            isId = true;
        }catch (Exception e){
            isId = false;
        }
        Material newMat;
        if(!isId){
         newMat = Material.matchMaterial(mat);
        }else {
            newMat = Material.getMaterial( Integer.parseInt(mat));
        }
        if(newMat == null) return;
        this.mat = newMat;
        itemStack.setType(newMat);
    }
    public void addEnchant(String enchant){
        Enchantment newEnc = Enchantment.getByName(enchant);
        if(newEnc == null) return;
        this.enchant = newEnc;
        itemStack.addEnchantment(newEnc,1);
    }

    public void setPriceEvent(int priceEvent) {
        this.priceEvent = priceEvent;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setSound(String sound) {
        this.sound = Sound.valueOf(sound.toUpperCase());
    }

    public void setSoundReceiver(SoundReceiver soundReceiver) {
        this.soundReceiver = soundReceiver;
    }

    public void playSound(Player player){
        switch (soundReceiver){
            case PLAYER: player.playSound(player.getLocation(),sound,volume,pitch); break;
            case WORLD: player.getWorld().playSound(player.getLocation(),sound,volume,pitch); break;
            case SERVER:
                for (Player players : Bukkit.getOnlinePlayers()){
                    players.playSound(players.getLocation(),sound,volume,pitch);
                } break;
            default: break;
        }
    }
    public void setLore(List<String> lore) {
        this.lore = lore;
        itemMeta.setLore(lore);
        updateItemMeta();
    }

    public void setCanBuyMultipleTimes(boolean canBuyMultipleTimes) {
        this.canBuyMultipleTimes = canBuyMultipleTimes;
    }
    public void setCanSellMultipleTimes(boolean canSellMultipleTimes) {
        this.canSellMultipleTimes = canSellMultipleTimes;
    }
    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }

    public void setItemFlags(ArrayList<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setSellMultiplierUp(double sellMultiplier) {
        this.sellMultiplierUp = sellMultiplier;
    }

    public void setSellMultiplierDown(double sellMultiplier) {
        this.sellMultiplierDown = sellMultiplier;
    }
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setBuyMultiplier(double buyMultiplier) {
        this.buyMultiplier = buyMultiplier;
    }
    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public void setEnchant(Enchantment enchant) {
        this.enchant = enchant;
    }
    //GETTER

    public List<Integer> getSlots() {
        ArrayList<Integer> slots = new ArrayList<>();
        if(slot != -1){
            slots.add(slot);
            return slots;
        }
        slots.addAll(this.slots);
        return slots;
    }
    public List<String> getLore() {
        return lore;
    }



    public String getRedirection(){
        return redirection;
    }

    public double getSellPrice(Player player) {
        if(Main.instance.getEventSell().containsKey(player)){
            if(TimeUnit.MILLISECONDS.toSeconds(new Date().getTime()-Main.instance.getEventSell().get(player)) >= getShopMenu().getEventtimer()){
                Main.instance.getEventSell().remove(player);
                player.sendMessage(getShopMenu().getEndevent().replaceAll("%sell_price%", String.valueOf(getSellPrice(player))).replaceAll("%buy_price%",String.valueOf(getBuyPrice())));
                Main.instance.getSell_amount().put(player,0);
                return sellPrice;
            }
            if(Main.instance.getLastShopItem().get(player).getShopMenu().equals(getShopMenu())){
                 if(Main.instance.getLastShopItem().get(player) == this){
                    return sellPrice*getSellMultiplierDown();
                }else {
                     return sellPrice*getSellMultiplierUp();
                 }
            }
        }


        return sellPrice;
    }
    public double getSellPrice() {
        return sellPrice;
    }
    public double getSellMultiplierUp() {
        return sellMultiplierUp;
    }

    public double getSellMultiplierDown() {
        return sellMultiplierDown;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public int getPriceEvent() {
        return priceEvent;
    }

    public double getBuyMultiplier() {
        return buyMultiplier;
    }

    public double getVolume() {
        return volume;
    }

    public double getPitch() {
        return pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public SoundReceiver getSoundReceiver() {
        return soundReceiver;
    }

    public String getName() {
        return name;
    }

    public Material getMat() {
        return mat;
    }

    public Enchantment getEnchant() {
        return enchant;
    }

    public ArrayList<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    public Byte getData() {
        return data;
    }

    private void updateItemMeta(){
        itemStack.setItemMeta(itemMeta);
    }

    public void execute(Player player){
        playSound(player);
        if(getRedirection() != null){
            Main.instance.getConfigReader().getShopMenu(getRedirection()).openInventory(player,0);
        }

        if(isBuyable()){
            if(isGiveCurrentItem()){
                player.getInventory().addItem(itemStack);
                player.updateInventory();
            }
            if(getCommands() != null){
                for (String cmd : getCommands()){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
                }
            }
        }
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    public void execute(Player player, ConfigReader configReader, OrderType orderType){
        playSound(player);
        if(getRedirection() != null){
         configReader.getShopMenu(getRedirection()).openInventory(player,0);
        }
        if(getToPage() != -1){

           getShopMenu().openInventory(player,getToPage());
           shopMenu.setActualPage(player,getToPage());
        }
        if(orderType.equals(OrderType.BUY) && canBuyMultipleTimes|| orderType.equals(OrderType.SELL) && canSellMultipleTimes){

            OrderGUI orderGUI = new OrderGUI(player,this,configReader.getOrderData(), orderType);
            orderGUI.openInventory();
            configReader.getCurrentShopMenu().put(player, shopMenu);
            shopMenu.getPlayerOrderGUI().put(player, orderGUI);
        }else {
            if(orderType.equals(OrderType.BUY)){
                buy(player,buyPrice,1);
            }else {
                sell(player,sellPrice,1);
            }
        }

    }
    public void buy(Player player,Double price,int amount){
        if(buyEnable){

        if(!(Main.getEconomy().getBalance(player) >= price)){
            player.sendMessage(getShopMenu().getNotamountmessage().replaceAll("%sell_price%", String.valueOf(getSellPrice(player))).replaceAll("%buy_price%",String.valueOf(getBuyPrice())));
           return;
        }
            if(isGiveCurrentItem()){
                ItemStack it = itemStack.clone();
                it.setAmount(amount);
                player.getInventory().addItem(it);
                player.updateInventory();
            }



        if(getCommands() != null){
            for (String cmd : getCommands()){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
            }
        }
        Main.getEconomy().withdrawPlayer(player,price);
        player.sendMessage(getShopMenu().getBuymessage().replaceAll("%sell_price%", String.valueOf(getSellPrice(player))).replaceAll("%buy_price%",String.valueOf(getBuyPrice())));

        }
    }
    public void sell(Player player,Double price,int amount){
        if(sellEnable){
            ItemStack it = itemStack.clone();
            it.setAmount(amount);
            int totalAmount = 0;
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (ItemStack its : player.getInventory().getContents()) {
                if(its != null){
                    if(its.getType().equals(it.getType())){
                        totalAmount = totalAmount + its.getAmount();
                        itemStacks.add(its);
                    }
                }
            }
            if(totalAmount< amount){
                player.sendMessage(getShopMenu().getNotenoughtitem().replaceAll("%sell_price%", String.valueOf(getSellPrice(player))).replaceAll("%buy_price%",String.valueOf(getBuyPrice())));
                return;
            }

            for (ItemStack its : itemStacks) {
                if(its.getAmount() < amount){
                    amount = amount - its.getAmount();
                    player.getInventory().remove(its);
                }else {
                    if(its.getAmount() - amount == 0){
                        player.getInventory().remove(its);
                    }else {
                        its.setAmount(its.getAmount()-amount);
                        break;
                    }
                }
            }
                    player.sendMessage(getShopMenu().getSellmessage().replaceAll("%sell_price%", String.valueOf(getSellPrice(player))).replaceAll("%buy_price%",String.valueOf(getBuyPrice())));

                    //VENDU
                    if(!Main.instance.getEventSell().containsKey(player)){
                    if(!Main.instance.getLastShopItem().containsKey(player)) Main.instance.getLastShopItem().put(player,this);
                    if(!Main.instance.getSell_amount().containsKey(player)) {
                        Main.instance.getSell_amount().put(player,0);
                    }
                    if(!Main.instance.getLastShopItem().get(player).equals(this)){
                        Main.instance.getLastShopItem().put(player,this);
                        Main.instance.getSell_amount().put(player,0);
                    }
                    int currentAmmount = Main.instance.getSell_amount().get(player)+amount;
                    Main.instance.getSell_amount().put(player,currentAmmount);
                    if(getPriceEvent() >= 0){
                        if(currentAmmount >= getPriceEvent()){
                            player.sendMessage(getShopMenu().getStartevent().replaceAll("%sell_price%", String.valueOf(getSellPrice(player))).replaceAll("%buy_price%",String.valueOf(getBuyPrice())));
                            Main.instance.getEventSell().put(player,new Date().getTime());
                            if(getShopMenu().isUseAsyncTimer()){
                                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.instance, new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Main.instance.getEventSell().remove(player);
                                        player.sendMessage(getShopMenu().getEndevent().replaceAll("%sell_price%", String.valueOf(getSellPrice(player))).replaceAll("%buy_price%",String.valueOf(getBuyPrice())));
                                        Main.instance.getSell_amount().put(player,0);
                                    }
                                },20*getShopMenu().getEventtimer());
                            }
                        }
                    }



                }

            }
            player.updateInventory();
            Main.getEconomy().depositPlayer(player,price);
        }

    }


