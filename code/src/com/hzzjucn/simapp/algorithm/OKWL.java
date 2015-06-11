package com.hzzjucn.simapp.algorithm;

import java.util.Random;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.jet.math.Functions;

public class OKWL {
	
	private double n; //number of kernels   
	private int iterations; // T: pre-determined number of iterations 	
	private double lambda; // regulization term	
	private DenseDoubleMatrix1D weights; //the weight vector
	private double lRate0;  // the learning rate
		
    public OKWL (double n, int iterations, double lambda, double learningRate)
    {
    	this.n = n;
    	this.iterations = iterations;
    	this.lambda = lambda;
    	this.lRate0 = learningRate;
    }
             
	public void train (String trainData)
	{
		//initialize weights
		weights = new DenseDoubleMatrix1D ((int)n);
		weights.assign (1d/n);
		
		//loading training triplets
	    String[] triplets = AlgoUtil.loadData (trainData, false); 
		
	    //update the model
        for (int iter=0; iter<iterations; iter++)
        {
			Random r = new Random();
			int index = r.nextInt(triplets.length); // choose a triplet at random				
			String triplet = triplets[index];			
			double lRate = lRate0/(1 + lRate0 * lambda * (iter+1)); //eta_t
			update (triplet, lRate);	
        }		
	}
	    
	public void update (String triplet, double lRate)
	{
		DenseDoubleMatrix1D sp = AlgoUtil.getSimilarityVector (triplet, true, (int)n, false);		
		DenseDoubleMatrix1D sn = AlgoUtil.getSimilarityVector (triplet, false, (int)n, false);		
		DenseDoubleMatrix1D sp_sn = (DenseDoubleMatrix1D) sp.copy().assign(sn, Functions.minus);
		
		/** compute hinge loss **/
		double loss = hingeLoss (sp_sn);
				
		/** update weights **/
		double multiplier = 1.0 - lambda * lRate;
		weights.assign(Functions.mult (multiplier));		
				
		if (loss > 0)
		{
		  weights.assign(sp_sn.copy().assign(Functions.mult(lRate)), Functions.plus);
		}		
	}
	
    public double hingeLoss (DenseDoubleMatrix1D sp_sn)
    {	
	   return Math.max(0, 1 - weights.zDotProduct (sp_sn));	   
    }
    		
	public static void main (String[] args)
	{
		OKWL okwl = new OKWL (10, 100000, 0.0001, 0.01); //number of kernels; iterations; lambda; eta_0
		String train_data = "data/train_Triplets.txt";
		okwl.train (train_data);
	    System.out.println ("Optimal Weights:\n" + AlgoUtil.Normalize (okwl.weights));
	}
}
