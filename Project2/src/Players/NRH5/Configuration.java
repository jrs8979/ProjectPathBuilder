package Players.NRH5;
import java.util.Collection;

/**
 * The representation of a single configuration for a puzzle.
 * The Backtracker depends on these routines in order to
 * solve a puzzle.  Therefore, all puzzles must implement this
 * interface.
 *
 * @author Ryan Chung, Jacob Scida
 */
public interface Configuration {
    /**
     * Get the collection of successors from the current one.
     * @param whoseTurn- the current player's turn
     * @return All successors, valid and invalid
     */
    public Collection<NRH5Config> getSuccessors(int whoseTurn);
    
    /**
     * Is the current configuration valid or not?
     * @return true if valid; false otherwise
     */
    public boolean isValid();
    
    /**
     * Is the current configuration a goal?
     * @return true if goal; false otherwise
     */
    public boolean isGoal();
}
