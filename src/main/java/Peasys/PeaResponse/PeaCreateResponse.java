package Peasys.PeaResponse;
import java.util.Dictionary;

/**
 * Represents the concept of response in the case of a CREATE query executed on the database of an AS/400 server by a 
 * PeaClient object.
 */
public final class PeaCreateResponse extends PeaResponse {
    public String DatasuperName;
    public String IndexName;
    public Dictionary<String, ColumnInfo> TableSchema;

    /**
     * 
     * @param hasSucceeded Boolean set to true if the query has correctly been executed. Set to true if the SQL state is 00000.
     * @param returnedSQLMessage SQL message return from the execution of the query.
     * @param returnedSQLState SQL state return from the execution of the query.
     * @param databaseName Name of the database if the SQL create query creates a new database.
     * @param indexName Name of the index if the SQL create query creates a new index.
     * @param tableSchema Schema of the table if the SQL create query creates a new table. The Schema is a Dictionary
     *                    with columns' name as key and a ColumnInfo object as value.
     * @see ColumnInfo
     */
    public PeaCreateResponse(boolean hasSucceeded, String returnedSQLMessage, String returnedSQLState, String databaseName, String indexName, Dictionary<String, ColumnInfo> tableSchema) {
    super(hasSucceeded, returnedSQLMessage, returnedSQLState);
        DatasuperName = databaseName;
        IndexName = indexName;
        TableSchema = tableSchema;
    }
}
