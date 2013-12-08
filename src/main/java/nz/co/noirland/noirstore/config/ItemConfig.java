package nz.co.noirland.noirstore.config;

import nz.co.noirland.noirstore.PriceRange;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.InputStream;
import java.util.*;

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

    public String getMaterial() { return config.getString("material", ""); }
    public String getData() { return config.getString("data", ""); }

    public ArrayList<PriceRange> getPrices() {

        ArrayList<PriceRange> ret = new ArrayList<PriceRange>();

        ConfigurationSection pricesConfig = config.getConfigurationSection("prices");

        Set<String> keysSet = pricesConfig.getKeys(false);
        List<String> keys = new ArrayList<String>(keysSet);

        for(int i = 0;i < (keys.size()-1);i++) {
            int lower = Integer.parseInt(keys.get(i));
            int upper = Integer.parseInt(keys.get(i+1));
            ret.add(new PriceRange(lower, upper, pricesConfig.getLong(keys.get(i)), pricesConfig.getLong(keys.get(i+1))));
        }

        return ret;


    }

}
