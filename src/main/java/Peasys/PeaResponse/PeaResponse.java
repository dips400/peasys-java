package Peasys.PeaResponse;

/**
 * Abstracts the concept of response in the case of a query executed on the database of an AS/400 server by a PeaClient object.
 */
public class PeaResponse {
    public boolean hasSucceeded;
    public String returnedSQLMessage;
    public String returnedSQLState;

    /**
     * Initialize a new instance of the PeaResponse class.
     * @param hasSucceeded Boolean set to true if the query has correctly been executed. Set to true if the SQL state is 00000.
     * @param returnedSQLMessage SQL message return from the execution of the query.
     * @param returnedSQLState SQL state return from the execution of the query.
     */
    public PeaResponse(boolean hasSucceeded, String returnedSQLMessage, String returnedSQLState) {
        this.hasSucceeded = hasSucceeded;
        this.returnedSQLMessage = returnedSQLMessage;
        this.returnedSQLState = returnedSQLState;
    }
}