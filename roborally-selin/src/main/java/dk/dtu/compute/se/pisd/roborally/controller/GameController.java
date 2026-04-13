/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.control.Alert;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO A6a: this method should be implemented for Assignment 6a:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if and when the player is moved (the counter and the status line
        //     message needs to be implemented at another place)

        Player currentPlayer = board.getCurrentPlayer(); // Hent nuværende spiller
        if (space.getPlayer() == null){ // Hvis pladsen er tom
            currentPlayer.setSpace(space); // Placer nuværende spiller på plads

            int nextPlayerNumber = board.getPlayerNumber(currentPlayer)+1; // Få fat på næste spiller

            if (nextPlayerNumber >= board.getPlayersNumber()){ // Hvis næste spiller er større end antallet af spiller
                board.setCurrentPlayer(board.getPlayer(0)); // Start fra 0 spiller
            } else {
                board.setCurrentPlayer(board.getPlayer(nextPlayerNumber)); // ellers spiller næste spiller
            }

            // opdater vores counter
            // Formål: tælle hvor mange gane en spiller er blevet flyttet i spillet
            // Hver gang en spiller klikker på et tomt felt og bliver flyttet, stiger counteren med 1
            // Counteren skal vises på statuslinke i GUI
            board.setCounter(board.getCounter() + 1); //
        }
    }


    // XXX A6c
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX A6c
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX A6c
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX A6c
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX A6c
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX A6c
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX A6c
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX A6c
    private void continuePrograms() {
        do {
            executeNextStep(null);
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX A6c
    // TODO A6d: add the execution of the field actions at the right
    //      place in this method
    // TODO A6e: implement the execution af an interactive card to
    //     this method (e.g. by switching to the PLAYER_INTERACTION phase
    //     at the right point)
    private void executeNextStep(Command chosenCommand) {
        Player currentPlayer = board.getCurrentPlayer(); // Finder nuværende spiller
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) { // Hvis fasen er activation og der er en spiller
            int step = board.getStep(); // henter step
            if (step >= 0 && step < Player.NO_REGISTERS) { // hvis step gyldigt
                CommandCard card = currentPlayer.getProgramField(step).getCard(); // så er command card = nuværende spillers kort
                if (card != null) { // hvis kortet ikke er null
                    if (card.command.isInteractive() && chosenCommand == null){ // Hvis kortet er inaktivt og spilleren ikke har valgt endnu
                        board.setPhase(Phase.PLAYER_INTERACTION); // Skift faste til P_Interaction og vent på spillerens valg
                        return; // Stop metoden og vent på spillerens valg
                    }
                    if (chosenCommand != null){ // Hvis spilleren allerede har valgt en kommando
                        executeCommand(currentPlayer, chosenCommand); // Udfør den valgte kommando
                    } else { // Hvis kortet ikke er interaktivt
                        Command command = card.command; // Hent kommandoen fra kortet
                        executeCommand(currentPlayer, command); // Udfør command for nuværende spiller
                    }

                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1; // Angiver næste spiller
                if (nextPlayerNumber < board.getPlayersNumber()) { // Hvis næste spillers nr er mindre end spillets antal spillere
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber)); // så sæt nuværende spiller
                } else {
                    step++; // Et step frem
                    if (step < Player.NO_REGISTERS) { // Hvis der er flere registers tilbage
                        for (int i = 0; i < board.getPlayersNumber(); i++){
                            Player gettingPlayer = board.getPlayer(i); // hent spiller
                            Space gettingPlayerSpace = gettingPlayer.getSpace(); // hent spillers felt
                            // for (FieldAction action : // check for conveyour)
                            for (FieldAction action : gettingPlayerSpace.getActions()){ // For hver action hent spillerens felts action
                                action.doAction(GameController.this, gettingPlayerSpace); // udfør action
                                if (board.getPhase() == Phase.FINISHED){ // hvis returnerer en spiller
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("You WON!!");
                                    alert.setContentText("Congratulations!! \n" + gettingPlayer.getName() + " has won the game! \n The end.");
                                    alert.showAndWait();
                                }
                            }
                        }
                        makeProgramFieldsVisible(step); // Gør programmet synligt
                        board.setStep(step); // Sæt boardets step til step
                        board.setCurrentPlayer(board.getPlayer(0)); // sæt nuværende spiller på boardet til spiller nr. 0


                    } else {

                        startProgrammingPhase(); // Begynd program
                    }

                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX A6c
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                // TODO A6c: add the cases for the new commands BACK and UTURN to
                //     this case statement.
                case UTURN:
                    this.uturn(player);
                    break;
                case BACKWARDS:
                    this.moveBackward(player);
                    break;
                default:
                    // DO NOTHING (for now)//
            }
        }
    }

    // TODO A6c: implement this method
    public void moveForward(@NotNull Player player) {
        Space currentSpace = player.getSpace(); // Finder nuværende felt
        Heading heading = player.getHeading(); // Finder nuværende retning

        // Spørg nabo om det næste felt
        Space nextSpace = board.getNeighbour(currentSpace, heading);

        // Tjek om næste felt er tilgængeligt, eller besat af anden spiller
        if (nextSpace != null){
            // Hvis der er en spiller
            if (nextSpace.getPlayer() != null){
                moveForward(nextSpace.getPlayer()); // Flyt spilleren foran frem
            }
            player.setSpace(nextSpace); // Flyt spiller
        }
    }

    // TODO A6c: implement this method
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    // TODO A6c: implement this method
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    // TODO A6c: implement this method
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    // TODO A6c: Add two methods for the new commands BACK and UTURN here.

    public  void uturn(@NotNull Player player){
        player.setHeading(player.getHeading().opposite());
    }

    public void moveBackward(@NotNull Player player){
        // Flytter retning
        player.setHeading(player.getHeading().opposite());

        // Flytter spiller frem
        moveForward(player);

        // Flytter retningen tilbage igen
        player.setHeading(player.getHeading().opposite());
    }

    public void executeCommandOption (@NotNull Command command){
        board.setPhase(Phase.ACTIVATION);
        executeNextStep(command);
        // Player currentPlayer = board.getCurrentPlayer(); - denne behøves heller ikke længere
        // executeCommand(currentPlayer, command); - fjernet da executeNextStep sørger for det nu

    }

    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
