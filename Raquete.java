/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pong;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author pchambino
 */
public class Raquete extends Entity {
    private int width;
    private int height;

    private int pontuacao;

    public Raquete(Game game, int width, int height) {
        super(game);
        this.width = width;
        this.height = height;
        this.pontuacao = 0;
    }

    public void setPontuacao(int p) {
        this.pontuacao = p;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void move(long delta) {
        if (dy < 0 && y < 0 || dy > 0 && y+height > game.getHeight())
            return;

        super.move(delta);
    }

    public void goUP() {
        setVelocityY(-game.velocity);
    }

    public void goDOWN() {
        setVelocityY(game.velocity);
    }

    public void stopMoving() {
        setVelocityY(0);
    }

    // draws a rectangle
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }

    public void doAI() {
        if (game.bola.getPositionY() < y+height/3)
            goUP();
        else if (game.bola.getPositionY() > y+height*2/3)
            goDOWN();
        else
            stopMoving();
    }
}
