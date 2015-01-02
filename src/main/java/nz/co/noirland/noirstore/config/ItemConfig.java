package nz.co.noirland.noirstore.config;

import nz.co.noirland.noirstore.*;
import nz.co.noirland.zephcore.Config;
import nz.co.noirland.zephcore.Debug;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class ItemConfig extends Config {

    private static Map<File, ItemConfig> instances = new HashMap<File, ItemConfig>();

    @Override
    protected Plugin getPlugin() {
        return NoirStore.inst();
    }

    @Override
    protected Debug getDebug() {
        return NoirStore.debug();
    }

    public static ItemConfig getInstance(File file) {
        if(!instances.containsKey(file)) {
            instances.put(file, new ItemConfig(file));
        }
        return instances.get(file);
    }

    private ItemConfig(File file) {
        super(file);
    }

    @Override
    protected InputStream getResource() {
        return getPlugin().getResource("item.yml");
    }

    public String getMaterial() { return config.getString("material", ""); }
    public String getData() { return config.getString("data", ""); }
    public int getSellPercent() { return config.getInt("sellpercent", StoreConfig.inst().getSellPercent()); }

    private ApproxPriceCalculator getApproxCalc() {
        ArrayList<PriceRange> prices = new ArrayList<PriceRange>();

        ConfigurationSection pricesConfig = config.getConfigurationSection("prices");

        Set<String> keysSet = pricesConfig.getKeys(false);
        List<String> keys = new ArrayList<String>(keysSet);

        for(int i = 0; i < (keys.size()-1); i++) {
            int lower = Integer.parseInt(keys.get(i));
            int upper = Integer.parseInt(keys.get(i+1));
            prices.add(new PriceRange(lower, upper, pricesConfig.getDouble(keys.get(i)), pricesConfig.getDouble(keys.get(i + 1))));
        }

        return new ApproxPriceCalculator(prices);
    }

    private ExpPriceCalculator getExpCalc() {
        ConfigurationSection section = config.getConfigurationSection("price");

        double max = section.getDouble("max");
        double min = section.getDouble("min");
        double grad = section.getDouble("gradient");

        return new ExpPriceCalculator(max, min, grad);
    }

    public PriceCalculator getPriceCalc() {
        if(config.getConfigurationSection("prices") != null) {
            return getApproxCalc();
        } else {
            return getExpCalc();
        }
    }

}
