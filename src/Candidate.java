/**
 * Represents a candidate in the election.
 */
public class Candidate {
    //Stores the name of the candidate.
    private String name;
    //Stores the matriculation of the candidate (the program assumes they are also students).
    private String matricNum;
    //Candidate date of birth
    private String dob;

    /**
     * Getter for name.
     * @return the candidate's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for matriculation number.
     * @return candidate matriculation number.
     */
    public String getMatricNum() {
        return matricNum;
    }

    /**
     * Getter for candidate date of birth.
     * @return the candidate's date of birth.
     */
    public String getDob() {
        return dob;
    }

    /**
     * Constructor for Candidate.
     * @param name of candidate.
     * @param matricNum of candidate.
     * @param dob of candidate.
     */
    public Candidate(String name, String matricNum, String dob) {
        this.name = name;
        this.matricNum = matricNum;
        this.dob = dob;
    }
}
