package Peasys.PeaResponse;
import java.util.List;

/**
 * Represents the concept of response in the case of an OS/400 command executed on the database of an AS/400 server
 * by a PeaClient object.
 */
public final class PeaCommandResponse {
    public List<String> warnings;

    /**
     *
     * @param warnings List of warnings that results form the command execution. Errors are of the form :
     *                 CP*xxxx Description of the warning.
     */
    public PeaCommandResponse(List<String> warnings) {
        this.warnings = warnings;
    }
}
