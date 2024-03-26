package Peasys;

import Peasys.PeaException.*;
import Peasys.PeaResponse.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the client part of the client-server architecture of the Peasys technology.
 */
public class PeaClient {

    private static final String END_PACK = "dipsjbiemg";
    private static final String API_URL = "http://localhost:8080";
    public final String idClient;
    public final boolean onlineVersion;
    public final String partitionName;
    public final String userName;
    public final int port;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    public boolean retrieveStatistics;
    public String connexionMessage;
    public int connectionStatus;

    /**
     * Initialize a new instance of the PeaClient class. Initiates a connexion with the AS/400 server.
     *
     * @param partitionName      DNS name (name of the partition) of the remote AS/400 server.
     * @param port               Port used for the data exchange between the client and the server.
     * @param userName           Username of the AS/400 profile used for connexion.
     * @param password           Password of the AS/400 profile used for connexion.
     * @param idClient           ID of the client on the DIPS website.
     * @param onlineVersion      Set to true if you want to use the online version of Peasys (<a href="https://dips400.com/docs/connexion">more information</a>).
     * @param retrieveStatistics Set to true if you want the statistics of the license key use to be collect.
     * @throws PeaConnexionException Exception thrown when the client was not able to successfully connect to the server.
     */
    public PeaClient(String partitionName, int port, String userName, String password, String idClient, boolean onlineVersion, boolean retrieveStatistics) throws PeaConnexionException {
        if (partitionName.isEmpty() || userName.isEmpty() || password.isEmpty() || idClient.isEmpty()) {
            throw new PeaInvalidCredentialsException("Parameters of the PeaClient should not be either null or empty");
        }

        if (userName.length() > 10 || password.length() > 10) {
            this.connectionStatus = -1;
            this.connexionMessage = "userName and/or password too long";
            throw new PeaInvalidCredentialsException("user & password must be less than 10 characters");
        }

        this.partitionName = partitionName;
        this.port = port;
        this.userName = userName;
        this.idClient = idClient;
        this.onlineVersion = onlineVersion;
        this.retrieveStatistics = retrieveStatistics;

        String token = "pldgchjtsxlqyfucjstpldgchjcjstemzplfpldgchjtsxlqyfucjstemzplfutysnchqternoutysnchqternoemzplfutysnchqterno";
        if (onlineVersion) {
            try {
                String url = String.format(API_URL + "/api/license-key/retrieve-token/%s/%s", partitionName, idClient);
                URL obj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                Map<String, String> response = readData(connection.getInputStream());

                // TODO : retrieve token
                System.out.println("Response: " + response);
            } catch (Exception exception) {
                System.out.println(exception);
                throw new PeaInvalidLicenseKeyException("The license key that you provided is not valid");
            }
        }

        try {
            Socket socket = new Socket(partitionName, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (Exception ex) {
            throw new PeaConnexionException("Error connecting the TCP client", ex);
        }

        // String login = userName + " ".repeat(10 - userName.length()) + token + " ".repeat(100 - password.length()) + password;
        String login = userName + " ".repeat(10 - userName.length()) + password + " ".repeat(10 - password.length());

        try {
            outputStream.write(login.getBytes(StandardCharsets.UTF_8));
            byte[] bb = new byte[1];
            inputStream.read(bb, 0, 1);
            connectionStatus = Integer.parseInt(new String(bb));
        } catch (IOException exception) {
            throw new PeaConnexionException("Unable to connect to the server", exception);
        }

        switch (connectionStatus) {
            case 1:
                try {
                    sendStatistics("{\"Name\": \"" + userName + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
                } catch (Exception e) {
                    break;
                }
                connexionMessage = "Connected";
                break;
            case 2:
                connexionMessage = "Unable to set profile";
                throw new PeaConnexionException("Unable to set profile + TODO");
            case 3:
                connexionMessage = "Invalid credential";
                throw new PeaInvalidCredentialsException("Invalid userName or password, check again");
            case 4:
                connexionMessage = "Invalid serial number/model";
                throw new PeaInvalidCredentialsException("Invalid model or serial number");
            case 5:
                connexionMessage = "Product expired";
                throw new PeaConnexionException("Product expired");
            default:
                throw new PeaConnexionException("Exception during connexion process, contact us for more information");
        }
    }

    /**
     * Sends the SELECT SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query SQL query that should start with the SELECT keyword.
     * @return An instance of the PeaSelectResponse object.
     * @throws PeaInvalidSyntaxQueryException Thrown if the query syntax is invalid.
     * @throws IOException                    Thrown if the connexion is lost when sending this query.
     */
    public PeaSelectResponse executeSelect(String query) throws PeaInvalidSyntaxQueryException, IOException, PeaQueryException {

        // check query null, empty and not starting with SELECT
        if (query.isEmpty()) throw new PeaInvalidSyntaxQueryException("Query should not be either null or empty");
        if (!query.toUpperCase().startsWith("SELECT"))
            throw new PeaInvalidSyntaxQueryException("Query should starts with the SELECT SQL keyword");

        String header = retrieveData("geth" + query + END_PACK);

        String returnedSQLState = "00000";
        String returnedSQLMessage = "SELECT query went well";
        ArrayList<String> listName = new ArrayList<>(), listType = new ArrayList<>(), listPrec = new ArrayList<>(), listScale = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Map<String, Object>> decodedData = mapper.readValue(header, new TypeReference<>() {
            });
            for (Map<String, Object> field : decodedData) {
                for (String key : field.keySet()) {
                    switch (key) {
                        case "name":
                            listName.add(field.get(key).toString());
                            break;
                        case "type":
                            listType.add(field.get(key).toString());
                            break;
                        case "prec":
                            listPrec.add(field.get(key).toString());
                            break;
                        case "scal":
                            listScale.add(field.get(key).toString());
                            break;
                        default:
                            throw new PeaQueryException();
                    }
                }
            }
        } catch (Exception ex) {
            returnedSQLState = header.substring(1, 6);
            returnedSQLMessage = header.substring(6);
            return new PeaSelectResponse(false, returnedSQLMessage, returnedSQLState, null, 0, null);
        }

        int nb_col = listPrec.size();
        String[] colname = new String[nb_col];

        // fill up the arrays with the value retrieved in the lists
        int sum_precision = 0;
        for (int j = 0; j < nb_col; j++) {
            colname[j] = listName.get(j).trim().toLowerCase();
            sum_precision += Integer.parseInt(listPrec.get(j));
        }

        Hashtable<String, List<String>> result = new Hashtable<>();
        for (int c = 0; c < nb_col; c++) {
            result.put(colname[c], new ArrayList<>());
        }

        // send the second command to retrieve the data
        String data = retrieveData("getd" + query + END_PACK);

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + data.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + returnedSQLState + "\", \"SqlMessage\": \"" + returnedSQLMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }

        int nb_row = data.length() / sum_precision;
        int pointer = 0;
        while (!(pointer == data.length())) {
            for (int m = 0; m < nb_col; m++) {
                int scale = Integer.parseInt(listScale.get(m));
                int precision = Integer.parseInt(listPrec.get(m));
                int type = Integer.parseInt(listType.get(m));

                // numeric packed
                if ((type == 484 && scale != 0) || (type == 485 && scale != 0) || (type == 488 && scale != 0) || (type == 489 && scale != 0)) {
                    double temp_float_data = Double.parseDouble(data.substring(pointer, pointer + precision)) / Math.pow(10, scale);
                    pointer += precision;
                    result.get(colname[m]).add(Double.toString(temp_float_data));
                }
                // long
                else if (type == 492 || type == 493) {
                    result.get(colname[m]).add(data.substring(pointer, pointer + 20));
                    pointer += 20;
                }
                // int
                else if (type == 496 || type == 497) {
                    result.get(colname[m]).add(data.substring(pointer, pointer + 10));
                    pointer += 10;
                }
                // short
                else if (type == 500 || type == 501) {
                    result.get(colname[m]).add(data.substring(pointer, pointer + 5));
                    pointer += 5;
                }
                // String, date, time, timestamp
                else {
                    if (type == 389) {
                        result.get(colname[m]).add(data.substring(pointer, pointer + precision));
                    } else if (type == 385) {
                        result.get(colname[m]).add(data.substring(pointer, pointer + precision));
                    } else {
                        result.get(colname[m]).add(data.substring(pointer, pointer + precision));
                    }
                    pointer += precision;
                }
            }
        }

        // retrieve data and create the PeaSelectResponse object to return
        return new PeaSelectResponse(returnedSQLState.equals("00000"), returnedSQLMessage, returnedSQLState, result, nb_row, colname);
    }

    /**
     * Sends the UPDATE SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query SQL query that should start with the UPDATE keyword.
     * @return An instance of the PeaUpdateResponse object.
     * @throws PeaInvalidSyntaxQueryException Thrown if the query syntax is invalid.
     * @throws IOException                    Thrown if the connexion is lost when sending this query.
     */
    public PeaUpdateResponse executeUpdate(String query) throws PeaQueryException, IOException {
        if (query.isEmpty()) throw new PeaInvalidSyntaxQueryException("Query should not be either null or empty");
        if (!query.toUpperCase().startsWith("UPDATE"))
            throw new PeaInvalidSyntaxQueryException("Query should starts with the UPDATE SQL keyword");

        String header = retrieveData("updt" + query + END_PACK);
        String sqlState = header.substring(1, 5);
        String sqlMessage = header.substring(6).trim();

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + header.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + sqlState + "\", \"SqlMessage\": \"" + sqlMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }

        int rowCount = sqlState.equals("00000") ? Integer.parseInt(sqlMessage.substring(0, 1)) : 0;
        boolean hasSucceeded = sqlState.equals("00000") || sqlState.equals("01504");

        return new PeaUpdateResponse(hasSucceeded, sqlState, sqlMessage, rowCount);
    }

