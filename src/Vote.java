import java.util.UUID;

/**
 * Represents a single vote in the election.
 */
public class Vote {
    //Matriculation number of candidate.
    private String matricNum;
    //Unique ID of vote
    private String id;

    /**
     * Constructor for Vote.
     * @param matricNum of candidate.
     */
    public Vote(String matricNum) {
        this.matricNum = matricNum;

        //Sets id equal to a randomUUID. Used for extra validation that a vote is genuine.
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Getter for matriculation number.
     * @return the matriculation number of the candidate voted for.
     */
    public String getMatricNum() {
        return matricNum;
    }

    /**
     * Getter for ID.
     * @return the ID of the vote.
     */
    public String getId() {
        return id;
    }
}
