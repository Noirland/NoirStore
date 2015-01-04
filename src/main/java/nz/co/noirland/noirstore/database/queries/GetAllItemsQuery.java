package nz.co.noirland.noirstore.database.queries;

public class GetAllItemsQuery extends StoreQuery {

    private static final String QUERY = "SELECT * FROM `{PREFIX}_items`";

    public GetAllItemsQuery() {
        super(QUERY);
    }

}
