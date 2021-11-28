/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceintruders;

public class Keyboard {

    Boolean left, right, up, down, space, escape, t, x, enter, spaceWasPressedLastFrame;

    public Keyboard() {
        left = false;
        right = false;
        up = false;
        down = false;
        space = false;
        escape = false;
        t = false;
        x = false;
        enter = false;
        spaceWasPressedLastFrame = false;
    }

}
