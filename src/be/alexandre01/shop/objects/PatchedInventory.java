package be.alexandre01.shop.objects;

import org.bukkit.inventory.Inventory;

public class PatchedInventory {
    private Inventory inventory;
    private String name;

    public PatchedInventory(Inventory inventory, String name){
        this.inventory = inventory;
        this.name = name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }
}
