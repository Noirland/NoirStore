package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.config.ItemConfig;
import nz.co.noirland.noirstore.config.PluginConfig;
import nz.co.noirland.noirstore.database.SQLDatabase;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class NoirStore extends JavaPlugin {

    private static NoirStore inst;
    private static ArrayList<TradeItem> items = new ArrayList<TradeItem>();
    private static ArrayList<TradeSign> signs = new ArrayList<TradeSign>();
    private SQLDatabase db;

    public static NoirStore inst() {
        return inst;
    }

    @Override
    public void onEnable() {
        inst = this;
        db = SQLDatabase.inst();
        db.checkSchema();

        getServer().getPluginManager().registerEvents(new SignListener(), this);

//        Economy econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        loadTradeItems();
        db.loadSigns();
    }

    @Override
    public void onDisable() {
        SQLDatabase.inst().disconnect();
    }

    /**
     * Finds all files in <code>plugins/NoirStore/items/</code> directory and parses them.
     */
    private void loadTradeItems() {

        File itemsDir = new File(getDataFolder(), "items");
        File[] itemFiles = itemsDir.listFiles();
        if(itemFiles == null) {
            disable("Could not load the 'items' directory!");
            return;
        }

        for( File itemFile : itemFiles) {
            ItemConfig itemConfig = ItemConfig.getInstance(itemFile);
            String material = itemConfig.getMaterial();
            String data = itemConfig.getData();
            ItemStack stack = Util.createItem(material, data);
            int id = db.getItemId(material, data);
            if(id == -1) {
                id = db.addItem(material, data);
            }
            int amount = db.getItemAmount(id);
            TradeItem tradeItem = new TradeItem(id, stack, amount, itemConfig.getPrices());
            items.add(tradeItem);
        }
    }

    public TradeItem getTradeItem(ItemStack item) {

        for(TradeItem ti : items) {
            ItemStack stack = ti.getItem();
            if(stack.getType().equals(item.getType()) && stack.getData().equals(item.getData())) return ti;
        }
        return null;
    }

    public TradeItem getTradeItem(int item_id) {

        for(TradeItem ti : items) {
            if(ti.getId() == item_id) return ti;
        }
        return null;
    }

    public void addTradeSign(TradeSign sign) {
        signs.add(sign);
    }

    public ArrayList<TradeSign> getTradeSigns() {
        return signs;
    }

    public void removeSign(TradeSign sign) {
        signs.remove(sign);
        db.removeSign(sign);
    }

    /**
     * Show a debug message if debug is true in config.
     * @param msg message to be shown in console
     */
    public void debug(String msg) {

        if(PluginConfig.inst().getDebug()) {
            getLogger().info("[DEBUG] " + msg);
        }

    }

    /**
     * Show an Exception's stack trace in console if debug is true.
     * @param e execption to be shown
     */
    public void debug(Throwable e) {
        debug(ExceptionUtils.getStackTrace(e));
    }

    /**
     * Show both a debug message and a stacktrace
     * @param msg message to be shown in console
     * @param e execption to be shown
     */
    public void debug(String msg, Throwable e) {
        debug(msg);
        debug(e);
    }

    /**
     * Disable plugin and show a severe message
     * @param error message to be shown
     */
    public void disable(String error) {
        getLogger().severe(error);
        getPluginLoader().disablePlugin(this);
    }

    /**
     * Disable plugin with severe message and stack trace if debug is enabled.
     * @param error message to be shown
     * @param e execption to be shown
     */
    public void disable(String error, Throwable e) {
        debug(e);
        disable(error);
    }

    public void sendMessage(Player player, String msg) {
        player.sendMessage(ChatColor.RED + "[NoirStore] " + ChatColor.RESET + msg);
    }
}
