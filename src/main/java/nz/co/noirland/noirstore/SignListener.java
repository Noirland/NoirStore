package nz.co.noirland.noirstore;

import com.google.common.collect.ImmutableList;
import nz.co.noirland.noirstore.config.StoreConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.TreeMap;

public class SignListener implements Listener {

    private ArrayList<Block> incompleteSigns = new ArrayList<Block>();
    private NoirStore plugin = NoirStore.inst();
    private TreeMap<String, Long> lastTrade = new TreeMap<String, Long>();

    private static ImmutableList<BlockFace> CHECK_FACES = ImmutableList.of(BlockFace.SELF, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
            BlockFace.WEST, BlockFace.UP);


    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignPlace(SignChangeEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if(!StoreUtil.isTradeLine(event.getLine(0))) return;
        if(!player.hasPermission("noirstore.create")) {
            event.setCancelled(true);
            block.breakNaturally();
            player.sendMessage(ChatColor.DARK_RED + "You do not have permission to create NoirStore signs.");
            return;
        }

        try {
            new BigDecimal(event.getLine(2), StoreUtil.ROUND_CONTEXT);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid price on line 3.");
            return;
        }

        incompleteSigns.add(block);
        plugin.sendMessage(player, "Please right-click the block with the item and amount to sell/buy to finish sign.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignFall(BlockBreakEvent event) { // When a sign is broken removing the block it's on
        Block block = event.getBlock();
        Collection<TradeSign> signs = new HashSet<>();
        for(BlockFace face : CHECK_FACES) {
            Block blockRel = block.getRelative(face);
            if(!StoreUtil.isTradeSign(blockRel)) continue;

            signs.add(NoirStore.inst().getTradeSign(blockRel.getLocation()));
        }

        if(signs.isEmpty()) return;

        if(!event.getPlayer().hasPermission("noirstore.break")) {
            plugin.sendMessage(event.getPlayer(), "You cannot break that sign!");
            event.setCancelled(true);
            return;
        }

        for(TradeSign sign : signs) NoirStore.inst().removeSign(sign);
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
        if(!StoreUtil.isTradeSign(block)) return;

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack held = event.getItem();

        if(incompleteSigns.contains(block)) {
            // Finish creating sign
            if(action != Action.RIGHT_CLICK_BLOCK || held.getType() == Material.AIR) return;
            finishSign(player, block, held);
            event.setCancelled(true);
            return;
        }
        TradeSign sign = plugin.getTradeSign(block.getLocation());
        if(sign == null) return;

        if(lastTrade.containsKey(player.getName())) {
            long last = lastTrade.get(player.getName());
            long delay = StoreConfig.inst().getTradeDelay();

            if(delay > 0 && last+delay > System.currentTimeMillis()) {
                event.setCancelled(true);
                return;
            }
        }

        if(action == Action.RIGHT_CLICK_BLOCK) {
            sellItems(player, sign);
            lastTrade.put(player.getName(), System.currentTimeMillis());
            event.setCancelled(true);
        }
    }

    private void finishSign(Player player, Block block, ItemStack held) {
        Sign blockSign = (Sign) block.getState();
        BigDecimal price;

        try {
            price = new BigDecimal(blockSign.getLine(2));
            price = price.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid price on line 3.");
            return;
        }
        TradeSign sign = new TradeSign(held.getType(), held.getAmount(), block.getLocation(), price);
        plugin.addTradeSign(sign, true);
        incompleteSigns.remove(block);
    }

    private void sellItems(Player player, TradeSign sign) {
        EconManager econ = EconManager.inst();

        int itemCount = 0;
        for(ItemStack item : player.getInventory().getContents()) {
            if(item == null || item.getType() != sign.getMaterial()) continue;

            itemCount += item.getAmount();
            if(itemCount >= sign.getSellAmount()) break;
        }

        if(itemCount < sign.getSellAmount()) {
            plugin.sendMessage(player, "You don't have items enough to sell.");
            return;
        }
        double price = sign.getPrice().doubleValue();

        int sellRemaining = sign.getSellAmount();
        ListIterator<ItemStack> it = player.getInventory().iterator();
        while(it.hasNext()) {
            ItemStack item = it.next();

            if(item == null || item.getType() != sign.getMaterial()) continue;

            if(sellRemaining >= item.getAmount()) {
                sellRemaining -= item.getAmount();
                it.set(null);
            } else {
                sellRemaining -= item.getAmount();
                item.setAmount(item.getAmount() - sign.getSellAmount());
            }

            if(sellRemaining == 0) break;
        }
        econ.deposit(player, price);

        player.updateInventory();
        NoirStore.inst().sendMessage(player, ChatColor.WHITE + "Sold " +
                ChatColor.GOLD + sign.getSellAmount() + " " + StoreUtil.format(sign.getMaterial()) +
                ChatColor.WHITE + " for " +
                ChatColor.GOLD + StoreUtil.formatPrice(sign.getPrice()) +
                ChatColor.WHITE + ".");
    }
}
