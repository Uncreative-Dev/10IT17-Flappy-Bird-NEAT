package game;

import neat.NeuralNetwork;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Bird {

    private int height;
    private int birdState;
    private int fitness;
    private double velocity;
    private boolean dead;
    private BufferedImage[] images;
    private double probability = 0;
    private double lowProb = 0;
    private double highProb = 0;

    private NeuralNetwork network;

    public Bird() {
        init();
        network = new NeuralNetwork();
    }

    public Bird(NeuralNetwork nn) {
        init();
        network = nn;
    }

    private void init() {
        height = 250 - (int) (Math.random() * 175);
        velocity = 0;
        birdState = 1;
        loadImages();
    }

    private void loadImages() {
        images = new BufferedImage[3];
        try {
            images[0] = ImageIO.read(new File("assets/bird0.png"));
            images[1] = ImageIO.read(new File("assets/bird1.png"));
            images[2] = ImageIO.read(new File("assets/bird2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void draw(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, isDead() ? 0.5f : 1.0f));
        AffineTransform at = new AffineTransform();
        at.translate(Settings.BIRD_X_POS + (images[birdState].getWidth() / 2), height + (images[birdState].getHeight() / 2));
        at.rotate(rotation(-Math.PI / 2 - 0.3, Math.PI / 2, ((velocity + 10) / 21)));
        at.translate(-images[0].getWidth() / 2, -images[0].getHeight() / 2);
        g.drawImage(images[birdState], at, null);
    }

    private void setVelocity(double newVelocity) {
        velocity = newVelocity;
        if (velocity < -0.3) {
            birdState = 0;
        } else if (velocity > 0.3) {
            birdState = 2;
        } else {
            birdState = 1;
        }
    }

    private double rotation(double a, double b, double f) {
        return a + f * (b - a);
    }

    void move() {
        if (!dead) {
            setVelocity(velocity + 0.2);
            height += velocity + (Math.pow(velocity / 2, 3)) / 3;
        }
    }

    void checkCollision(int[] borders) {
        if (
                borders[0] < Settings.BIRD_X_POS + images[birdState].getWidth(null) && borders[1] > Settings.BIRD_X_POS && (
                        height < borders[2] || height + images[birdState].getHeight(null) > borders[3]
                )) {
            if (borders[1] > Settings.BIRD_X_POS + images[birdState].getWidth(null) && borders[0] < Settings.BIRD_X_POS && height + images[birdState].getHeight(null) > borders[3]) {
                height = borders[3] - images[birdState].getHeight(null); //Höhenkorrektur
            }
            kill();
        }
    }

    void jump() {
        if (!dead) {
            setVelocity(-3.2);
        }
    }

    void addFitness() {
        fitness++;
        if (fitness > Settings.MAX_FITNESS) {
            Settings.MAX_FITNESS = fitness;
        }
    }

    public int getFitness() {
        return fitness;
    }

    public NeuralNetwork getNetwork() {
        return network;
    }

    private void kill() {
        dead = true;
        Settings.ANZAHL_VOEGEL--;
    }

    public void reset() {
        height = 250 - (int) (Math.random() * 175);
        birdState = 1;
        fitness = 0;
        velocity = 0;
        dead = false;
        probability = 0;
        lowProb = 0;
        highProb = 0;
    }

    boolean isDead() {
        return dead;
    }

    int getHeight() {
        return height;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getLowProb() {
        return lowProb;
    }

    public void setLowProb(double lowProb) {
        this.lowProb = lowProb;
    }

    public double getHighProb() {
        return highProb;
    }

    public void setHighProb(double highProb) {
        this.highProb = highProb;
    }
}
