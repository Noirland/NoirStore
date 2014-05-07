package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.config.ItemConfig;
import nz.co.noirland.noirstore.config.PluginConfig;
import nz.co.noirland.noirstore.database.SQLDatabase;
import nz.co.noirland.zephcore.Debug;
import nz.co.noirland.zephcore.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class NoirStore extends JavaPlugin {

    private static NoirStore inst;
    private static ArrayList<TradeItem> items = new ArrayList<TradeItem>();
    private static ArrayList<TradeSign> signs = new ArrayList<TradeSign>();
    private SQLDatabase db;
    private static Debug debug;

    public static NoirStore inst() {
        return inst;
    }

    public static Debug debug() { return debug; }

    @Override
    public void onEnable() {
        inst = this;
        debug = new Debug(this);
        db = SQLDatabase.inst();
        db.checkSchema();

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginCommand("noirstore").setExecutor(new NoirStoreCommand());

        reload();
    }

    @Override
    public void onDisable() {
        SQLDatabase.inst().disconnect();
    }

    public void reload() {
        PluginConfig.inst().reload();

        loadTradeItems();
        getLogger().info("Loaded " + items.size() + " items.");

        NoirStore.signs.clear();
        ArrayList<TradeSign> signs = db.getTradeSigns();
        for(TradeSign sign : signs) {
            addTradeSign(sign, false);
        }
        getLogger().info("Loaded " + signs.size() + " signs.");
    }

    /**
     * Finds all files in <code>plugins/NoirStore/items/</code> directory and parses them.
     */
    private void loadTradeItems() {
        items.clear(); // Avoid duplicates if ran multiple times

        File itemsDir = new File(getDataFolder(), "items");
        File[] itemFiles = itemsDir.listFiles();
        if(itemFiles == null) {
            debug().disable("Could not load the 'items' directory!");
            return;
        }

        for( File itemFile : itemFiles) {
            ItemConfig itemConfig = ItemConfig.getInstance(itemFile);
            String material = itemConfig.getMaterial();
            String data = itemConfig.getData();
            ItemStack stack = Util.createItem(material, data);

            double sellPercent = itemConfig.getSellPercent() / 100.0;
            int id = db.getItemId(material, data);
            if(id == -1) {
                id = db.addItem(material, data);
            }
            int amount = db.getItemAmount(id);
            TradeItem tradeItem = new TradeItem(id, stack, amount, itemConfig.getPrices(), sellPercent);
            items.add(tradeItem);
        }
    }

    public TradeItem getTradeItem(ItemStack item) {

        for(TradeItem ti : items) {
            ItemStack stack = ti.getItem();
            if(item.isSimilar(stack)) return ti;
        }
        return null;
    }

    public TradeItem getTradeItem(int item_id) {

        for(TradeItem ti : items) {
            if(ti.getId() == item_id) return ti;
        }
        return null;
    }

    public void addTradeSign(TradeSign sign, boolean addToDB) {
        if(addToDB) db.addSign(sign);
        signs.add(sign);
    }

    public TradeSign getTradeSign(Location loc) {
        for(TradeSign sign : signs) {
            if(sign.getLocation().equals(loc)) return sign;
        }
        return null;
    }

    public ArrayList<TradeSign> getTradeSigns() {
        return signs;
    }

    public ArrayList<TradeItem> getTradeItems() { return items; }

    public void removeSign(TradeSign sign) {
        signs.remove(sign);
        db.removeSign(sign);
    }

    public void updateSigns(TradeItem item) {
        for(TradeSign sign : signs) {
            if(sign.getItem() == item) sign.update();
        }
    }

    public void sendMessage(CommandSender to, String msg) {
        to.sendMessage(ChatColor.RED + "[NoirStore] " + ChatColor.RESET + msg);
    }
}
