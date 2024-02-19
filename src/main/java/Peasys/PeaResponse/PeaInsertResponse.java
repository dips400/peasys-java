package Peasys.PeaResponse;

/**
 * Represents the concept of response in the case of an INSERT query executed on the database of an AS/400 server by
 * a PeaClient object.
 */
public final class PeaInsertResponse extends PeaResponse {
    public int RowCount;

    /**
     * Initialize a new instance of the PeaInsertResponse class.
     * @param hasSucceeded Boolean set to true if the query has correctly been executed. Set to true if the SQL state is 00000.
     * @param returnedSQLMessage SQL message return from the execution of the query.
     * @param returnedSQLState SQL state return from the execution of the query.
     * @param rowCount Represents the number of rows that have been deleted by the query.
     */
    public PeaInsertResponse(boolean hasSucceeded, String returnedSQLMessage, String returnedSQLState, int rowCount){
    super(hasSucceeded, returnedSQLMessage, returnedSQLState);
        this.RowCount = rowCount;
    }
}
