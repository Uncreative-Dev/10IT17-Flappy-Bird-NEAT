package game;

import neat.Generations;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;

public class Window extends JFrame {

    private Window() {
        init();
    }

    private void init() {
        add(new Board());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Flappy Bird");
        try {
            setIconImage(ImageIO.read(new File("assets/bird0.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSize(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Window();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Generations.setPrevGeneration(Board.getGen().getBirds());
                Generations.disconnect();
        }));
    }
}
