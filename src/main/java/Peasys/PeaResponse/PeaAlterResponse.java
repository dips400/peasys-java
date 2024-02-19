package Peasys.PeaResponse;
import java.util.Dictionary;

/**
 * Represents the concept of response in the case of an ALTER query executed on the database of an AS/400 server by
 * a PeaClient object.
 */
public final class PeaAlterResponse extends PeaResponse {
    public Dictionary<String, ColumnInfo> tableSchema;
    /**
     * Initialize a new instance of the PeaAlterResponse class.
     * @param hasSucceeded Boolean set to true if the query has correctly been executed. Set to true if the SQL state
     *                     is 00000.
     * @param returnedSQLMessage SQL message return from the execution of the query.
     * @param returnedSQLState SQL state return from the execution of the query.
     * @param tableSchema  Schema of the updated table that has been modified by the SQL ALTER query. The Schema is a
     *                     Dictionary with columns' name as key and a ColumnInfo object as value.
     */
    public PeaAlterResponse(boolean hasSucceeded, String returnedSQLMessage, String returnedSQLState, Dictionary<String, ColumnInfo> tableSchema){
    super(hasSucceeded, returnedSQLMessage, returnedSQLState);
        this.tableSchema = tableSchema;
    }
}
