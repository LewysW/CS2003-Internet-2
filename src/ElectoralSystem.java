
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Represents the electoral system responsible for counting the results of the vote.
 */
public class ElectoralSystem {
    //Ballot to have access to candidates.
    private Ballot ballot;
    //Votes cast in election.
    private ArrayList<Vote> votes;

    /**
     * Constructor for ElectoralSystem.
     * @param ballot containing the candidates.
     * @param votes cast in the election.
     */
    public ElectoralSystem(Ballot ballot, ArrayList<Vote> votes) {
        this.ballot = ballot;
        this.votes = votes;
    }

    /**
     * Gets the results of the vote in the election.
     * @return a hash map containing names of candidates and their associated number of votes.
     */
    public HashMap<String, Integer> getVoteResults() {
        HashMap<String, Integer> voteResults = new HashMap<>();

        //Iterates over each vote in the vote list.
        for (Vote v: votes) {
            try {
                //Produces a new UUID from the vote ID.
                UUID uuid = UUID.fromString(v.getId());

                /*If the new UUID does not match the original vote ID then throw IllegalArgumentException
                and skip counting the vote as its ID was not valid and must as a precaution be considered
                a fraudulent/suspicious vote.
                 */
                if (!uuid.toString().equals(v.getId())) {throw new IllegalArgumentException();}

                //If the counted results already contains the matriculation number of the current candidate voted for
                //increment that candidates number of votes.
                if (voteResults.keySet().contains(v.getMatricNum())) {
                    voteResults.put(v.getMatricNum(), voteResults.get(v.getMatricNum()) + 1);

                //Otherwise add that candidates number to the results and set their initial votes to 1.
                } else {
                    voteResults.put(v.getMatricNum(), 1);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        /*Iterates over the results and replaces candidate matriculation numbers with names to make them
        more meaningful when displayed to the user.
         */
        for (int i = 0; i < voteResults.size(); i++) {
            String matricNum = ballot.getCandidates().get(i).getMatricNum();
            Integer votes = voteResults.get(matricNum);
            voteResults.remove(matricNum);
            voteResults.put(ballot.getCandidates().get(i).getName(), votes);
        }


        return voteResults;
    }
}
