/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pong;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author pchambino
 */
public abstract class Entity {

    // game where the entity is
    protected Game game;

    // position
    protected int x, y;
    
    // velocity
    protected int dx, dy;

    protected Entity(Game game) {
        this.game = game;

        // add the entity to the game
        game.addEntity(this);
    }

    protected Entity(Game game, int x, int y, int dx, int dy) {
        this(game);
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public int getPositionX() {
        return x;
    }

    public int getPositionY() {
        return y;
    }

    public int getVelocityX() {
        return dx;
    }

    public int getVelocityY() {
        return dy;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPositionX(int x) {
        setPosition(x, y);
    }

    public void setPositionY(int y) {
        setPosition(x, y);
    }

    public void setVelocity(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void setVelocityX(int dx) {
        setVelocity(dx, dy);
    }

    public void setVelocityY(int dy) {
        setVelocity(dx, dy);
    }

    public void move(long delta) {
        setPosition((int) (x+dx*delta/1000), (int) (y+dy*delta/1000));
    }

    public abstract void draw(Graphics2D g);
}
