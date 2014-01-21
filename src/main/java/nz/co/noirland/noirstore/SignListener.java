package nz.co.noirland.noirstore;

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
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SignListener implements Listener {

    private ArrayList<Block> incompleteSigns = new ArrayList<Block>();
    private NoirStore plugin = NoirStore.inst();


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
    public void onSignFall(BlockBreakEvent event) { // When a sign is broken removing the block it's on
        Block block = event.getBlock();
        Iterator it = plugin.getTradeSigns().iterator();
        while(it.hasNext()) {
            TradeSign sign = (TradeSign) it.next();
            Block sBlock = sign.getLocation().getBlock();
            Sign sData = (Sign) sBlock.getState().getData();
            Block attached = sBlock.getRelative(sData.getAttachedFace());
            if(!attached.equals(block)) continue; // Has no trade sign attached
            if(!event.getPlayer().hasPermission("noirstore.break")) {
                plugin.sendMessage(event.getPlayer(), "You cannot break that sign!");
                event.setCancelled(true);
                return;
            }
            it.remove();
            plugin.removeSign(sign);
        }
    }

    /*
    if (block.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
        Sign s = (Sign) block.getState().getData();
        Block attachedBlock = b.getRelative(s.getAttachedFace());
        // ...
    }
     */

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
            finishSign(player, block, held);
            return;
        }
        TradeSign sign = plugin.getTradeSign(block.getLocation());
        if(sign == null) return;

        switch(action) {
            case RIGHT_CLICK_BLOCK:
                // Player buying from sign
                buyItems(player, sign);
                break;
            case LEFT_CLICK_BLOCK:
                //Player is selling to sign
                sellItems(player, sign);
                break;
        }
    }

    private void finishSign(Player player, Block block, ItemStack held) {
        TradeItem tradeItem = plugin.getTradeItem(held);
        if(tradeItem == null) {
            plugin.sendMessage(player, "Item is not configured to be sold.");
            return;
        }
        TradeSign sign = new TradeSign(tradeItem, held.getAmount(), block.getLocation());
        plugin.addTradeSign(sign, true);
        incompleteSigns.remove(block);
    }

    private void buyItems(Player player, TradeSign sign) {
        EconManager econ = EconManager.inst();
        TradeItem item = sign.getItem();
        double price = Util.round(item.getPrice() * sign.getSellAmount(), TradeItem.format);
        if(item.getAmount() < sign.getSellAmount()) {
            plugin.sendMessage(player, "No stock available.");
            return;
        }
        if(!econ.canWithdraw(player, price)) {
            plugin.sendMessage(player, "You can't afford that, you need $" + (price*sign.getSellAmount() - econ.getBalance(player)) + ".");
            return;
        }
        econ.withdraw(player, price);
        item.setAmount(item.getAmount() - sign.getSellAmount());

        ItemStack stack = item.getItem();
        stack.setAmount(sign.getSellAmount());
        HashMap<Integer,ItemStack> leftovers = player.getInventory().addItem(stack);
        for(ItemStack left : leftovers.values()) {
            player.getWorld().dropItem(player.getLocation(), left);
        }
        player.updateInventory();
        plugin.updateSigns(item);
    }

    private void sellItems(Player player, TradeSign sign) {
        EconManager econ = EconManager.inst();
        TradeItem item = sign.getItem();
        ItemStack stack = item.getItem();
        stack.setAmount(sign.getSellAmount());
        ItemStack hand = player.getItemInHand();
        if(!hand.isSimilar(stack)) {
            plugin.sendMessage(player, "You cannot sell that item to this sign.");
            return;
        }
        if(hand.getAmount() < sign.getSellAmount()) {
            plugin.sendMessage(player, "You don't have items enough to sell.");
            return;
        }
        double price = Util.round(item.getSellPrice() * sign.getSellAmount(), TradeItem.format);
        econ.deposit(player, price);
        item.setAmount(item.getAmount() + sign.getSellAmount());
        if(hand.getAmount() - sign.getSellAmount() <= 0) {
            player.setItemInHand(null);
        }else{
            player.getItemInHand().setAmount(hand.getAmount() - sign.getSellAmount());
        }
        player.updateInventory();
        plugin.updateSigns(item);
    }

}
