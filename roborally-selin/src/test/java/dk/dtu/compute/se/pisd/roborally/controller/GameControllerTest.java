package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment 6a (can be deleted later once Assignment 6a was shown to the teacher)
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player1 = board.getCurrentPlayer();
        Player player2 = board.getPlayer(1);
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should be on Space (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }


    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void turnRight(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnRight(current);

        Assertions.assertEquals(Heading.WEST, current.getHeading(),"Player 0 should be heading WEST!");
    }

    @Test
    void turnLeft(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnLeft(current);

        Assertions.assertEquals(Heading.EAST, current.getHeading(),"Player 0 should be heading EAST!");
    }

    @Test
    void fastForward(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.fastForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void moveBackward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveBackward(current);

        Assertions.assertEquals(current, board.getSpace(0, 7).getPlayer(), "Player " + current.getName() + " should beSpace (0,7)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
    }

    @Test
    void uturn(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uturn(current);

        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player 0 should be heading NORTH!");
    }
    // TODO and there should be more tests added for the different assignments eventually

    @Test
    void moveForwardWhenWallBlocks(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        board.getSpace(0,0).getWalls().add(Heading.SOUTH);

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0,0).getPlayer(), "Player should be blocked by wall!");
    }

    // For 6d
    @Test
    void robotPush(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        board.getPlayer(1).setSpace(board.getSpace(0,1)); // Placer spiller 1 foran spiller 0
        board.getPlayer(1).setHeading(Heading.SOUTH); // Sæt spiller 1's retning til SYD

        gameController.moveForward(current); // Spiller 0 flytter frem og skubber spiller 1

        // Tjek: Spiller 0 er flyttet til (0,1)
        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Current player " + current.getName() + " should beSpace (0,1)!");

        // Tjek: Spiller 1 skubbet til (0,2)
        Assertions.assertEquals(board.getPlayer(1), board.getSpace(0, 2).getPlayer(), "Second player " + board.getPlayer(1).getName() + " should beSpace (0,2)!");

        // Tjek: Spiller 0 stadig kigger mod SYD
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");

    }

    @Test
    void forCheckpoints(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Checkpoint checkpoint = new Checkpoint(); // Opretter et checkpoint
        checkpoint.setNumber(1); // Sæt checkpoint nummer til 1
        board.getSpace(0,1).getActions().add(checkpoint); // Tilføjer checkpoint til felt (0,1)

        gameController.moveForward(current); // Flytter spiller til (0,1)
        checkpoint.doAction(gameController, board.getSpace(0,1)); // Udfør checkpoint action

        // Tjek: Spiller 0 er på (0,1)
        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Current player " + current.getName() + " should beSpace (0,0)!");

        // Tjek: Spiller har samlet checkpoint 1
        Assertions.assertEquals(1, current.getCheckpoint(), "Player should have collected checkpoint 1!");

        // Tjek: Spiller kigger mod SYD
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");

    }
}