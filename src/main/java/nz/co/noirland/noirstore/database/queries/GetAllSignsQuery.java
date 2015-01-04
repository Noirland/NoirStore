package nz.co.noirland.noirstore.database.queries;

public class GetAllSignsQuery extends StoreQuery {

    private static final String QUERY = "SELECT * FROM `{PREFIX}_signs`";

    public GetAllSignsQuery() {
        super(QUERY);
    }

}
