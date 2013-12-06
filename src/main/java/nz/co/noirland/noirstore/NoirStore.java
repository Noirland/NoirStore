package nz.co.noirland.noirstore;

import net.milkbowl.vault.economy.Economy;
import nz.co.noirland.noirstore.config.ItemConfig;
import nz.co.noirland.noirstore.config.PluginConfig;
import nz.co.noirland.noirstore.database.SQLDatabase;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class NoirStore extends JavaPlugin {

    private static NoirStore inst;
    private static ArrayList<TradeItem> items = new ArrayList<TradeItem>();
    private SQLDatabase db;

    public static NoirStore inst() {
        return inst;
    }

    @Override
    public void onEnable() {
        inst = this;
        db = SQLDatabase.inst();
        db.checkSchema();

        Economy econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        loadTradeItems();
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
}
