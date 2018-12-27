package com.company;

public class NeuralNetwork {
    double[][][] weight;
    int node;
    int[] model;

    public Double sigmoid(Double x){
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public NeuralNetwork(int[] model){
        this.model = model;
        int cell = 0;
        for (int layer: model){
            if(node < layer){
                node = layer;
            }
            node += layer;
        }
        weight = new double[model.length-1][node][node];
    }

    public void initWeight(Chromosome chromosome){
        int w = 0;
        for (int i = 0; i < model.length-1;i++){
            for(int j = 0; j < model[i]; j++){
                for (int k = 0; k < model[i+1];k++){
                    weight[i][j][k] = chromosome.gene[w];
                    w++;
                }
            }
        }
    }

    public boolean feedForward(Double[] input){
        Double[][] output = new Double[model.length][node];
        output[0] = input;

        for (int i = 1; i < model.length; i++){
            for(int j = 0;j < model[i]; j++){
                Double  x = 0.0;
                for(int k = 0;k < model[i - 1];k++){
                    x += output[i - 1][k] * weight[i - 1][k][j];
                }
                output[i][j] = Math.tanh(x);
            }
        }

        if(input [30] == 0.0){
            if (output[model.length-1][0] > output[model.length-1][1]){
                return  true;
            }else return false;
        }else {
            if (output[model.length - 1][0] < output[model.length - 1][1]) {
                return true;
            } else return false;
        }
    }

}
