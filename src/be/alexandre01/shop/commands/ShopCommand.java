package be.alexandre01.shop.commands;



import be.alexandre01.shop.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ShopCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                if(sender.hasPermission("shop.admin")){
                    for(Player players: Bukkit.getOnlinePlayers()){
                        players.closeInventory();
                    }
                    Main.instance.getConfigReader().reloadConfig();
                    sender.sendMessage("§cVous avez reload la config avec succès");
                    return true;
                }
            }
        }
        if(!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        player.openInventory(Main.instance.getConfigReader().getMainInventory());

        return true;
    }



}