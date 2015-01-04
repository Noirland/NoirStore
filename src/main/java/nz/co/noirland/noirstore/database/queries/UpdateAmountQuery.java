package nz.co.noirland.noirstore.database.queries;

import nz.co.noirland.noirstore.TradeItem;

public class UpdateAmountQuery extends StoreQuery {

    private static final String QUERY = "UPDATE `{PREFIX}_items` SET `amount` = ? WHERE `item_id` = ?";

    public UpdateAmountQuery(TradeItem item) {
        super(2, QUERY);
        setValue(1, item.getAmount());
        setValue(2, item.getId());
    }

}
