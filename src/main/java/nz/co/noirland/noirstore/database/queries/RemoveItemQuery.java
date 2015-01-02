package nz.co.noirland.noirstore.database.queries;

import nz.co.noirland.noirstore.TradeItem;

public class RemoveItemQuery extends StoreQuery {

    private static final String QUERY = "DELETE FROM `{PREFIX}_items` WHERE `item_id` = ?";

    public RemoveItemQuery(TradeItem item) {
        super(1, QUERY);
        setValue(1, item.getId());
    }

}
