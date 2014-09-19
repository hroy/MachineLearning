import java.util.ArrayList;


public class ID3Util {

	public int nInstances = 0;
	public int nFeatures = 0;
	
	public ArrayList<int[]> inputMatrix = new ArrayList<int[]>();   
	public ArrayList<int[]> pInstances = new ArrayList<int[]>();
	public ArrayList<String> pNames = new ArrayList<String>();
	
	public ID3Util(int i, int f, ArrayList<int[]> Matrix, ArrayList<int[]> Instances, ArrayList<String> Names)
	{
		this.nInstances = i;
		this.nFeatures = f;
		
		this.inputMatrix = Matrix;
		this.pInstances = Instances;
		this.pNames = Names;
	}
	
	public double getTargetFeatureEntropy(int p)
	{
		Feature tFeature = getFeatureObject(pInstances.get(p), nFeatures-1);
		
		double[] fProbabilities = new double[tFeature.getNumberOfUniqueFeatures()];
		tFeature.getFeatureProbabilities(fProbabilities);
		
		return getEntropy(fProbabilities); 
	}
	
	public Feature getFeatureObject(int[] iArray, int f)
	{
		Feature obFeature = new Feature();
		
		int totalInstance = iArray.length;
		int[] fTempList = new int[totalInstance];
		
		for (int index = 0; index < totalInstance; index++)
		{
			fTempList[index] = inputMatrix.get(f)[iArray[index]-1];
		}		
		
		obFeature.fInitialization(fTempList);
		return obFeature;
	}
	
	public double getEntropy(double[] prob)
	{
		if (prob.length == 0 || prob.length > 2)
		{
			System.out.println("More than 2 distinct target attributes!");
			return ID3Main.iValue;
		}
			
		if (prob.length == 1)
			return 0;
		
		if (prob[0] == 0 || prob[1] == 0)
			return 0;
		else if (prob[0] == prob[1])
			return 1;
		else
		{
			return (prob[0]*Math.log(1/prob[0])+prob[1]*Math.log(1/prob[1]))/Math.log(2);
		}
	}
	
	public double getNonTargetFeatureEntropy(int p, int fIndex)
	{
		ArrayList<double[]> tFeatureProbabilities = new ArrayList<double[]>();
		
		Feature tFeaturesObj = getFeatureObject(pInstances.get(p), nFeatures-1);
		Feature cFeaturesObj = getFeatureObject(pInstances.get(p), fIndex);
				
		int nDistinctCurrentFeatures = cFeaturesObj.getNumberOfUniqueFeatures();
		double[] cFeatureProbabilities = new double[nDistinctCurrentFeatures];
		cFeaturesObj.getFeatureProbabilities(cFeatureProbabilities);
		
		//Finding Probabilities
		for(int i = 0; i < nDistinctCurrentFeatures; i++)
		{
			int value = cFeaturesObj.fDistinctSet[i];
			
			ArrayList<Integer> tNewFeatureList = new ArrayList<Integer>();
			for (int index = 0; index < cFeaturesObj.nFeatureInstances; index++)
			{
				if (value == cFeaturesObj.fList[index])
				{
					tNewFeatureList.add(tFeaturesObj.fList[index]);
				}
			}
						
			Feature tNewFeatureObj = new Feature();
			tNewFeatureObj.loadList(tNewFeatureList);
			
			double[] tNewFeatureProbabilities = new double[tNewFeatureObj.getNumberOfUniqueFeatures()];
			tNewFeatureObj.getFeatureProbabilities(tNewFeatureProbabilities);
			tFeatureProbabilities.add(tNewFeatureProbabilities);
		}
		
		return getEntropy(cFeatureProbabilities, tFeatureProbabilities);
	}
	
	public double getEntropy(double[] fProbabilities, ArrayList<double[]> targetFeatureProbabilities)
	{
		if (fProbabilities.length != targetFeatureProbabilities.size())
		{
			System.out.println("Error in calculating feature's Entropy!");
			return ID3Main.iValue;
		}			
		
		double entropy = 0.0;
		
		for (int i = 0; i < fProbabilities.length; i++ )
		{
			entropy += fProbabilities[i]*getEntropy(targetFeatureProbabilities.get(i));
		}
		
		return entropy;
	}
	
}
