package Players.NRH5;

import Interface.Coordinate;
import Interface.PlayerModule;
import Interface.PlayerModulePart3;
import Interface.PlayerMove;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.util.*;

/**
 * Authors: Ryan Chung, Jacob Scida
 */

/**
 * Class representing a player move with dist1 representing the player's fewestSegmentsToVictory and
 * dist2 representing the opponent's fewestSegmentsToVictory
 */
class PlayerMoveInfo{
    private PlayerMove pm;
    private int dist1;
    private int dist2;

    /**
     * Constructor for PlayerMoveInfo class
     * @param pm- the player move
     * @param dist1- fewestSegmentsToVictory for player of interest
     * @param dist2- fewestSegmentsToVictory for opponent
     */
    public PlayerMoveInfo(PlayerMove pm, int dist1, int dist2){
        this.pm = pm;
        this.dist1 = dist1;
        this.dist2 = dist2;
    }

    /**
     * Getter for dist1
     * @return fewestSegmentsToVictory for player of interest
     */
    public int getDist1(){
        return dist1;
    }

    /**
     * Getter for dist2
     * @return fewestSegmentsToVictory for opponent
     */
    public int getDist2(){
        return dist2;
    }

    /**
     * Getter for player move
     * @return The player move
     */
    public PlayerMove getPlayerMove(){
        return pm;
    }
}

public class NRH5 implements PlayerModule {
    private int[][] board;
    private int playerId;
    private int dimension;
    private List<PlayerMove> moves;
    private Random rand = new Random();
    private int Rctr = 0;


    /* Initializes player and board, using helper method ot make the board */
    @Override
    public void initPlayer(int dim, int playerId) {
        this.playerId = playerId;
        dimension = dim * 2 + 1;
        board = new int[dimension][dimension];
        initBoard(board);
        moves = new LinkedList<>();
    }

