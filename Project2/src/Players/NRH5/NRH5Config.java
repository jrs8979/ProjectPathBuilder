package Players.NRH5;

/**
 * Created by Ryan Chung and Jacob Scida on 4/22/2017.
 */

import Interface.Coordinate;
import Interface.PlayerMove;

import java.util.*;

/**
 * A class to represent a single configuration of NRH5
 */
public class NRH5Config implements Configuration {
    protected int[][] board;
    protected int playerId;
    protected int dimension;
    protected List<PlayerMove> moves;
    protected int id;

    //Constructor for NRH5Config configuration
    public NRH5Config(int[][] board, int playerId, int dimension, List<PlayerMove> moves, int id){
        this.board = board;
        this.playerId = playerId;
        this.dimension = dimension;
        this.moves = moves;
        this.id = id;

    }
    /**
     * Copy constructor for NRH5
     * @param other- the original configuration that will be copied
     */
    protected NRH5Config(NRH5Config other){
        board = new int[other.dimension][other.dimension];
        for (int row = 0; row < other.dimension; row++){
            for (int col = 0; col < other.dimension; col++){
                board[row][col] = other.board[row][col];
            }
        }
        moves = other.moves;
        playerId = other.playerId;
        dimension = other.dimension;
    }

    /**
     * Generates successors from an original NRH5 configuration
     * @return a list of all successors of the current configuration
     */
    public Collection<NRH5Config> getSuccessors(int whoseTurn) {
        LinkedList<NRH5Config> successors = new LinkedList<>();
        if (moves.size() != 0) {
            for (PlayerMove m : moves) {
                if (whoseTurn == 1) {
                    NRH5Config copy = new NRH5Config(this);
                    if (copy.board[m.getCoordinate().getRow()][m.getCoordinate().getCol()] == 0) {
                        copy.board[m.getCoordinate().getRow()][m.getCoordinate().getCol()] = 1;
                        successors.add(copy);
                    }
                } else {
                    NRH5Config copy = new NRH5Config(this);
                    if (copy.board[m.getCoordinate().getRow()][m.getCoordinate().getCol()] == 0) {
                        copy.board[m.getCoordinate().getRow()][m.getCoordinate().getCol()] = 2;
                        successors.add(copy);
                    }
                }
            }
        }
        return successors;
    }

    /**
     * @return Always returns true because moves are generated from allLegalMoves()
     */
    public boolean isValid() {
        return true;
    }

    /**
     * Returns true if the current board represents a win for the player, false otherwise
     * @return A boolean indicating a winning path for the player of interest
     */
    public boolean isGoal(){
        return hasWonGame(id);
    }

    /**
     * Parses through the board and detects which player won the game
     * @param id- the player id of interest
     */
    public boolean hasWonGame(int id){
        ArrayList<Coordinate> queue = new ArrayList<>();
        HashSet<Coordinate> visited = new HashSet<>();
        if(id == 1){
            for(int i = 1; i < dimension; i = i + 2){
                Coordinate add = new Coordinate(i, 0);
                queue.add(add);
            }
        } else {
            for(int i = 1; i < dimension; i = i + 2){
                Coordinate add = new Coordinate(0, i);
                queue.add(add);
            }
        }
        while(!queue.isEmpty()){
            Coordinate currCoordiante = queue.get(0);
            int col = currCoordiante.getCol();
            int row = currCoordiante.getRow();
            if((col == dimension - 1 && id == 1) || (row == dimension - 1 && id == 2)){
                return true;
            } else {
                if(!(row + 1 > dimension - 1) && board[row + 1][col] == id){
                    Coordinate front = new Coordinate(row + 1, col);
                    if(!queue.contains(front) && !visited.contains(front)){
                        queue.add(front);
                    }
                }
                if(!(col + 1 > dimension - 1) && board[row][col + 1] == id){
                    Coordinate bottom = new Coordinate(row, col + 1);
                    if(!queue.contains(bottom) && !visited.contains(bottom)){
                        queue.add(bottom);
                    }
                }
                if(!(row - 1 < 1) && board[row - 1][col] == id){
                    Coordinate back = new Coordinate(row - 1, col);
                    if(!queue.contains(back) && !visited.contains(back)){
                        queue.add(back);
                    }
                }
                if(!(col - 1 < 1) && board[row][col - 1] == id){
                    Coordinate top = new Coordinate(row, col - 1);
                    if(!queue.contains(top) && !visited.contains(top)){
                        queue.add(top);
                    }
                }
                queue.remove(currCoordiante);
                visited.add(currCoordiante);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "";
        for (int row = 0; row < dimension; row++) {
            result+= "\n";
            for(int col = 0; col < dimension; col++){
                result+= board[row][col];
            }
        }
        return result;
    }
}
