package neat;

import game.Bird;
import game.Settings;

import java.util.ArrayList;
import java.util.Comparator;

public class Generation {
    private static ArrayList<Bird> birds;

    public Generation() {
        if (birds != null) {
            Generations.setPrevGeneration(this);
        }
        birds = generateNewGeneration();
        Generations.setCurrGeneration(this);
    }

    private static void sortBirdsFitnessAsc(){
        birds.sort(new Comparator<Bird>() {
            @Override
            public int compare(Bird o1, Bird o2) {
                if (o1.getFitness() < o2.getFitness()){
                    return -1;
                }
                if(o1.getFitness() > o2.getFitness()){
                    return 1;
                }
                return 0;
            }
        });
    }


    public static ArrayList<Bird> generateNewGeneration() {
        Settings.ANZAHL_VOEGEL = 50;

        ArrayList<Bird> birdNew = new ArrayList<>();
        if (Generations.getPrevGeneration() == null) {
            for (int i = 0; i < Settings.POPULATION; i++) {
                birdNew.add(new Bird());
            }
            return birdNew;
        }
        sortBirdsFitnessAsc();
        for (int i = 0; i < Math.round(Settings.elitism * Settings.POPULATION); i++) {
            if (birdNew.size() < Settings.POPULATION) {
                birdNew.add(birds.get(i));
            }
        }

        for (int i = 0; i < Math.round(Settings.zufallVerhalten * Settings.POPULATION); i++) {
            for (int k = 0; k < birds.get(i).getNetwork().getInputLayer().neurons.size(); k++) {
                birds.get(i).getNetwork().getInputLayer().neurons.get(k).populate(10);
                birdNew.add(birds.get(i));
            }
            for (int k = 0; k < birds.get(i).getNetwork().getHiddenLayer().neurons.size(); k++) {
                birds.get(i).getNetwork().getHiddenLayer().neurons.get(k).populate(1);
                birdNew.add(birds.get(i));
            }
        }

        int max = 0;
        while (true) {
            for (int i = 0; i < max; i++) {
                Bird childBird = breed(birds.get(i), birds.get(max));
                birdNew.add(childBird);
                if (birdNew.size() >= Settings.POPULATION) {
                    return birdNew;
                }
            }
            max++;
            if (max >= birds.size() - 1) {
                max = 0;
            }
        }
    }

    public static Bird breed(Bird bird1, Bird bird2) {

        for (int i = 0; i < bird1.getNetwork().getInputLayer().neurons.get(0).getWeights().size(); i++) {
            if (Math.random() <= Settings.crossoverRate) {
                bird1.getNetwork().getInputLayer().neurons.get(0).getWeights().set(i, bird2.getNetwork().getInputLayer().neurons.get(0).getWeights().get(i));
                bird1.getNetwork().getInputLayer().neurons.get(1).getWeights().set(i, bird2.getNetwork().getInputLayer().neurons.get(1).getWeights().get(i));
            }
        }
        for (int i = 0; i < bird1.getNetwork().getHiddenLayer().neurons.size(); i++) {
            if (Math.random() <= Settings.crossoverRate) {
                bird1.getNetwork().getHiddenLayer().neurons.get(i).getWeights().set(0, bird2.getNetwork().getHiddenLayer().neurons.get(i).getWeights().get(0));
            }
        }

        for (int i = 0; i < bird1.getNetwork().getInputLayer().neurons.get(0).getWeights().size(); i++) {
            if (Math.random() <= Settings.mutationsRate) {
                bird1.getNetwork().getInputLayer().neurons.get(0).getWeights().set(i, bird1.getNetwork().getInputLayer().neurons.get(0).getWeights().get(i) + Math.random() * Settings.mutationRange * 2 - Settings.mutationRange);
                bird1.getNetwork().getInputLayer().neurons.get(1).getWeights().set(i, bird1.getNetwork().getInputLayer().neurons.get(1).getWeights().get(i) + Math.random() * Settings.mutationRange * 2 - Settings.mutationRange);
            }
        }
        for (int i = 0; i < bird1.getNetwork().getHiddenLayer().neurons.size(); i++) {
            if (Math.random() <= Settings.mutationsRate) {
                bird1.getNetwork().getHiddenLayer().neurons.get(i).getWeights().set(0, bird1.getNetwork().getHiddenLayer().neurons.get(i).getWeights().get(0) + Math.random() * Settings.mutationRange * 2 - Settings.mutationRange);
            }


        }
        return bird1;

    }

    public ArrayList<Bird> getBirds() {
        return birds;
    }
    public void setBirds(ArrayList<Bird> birdSet){
        birds = birdSet;
    }
}