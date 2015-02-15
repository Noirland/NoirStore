package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.config.ItemConfig;
import nz.co.noirland.noirstore.config.StoreConfig;
import nz.co.noirland.noirstore.database.StoreDatabase;
import nz.co.noirland.zephcore.Debug;
import nz.co.noirland.zephcore.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class NoirStore extends JavaPlugin {

    private static NoirStore inst;
    private static ArrayList<TradeItem> items = new ArrayList<TradeItem>();
    private static ArrayList<TradeSign> signs = new ArrayList<TradeSign>();
    private StoreDatabase db;
    private static Debug debug;

    public static NoirStore inst() {
        return inst;
    }

    public static Debug debug() { return debug; }

    @Override
    public void onEnable() {
        inst = this;
        debug = new Debug(this);
        db = StoreDatabase.inst();
        db.checkSchema();

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginCommand("noirstore").setExecutor(new NoirStoreCommand());

        reload();
    }

    public void reload() {
        StoreConfig.inst().reload();

        loadTradeItems();
        getLogger().info("Loaded " + items.size() + " items.");

        Iterator<TradeSign> it = signs.iterator();
        while(it.hasNext()) {
            TradeSign sign = it.next();
            TradeItem signItem = sign.getItem();
            TradeItem newItem = getTradeItem(signItem.getId());

            if(newItem == null) {
                sign.clean();
                it.remove();
                continue;
            }

            sign.setItem(newItem);
        }

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

        Map<ItemStack, ItemConfig> fileItems = new HashMap<ItemStack, ItemConfig>();

        for(File itemFile : itemFiles) {
            ItemConfig itemConfig = ItemConfig.getInstance(itemFile);
            String material = itemConfig.getMaterial();
            String data = itemConfig.getData();

            fileItems.put(Util.createItem(material, data), itemConfig);
        }

        List<TradeItem> dbItems = db.getItems();

        Iterator<TradeItem> dbIterator = dbItems.iterator();
        db:
        while(dbIterator.hasNext()) {
            TradeItem trade = dbIterator.next();
            Iterator<ItemStack> fileIterator = fileItems.keySet().iterator();
            while(fileIterator.hasNext()) {
                ItemStack fileItem = fileIterator.next();
                if(fileItem.isSimilar(trade.getItem())) {
                    ItemConfig conf = fileItems.get(fileItem);
                    int sell = conf.getSellPercent();
                    PriceCalculator calc = conf.getPriceCalc();
                    trade.setSellPercent(sell); // Fully configure the item, now we have the config
                    trade.setCalculator(calc);
                    fileIterator.remove();
                    continue db;
                }
            }
            db.removeItem(trade);
            dbIterator.remove();
        }

        items.addAll(dbItems);

        for(ItemConfig itemConfig : fileItems.values()) {
            String material = itemConfig.getMaterial();
            String data = itemConfig.getData();
            ItemStack stack = Util.createItem(material, data);

            int id = db.addItem(material, data);
            TradeItem tradeItem = new TradeItem(id, stack, 0, itemConfig.getPriceCalc(), itemConfig.getSellPercent());
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

    public int generateItemId() {
        while(true) {
            int id = Util.createRandomHex(4); // Random id
            boolean found = false;
            for(TradeItem item : items) {
                if(item.getId() == id) {
                    found = true;
                    break;
                }
            }
            if(!found) return id;
        }
    }

}
