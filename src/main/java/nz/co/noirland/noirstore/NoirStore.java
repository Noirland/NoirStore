package nz.co.noirland.noirstore;

import net.milkbowl.vault.economy.Economy;
import nz.co.noirland.noirstore.config.ItemConfig;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class NoirStore extends JavaPlugin {

    private static NoirStore inst;
    private static ArrayList<TradeItem> items = new ArrayList<TradeItem>();

    public static NoirStore inst() {
        return inst;
    }

    @Override
    public void onEnable() {
        inst = this;

        Economy econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        loadTradeItems();
    }

    @Override
    public void onDisable() {

    }

    private void loadTradeItems() {

        File itemsDir = new File(getDataFolder(), "items");
        File[] itemFiles = itemsDir.listFiles();
        if(itemFiles == null) {
            getLogger().severe("Could not load the 'items' directory! Stopping plugin.");
            getPluginLoader().disablePlugin(this);
            return;
        }


        for( File itemFile : itemFiles) {
            ItemConfig itemConfig = ItemConfig.getInstance(itemFile);
            ItemStack stack = Util.createItem(itemConfig.getMaterial(), itemConfig.getData());
            TradeItem tradeItem = new TradeItem(stack, 0, itemConfig.getPrices());
            items.add(tradeItem);
        }

    }


}
