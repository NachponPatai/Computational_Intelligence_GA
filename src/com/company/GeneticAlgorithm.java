package com.company;

import java.util.*;

import static jdk.nashorn.internal.objects.NativeMath.round;

public class GeneticAlgorithm {
    int[] model;
    double min = -1.0;
    double max = 1.0;
    ArrayList<Chromosome> initChrom = new ArrayList<Chromosome>();
    ArrayList<Chromosome> chroms = new ArrayList<Chromosome>();
    ArrayList<Double[]> dataSet;

    public void calFitness(Chromosome chrom){
        chrom.fitness = 0.0;
        NeuralNetwork NN = new NeuralNetwork(model);
        NN.initWeight(chrom);
        for(Double[] data: dataSet){
            if(NN.feedForward(data)){
                chrom.fitness += 1;
            }
        }
    }

    public void  findFitness(ArrayList<Chromosome> chroms){
        for (Chromosome chrom : chroms){
            calFitness(chrom);
        }
    }


    public ArrayList<Chromosome> random(int fit){
        int index;
        ArrayList<Chromosome> sel = new ArrayList<Chromosome>();
        for(int i = 0; i < fit; i++){
            index = new Random().nextInt(chroms.size());
            sel.add(chroms.get(index));
        }
        return  sel;
    }

    public ArrayList<Chromosome> crossOver(ArrayList<Chromosome> sel,int fit){
        ArrayList<Chromosome> crossover = new ArrayList<Chromosome>();
        for(int i = 0;i < fit; i++){
            int indexFather = new Random().nextInt(sel.size());
            int indexMother = new Random().nextInt(sel.size());
            Chromosome Father = sel.get(indexFather);
            Chromosome Mother = sel.get(indexMother);
            int selectLen = sel.get(0).gene.length;
            Double[] gene = new Double[selectLen];
            for (int j = 0; j < selectLen / 2; j++){
                gene[j] = Father.gene[j];
            }
            for (int k = selectLen / 2; k < selectLen; k++){
                gene[k] = Mother.gene[k];
            }
            crossover.add(new Chromosome(gene));
        }
        findFitness(crossover);
        return crossover;
    }

    public ArrayList<Chromosome> mutation(int fit, ArrayList<Chromosome> chromset){
        ArrayList<Chromosome> mutate = new ArrayList<Chromosome>();
        Set<Integer> index = new LinkedHashSet<Integer>();
        Random rand = new Random();
        int g = 0;
        while (index.size() < fit ){
            Integer gen = rand.nextInt(chromset.size());
            index.add(gen);
        }
        for (int i : index){
            Double[] gene = chromset.get(i).gene.clone();
            int prob = (int) (chromset.get(i).gene.length * 0.3);
            for (int j = 0;j < prob;j++){
                int k = rand.nextInt(chromset.get(i).gene.length);
                gene[k] += min + (max - min)*new Random().nextDouble();
                if (gene[k] < min){
                    gene[k] = min;
                }
                if (gene[k] > max){
                    gene[k] = max;
                }
            }
            mutate.add(new Chromosome(gene));
            calFitness(mutate.get(g++));
        }
        return mutate;
    }

    public void initChrom(int fit,int[] model){
        this.model = model;
        int len = 0;
        for(int i = 1;i < model.length;i++){
            len += model[i-1]*model[i];
        }
        for(int j = 0;j < fit; j++){
            Double[] gene = new Double[len];
            for(int k = 0;k < gene.length;k++){
                gene[k] = min + (max - min)*new Random().nextDouble();
            }
            initChrom.add(new Chromosome(gene));
        }
    }

    public void trainNN (int maxGen, ArrayList<Double[]> dataSet){
        this.dataSet = dataSet;
        chroms = (ArrayList<Chromosome>) initChrom.clone();
        findFitness(chroms);
        for(int i = 0;i <  maxGen;i++){
            ArrayList<Chromosome> sel = random((int)(chroms.size()*0.3));
            ArrayList<Chromosome> crossover = crossOver(sel,(int) (sel.size()*0.3));
            ArrayList<Chromosome> chromset = (ArrayList<Chromosome>) chroms.clone();
            chromset.addAll(crossover);
            int mutateSize = (int) (chroms.size()*0.3);
            ArrayList<Chromosome> mutate = mutation(mutateSize,chromset);
            Collections.sort(chromset, (o1, o2) -> (int)(o2.fitness - o1.fitness));
            chroms = new ArrayList<Chromosome>(chromset.subList(0,chroms.size() - mutateSize));
            chroms.addAll(mutate);

        }
        System.out.println("Gen " + maxGen + ": " + (chroms.get(0).fitness*100/dataSet.size()));
        Collections.sort(chroms, (o1, o2) -> (int)(o2.fitness - o1.fitness));
    }

    public double testNN(ArrayList<Double[]> test){
        NeuralNetwork NN = new NeuralNetwork(model);
        Chromosome bchrom = chroms.get(0);
        NN.initWeight(bchrom);
        bchrom.fitness = 0.0;
        for(Double[] data: test){
            if(NN.feedForward(data)){
                bchrom.fitness += 1;
            }
        }
        System.out.println("Test Accuracy: "  + (bchrom.fitness * 100 / test.size()));
        return (bchrom.fitness*100/test.size());
    }
}
