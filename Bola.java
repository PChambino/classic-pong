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
public class Bola extends Entity {
    private int diametro;

    public Bola(Game game, int d) {
        super(game);
        this.diametro = d;
    }

    public int getDiametro() {
        return diametro;
    }

    @Override
    public void setVelocityX(int dx) {
        int maxVel = 3*game.velocity;
        if (dx > maxVel)
            dx = maxVel;

        super.setVelocityX(dx);
    }

    @Override
    public void move(long delta) {
        // ball hits top or bottom limits
        if (dy < 0 && y < 0 || dy > 0 && y+diametro > game.getHeight())
        {
            // plays pong sound
            game.playClip(game.pongClip);

            // change direction
            dy *= -1;
        }

        // raquete da esquerda ganha!
        if (dx > 0 && x+diametro > game.getWidth())
            game.win(game.raquetes.get(0));

        // raquete da direita ganha!
        if (dx < 0 && x < 0)
            game.win(game.raquetes.get(1));

        super.move(delta);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, diametro, diametro);
    }
}