    /* Helper method to create the game board */
    private void initBoard(int[][] board) {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i % 2 == 0) {
                    if (j % 2 == 1) {
                        board[i][j] = 2;
                    }
                } else {
                    if (j % 2 == 0) {
                        board[i][j] = 1;
                    }
                }
            }
        }
    }

    /**
     * Part 2 task that tests if a player can correctly generate all legal moves, assuming that it is that
     * player's turn and given the current game status.
     * Preconditions: There is no winner yet based on prior moves.
     * This method will only be called when it is actually your player's turn based on prior moves.
     *
     * @return a List of all legal PlayerMove objects. They do not have to be in any particular order.
     */
    public List<PlayerMove> allLegalMoves() {
        int x, y;
        ArrayList<PlayerMove> legalMoves = new ArrayList<>();
        for (y = 1; y < dimension - 1; y++) {
            for (x = 1; x < dimension - 1; x++) {
                if (board[x][y] == 0) {
                    Coordinate legal = new Coordinate(x, y);
                    PlayerMove legalMove = new PlayerMove(legal, playerId);
                    legalMoves.add(legalMove);
                }
            }
        }
        return legalMoves;
    }

    /**
     * Finds a winning path for a single configuration if it exists
     *
     * @param config-    A NRH5 configuration
     * @param playerId-  player to determine winnable status for
     * @param whoseTurn- player whose turn it is currently
     * @param numMoves-  num of total moves by which the player of interest must be able to guarantee victory to
     *                   satisfy the requirement to return a value of true
     * @return a winning path solution if it exists, empty otherwise
     */
    private NRH5Config solve(NRH5Config config, int playerId, int whoseTurn, int numMoves) {
        if (config.hasWonGame(playerId) && numMoves > -1) {
            return config;
        }
        if (numMoves <= 0) {
            return null;
        } else {
            if (playerId == whoseTurn) {
                for (NRH5Config child : config.getSuccessors(whoseTurn)) {
                    NRH5Config solution = solve(child, playerId, switchTurn(whoseTurn), numMoves - 1);
                    if (solution != null) {
                        return solution;
                    }
                }
            } else {
                for (NRH5Config child : config.getSuccessors(whoseTurn)) {
                    NRH5Config solution = solve(child, playerId, switchTurn(whoseTurn), numMoves - 1);
                    if (solution == null) {
                        return null;
                    }
                }
                return config;
            }
            return null;
        }
    }


    /**
     * Switches the turn to the other player
     * @param whoseTurn- the current player's turn
     * @return the next player's turn
     */
    private int switchTurn(int whoseTurn){
        int turn = whoseTurn;
        if (turn == 1) {
            turn = 2;
        } else {
            turn = 1;
        }
        return turn;
    }
    /**
     * Determines whether is is possible for the indicated player to win after the specified number of total moves.
     * @param playerId- player to determine winnable status for
     * @param whoseTurn- player whose turn it is currently
     * @param numMoves- num of total moves by which the player of interest must be able to guarantee victory to
     *                  satisfy the requirement to return a value of true
     * @return boolean indicating whether it is possible for the indicated player to guarantee a win
     *         after the specified number of total moves.
     */
    public boolean isWinnable(int playerId, int whoseTurn, int numMoves){
        if (moves.size() == 0) {
            this.moves = allLegalMoves();
        }
        NRH5Config config = new NRH5Config(board, this.playerId, dimension, moves, whoseTurn);
        if (numMoves == 0){
            return hasWonGame(playerId);
        }
        else {
            if (config.hasWonGame(playerId)) {
                return true;
            } else {
                if (config.isValid()) {
                    NRH5Config solution = solve(config, playerId, whoseTurn, numMoves);
                    if (solution != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Searches entire array for least number of moves to be made for a win.
     * @param ID- Current player ID
     * @param coord- Current coordinate being searched.
     * @param numMoves- Number of moves to win
     * @param currMoves- Current number of moves that have been made
     * @param visited- List of visited nodes so far in the path
     * @return Lowest number of moves to be made for a win.
     */
    private int search(int ID, Coordinate coord, int numMoves, int currMoves, HashSet<Coordinate> visited){
        visited.add(coord);
        if(currMoves >= numMoves){
            return numMoves;
        } else {
            int row = coord.getRow();
            int column = coord.getCol();
            Coordinate coord2;
            if((ID == 2 && row == dimension - 1 )||(ID == 1 && column == dimension - 1)){
                numMoves = currMoves;
                return numMoves;
            } else {
                if(ID == 1){
                    if(column + 1 != dimension && board[row][column + 1] != 2){
                        if(board[row][column + 1] == 0){
                            coord2 = new Coordinate(row, column + 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row, column + 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(row - 1 > 0&& board[row - 1][column] != 2){
                        if(board[row - 1][column] == 0){
                            coord2 = new Coordinate(row - 1, column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row - 1,column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(column - 1 > 0 &&board[row][column - 1] != 2){
                        if(board[row][column - 1] == 0){
                            coord2 = new Coordinate(row, column - 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row, column - 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(row + 1 != dimension && column != 0 && board[row + 1][column] != 2){
                        if(board[row + 1][column] == 0){
                            coord2 = new Coordinate(row + 1, column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row + 1, column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(column == 0 && row < dimension - 3) {
                        coord2 = new Coordinate(row + 2, column);
                        return search(ID, coord2, numMoves, currMoves, visited);
                    }
                } else {
                    if(row != 0 && column + 1 != dimension && board[row][column + 1] != 1){
                        if(board[row][column + 1] == 0){
                            coord2 = new Coordinate(row, column + 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row, column + 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(row + 1 != dimension && column != 0 && board[row + 1][column] != 1){
                        if(board[row + 1][column] == 0){
                            coord2 = new Coordinate(row + 1, column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row + 1, column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(column - 1 > 0 &&board[row][column - 1] != 1){
                        if(board[row][column - 1] == 0){
                            coord2 = new Coordinate(row, column - 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row, column - 1);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(row - 1 > 0 &&board[row - 1][column] != 1){
                        if(board[row - 1][column] == 0){
                            coord2 = new Coordinate(row - 1, column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves + 1, visited);
                                visited.remove(coord2);
                            }
                        } else {
                            coord2 = new Coordinate(row - 1, column);
                            if(!visited.contains(coord2)) {
                                numMoves = search(ID, coord2, numMoves, currMoves, visited);
                                visited.remove(coord2);
                            }
                        }
                    }
                    if(row == 0 && column < dimension - 2) {
                        coord2 = new Coordinate(row, column + 2);
                        return search(ID, coord2, numMoves, currMoves, visited);
                    }
                }
            }
            return numMoves;
        }
    }

    /**
     * Part 2 task that computes the fewest segments that a given player needs to add to complete a winning path.
     This ignores the possibility that the opponent might block the path.
     * Precondition: The other player has not already won the game, a winning path still exists for the player of interest.
     * @param playerId- the player of interest
     * @return the fewest number of segments to add to complete a path
     */
    public int fewestSegmentsToVictory(int playerId){
        int moves = Integer.MAX_VALUE;
        int initialMoves = 0;
        int row = 0, column = 0;
        Coordinate coord;
        HashSet<Coordinate> visited = new HashSet<>();
        if(playerId == 1){
            row = 1;
            coord = new Coordinate(row, column);
        } else {
            column = 1;
            coord = new Coordinate(row, column);
        }
        return search(playerId, coord, moves, initialMoves, visited);
    }

    // Places the last move on the board
    @Override
    public void lastMove(PlayerMove m){
        Coordinate move = m.getCoordinate();
        int x = move.getRow();
        int y = move.getCol();
        int id = m.getPlayerId();
        board[x][y] = id;
    }

    /* Unnecessary with this code setup */
    @Override
    public void otherPlayerInvalidated(){}

    /* Gives the move for the player to use, not valid for player configs */
    @Override
    public PlayerMove move() {

        List<PlayerMove> allMoves = allLegalMoves();
        if(Rctr == 0){
            int x = rand.nextInt(allMoves.size()-1);
            Rctr++;
            return allMoves.get(x);
        }
        ArrayList<PlayerMoveInfo> allMovesInfo = new ArrayList<>();
        ArrayList<PlayerMoveInfo> defensiveMoves = new ArrayList<>();
        ArrayList<PlayerMoveInfo> offensiveMoves = new ArrayList<>();
        for (PlayerMove p : allMoves) {
            int r = p.getCoordinate().getRow();
            int c = p.getCoordinate().getCol();
            board[r][c] = playerId;
            int dist1 = fewestSegmentsToVictory(playerId);
            int dist2 = fewestSegmentsToVictory(3 - playerId);
            allMovesInfo.add(new PlayerMoveInfo(p, dist1, dist2));
            board[r][c] = 0;
        }
        PlayerMoveInfo bestMove = allMovesInfo.get(0);
        for (PlayerMoveInfo move : allMovesInfo) {
            if (move.getDist1() < bestMove.getDist1()) {
                bestMove = move;
                offensiveMoves.add(bestMove);
            }
            if (move.getDist2() >= bestMove.getDist2()) {
                bestMove = move;
                defensiveMoves.add(bestMove);
            }
        }
        while (offensiveMoves.size() > 1) {
            PlayerMoveInfo offensive1 = offensiveMoves.remove(0);
            PlayerMoveInfo offensive2 = offensiveMoves.remove(0);
            if (offensive1.getDist1() > offensive2.getDist1()) {
                offensiveMoves.add(offensive2);
            } else {
                offensiveMoves.add(offensive1);
            }
        }
        while (defensiveMoves.size() > 1) {
            PlayerMoveInfo defensive1 = defensiveMoves.remove(0);
            PlayerMoveInfo defensive2 = defensiveMoves.remove(0);
            if (defensive1.getDist2() < defensive2.getDist2()) {
                defensiveMoves.add(defensive2);
            } else {
                defensiveMoves.add(defensive1);
            }
        }
        if(defensiveMoves.size() != 0 && offensiveMoves.size() != 0) {
            if (defensiveMoves.get(0).getDist2() > offensiveMoves.get(0).getDist1() + 3) {
                bestMove = offensiveMoves.remove(0);
                defensiveMoves.clear();
            } else {
                bestMove = defensiveMoves.remove(0);
                offensiveMoves.clear();
            }
        } else {
            if (defensiveMoves.size() == 0 && offensiveMoves.size() != 0) {
                bestMove = offensiveMoves.remove(0);
            } else if (offensiveMoves.size() == 0 && defensiveMoves.size() != 0) {
                bestMove = defensiveMoves.remove(0);
            }
        }
        int fstv = fewestSegmentsToVictory(3-playerId);
        if (fstv <= 2){
            int bestDist2 = fstv;
            for (PlayerMoveInfo m : allMovesInfo){
                if (m.getDist2() >= bestDist2 && m.getDist2() > 1){
                    bestMove = m;
                    bestDist2 = m.getDist2();
                }
            }
        }
        return bestMove.getPlayerMove();
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
    
    /**
     * String representation of the game board 2D array
     * @return result- the string representation of the game board
     */
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
