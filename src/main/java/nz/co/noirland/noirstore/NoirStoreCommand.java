package nz.co.noirland.noirstore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoirStoreCommand implements CommandExecutor {

    private NoirStore plugin = NoirStore.inst();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;

        if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("noirstore.reload")) {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use that command.");
                return true;
            }

            plugin.reload();
            if(sender instanceof Player) plugin.sendMessage(sender, "Loaded " + plugin.getTradeItems().size() + " items and " + plugin.getTradeSigns().size() + " signs.");
            return true;
        }else{
            return false;
        }
    }
}