    /**
     * Sends the CREATE SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query SQL query that should start with the CREATE keyword.
     * @return An instance of the PeaCreateResponse object.
     * @throws PeaInvalidSyntaxQueryException   Thrown if the query syntax is invalid.
     * @throws IOException                      Thrown if the connexion is lost when sending this query.
     * @throws PeaUnsupportedOperationException Thrown if the query is used to create something else than a table, an index or a database.
     */
    public PeaCreateResponse executeCreate(String query) throws IOException, PeaQueryException {
        if (query.isEmpty()) throw new PeaInvalidSyntaxQueryException("Query should not be either null or empty");
        if (!query.toUpperCase().startsWith("CREATE"))
            throw new PeaInvalidSyntaxQueryException("Query should starts with the CREATE SQL keyword");

        String header = retrieveData("updt" + query + END_PACK);

        String sqlState = header.substring(1, 5);
        String sqlMessage = header.substring(6).trim();

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + header.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + sqlState + "\", \"SqlMessage\": \"" + sqlMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }

        String[] query_words = query.split(" ");

        // retrieve table schema if a table has been created
        Dictionary<String, ColumnInfo> tb_schema = null;
        if (query_words[1].equalsIgnoreCase("TABLE")) {
            String[] names = query_words[2].split("/");
            tb_schema = retrieveTableSchema(names[1], names[0]);
        }

