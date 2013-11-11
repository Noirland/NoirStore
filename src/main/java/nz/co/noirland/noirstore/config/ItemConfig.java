package nz.co.noirland.noirstore.config;

import nz.co.noirland.noirstore.PriceRange;
import nz.co.noirland.noirstore.Util;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemConfig extends Config {

    private static Map<File, ItemConfig> instances = new HashMap<File, ItemConfig>();

    public static ItemConfig getInstance(File file) {
        if(!instances.containsKey(file)) {
            instances.put(file, new ItemConfig(file));
        }
        return instances.get(file);
    }

    public static Map<File, ItemConfig> getInstances() {
        return instances;
    }

    public static void removeInstance(ItemConfig config) {
        if(instances.containsValue(config)) {
            instances.remove(config.configFile);
        }
    }

    private ItemConfig(File file) {
        super(file);
    }

    @Override
    protected InputStream getResource() {
        return plugin.getResource("item.yml");
    }

    public String getMaterial() { return config.getString("material"); }
    public String getData() { return config.getString("data"); }

    public ArrayList<PriceRange> getPrices() {

        ArrayList<PriceRange> ret = new ArrayList<PriceRange>();

        ConfigurationSection pricesConfig = config.getConfigurationSection("prices");
        int maxUpper = 0;
        int maxLower = Integer.MAX_VALUE;

        for(String range : pricesConfig.getKeys(false)) {

            PriceRange pRange = Util.parseRange(range, pricesConfig.getDouble(range + ".buy"), pricesConfig.getDouble(range + ".sell"));

            if(pRange == null) continue;

            if(pRange.getLower() < maxLower) maxLower = pRange.getLower();
            if(pRange.getUpper() > maxUpper) maxUpper = pRange.getUpper();

            ret.add(pRange);

        }

        ret.add(new PriceRange(0, maxLower-1, pricesConfig.getDouble("max.buy"), pricesConfig.getDouble("max.sell")));
        ret.add(new PriceRange(maxUpper+1, Integer.MAX_VALUE, pricesConfig.getDouble("min.buy"), pricesConfig.getDouble("min.sell")));

        return ret;


    }

}
