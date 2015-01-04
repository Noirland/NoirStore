package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.database.queries.StoreQuery;
import nz.co.noirland.zephcore.database.Schema;

import java.sql.SQLException;

public class Schema2 implements Schema {

    @Override
    public void run() {
        try {
            new StoreQuery("ALTER TABLE `{PREFIX}_signs` ADD COLUMN `sell` INT UNSIGNED").execute();
            new StoreQuery("UPDATE `{PREFIX}_schema` SET `version` = 2").execute();
        } catch (SQLException e) {
            NoirStore.debug().disable("Could not update to Schema 2!", e);
        }
    }
}
