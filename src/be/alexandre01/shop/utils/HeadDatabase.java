package be.alexandre01.shop.utils;

import be.alexandre01.shop.api.ShopAPI;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import be.alexandre01.shop.Main;
import be.alexandre01.shop.configs.YamlUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

public class HeadDatabase {
    YamlUtils yaml;
    private boolean olderVersion = false;
    HashMap<String,ItemStack> index;
    public HeadDatabase(){
         yaml = new YamlUtils(Main.instance, "orderMenu.yml");
        index = new HashMap<>();
        loadHead();
    }

    public void loadHead(){
        Material headMat;
        if(Main.instance.isLegacy()){
            headMat = Material.matchMaterial("LEGACY_SKULL_ITEM");
        }else {
            headMat = Material.getMaterial("SKULL_ITEM");
        }
        for (String key : yaml.getConfig().getConfigurationSection("head-db").getKeys(false)){
            String headTexture = yaml.getConfig().getString("head-db."+key+".texture");
            String name =  yaml.getConfig().getString("head-db."+key+".name");
            index.put(key,createCustomHead(headTexture,name.replace("&","§"),headMat));
        }
    }

    private  ItemStack createCustomHead(String texture,String name,Material skullMaterial){
        ItemStack skull = new ItemStack(skullMaterial, 1, (short) 3);

        SkullMeta skullM = (SkullMeta) skull.getItemMeta();
        skullM.setOwner(null);
        if(getGameProfile() == null){
            System.out.println("Error: Cannot find Gameprofile in your server");
            return null;
        }

        //   GameProfile profile = new GameProfile(UUID.randomUUID(), name);
       // profile.getProperties().put("textures", new Property("textures", texture));
        Field profileField = null;
        try {

            Object gameProfile = getGameProfile().getDeclaredConstructor(UUID.class,String.class).newInstance(UUID.randomUUID(),name);


            profileField = skullM.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullM, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        skullM.setDisplayName(name);
        skull.setItemMeta(skullM);
        return skull;
    }

    public ItemStack getHead(String key){
        return index.get(key).clone();
    }

    public Class getGameProfile(){
            try {
                olderVersion = true;
               return  Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException ignored){

            }
            try {
                return  Class.forName("com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException ignored) {

            }
        return null;
    }

    public boolean isOlderVersion() {
        return olderVersion;
    }
}
