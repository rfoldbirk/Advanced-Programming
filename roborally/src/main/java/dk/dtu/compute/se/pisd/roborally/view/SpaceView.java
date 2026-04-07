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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75;
    final public static int SPACE_WIDTH = 40;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * @param subject subject.
     *
     * Draws conveyerbelts, checkpoints, players and walls on the map
     *
     *
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();

            // TODO-DONE A6b: drawing the walls and the field action(s) on
            //     this space could be implemented here.

            // draw walls
            for (Heading heading : space.getWalls()) {
                Line wall = new Line();

                int thickness = 3;

                switch (heading) {
                    case NORTH:
                        wall.setStartX(thickness);
                        wall.setStartY(0);
                        wall.setEndX(SPACE_WIDTH);
                        wall.setEndY(0);
                        wall.setTranslateY(-SPACE_HEIGHT / 2.0 + thickness/2.0);
                        break;

                    case SOUTH:
                        wall.setStartX(thickness);
                        wall.setStartY(0);
                        wall.setEndX(SPACE_WIDTH);
                        wall.setEndY(0);
                        wall.setTranslateY(SPACE_HEIGHT / 2.0 - thickness/2.0);
                        break;

                    case WEST:
                        wall.setStartX(0);
                        wall.setStartY(thickness);
                        wall.setEndX(0);
                        wall.setEndY(SPACE_HEIGHT);
                        wall.setTranslateX(-SPACE_WIDTH / 2.0 + thickness/2.0);
                        break;

                    case EAST:
                        wall.setStartX(0);
                        wall.setStartY(thickness);
                        wall.setEndX(0);
                        wall.setEndY(SPACE_HEIGHT);
                        wall.setTranslateX(SPACE_WIDTH / 2.0 - thickness/2.0);
                        break;
                }

                wall.setStrokeWidth(3);
                wall.setStroke(Color.RED);

                this.getChildren().add(wall);
            }

            List<FieldAction> actions = space.getActions();

            for (var action : actions) {
                if (action instanceof ConveyorBelt) {
                    Polygon arrow = new Polygon(0.0, 0.0, 15.0, 25.0, 30.0, 0.0 );

                    arrow.setFill(Color.GRAY);

                    Heading heading = ((ConveyorBelt) action).getHeading();

                    arrow.setRotate((90*heading.ordinal())%360);
                    this.getChildren().add(arrow);
                }

                if (action instanceof Checkpoint check) {
                    Circle circle = new Circle(SPACE_HEIGHT/4.0, Color.YELLOW);

                    Text text = new javafx.scene.text.Text("" + check.getNumber());
                    text.setFill(Color.GREEN);
                    text.setStyle("-fx-font-weight: bold");

                    this.getChildren().add(circle);
                    this.getChildren().add(text);
                }
            }


            updatePlayer();
        }
    }

}
