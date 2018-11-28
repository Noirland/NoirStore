package nz.co.noirland.noirstore.database;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.TradeSign;
import nz.co.noirland.noirstore.config.StoreConfig;
import nz.co.noirland.noirstore.database.queries.AddSignQuery;
import nz.co.noirland.noirstore.database.queries.GetAllSignsQuery;
import nz.co.noirland.noirstore.database.queries.RemoveSignQuery;
import nz.co.noirland.noirstore.database.schema.Schema1;
import nz.co.noirland.noirstore.database.schema.Schema2;
import nz.co.noirland.noirstore.database.schema.Schema3;
import nz.co.noirland.noirstore.database.schema.Schema4;
import nz.co.noirland.zephcore.Debug;
import nz.co.noirland.zephcore.database.mysql.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.math.BigDecimal;
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
        schemas.put(4, new Schema4());
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
                String itemId = ((String) row.get("item_id"));
                int sell = ((Number) row.get("sell")).intValue();
                String w = (String) row.get("world");
                int x = ((Number) row.get("x")).intValue();
                int y = ((Number) row.get("y")).intValue();
                int z = ((Number) row.get("z")).intValue();
                BigDecimal price = ((BigDecimal) row.get("price"));

                World world = Bukkit.getWorld(w);
                Location loc = new Location(world, x, y, z);

                Material material = Material.getMaterial(itemId);

                if(material == null) {
                    debug().warning("Unable to find material type! " + itemId);
                }

                TradeSign sign = new TradeSign(material, sell, loc, price);
                signs.add(sign);
            }

        } catch (SQLException e) {
            debug().warning("Could not get signs from database!", e);
        }
        return signs;
    }
}
