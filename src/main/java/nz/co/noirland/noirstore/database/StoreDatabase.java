package nz.co.noirland.noirstore.database;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.TradeItem;
import nz.co.noirland.noirstore.TradeSign;
import nz.co.noirland.noirstore.config.StoreConfig;
import nz.co.noirland.noirstore.database.queries.*;
import nz.co.noirland.noirstore.database.schema.Schema1;
import nz.co.noirland.noirstore.database.schema.Schema2;
import nz.co.noirland.noirstore.database.schema.Schema3;
import nz.co.noirland.zephcore.Debug;
import nz.co.noirland.zephcore.Util;
import nz.co.noirland.zephcore.database.mysql.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoreDatabase extends MySQLDatabase {

    private static StoreDatabase inst;
    private static StoreConfig config = StoreConfig.inst();

    public static StoreDatabase inst() {
        if(inst == null) {
            inst = new StoreDatabase();
        }
        return inst;
    }

    private StoreDatabase() {
        schemas.put(1, new Schema1());
        schemas.put(2, new Schema2());
        schemas.put(3, new Schema3());
    }

    @Override
    public Debug debug() {
        return NoirStore.debug();
    }

    @Override
    protected String getHost() {
        return config.getHost();
    }

    @Override
    protected int getPort() {
        return config.getPort();
    }

    @Override
    protected String getDatabase() {
        return config.getDatabase();
    }

    @Override
    protected String getUsername() {
        return config.getUsername();
    }

    @Override
    protected String getPassword() {
        return config.getPassword();
    }

    @Override
    public String getPrefix() {
        return config.getPrefix();
    }

    /**
     * Adds a new trade item to the database.
     * @param material item's material type
     * @param data data the item has
     * @return returns a unique id to identify the item.
     */
    public int addItem(String material, String data) {
        int id = NoirStore.inst().generateItemId();
        AddItemQuery query = new AddItemQuery(id, material, data, 0);
        query.executeAsync();
        return id;
    }

    /**
     * Removes the given item from the database.
     * @param item item to remove
     */
    public void removeItem(TradeItem item) {
        new RemoveItemQuery(item).executeAsync();
    }

    /**
     * Gets all TradeItems currently in the database.
     * @return a list of half-configured items currently in the database
     */
    public List<TradeItem> getItems() {
        List<TradeItem> ret = new ArrayList<TradeItem>();
        GetAllItemsQuery query = new GetAllItemsQuery();
        try {
            List<Map<String, Object>> result = query.executeQuery();
            for(Map<String, Object> row : result) {
                int id = ((Number) row.get("item_id")).intValue();
                String material = (String) row.get("item");
                String data = (String) row.get("data");
                int amount = ((Number) row.get("amount")).intValue();
                ItemStack stack = Util.createItem(material, data);

                TradeItem item = new TradeItem(id, stack, amount, null, 0); //Warning: add calculator ASAP
                ret.add(item);
            }
            return ret;
        } catch (SQLException e) {
            debug().warning("Unable to get items in database!", e);
            return ret;
        }
    }

    public void updateAmount(TradeItem item) {
        new UpdateAmountQuery(item).executeAsync();
    }

    public void addSign(TradeSign sign) {
        new AddSignQuery(sign).executeAsync();
    }

    public void removeSign(TradeSign sign) {
        new RemoveSignQuery(sign).executeAsync();
    }

    public ArrayList<TradeSign> getSigns() {
        ArrayList<TradeSign> signs = new ArrayList<TradeSign>();

        try {
            List<Map<String, Object>> result = new GetAllSignsQuery().executeQuery();

            for(Map<String, Object> row : result) {
                int itemId = ((Number) row.get("item_id")).intValue();
                int sell = ((Number) row.get("sell")).intValue();
                String w = (String) row.get("world");
                int x = ((Number) row.get("x")).intValue();
                int y = ((Number) row.get("y")).intValue();
                int z = ((Number) row.get("z")).intValue();

                World world = Bukkit.getWorld(w);
                Location loc = new Location(world, x, y, z);
                TradeItem item = NoirStore.inst().getTradeItem(itemId);

                TradeSign sign = new TradeSign(item, sell, loc);
                signs.add(sign);
            }

        } catch (SQLException e) {
            debug().warning("Could not get signs from database!", e);
        }
        return signs;
    }
}
