package Peasys.PeaResponse;

import java.util.Dictionary;
import java.util.List;

/**
 * Represents the concept of response in the case of a SELECT query executed on the database of an AS/400 server
 * by a PeaClient object.
 */
public final class PeaSelectResponse extends PeaResponse {
    public Dictionary<String, List<String>> result;
    public int rowCount;
    public String[] columnsName;

    /**
     * Initialize a new instance of the PeaSelectResponse class.
     * @param hasSucceeded Boolean set to true if the query has correctly been executed. Set to true if the SQL state
     *                     is 00000.
     * @param returnedSQLMessage SQL message return from the execution of the query.
     * @param returnedSQLState SQL state return from the execution of the query.
     * @param result Results of the query in the form of a Dictionary where the columns' name are the key and the
     *               values are the elements of this column in the SQL table.
     * @param rowCount Represents the number of rows that have been retrieved by the query.
     * @param columnsName Array representing the name of the columns in the order of the SELECT query.
     */
    public PeaSelectResponse(boolean hasSucceeded, String returnedSQLMessage, String returnedSQLState, Dictionary<String, List<String>> result, int rowCount, String[] columnsName){
    super(hasSucceeded, returnedSQLMessage, returnedSQLState);
        this.result = result;
        this.rowCount = rowCount;
        this.columnsName = columnsName;
    }
}
