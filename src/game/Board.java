package game;

import neat.Generation;
import neat.Generations;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener {

    private static final long serialVersionUID = -2469276924387568876L;
    private static Generation gen = new Generation();
    private int iterationCounter;
    private ArrayList<Pipe> pipes;
    private Ground ground;
    private Background background;

    Board() {
        init();
        Timer timer = new Timer(10, this);
        timer.start();
    }

    private void init() {
        Settings.GENERATION++;
        if (Settings.MAX_FITNESS % 5000 == 0 && Settings.CROSSOVER_RATE != 0.1) {
            Settings.CROSSOVER_RATE -= 0.1;
        }
        pipes = new ArrayList<>();
        pipes.add(new Pipe());
        ground = new Ground();
        background = new Background();
        iterationCounter = 0;
        setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        background.draw(g);
        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }
        ground.draw(g);

        for (Bird b : gen.getBirds()) {
            if (!b.isDead()) {
                b.draw((Graphics2D) g);
            }
        }
        g.drawString("Fitness: " + gen.getMaxFitness() + "      Generation: " + Settings.GENERATION + "      Anzahl Vögel: " + Settings.ANZAHL_VOEGEL, 10, Settings.WINDOW_HEIGHT - 35);
        g.drawString("Max-Fitness: " + Settings.MAX_FITNESS, 10, Settings.WINDOW_HEIGHT - 48);
        g.drawString("Score: " + Settings.SCORE / 250, Settings.WINDOW_WIDTH - 75, Settings.WINDOW_HEIGHT - 35);
    }

    static Generation getGen() {
        return gen;
    }

    @Override

    public void actionPerformed(ActionEvent e) {
        if (iterationCounter == 80) {
            pipes.add(new Pipe());
        } else if (iterationCounter > 80) {
            iterationCounter = -1;
        }
        iterationCounter++;
        for (Pipe pipe : pipes) {
            pipe.move();
        }
        if (pipes.get(0).getXPos() < -pipes.get(0).getPipeWidth()) {
            pipes.remove(0);
        }

        ground.move();
        background.move();
        for (Bird bird : gen.getBirds()) {
            bird.move();
            double result;
            if (pipes.get(0).getXPos() + pipes.get(0).getPipeWidth() < Settings.BIRD_X_POS) {
                result = bird.getNetwork().activate(bird.getHeight() - pipes.get(1).getHeight(), pipes.get(1).getXPos() - Settings.BIRD_X_POS);
                Settings.SCORE++;

            } else {
                result = bird.getNetwork().activate(bird.getHeight() - pipes.get(0).getHeight(), pipes.get(0).getXPos() - Settings.BIRD_X_POS);
            }
            if (result > 0.5) {
                bird.jump();
            }
            if (!bird.isDead()) {
                bird.checkCollision(pipes.get(0).getCollisionBorders());
                bird.checkCollision(ground.getCollisionBorders());
                bird.addFitness();
            }

        }
        if (Settings.ANZAHL_VOEGEL <= 0) {
            Generations.setPrevGeneration(gen.getBirds());
            gen.setBirds(Generation.generateNewGeneration());
            init();
        }
        repaint();
    }
}