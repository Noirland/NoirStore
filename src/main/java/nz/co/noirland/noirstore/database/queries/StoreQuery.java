package nz.co.noirland.noirstore.database.queries;

import nz.co.noirland.noirstore.database.StoreDatabase;
import nz.co.noirland.zephcore.database.mysql.MySQLDatabase;
import nz.co.noirland.zephcore.database.mysql.MySQLQuery;

public class StoreQuery extends MySQLQuery {
    @Override
    protected MySQLDatabase getDB() {
        return StoreDatabase.inst();
    }

    public StoreQuery(int nargs, String query) {
        super(nargs, query);
    }

    public StoreQuery(String query) {
        super(query);
    }

    public StoreQuery(Object[] values, String query) {
        super(values, query);
    }
}
