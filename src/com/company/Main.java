package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

    public static void main(String[] args) throws IOException {
	    String path = "C:\\Users\\nachp\\IdeaProjects\\GeneticAlgorithm\\src\\com\\company\\wdbc.txt";
        File data = new File(path);
        ArrayList<Double []> dataset = new ArrayList<Double[]>();

        BufferedReader buff = new BufferedReader(new FileReader(data));
        String read;
        while((read = buff.readLine())!= null){
            String[] line = read.split(",");
            Double[] feature = new Double[31];
            if (line[1].equals("M")){
                feature[30] = 0.0;
            }else{
                feature[30] = 1.0;
            }
            for (int i = 2; i < line.length; i++){
                feature[i-2] = Double.parseDouble(line[i]);
            }
            dataset.add(feature);
        }
        Collections.shuffle(dataset);
        System.out.println(dataset.size());
        buff.close();
        int[] model ={30,15,5,2};
        GeneticAlgorithm GA = new GeneticAlgorithm();

        for(int i = 0;i < model.length;i++){
            if(i != 0){
                System.out.print("-");
                System.out.print(model[i]);
            }
        }

        GA.initChrom(10,model);
        double acc = 0.0;
        for(int i = 0;i < 10;i++){
            System.out.println("-------------------Fold: "+(i + 1)+" --------------------");
            int j = (int)(i*dataset.size()*0.1);
            ArrayList<Double[]> testset = new ArrayList<Double[]>(dataset.subList(j,(int)(j + (dataset.size()*0.1))));
            ArrayList<Double[]> trainset = (ArrayList<Double[]>) dataset.clone();
            trainset.subList(j,(int)(j+(trainset.size()*0.1))).clear();
            GA.trainNN(200,(ArrayList<Double[]>) trainset);
            System.out.println("---------------------- Start Test -----------------------");
            acc += GA.testNN(testset);
        }
        System.out.println("Average Accuracy: " + acc/10);
    }
}
