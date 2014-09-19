import java.util.ArrayList;


public class Feature {

	public int[] fDistinctSet;
	public int[] fFreqencies;
	public int[] fList;
	public int nFeatureInstances;
	public int nUniqueFeatures;
	
	//Initialization
	public void fInitialization(int[] fArray)
	{
		nFeatureInstances = fArray.length;
		fList = new int[nFeatureInstances];
		
		System.arraycopy(fArray, 0, fList, 0, nFeatureInstances);
		
		fDistinctSet = new int[ID3Main.maxDistinctFeatures];
		fFreqencies = new int[ID3Main.maxDistinctFeatures];
		
		for (int index = 0; index < ID3Main.maxDistinctFeatures; index++)
		{
			fDistinctSet[index] = ID3Main.iValue;
			fFreqencies[index] = ID3Main.iValue;
		}
		
		nUniqueFeatures = 0;
		for (int i = 0; i<nFeatureInstances; i++)
		{
			int value = fList[i];
			int index = getFirstIndex(fDistinctSet, value);

			if(index >= 0) fFreqencies[index]++;
			else
			{
				fDistinctSet[nUniqueFeatures] = value;
				fFreqencies[nUniqueFeatures] = 1;
				nUniqueFeatures++;
			}
		}
	}
	
	public void loadList(ArrayList<Integer> fArray)
	{
		int[] tempArray = new int[fArray.size()];
		
		for (int i = 0; i < fArray.size(); i++)
			tempArray[i] = fArray.get(i).intValue();

		fInitialization(tempArray);
	}	
	
	public int getFirstIndex(int[] array, int val)
	{
		for (int index = 0; index < array.length; index++)
		{
			if (array[index] == val)
				return index;
		}
		
		return ID3Main.iValue;
	}
	
	public int getNumberOfUniqueFeatures()
	{
		return nUniqueFeatures;
	}
	
	public void getFeatureProbabilities(double[] probability)
	{
		for (int index = 0; index < nUniqueFeatures; index++)
		{
			probability[index] = (double)fFreqencies[index] / (double)nFeatureInstances;
		}
	}
}
