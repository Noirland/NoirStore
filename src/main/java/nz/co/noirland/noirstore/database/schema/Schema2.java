package nz.co.noirland.noirstore.database.schema;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.noirstore.config.PluginConfig;
import nz.co.noirland.noirstore.database.SQLDatabase;

import java.sql.SQLException;

public class Schema2 extends Schema {


    private SQLDatabase db = SQLDatabase.inst();
    String prefix;
    @Override
    public void updateDatabase() {
        prefix = PluginConfig.inst().getPrefix();

        try {
            db.prepareStatement("ALTER TABLE `" + prefix + "signs` ADD COLUMN `sell` INT UNSIGNED").execute();
            db.prepareStatement("UPDATE `" + prefix + "schema` SET `version` = 2").execute();
        } catch (SQLException e) {
            NoirStore.debug().disable("Could not update to Schema 2!", e);
        }


    }
}
