package be.alexandre01.shop.objects;

import be.alexandre01.shop.Main;
import be.alexandre01.shop.configs.ConfigReader;
import be.alexandre01.shop.enums.SoundReceiver;
import org.bukkit.*;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class MainItem {
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
    private String redirection = null;
    private float volume;
    private float pitch;
    private Sound sound;
    private SoundReceiver soundReceiver = SoundReceiver.NONE;
    private boolean giveCurrentItem = false;
    private List<String> commands;

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(ArrayList<String> commands) {
        this.commands = commands;
    }

    public boolean isGiveCurrentItem() {
        return giveCurrentItem;
    }

    public void setGiveCurrentItem(boolean giveCurrentItem) {
        this.giveCurrentItem = giveCurrentItem;
    }

    public MainItem(int slot){
        this.slot = slot;
        itemFlags = new ArrayList<>();
    }
    public MainItem(List<Integer> slots){
        this.slots = slots;
        itemFlags = new ArrayList<>();
    }
    public String getRedirection(){
        return redirection;
    }

    public void setRedirection(String redirection) {
        this.redirection = redirection;
    }

    public ItemStack toItemStack(){
        return itemStack;
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
        itemMeta.addEnchant(newEnc,1,false);

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

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
        itemMeta.setLore(lore);
        updateItemMeta();
    }

    public void playSound(Player player){
        switch (soundReceiver){
            case PLAYER: player.playSound(player.getLocation(),sound,volume,pitch); break;
            case WORLD: player.getWorld().playSound(player.getLocation(),sound,volume,pitch); break;
            case SERVER:
                for (Player players : Bukkit.getOnlinePlayers()){
                    players.playSound(players.getLocation(),sound,volume,pitch);
               }  break;
            default: break;
        }
    }

    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }

    public void setItemFlags(ArrayList<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
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

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public void setEnchant(Enchantment enchant) {
        this.enchant = enchant;
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

    public Material getMaterial() {
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
    }
    public void execute(Player player, ConfigReader configReader){
        playSound(player);
        if(getRedirection() != null){
          configReader.getShopMenu(getRedirection()).openInventory(player,0);
        }
    }
}