        switch (query_words[1].toUpperCase()) {
            case "TABLE":
                return new PeaCreateResponse(sqlState.equals("00000"), sqlMessage, sqlState, "", "", tb_schema);
            case "INDEX":
                return new PeaCreateResponse(sqlState.equals("00000"), sqlMessage, sqlState, "", query_words[2], tb_schema);
            case "DATABASE":
                return new PeaCreateResponse(sqlState.equals("00000"), sqlMessage, sqlState, query_words[2], "", tb_schema);
            default:
                throw new PeaUnsupportedOperationException("This query is not yet supported : " + query);
        }
    }

    /**
     * Sends the DELETE SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query SQL query that should start with the DELETE keyword.
     * @return An instance of the PeaDeleteResponse object.
     * @throws PeaInvalidSyntaxQueryException Thrown if the query syntax is invalid.
     * @throws IOException                    Thrown if the connexion is lost when sending this query.
     */
    public PeaDeleteResponse executeDelete(String query) throws PeaQueryException, IOException {
        if (query.isEmpty()) throw new PeaInvalidSyntaxQueryException("Query should not be either null or empty");
        if (!query.toUpperCase().startsWith("DELETE"))
            throw new PeaInvalidSyntaxQueryException("Query should starts with the DELETE SQL keyword");

        String header = retrieveData("updt" + query + END_PACK);

        String sqlState = header.substring(1, 5);
        String sqlMessage = header.substring(6).trim();

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + header.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + sqlState + "\", \"SqlMessage\": \"" + sqlMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }
        int rowCount = sqlState.equals("00000") ? Integer.parseInt(sqlMessage.substring(0, 1)) : 0;

        return new PeaDeleteResponse(sqlState.equals("00000"), sqlMessage, sqlState, rowCount);
    }

    /**
     * Sends the ALTER SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query               SQL query that should start with the ALTER keyword.
     * @param retrieveTableSchema Set to true if the call should return the table schema.
     * @return An instance of the PeaAlterResponse object.
     * @throws PeaInvalidSyntaxQueryException   Thrown if the query syntax is invalid.
     * @throws IOException                      Thrown if the connexion is lost when sending this query.
     * @throws PeaUnsupportedOperationException Thrown when retrieving the table schema.
     */
    public PeaAlterResponse executeAlter(String query, boolean retrieveTableSchema) throws PeaQueryException, IOException {
        if (query.isEmpty()) throw new PeaInvalidSyntaxQueryException("Query should not be either null or empty");
        if (!query.toUpperCase().startsWith("ALTER"))
            throw new PeaInvalidSyntaxQueryException("Query should starts with the ALTER SQL keyword");

        String header = retrieveData("updt" + query + END_PACK);

        String sqlState = header.substring(1, 5);
        String sqlMessage = header.substring(6).trim();

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + header.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + sqlState + "\", \"SqlMessage\": \"" + sqlMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }

        // retrieve table schema if wanted
        Dictionary<String, ColumnInfo> tb_schema = null;
        if (retrieveTableSchema) {
            String[] query_words = query.split(" ");
            String[] names = query_words[2].split("/");
            tb_schema = retrieveTableSchema(names[1], names[0]);
        }

        return new PeaAlterResponse(sqlState.equals("00000"), sqlMessage, sqlState, tb_schema);
    }

    /**
     * Sends the DROP SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query SQL query that should start with the DROP keyword.
     * @return An instance of the PeaDropResponse object.
     * @throws PeaInvalidSyntaxQueryException Thrown if the query syntax is invalid.
     * @throws IOException                    Thrown if the connexion is lost when sending this query.
     */
    public PeaDropResponse executeDrop(String query) throws PeaQueryException, IOException {
        if (query.isEmpty()) throw new PeaInvalidSyntaxQueryException("Query should not be either null or empty");
        if (!query.toUpperCase().startsWith("DROP"))
            throw new PeaInvalidSyntaxQueryException("Query should starts with the DROP SQL keyword");

        String header = retrieveData("updt" + query + END_PACK);

        String sqlState = header.substring(1, 5);
        String sqlMessage = header.substring(6).trim();

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + header.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + sqlState + "\", \"SqlMessage\": \"" + sqlMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }

        return new PeaDropResponse(sqlState.equals("00000"), sqlMessage, sqlState);
    }

    /**
     * Sends the INSERT SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query SQL query that should start with the INSERT keyword.
     * @return An instance of the PeaInsertResponse object.
     * @throws PeaInvalidSyntaxQueryException Thrown if the query syntax is invalid.
     * @throws IOException                    Thrown if the connexion is lost when sending this query.
     */
    public PeaInsertResponse executeInsert(String query) throws PeaQueryException, IOException {
        // check query null, empty and not starting with INSERT
        if (query.isEmpty()) throw new PeaInvalidSyntaxQueryException("Query should not be either null or empty");
        if (!query.toUpperCase().startsWith("INSERT"))
            throw new PeaInvalidSyntaxQueryException("Query should starts with the INSERT SQL keyword");

        String header = retrieveData("updt" + query + END_PACK);

        String sqlState = header.substring(1, 5);
        String sqlMessage = header.substring(6).trim();

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + header.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + sqlState + "\", \"SqlMessage\": \"" + sqlMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }

        int rowCount = sqlState.equals("00000") ? Integer.parseInt(sqlMessage.substring(0, 1)) : 0;

        return new PeaInsertResponse(sqlState.equals("00000"), sqlState, sqlMessage, rowCount);
    }

    /**
     * Sends the  SQL query to the server that execute it and retrieve the desired data.
     *
     * @param query SQL query that should start with the  keyword.
     * @return An instance of the PeaResponse object.
     * @throws IOException Thrown if the connexion is lost when sending this query.
     */
    public PeaResponse execute(String query) throws IOException, PeaQueryException {

        String header = retrieveData("updt" + query + END_PACK);

        String sqlState = header.substring(1, 5);
        String sqlMessage = header.substring(6).trim();

        // send statistics if wanted
        if (retrieveStatistics) {
            sendStatistics("{\"Name\": \"data_in\", \"Bytes\":\"" + query.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"data_out\", \"Bytes\": \"" + header.getBytes().length + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
            sendStatistics("{\"Name\": \"log\", \"UserName\" : \"" + userName + "\", " + "\"Query\": \"" + query.split(" ")[0] + "\", " +
                    "\"SqlCode\": \"" + sqlState + "\", \"SqlMessage\": \"" + sqlMessage + "\", \"IdClient\" : \"" + idClient + "\", \"PartitionName\" : \"" + partitionName + "\"}");
        }

        return new PeaResponse(sqlState.equals("00000"), sqlState, sqlMessage);
    }

    /**
     * Sends the SELECT SQL query to the server that execute it and retrieve the desired data.
     *
     * @param command OS400 command to be executed by the server.
     * @return An instance of the PeaSelectResponse object.
     * @throws IOException Thrown if the connexion is lost when sending this query.
     */
    public PeaCommandResponse executeCommand(String command) throws IOException {
        String customCmd = "exas" + command + END_PACK;
        int descriptionOffset = 112;

        String result_raw = retrieveData(customCmd);
        List<String> result = new ArrayList<>();
        boolean hasSucceeded = true;
        try {
            Pattern rx = Pattern.compile("C[A-Z]{2}[0-9]{4}");
            Matcher m = rx.matcher(result_raw);
            while (m.find()) {

                if (!m.group().startsWith("CPI")) {
                    String description = result_raw.substring(m.start() + descriptionOffset, result_raw.length() - m.start() - descriptionOffset);
                    description = description.substring(0, description.indexOf('.'));
                    description = description.replaceAll("[^a-zA-Z0-9 áàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÀÂÄÃÅÇÉÈÊËÌÎÑÓÒÔÖÕÚÙÛÆŒ._'*/:-]", "");

                    result.add(m.group() + " " + description);
                }

                if (m.group().startsWith("CPF")) {
                    hasSucceeded = false;
                }
            }
            return new PeaCommandResponse(hasSucceeded, result);
        } catch (Exception e) {
            return new PeaCommandResponse(false, new ArrayList<>());
        }
    }

    public void disconnect() throws IOException {
        // change command to byte array, send to server and cloe the TCP client
        byte[] ba = "stopdipsjbiemg".getBytes(StandardCharsets.UTF_8);

        outputStream.write(ba, 0, ba.length);
        outputStream.flush();
        outputStream.close();
    }

    // private functions
    private String retrieveData(String command) throws IOException {
        StringBuilder data = new StringBuilder();

        // transform to byte array and send it to the server
        byte[] ba = command.getBytes(StandardCharsets.UTF_8);
        outputStream.write(ba, 0, ba.length);
        outputStream.flush();

        if (command.startsWith("geth") || command.startsWith("updt")) {
            data.append('[');
        }
        while (data.length() < END_PACK.length() || !Objects.equals(data.substring(data.length() - END_PACK.length(), data.length()), END_PACK)) {
            // read byte by byte the response from the server each byte into a char and append to the data (raw form)
            byte[] bb = new byte[1];
            inputStream.read(bb, 0, 1);
            data.append(new String(bb));
        }

        // build data as a String with the String building a remove the suffix
        return data.substring(0, data.length() - END_PACK.length());
    }

    private Dictionary<String, ColumnInfo> retrieveTableSchema(String tableName, String schemaName) throws IOException, PeaUnsupportedOperationException {
        String query =
                "SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE, LENGTH, NUMERIC_SCALE, IS_NULLABLE, IS_UPDATABLE, LONG_COMMENT, NUMERIC_PRECISION " +
                        String.format("FROM QSYS2.SYSCOLUMNS WHERE SYSTEM_TABLE_NAME = %s AND SYSTEM_TABLE_SCHEMA = %s",
                                tableName.toUpperCase(), schemaName.toUpperCase());

        String header = retrieveData("geth" + query + END_PACK);

        ArrayList<String> listName = new ArrayList<>(), listType = new ArrayList<>(), listPrec = new ArrayList<>(), listScale = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Map<String, Object>> decodedData = mapper.readValue(header, new TypeReference<>() {
            });
            for (Map<String, Object> field : decodedData) {
                for (String key : field.keySet()) {
                    switch (key) {
                        case "name":
                            listName.add(field.get(key).toString());
                            break;
                        case "type":
                            listType.add(field.get(key).toString());
                            break;
                        case "prec":
                            listPrec.add(field.get(key).toString());
                            break;
                        case "scal":
                            listScale.add(field.get(key).toString());
                            break;
                        default:
                            throw new PeaQueryException();
                    }
                }
            }
        } catch (Exception ex) {
            throw new PeaUnsupportedOperationException("It seems that the query your trying to make is not yet supported by Peasys, feel free to contact us");
        }
        int nb_col = listPrec.size();
        String[] colname = new String[nb_col];

        int sum_precision = 0;
        for (int j = 0; j < nb_col; j++) {
            colname[j] = listName.get(j).trim().toLowerCase();
            sum_precision += Integer.parseInt(listPrec.get(j));
        }

        Hashtable<String, List<String>> result = new Hashtable<>();
        for (int c = 0; c < nb_col; c++) {
            result.put(colname[c], new ArrayList<>());
        }

        String data = retrieveData("getd" + query + END_PACK);

        int nb_row = data.length() / sum_precision;
        int pointer = 0;
        while (!(pointer == data.length())) {
            for (int m = 0; m < nb_col; m++) {
                int scale = Integer.parseInt(listScale.get(m));
                int precision = Integer.parseInt(listPrec.get(m));
                int type = Integer.parseInt(listType.get(m));

                // numeric packed
                if ((type == 484 && scale != 0) || (type == 485 && scale != 0) || (type == 488 && scale != 0) || (type == 489 && scale != 0)) {
                    double temp_float_data = Double.parseDouble(data.substring(pointer, pointer + precision)) / Math.pow(10, scale);
                    pointer += precision;
                    result.get(colname[m]).add(Double.toString(temp_float_data));
                }
                // long
                else if (type == 492 || type == 493) {
                    result.get(colname[m]).add(data.substring(pointer, pointer + 20));
                    pointer += 20;
                }
                // int
                else if (type == 496 || type == 497) {
                    result.get(colname[m]).add(data.substring(pointer, pointer + 10));
                    pointer += 10;
                }
                // short
                else if (type == 500 || type == 501) {
                    result.get(colname[m]).add(data.substring(pointer, pointer + 5));
                    pointer += 5;
                }
                // String, date, time, timestamp
                else {
                    if (type == 389) {
                        result.get(colname[m]).add(data.substring(pointer, pointer + precision));
                    } else if (type == 385) {
                        result.get(colname[m]).add(data.substring(pointer, pointer + precision));
                    } else {
                        result.get(colname[m]).add(data.substring(pointer, pointer + precision));
                    }
                    pointer += precision;
                }
            }
        }

        Hashtable<String, ColumnInfo> tb_name = new Hashtable<>();
        for (List<String> list : result.values()) {
            tb_name.put(list.get(0), new ColumnInfo(list.get(0), Integer.parseInt(list.get(1)), list.get(2),
                    Integer.parseInt(list.get(3)), Integer.parseInt(list.get(4)), list.get(5), list.get(6),
                    list.get(7), Integer.parseInt(list.get(8))));
        }

        return tb_name;
    }

    private void sendStatistics(String data) throws PeaQueryException {
        try {
            URL obj = new URL(API_URL + "/api/license-key/update");
            HttpRequest req = HttpRequest.newBuilder(obj.toURI())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(data))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(req, HttpResponse.BodyHandlers.ofString());
            response.body();

        } catch (Exception exception) {
            throw new PeaQueryException("Currently impossible to send statistics to the server. You can " +
                    "temporarily disable it to continue to use Peasys.", exception);
        }
    }

    private Map<String, String> readData(InputStream stream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        // Close the BufferedReader
        in.close();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.toString(), new TypeReference<>() {
        });
    }
}
