package nz.co.noirland.noirstore.database.queries;

import nz.co.noirland.noirstore.TradeSign;
import org.bukkit.Location;

public class AddSignQuery extends StoreQuery {

    private static final String QUERY = "INSERT INTO `{PREFIX}_signs` VALUES(?,?,?,?,?,?,?)";

    public AddSignQuery(TradeSign sign) {
        super(7, QUERY);

        Location loc = sign.getLocation();

        setValue(1, loc.getBlockX());
        setValue(2, loc.getBlockY());
        setValue(3, loc.getBlockZ());
        setValue(4, loc.getWorld().getName());
        setValue(5, sign.getMaterial().toString());
        setValue(6, sign.getSellAmount());
        setValue(7, sign.getPrice());
    }

}
