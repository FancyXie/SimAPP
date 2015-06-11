package com.hzzjucn.simapp.algorithm;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.jet.math.Functions;

public class AlgoUtil {
	
	public static double test (int n, String testdata, DenseDoubleMatrix1D w)
	{
		double totalError = 0;
		
		String[] triplets = AlgoUtil.loadData (testdata, false); 		
		for (String triplet: triplets)
		{
			totalError += testOneTriplet(n, triplet, w);			
		}
		
		totalError = totalError/triplets.length;				
		return totalError;
	}
	
    public static double testOneTriplet (int n, String triplet, DenseDoubleMatrix1D w)
	{
		DenseDoubleMatrix1D sp = AlgoUtil.getSimilarityVector (triplet, true, n, false);		
		DenseDoubleMatrix1D sn = AlgoUtil.getSimilarityVector (triplet, false, n, false);				
		DenseDoubleMatrix1D sp_sn = (DenseDoubleMatrix1D) sp.copy().assign(sn, Functions.minus);			
		double error = 0;		
		if (w.zDotProduct (sp_sn) < 0.01)
		{
			error = 1;
		}		
		return error;
	}
	    
	public static String[] loadData (String file, boolean shuffle)
    {
        List<String> ret = new ArrayList<String>();
        try
        {
        	BufferedReader sr = new BufferedReader(new FileReader(file));
            String temp;
                while ((temp = sr.readLine()) != null)
                {
                    ret.add(temp);
                }
                sr.close();
            }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
        if (shuffle)
        {
           Collections.shuffle(ret);  //shuffle the triplets
        }
           return ret.toArray(new String[ret.size()]);
    }

	public static DenseDoubleMatrix1D Normalize (DenseDoubleMatrix1D v)
	{
		double sum = v.zSum();
		return (DenseDoubleMatrix1D) v.assign(Functions.mult(1/sum));		
	}
	
	public static DenseDoubleMatrix1D getSimilarityVector (String triplet, boolean type, int n, boolean normalize)
	{
		DenseDoubleMatrix1D s = new DenseDoubleMatrix1D ((int)n);		
		
		if (type == true)
		{
			String sp = triplet.split("\t")[1].replaceAll("\\{|\\}", "");
			String []entries = sp.split("; ");
			
			for (int i=0; i < entries.length; i++)
			{
			     s.setQuick(i, Double.parseDouble(entries[i]));
			}			
		}
		
		else
		{
			String sp = triplet.split("\t")[2].replaceAll("\\{|\\}", "");
			String []entries = sp.split("; ");
			
			for (int i=0; i<entries.length; i++)
			{
               s.setQuick(i, Double.parseDouble(entries[i])); 
			}	
		}
		if (normalize)
		{			
			double d = s.zDotProduct(s);
			
			if (d > 0 && d != 1.0)
			{
			  DenseDoubleMatrix1D divisor = new DenseDoubleMatrix1D ((int)n);
			  divisor.assign(1.0/Math.sqrt(d));
			  s.assign(divisor, cern.jet.math.Functions.mult);			  
		    }
		}		
		return s;				
	}
	
    public static double norm (DenseDoubleMatrix1D v)
    {
    	return  v.zDotProduct (v);
    }
}
