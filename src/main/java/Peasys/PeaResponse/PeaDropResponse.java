package Peasys.PeaResponse;

/**
 * Represents the concept of response in the case of a DROP query executed on the database of an AS/400 server
 * by a PeaClient object.
 */
public final class PeaDropResponse extends PeaResponse {
    /**
     * Initialize a new instance of the PeaDropResponse class.
     * @param hasSucceeded Boolean set to true if the query has correctly been executed. Set to true if the SQL state is 00000.
     * @param returnedSQLMessage SQL message return from the execution of the query.
     * @param returnedSQLState SQL state return from the execution of the query.
     */
    public PeaDropResponse(boolean hasSucceeded, String returnedSQLMessage, String returnedSQLState) {
    super(hasSucceeded, returnedSQLMessage, returnedSQLState);
    }
}
