package nz.co.noirland.noirstore.database.queries;

import nz.co.noirland.noirstore.TradeSign;
import org.bukkit.Location;

public class RemoveSignQuery extends StoreQuery {

    private static final String QUERY = "DELETE FROM `{PREFIX}_signs` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?";

    public RemoveSignQuery(TradeSign sign) {
        super(4, QUERY);

        Location loc = sign.getLocation();

        setValue(1, loc.getBlockX());
        setValue(2, loc.getBlockY());
        setValue(3, loc.getBlockZ());
        setValue(4, loc.getWorld().getName());
    }

}
