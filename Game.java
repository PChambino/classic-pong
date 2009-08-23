package pong;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class Game extends Canvas {

    private JFrame frame;
    // true while game is running
    private boolean running;
    // to pause the game
    private boolean pause;
    // for the game begin
    private boolean begin;
    // is fullscreen
    private boolean fullscreen = false;
    // strategy buffer
    private BufferStrategy strategy;
    // graphics to be drawn
    private Graphics2D g;
    // size of window
    private final Dimension size = new Dimension(800, 600);
    // a list of entities
    private ArrayList<Entity> entities;
    // the raquetes
    public ArrayList<Raquete> raquetes;
    // bola
    public Bola bola;
    // velocidade das raquetes
    public final int velocity = 200;
    // game types, single player has AI
    private enum gameType {SINGLE_PLAYER, TWO_PLAYERS};
    private gameType type;
    // sounds
    private Clip pingClip;
    public Clip pongClip;

    public Game() {
        // frame
        frame = new JFrame("Ping Pong");

        // add keyborad listener
        addKeyListener(new Keyboard());

        // add canvas
        frame.add(this);

        // set close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // disable repaint
        frame.setIgnoreRepaint(true);
        setIgnoreRepaint(true);

        // set frame size
        frame.setSize(size);

        // set not resizable
        frame.setResizable(false);

        // center frame
        frame.setLocationRelativeTo(null);

        // set visible and request focus
        frame.setVisible(true);

        // create buffer
        createBufferStrategy(3);
        strategy = getBufferStrategy();

        // request focus
        requestFocusInWindow();

        // game
        init();
        goToMenu();
        gameLoop();
    }

    // adds an entity to the game
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    private void init() {
        //load entities
        entities = new ArrayList<Entity>(3);
        raquetes = new ArrayList<Raquete>(2);

        // size of raquetes
        int width = 10;
        int height = 150;

        raquetes.add(new Raquete(this, width, height));
        raquetes.add(new Raquete(this, width, height));

        // size of bola
        int diametro = 20;

        bola = new Bola(this, diametro);

        // load sounds
        try {
            // ping
            InputStream stream = this.getClass().getResourceAsStream("Sounds/ping.wav");

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(stream);
            DataLine.Info info = new DataLine.Info(Clip.class,audioStream.getFormat());

            pingClip = (Clip) AudioSystem.getLine(info);
            pingClip.open(audioStream);

            // pong
            stream = this.getClass().getResourceAsStream("Sounds/pong.wav");

            audioStream = AudioSystem.getAudioInputStream(stream);
            info = new DataLine.Info(Clip.class,audioStream.getFormat());

            pongClip = (Clip) AudioSystem.getLine(info);
            pongClip.open(audioStream);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        // init variables
        running = true;
        pause = false;
        begin = true;
    }

    private void quit() {
        System.exit(0);
    }

    private void startGame() {

        // size of raquetes
        int width = raquetes.get(0).getWidth();
        int height = raquetes.get(0).getHeight();

        // inicial x os raquetes
        int x = 20;

        // set inicial positions
        raquetes.get(0).setPosition( x, size.height/2-height/2);
        raquetes.get(1).setPosition( size.width-x-width, size.height/2-height/2);

        // set inicial velocity to zero
        raquetes.get(0).stopMoving();
        raquetes.get(1).stopMoving();

        // velocity of ball
        Random rand = new Random();
        int dx, dy;
        if (rand.nextInt(2) == 0) {
            dx = velocity;
            if (rand.nextInt(2) == 0)
                dy = dx / 2;
            else
                dy = -dx / 2;
        } else {
            dx = - velocity;
            if (rand.nextInt(2) == 0)
                dy = dx / 2;
            else
                dy = -dx / 2;
        }

        // size of bola
        int diametro = bola.getDiametro();

        bola.setPosition( size.width/2-diametro/2, size.height/2-diametro/2);
        bola.setVelocity(dx, dy);
    }

    public void goToMenu() {
        pause = false;
        begin = true;
        startGame();

        // set pontuacao inicial para zero
        raquetes.get(0).setPontuacao(0);
        raquetes.get(1).setPontuacao(0);
    }

    public void win(Raquete raquete) {
        raquete.setPontuacao(raquete.getPontuacao()+1);

        startGame();
    }

    private void testCollisions() {
        boolean hits = false;

        // check: bate na raquete da esquerda
        Raquete raquete = raquetes.get(0);
        if (bola.getPositionX() < raquete.getPositionX()+raquete.getWidth()
                && bola.getPositionY()+bola.getDiametro() > raquete.getPositionY()
                && bola.getPositionY() < raquete.getPositionY()+raquete.getHeight())
            if (bola.getVelocityX() < 0) {
                hits = true;
            }

        // se nao bateu
        if (!hits) {
            // check: bate na raquete da direita
            raquete = raquetes.get(1);
            if (bola.getPositionX()+bola.getDiametro() > raquete.getPositionX()
                    && bola.getPositionY()+bola.getDiametro() > raquete.getPositionY()
                    && bola.getPositionY() < raquete.getPositionY()+raquete.getHeight())
                if (bola.getVelocityX() > 0) {
                    hits = true;
                }
        }

        // hits
        if (hits) {
            // play ping sound
            playClip(pingClip);

            // aumenta velocidade em x e altera velocidade em y
            bola.setVelocityX((int)(-bola.getVelocityX()*1.05));
            bola.setVelocityY((int)((bola.getPositionY() + bola.getDiametro()/2 - raquete.getPositionY() - raquete.getHeight()/2)
                    *velocity*1.5/(raquete.getHeight()/2)));
        }
    }

    private void drawBackground(Graphics2D g) {
        // titulo
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Courier", Font.BOLD, 100));
        g.drawString("PING PONG", 125, 70);

        // linha do meio
        g.fillRect(size.width/2-10, 0, 20, size.height);
        g.fillOval(size.width/2-20, size.height/2-20, 40, 40);

        // pontuacoes
        g.setFont(new Font("Courier", Font.PLAIN, 400));

        // pontuacao da raquete da esquerda
        int pontuacao = raquetes.get(0).getPontuacao();
        for (int i = 0; pontuacao >= 10; i++) {
            g.fillRect(110+i*5, 410, 2, 10);

            pontuacao -= 10;
        }
        g.drawString(""+pontuacao, 110, 400);

        // pontuacao da raquete da direita
        pontuacao = raquetes.get(1).getPontuacao();
        for (int i = 0; pontuacao >= 10; i++) {
            g.fillRect(450+i*5, 410, 2, 10);

            pontuacao -= 10;
        }
        g.drawString(""+pontuacao, 450, 400);
    }

    private void gameLoop() {
        long lastLoop = System.currentTimeMillis();
        long delta;

        while(running) {
            // time since last loop
            delta = System.currentTimeMillis() - lastLoop;
            lastLoop = System.currentTimeMillis();

            // get Graphics
            g = (Graphics2D) strategy.getDrawGraphics();

            // clean screen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            // for the entities
            Entity entity;

            if (!pause && !begin) {
                // collisions
                testCollisions();

                // do AI if needed
                if (type == gameType.SINGLE_PLAYER)
                    raquetes.get(0).doAI();

                // move all entities
                for (int i = 0; i < entities.size(); i++) {
                    entity = entities.get(i);

                    entity.move(delta);
                }
            }

            // draw background
            drawBackground(g);

            if (pause) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Courier", Font.PLAIN, 200));
                g.drawString("Paused", 40, 350);
            }

            if (begin) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Courier", Font.PLAIN, 120));
                g.drawString("Start Game", 40, 300);
                g.setFont(new Font("Courier", Font.PLAIN, 25));
                g.drawString("1 - Single Player", 300, 350);
                g.drawString("2 - Two Players", 300, 380);

                // instructions
                g.setFont(new Font("Courier", Font.PLAIN, 20));
                g.drawString("Intructions:", 10, 460);
                g.drawString("Left Racket: A, Z", 10, 480);
                g.drawString("Right Racket: UP, DOWN", 10, 500);
                g.drawString("Pause: P", 10, 520);
                g.drawString("Menu: M", 10, 540);
                g.drawString("Quit: Q", 10, 560);
            }

            // draw all entities
            for (int i = 0; i < entities.size(); i++) {
                entity = entities.get(i);

                entity.draw(g);
            }

            // clear graphics and flip buffer page
            g.dispose();
            strategy.show();

            // does about 100fps
            try { Thread.sleep(10); } catch (InterruptedException ex) {}
        }
    }

    public static void main(String[] args) {
        new Game();
    }

    class Keyboard implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            // raquete da direita
            if (key == KeyEvent.VK_UP)
                raquetes.get(1).goUP();
            else if (key == KeyEvent.VK_DOWN)
                raquetes.get(1).goDOWN();
            
            // raquete da esquerda
            if (key == KeyEvent.VK_A)
                raquetes.get(0).goUP();
            else if (key == KeyEvent.VK_Z)
                raquetes.get(0).goDOWN();
        }

        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            // set game type and start game
            if (key == KeyEvent.VK_1) {
                type = gameType.SINGLE_PLAYER;
                begin = false;
            } else if (key == KeyEvent.VK_2) {
                type = gameType.TWO_PLAYERS;
                begin = false;
            }

            // go to Menu
            if (key == KeyEvent.VK_M) {
                goToMenu();
            }

            // pause game
            if (key == KeyEvent.VK_P && !begin)
                pause = !pause;

            // quit game
            if (key == KeyEvent.VK_Q)
                quit();

            // raquete da direita
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN)
                raquetes.get(1).stopMoving();

            // raquete da esquerda
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_Z)
                raquetes.get(0).stopMoving();
        }
    }

    public void playClip(Clip clip) {
        new PlayClip(clip);
    }

    class PlayClip implements Runnable {
        Clip clip;

        public PlayClip(Clip clip) {
            this.clip = clip;

            Thread thread = new Thread(this);
            thread.start();
        }

        public void run() {
            clip.start();

            try {
                Thread.sleep(clip.getMicrosecondLength()/100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }

            clip.stop();
        }

    }
}