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

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a checkpoint on a space with a number
 * which players must obtain.
 */

// XXX A6d remember to also implement the doAction method for the
//         class CheckPoint you added in Assignment 6b
public class Checkpoint extends FieldAction {

    private int number;

    private boolean last;

    public int getNumber(){
        return number;
    }

    public void setNumber(int num){
        this.number = num;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    /**
     * Implementation of the action of a conveyor belt. Needs to be implemented for A3.
     */


    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // TODO A6d: needs to be implemented
        // ...
        Player player = space.getPlayer();

        if (player != null){
            if (player.getCheckpoint() == this.number - 1){ // tjekker om spilleren har nået alle lavere checkpoints
                player.setCheckpoints(this.number); // Sætter spillerens checkpoint til nummeret

                // Tjek: Har spilleren vundet
                if (last == true){
                    gameController.board.setPhase(Phase.FINISHED);
                }


                return true;
            }
        }

        return false;
    }

}
