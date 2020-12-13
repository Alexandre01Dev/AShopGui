package be.alexandre01.shop.objects;

import be.alexandre01.shop.Main;
import be.alexandre01.shop.configs.YamlUtils;
import be.alexandre01.shop.enums.OrderType;
import be.alexandre01.shop.utils.HeadDatabase;

public class OrderData {
    private int slotInv = 36;
    private String nameInvSell = "Order";
    private String nameInvBuy = "Order";
    private HeadDatabase headDatabase;
    public OrderData(HeadDatabase headDatabase){
        readData();
    }
    private void readData(){
        YamlUtils yamlUtils = new YamlUtils(Main.instance, "orderMenu.yml");
        nameInvBuy = yamlUtils.getConfig().getString("config.inventorytitleBuy").replaceAll("&","§");
        nameInvSell = yamlUtils.getConfig().getString("config.inventorytitleSell").replaceAll("&","§");
        headDatabase = new HeadDatabase();
    }

    public int getSlotInv() {
        return slotInv;
    }

    public void setSlotInv(int slotInv) {
        this.slotInv = slotInv;
    }

    public String getNameInv(OrderType orderType) {
        if(orderType.equals(OrderType.SELL)){
            return nameInvSell;
        }
        return nameInvBuy;
    }

    public HeadDatabase getHeadDatabase() {
        return headDatabase;
    }
}
