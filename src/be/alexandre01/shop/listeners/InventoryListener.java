package be.alexandre01.shop.listeners;

import be.alexandre01.shop.configs.ConfigReader;
import be.alexandre01.shop.enums.OrderType;
import be.alexandre01.shop.gui.OrderGUI;
import be.alexandre01.shop.objects.MainItem;
import be.alexandre01.shop.objects.ShopItem;
import be.alexandre01.shop.objects.ShopMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class InventoryListener implements org.bukkit.event.Listener {
    private ConfigReader configReader;
    public InventoryListener(ConfigReader configReader){
        this.configReader = configReader;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        String title = ((InventoryEvent)event).getView().getTitle();

        if(event.getClickedInventory() != null){

        //BASE INVENTORY
        if(title.equals(configReader.getTitle())){
            Player player = (Player) event.getWhoClicked();
            if(configReader.hasItemInSlot(event.getSlot())){
                MainItem mainItem = configReader.getItemBySlot(event.getSlot());
                mainItem.execute(player,configReader);
                event.setCancelled(true);
                return;
            }

    }
        //SHOP MENU INVENTORY
        if(configReader.getShopInventories().containsKey(title)){
            Player player = (Player) event.getWhoClicked();
            Inventory inv = configReader.getShopInventories().get(title);
            ShopMenu shopMenu = configReader.getShopMenuByInventory(inv);
            if(shopMenu.hasItemInSlot(event.getSlot(),shopMenu.getActualPage(player))){
                event.setCancelled(true);
                ShopItem shopItem = shopMenu.getItemBySlot(event.getSlot(),shopMenu.getActualPage(player));
                switch (event.getClick()){
                    case LEFT: shopItem.execute(player,configReader,OrderType.BUY); break;
                    case MIDDLE:    int amount= 0;
                        for (ItemStack its : player.getInventory().getContents()) {
                            if(its != null){
                                if(its.getType().equals(shopItem.toItemStack().getType())){
                                   amount= its.getAmount()+amount;
                                }
                            }
                        }
                        player.closeInventory();
                        if(amount == 0){
                           player.sendMessage(shopMenu.getNotenoughtitem());
                            break;
                        }
                        shopItem.sell(player,shopItem.getSellPrice(player)*amount,amount);
                        break;
                    case RIGHT: shopItem.execute(player,configReader, OrderType.SELL); break;

                }
                return;

            }
        }
        //ORDER INVENTORY
        Player player = (Player) event.getWhoClicked();
            if(configReader.getCurrentShopMenu().containsKey(player)){
                System.out.println("yes");
                ShopMenu shopMenu = configReader.getCurrentShopMenu().get(player);
                if(shopMenu.getPlayerOrderGUI().containsKey(player)){
                    System.out.println("yes2");
                    OrderGUI orderGUI = shopMenu.getPlayerOrderGUI().get(player);
                    if(orderGUI.getTitle().equals(title)){
                        System.out.println("yes3");
                        event.setCancelled(true);
                        if(event.getSlot() == 0){
                           shopMenu.openInventory(player,0);
                        }
                        switch (event.getSlot()){
                            case 19: orderGUI.setPrice(orderGUI.getPrice()-orderGUI.getInitialPrice()*64,-64); break;
                            case 20: orderGUI.setPrice(orderGUI.getPrice()-orderGUI.getInitialPrice()*16,-16); break;
                            case 21:orderGUI.setPrice(orderGUI.getPrice()-orderGUI.getInitialPrice(),-1); break;
                            case 23: orderGUI.setPrice(orderGUI.getPrice()+orderGUI.getInitialPrice(),1); break;
                            case 24:orderGUI.setPrice(orderGUI.getPrice()+orderGUI.getInitialPrice()*16,16); break;
                            case 25:orderGUI.setPrice(orderGUI.getPrice()+orderGUI.getInitialPrice()*64,64); break;
                            case 31: if(orderGUI.getOrderType().equals(OrderType.BUY)){
                                orderGUI.getShopItem().buy(player,orderGUI.getPrice(),orderGUI.getAmount());
                                shopMenu.openInventory(player);
                            }else {
                                orderGUI.getShopItem().sell(player,orderGUI.getPrice(),orderGUI.getAmount());
                            }

                        }
                    }
                }
                }
            }

    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = (Player) event.getPlayer();
        if(configReader.getCurrentShopMenu().containsKey(player)){
            ShopMenu shopMenu = configReader.getCurrentShopMenu().get(player);
            if(shopMenu.getPlayerOrderGUI().containsKey(player)){
                   configReader.getCurrentShopMenu().remove(player);
                   shopMenu.getPlayerOrderGUI().remove(player);
                }
            }

    }

}
