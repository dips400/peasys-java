package Peasys.PeaResponse;
import java.util.List;

/**
 * Represents the concept of response in the case of an OS/400 command executed on the database of an AS/400 server
 * by a PeaClient object.
 */
public final class PeaCommandResponse {
    public boolean hasSucceeded;
    public List<String> warnings;

    /**
     *
     * @param hasSucceeded Boolean set to true if the command has correctly been executed meaning that no
     *                     CPFxxxx was return. Still, description messages can be return along with CP*xxxx.
     * @param warnings List of warnings that results form the command execution. Errors are of the form :
     *                 CP*xxxx Description of the warning.
     */
    public PeaCommandResponse(boolean hasSucceeded, List<String> warnings) {
        this.hasSucceeded = hasSucceeded;
        this.warnings = warnings;
    }
}
