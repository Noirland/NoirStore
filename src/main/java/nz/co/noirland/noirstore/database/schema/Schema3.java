package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.database.queries.StoreQuery;
import nz.co.noirland.zephcore.database.Schema;

import java.sql.SQLException;

public class Schema3 implements Schema {

    @Override
    public void run() {
        try {
            //ALTER TABLE companies CHANGE id id int(11);
            new StoreQuery("ALTER TABLE `{PREFIX}_items` CHANGE `item_id` `item_id` INT(10) UNSIGNED").execute();
        } catch (SQLException e) {
            NoirStore.debug().disable("Unable to update database to schema 3!", e);
        }
    }
}
