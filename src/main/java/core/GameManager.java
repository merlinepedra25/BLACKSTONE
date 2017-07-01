package core;

import gui.BoardPane;
import players.Player;
import players.human.HumanPlayer;
import players.ai.NegamaxAI;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used by GUI components to control the the current game and receive
 * events
 */
public class GameManager {

    private List<GameEventListener> listeners;

    private final BoardPane board;
    private Game currentGame;
    private Thread currentThread;

    private String player1;
    private String player2;

    private int intersections;
    private int moveTime;
    private int gameTime;
    private boolean gameTimingEnabled;
    private boolean moveTimingEnabled;

    protected GameManager(BoardPane board) {
        this.board = board;
        this.listeners = new ArrayList<>();
        this.updateIntersections(15);
        this.updateMoveTime(5000);
        this.updateGameTime(1200000);
        this.setGameTimingEnabled(true);
        this.setMoveTimingEnabled(false);
        this.updatePlayer1("Human");
        this.updatePlayer2("Computer");
    }

    /**
     * Return the current board size in intersections
     * @return Board size n (n*n intersections)
     */
    public int getIntersections() {
        return this.intersections;
    }

    /**
     * Return whether or not move timing is currently enabled
     * @return
     */
    public boolean moveTimingEnabled() {
        return this.moveTimingEnabled;
    }

    /**
     * Return whether or not game timing is currently enabled
     * @return
     */
    public boolean gameTimingEnabled() {
        return this.gameTimingEnabled;
    }

    /**
     * Get the current move timeout value
     * @return Maximum time per move in milliseconds
     */
    public int getMoveTime() {
        return this.moveTime;
    }

    /**
     * Get the current game timeout value
     * @return Maximum game time in milliseconds
     */
    public int getGameTime() {
        return this.gameTime;
    }

    /**
     * Register a listener to receive updates for game events
     * @param listener GameEventListener to register
     */
    public void addListener(GameEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Start a new game with the current settings.
     */
    public void startGame() {
        this.currentGame = new Game(this, board, createPlayer(player1, 1),
                createPlayer(player2, 2),
                intersections, moveTime, gameTime, moveTimingEnabled,
                gameTimingEnabled);
        this.currentThread = new Thread(currentGame);
        currentThread.start();
        for(GameEventListener listener : listeners) {
            listener.gameStarted();
        }
    }

    private Player createPlayer(String player, int index) {
        switch (player) {
            case "Human":
                return new HumanPlayer(new GameInfo(intersections,
                        gameTime, moveTime, index, index == 1? 2 : 1));
            case "Computer":
                return new NegamaxAI(new GameInfo(intersections,
                        gameTime, moveTime, index, index == 1? 2 : 1));
            default:
                return null;
        }
    }

    /**
     * Stop the current game, if one exists.
     */
    public void stopGame() {
        for(GameEventListener listener : listeners) {
            listener.gameOver();
        }
        if(currentThread != null) currentThread.interrupt();
    }

    /**
     * Undo the last move
     */
    public void undo() {
        for(GameEventListener listener : listeners) {
            listener.undo();
        }
    }

    /**
     * Update the number of intersections on the board
     * @param intersections New # of intersections
     */
    public void updateIntersections(int intersections) {
        this.board.clear();
        this.intersections = intersections;
        this.board.setIntersections(intersections);
    }

    /**
     * Update the timeout for a single move in milliseconds
     * @param milliseconds New move timeout value
     */
    public void updateMoveTime(int milliseconds) {
        this.moveTime = milliseconds;
        moveTimeChanged(1, moveTime);
        moveTimeChanged(2, moveTime);
    }

    /**
     * Update the timout for the whole game in milliseconds
     * @param milliseconds New game timeout value
     */
    public void updateGameTime(int milliseconds) {
        this.gameTime = milliseconds;
        gameTimeChanged(1, gameTime);
        gameTimeChanged(2, gameTime);
    }

    public void setMoveTimingEnabled(boolean enabled) {
        this.moveTimingEnabled = enabled;
        for(GameEventListener listener : listeners) {
            listener.moveTimingEnabled(enabled);
        }
    }

    public void setGameTimingEnabled(boolean enabled) {
        this.gameTimingEnabled = enabled;
        for(GameEventListener listener : listeners) {
            listener.gameTimingEnabled(enabled);
        }
    }

    /**
     * Update the first player
     * @param playerString Player value (Human/Computer)
     */
    public void updatePlayer1(String playerString) {
        this.player1 = playerString;
        for (GameEventListener listener : listeners) {
            listener.playersChanged();
        }
    }

    /**
     * Update the second player
     * @param playerString Player value (Human/Computer)
     */
    public void updatePlayer2(String playerString) {
        this.player2 = playerString;
        for(GameEventListener listener : listeners) {
            listener.playersChanged();
        }
    }

    protected void gameTimeChanged(int player, long time) {
        for(GameEventListener listener : listeners) {
            listener.gameTimeChanged(player, time);
        }
    }

    protected void moveTimeChanged(int player, long time) {
        for(GameEventListener listener : listeners) {
            listener.moveTimeChanged(player, time);
        }
    }

    protected void turn(int player) {
        for(GameEventListener listener : listeners) {
            listener.turn(player);
        }
    }
}
