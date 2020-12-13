package be.alexandre01.shop.gui;

import be.alexandre01.shop.Main;
import be.alexandre01.shop.enums.OrderType;
import be.alexandre01.shop.objects.OrderData;
import be.alexandre01.shop.objects.ShopItem;
import be.alexandre01.shop.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OrderGUI {
    ShopItem shopItem;
    OrderData orderData;
    OrderType orderType;
    Double price;
    Double initialPrice;
    Inventory inv;
    int amount = 1;
    ItemStack it;
    String invTitle;
    Player player;
    public OrderGUI(Player player,ShopItem shopItem, OrderData orderData, OrderType orderType){
        this.player = player;
        this.shopItem = shopItem;
        this.orderData = orderData;
        this.it = shopItem.toItemStack();
        this.orderType = orderType;
        if(orderType.equals(OrderType.BUY)){
            this.price = shopItem.getBuyPrice();
            this.initialPrice = shopItem.getBuyPrice();
        }else {
            this.price = shopItem.getSellPrice(player);
            this.initialPrice = shopItem.getSellPrice(player);
        }
        this.invTitle = orderData.getNameInv(orderType).replaceAll("%sell_price%", String.valueOf(this.price)).replaceAll("%buy_price%",String.valueOf(this.price));
        createInventory();
    }

    private void createInventory(){
        inv = Bukkit.createInventory(null, orderData.getSlotInv(),invTitle);
        if(orderData.getHeadDatabase().isOlderVersion()){
            inv.setItem(0, new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.RED).setName("§cRetour").toItemStack());
            inv.setItem(13,it);
            ItemStack minus64 = new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.RED).setName("§c-").toItemStack();
            minus64.setAmount(64);
            inv.setItem(19,minus64);
            ItemStack minus16 = new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.RED).setName("§c-").toItemStack();
            minus16.setAmount(16);
            inv.setItem(20,minus16);
            inv.setItem(21,new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.RED).setName("§c-").toItemStack());
            if(Main.instance.isLegacy()){
                inv.setItem(22,new ItemBuilder(Material.getMaterial("GRAY_STAINED_GLASS_PANE")).toItemStack());
            }else {
                inv.setItem(22,new ItemBuilder(Material.STAINED_GLASS_PANE).setWoolColor(DyeColor.GRAY).setName(" ").toItemStack());
            }

            inv.setItem(23,new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.GREEN).setName("§a+").toItemStack());
            ItemStack plus16 = new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.GREEN).setName("§a+").toItemStack();
            plus16.setAmount(16);
            inv.setItem(24,plus16);
            ItemStack plus64 = new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.GREEN).setName("§a+").toItemStack();
            plus64.setAmount(64);
            inv.setItem(25,plus64);
            inv.setItem(31,new ItemBuilder(Material.EMERALD_BLOCK).setName("§aConfirmer").toItemStack());
            return;
        }

        inv.setItem(0,orderData.getHeadDatabase().getHead("back"));
        inv.setItem(13,it);
        ItemStack minus64 = orderData.getHeadDatabase().getHead("minus");
        minus64.setAmount(64);
        inv.setItem(19,minus64);
        ItemStack minus16 = orderData.getHeadDatabase().getHead("minus");
        minus16.setAmount(16);
        inv.setItem(20,minus16);
        inv.setItem(21,orderData.getHeadDatabase().getHead("minus"));
        if(Main.instance.isLegacy()){
            inv.setItem(22,new ItemBuilder(Material.getMaterial("GRAY_STAINED_GLASS_PANE")).toItemStack());
        }else {
            inv.setItem(22,new ItemBuilder(Material.STAINED_GLASS_PANE).setWoolColor(DyeColor.GRAY).setName(" ").toItemStack());
        }

        inv.setItem(23,orderData.getHeadDatabase().getHead("plus"));
        ItemStack plus16 = orderData.getHeadDatabase().getHead("plus");
        plus16.setAmount(16);
        inv.setItem(24,plus16);
        ItemStack plus64 = orderData.getHeadDatabase().getHead("plus");
        plus64.setAmount(64);
        inv.setItem(25,plus64);
        inv.setItem(31,orderData.getHeadDatabase().getHead("check"));
    }
    public void setPrice(double price,int amount){
        this.price = price;
        this.amount = this.amount+amount;
        if(price < initialPrice){
            this.price = initialPrice;
            this.amount = 1;
        }
        if(initialPrice*64 < price){
            this.price = initialPrice*64;
            this.amount = 64;
        }
        it.setAmount(this.amount);
        inv.setItem(13,it);
        invTitle = orderData.getNameInv(orderType).replaceAll("%sell_price%", String.valueOf(this.price)).replaceAll("%buy_price%",String.valueOf(this.price));
           Inventory clone = Bukkit.createInventory(null,orderData.getSlotInv(),invTitle);
                 clone.setContents(inv.getContents().clone());
                 inv = clone;
                 player.openInventory(clone);
    }




    public Double getPrice() {
        return price;
    }

    public void openInventory(){
        player.openInventory(inv);
    }
    public Inventory getInventory() {
        return inv;
    }
    public ShopItem getShopItem(){
        return shopItem;
    }

    public Double getInitialPrice() {
        return initialPrice;
    }

    public ItemStack getItemStack() {
        return it;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public int getAmount() {
        return amount;
    }

    public String getTitle() {
        return invTitle;
    }
}
