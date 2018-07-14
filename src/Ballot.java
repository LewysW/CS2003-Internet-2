import java.util.*;

import static java.lang.System.exit;

/**
 * Represents the ballot of the election containing the candidates standing for election.
 */
public class Ballot {

    //Array list of candidates
    private ArrayList<Candidate> candidates = new ArrayList<>();

    //Number of candidates
    private int numCandidates;

    //Index of each detail of the candidates to be stored.
    private static final int NAME = 0;
    private static final int MATRIC_NUM = 1;
    private static final int DATE_OF_BIRTH = 2;

    /**
     * Getter for candidates array.
     * @return the array list of electoral candidates.
     */
    public ArrayList<Candidate> getCandidates() {
        return candidates;
    }

    /**
     * Getter for numCandidates.
     * @return the number of candidates standing for election.
     */
    public int getNumCandidates() {
        return numCandidates;
    }

    /**
     * Constructor for Ballot class.
     */
    public Ballot() {

        //Stores the candidates in a random ordering for the current ballot.
        candidates = randomiseCandidateOrder();

        //Stores the size of the candidates array in numCandidates.
        numCandidates = candidates.size();
    }

    /**
     * Randomises the order of the candidates in the ballot.
     * This means that the voters do not necessarily receive the candidates in the same ordering.
     * This video explains why this is beneficial for anonymity in electronic voting:
     * https://www.youtube.com/watch?v=izddjAp_N4I
     * @return the randomly ordered array list of election candidates.
     */
    private ArrayList<Candidate> randomiseCandidateOrder() {
        /*
        Gets current directory, stores the list of candidate details in an array list of strings, and sets up a new
        array list of candidates.
         */
        FileIO fileIO = new FileIO(System.getProperty("user.dir"));
        ArrayList<String> candidateDetails = fileIO.storeAsArrayList(fileIO.readFile("candidates"), "\n");
        ArrayList<Candidate> electionCandidates = new ArrayList<>();

        /*
        Iterates over the candidates details, splitting them by commas and then assigning them to the electionCandidates
        array list.
         */
        for (int i = 0; i < candidateDetails.size(); i++) {
            String[] credentials = candidateDetails.get(i).split(",");
            electionCandidates.add(new Candidate(credentials[NAME], credentials[MATRIC_NUM], credentials[DATE_OF_BIRTH]));
        }

        //Shuffles the order of the candidates.
        Collections.shuffle(electionCandidates);

        return electionCandidates;
    }

    /**
     * Generates a String storing the "ballot paper" which is sent to the user and displays the candidates in their
     * randomly generated ordering.
     * @return the ballot as a String.
     */
    public String generateBallotPaper() {
        //If no candidates then an error message is displayed server side and the program exits.
        if (candidates.size() == 0) {
            System.out.println("No candidates on ballot.");
            exit(0);
        }

        String ballot = "";

        ballot += "Candidate List\n";

        //Iterates over each candidate and adds to the ballot.
        for (int i = 0; i < candidates.size(); i++) {
            ballot += "Candidate " + (i + 1) + ": " + candidates.get(i).getName() + "\n";
        }

        return ballot;
    }



}
