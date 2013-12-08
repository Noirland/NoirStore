package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.database.SQLDatabase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;

public class SignListener implements Listener {

    private ArrayList<Block> incompleteSigns = new ArrayList<Block>();
    private NoirStore plugin = NoirStore.inst();
    private SQLDatabase db = SQLDatabase.inst();


    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignPlace(SignChangeEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase(TradeSign.SIGN_TITLE)) return;
        if(!player.hasPermission("noirstore.create")) {
            event.setCancelled(true);
            block.breakNaturally();
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to create NoirStore signs.");
            return;
        }

        incompleteSigns.add(block);
        plugin.sendMessage(player, "Please right-click the block with the item and amount to sell/buy to finish sign.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(!Util.isTradeSign(block)) return;

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack held = event.getItem();

        event.setCancelled(true);
        if(incompleteSigns.contains(block) && action == Action.RIGHT_CLICK_BLOCK && held.getType() != Material.AIR) {
            // Finish creating sign
            TradeItem tradeItem = plugin.getTradeItem(held);
            if(tradeItem == null) {
                plugin.sendMessage(player, "Item is not configured to be sold.");
                return;
            }
            TradeSign sign = new TradeSign(tradeItem, held.getAmount(), block.getLocation());
            db.addSign(sign);
            incompleteSigns.remove(block);
            plugin.addTradeSign(sign);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignFall(BlockBreakEvent event) {
        Block block = event.getBlock();
        Iterator it = plugin.getTradeSigns().iterator();
        while(it.hasNext()) {
            TradeSign sign = (TradeSign) it.next();
            if(block.getFace(sign.getLocation().getBlock()) == null) continue; // Sign not attached
            if(!event.getPlayer().hasPermission("noirstore.break")) {
                plugin.sendMessage(event.getPlayer(), "You cannot break that sign!");
                event.setCancelled(true);
                return;
            }
            it.remove();
            db.removeSign(sign);
        }
    }

}
