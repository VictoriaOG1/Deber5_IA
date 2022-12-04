/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myagent;

import agent.Action;
import agent.Agent;
import agent.Percept;
import vacworld.*;

import java.util.Random;

import static java.lang.Math.random;

/* Change the code as appropriate.  This code
   is here to help you understand the mechanism
   of the simulator.

   Array para guardar ubicaciones
   Variable para la direccion traer de la clase VacPercept
*/

public class VacAgent extends Agent {

    private boolean seeDirt;
    private boolean seeObstacle;
    private int numberMovements = 0;

    public VacAgent() {
        seeDirt = false;
        seeObstacle = false;
    }

    @Override
    public void see(Percept p) {
        VacPercept vp = (VacPercept) p;
        seeDirt = vp.seeDirt();
        seeObstacle = vp.seeObstacle();
    }

    @Override
    public Action selectAction() {
        Random r = new Random();
        float chance;

        if (seeObstacle) {
            chance = r.nextFloat();
            if (chance < 0.5) {
                return new TurnLeft();
            } else {
                return new TurnRight();
            }
        } else if (seeDirt) {
            return new SuckDirt();
        } else {
            chance = r.nextFloat();
            if (chance < 0.2) {
                return new TurnLeft();
            } else if (chance < 0.4) {
                return new TurnRight();
            } else if (chance < 0.95) {
                return new GoForward();
            } else {
                return new ShutOff();
            }
        }

    }

    @Override
    public String getId() {
        return "Random Agent";
    }

    /*
     * private final String ID = "1";
     * // Think about locations you already visited. Remember those.
     * private boolean dirtStatus = true;
     * private boolean bumpFeltInPrevMove = true;
     * private boolean obstacleInFront = true;
     * private int numberMovements = 0;
     * 
     * public void see(Percept p) {
     * VacPercept vp = (VacPercept) p;
     * dirtStatus = vp.seeDirt();
     * bumpFeltInPrevMove = vp.feelBump();
     * obstacleInFront = vp.seeObstacle();
     * }
     * 
     * public Action selectAction() {
     * numberMovements++;
     * Action action = new SuckDirt();
     * SuckDirt suckDirt = new SuckDirt();
     * TurnLeft turnLeft = new TurnLeft();
     * TurnRight turnRight = new TurnRight();
     * GoForward goForward = new GoForward();
     * ShutOff shutOff = new ShutOff();
     * Random r = new Random();
     * float chance = r.nextFloat();
     * 
     * if (obstacleInFront) {
     * if (chance < 0.5)
     * action = turnLeft;
     * else
     * action = turnRight;
     * } else {
     * if (chance < 0.2)
     * action = turnLeft;
     * else if (chance < 0.4)
     * action = turnRight;
     * else
     * action = goForward;
     * }
     * 
     * if (dirtStatus)
     * action = suckDirt;
     * if (bumpFeltInPrevMove)
     * action = turnLeft;
     * if (numberMovements == 200)
     * action = shutOff;
     * 
     * return action;
     * }
     * 
     * public String getId() {
     * return ID;
     * }
     */

}
