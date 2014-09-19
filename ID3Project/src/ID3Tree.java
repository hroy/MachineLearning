import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class ID3Tree{
	
	public int nInstances = 0;
	public int nFeatures = 0;
	
	public ArrayList<int[]> inputMatrix = new ArrayList<int[]>(); //stores input matrix  
	public ArrayList<int[]> pInstances = new ArrayList<int[]>(); //list of partitions
	public ArrayList<String> pNames = new ArrayList<String>(); //list of partition names

	public ID3Util id3Util;
	//public int nUniqueFeatures = 5;
	public ArrayList<int[]> newPartitions = new ArrayList<int[]>(); //list of new partitions
	
	public boolean buildTree(String[] fNames)
	{
		try
		{
			if(!loadFiles(fNames)) throw new Exception("Exception in file loading!");
			
			double maxFnValue = ID3Main.iValue;
			int selectedPartition = ID3Main.iValue;
			int selectedFeature = ID3Main.iValue;
			
			//1. select partition and feature
			try
			{	
				id3Util = new ID3Util(nInstances, nFeatures, inputMatrix, pInstances, pNames);
				
				for (int p = 0; p < pNames.size(); p++)
				{
					double pEntropy = id3Util.getTargetFeatureEntropy(p); 
					if(pEntropy<0) throw new Exception("Exception in calculating target entropy!");
					
					int locallySelectedFeature = ID3Main.iValue;
					double maxGain = ID3Main.iValue;
					
					for (int f = 0; f < nFeatures-1; f++)
					{
						double fEntropy = id3Util.getNonTargetFeatureEntropy(p, f); 
						if(fEntropy<0) throw new Exception("Exception in calculating feature's entropy!");
						
						double fGain = pEntropy - fEntropy;
						
						if (fGain > maxGain)
						{
							maxGain = fGain;
							locallySelectedFeature = f;
						}
					}
					
					//F = pb*gain; 
					double fnValue = ((double)pInstances.get(p).length/(double)nInstances)*maxGain;
					
					if (fnValue > maxFnValue)
					{
						maxFnValue = fnValue;
						selectedPartition = p;
						selectedFeature = locallySelectedFeature;
					}
				}
			}
			catch(Exception ex)
			{
				System.out.println("Exception in part 1: "+ex.getMessage());
				return false;				
			}
			
			//2. extract feature values of selected partitions
			try
			{	
				int[] selectedFeaturesOfSelectedPartitions = new int[pInstances.get(selectedPartition).length];
				for (int instance = 0; instance < selectedFeaturesOfSelectedPartitions.length; instance++)
				{
					int index = pInstances.get(selectedPartition)[instance]-1;
					selectedFeaturesOfSelectedPartitions[instance] = inputMatrix.get(selectedFeature)[index];
				}
			
				//3. find out distinct feature values, divide partition
				int[] selectedDistinctFeatureValues = new int[ID3Main.maxDistinctFeatures]; //nUniqueFeatures
				for (int index = 0; index < ID3Main.maxDistinctFeatures; index++) //nUniqueFeatures
				{
					selectedDistinctFeatureValues[index] = ID3Main.iValue;
				}
				
				int distinctFeatureIndex = 0;
				for(int index = 0; index < selectedFeaturesOfSelectedPartitions.length; index++)
				{
					int fValue = selectedFeaturesOfSelectedPartitions[index];
					int npIndex = getNewPartionIndex(selectedDistinctFeatureValues, fValue); 
					
					if (npIndex >= 0)
					{
						int[] instancesInPartition = newPartitions.get(npIndex);
						int[] latestInstancesPartitions = new int[instancesInPartition.length+1];
						System.arraycopy(instancesInPartition, 0, latestInstancesPartitions, 0, instancesInPartition.length);
						
						latestInstancesPartitions[instancesInPartition.length] = pInstances.get(selectedPartition)[index];
						newPartitions.set(npIndex, latestInstancesPartitions);					
					}
					else
					{
						selectedDistinctFeatureValues[distinctFeatureIndex] = fValue;
						int[] instancesInPartition = new int[1];
						instancesInPartition[0] = pInstances.get(selectedPartition)[index];
						newPartitions.add(instancesInPartition);
						distinctFeatureIndex++;
					}
				}
			}
			catch(Exception ex)
			{
				System.out.println("Exception in part 2 or 3: "+ex.getMessage());
				return false;				
			}
			
			//4. naming divided partitions			
			String[] npNames = new String[newPartitions.size()];
			for (int i = 0; i < newPartitions.size(); i++)
			{
				npNames[i] = pNames.get(selectedPartition)+i;
			}
			
			//5. write to output file
	        try
	        {
	            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fNames[2]), true));
	            
	            for (int i = 0; i < pNames.size(); i++)
	            {
	            	if (i != selectedPartition)
	            	{
	                	String lineText = pNames.get(i) + " ";
	                	String st = Arrays.toString(pInstances.get(i)).replace(",","");
	                	st = st.substring(1, st.length()-1);
	                    bw.write(lineText + st);
	                    bw.newLine();
	            	}
	            	else
	            	{
	            		for (int ni = 0; ni < npNames.length; ni++)
	    	            {
	    	            	String lineText = npNames[ni] + " ";
	    	            	String st = Arrays.toString(newPartitions.get(ni)).replace(",", "");
	    	                st = st.substring(1, st.length()-1);
	    	            	bw.write(lineText+st);
	    	                bw.newLine();
	    	            }
	            	}
	            }

	            bw.newLine();
	            bw.close();
	        } 
	        catch (Exception ex)
	        {
	        	System.out.println("Exception in part 5." + ex.getMessage());
	        	return false;
	        }
	    	
	        String st = Arrays.toString(npNames);
	        st = st.substring(1, st.length()-1);
			System.out.println("Partition "+pNames.get(selectedPartition)+" was replaced with partitions "+Arrays.toString(npNames)+" using Feature " + (selectedFeature+1));
			
			return true;
			
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			return false;
		}
	}
	
	private boolean loadFiles(String[] fNames)
	{
		if (!readDataFile(fNames[0]))
			return false;
		
		if (!readIntermediateFile(fNames[1]))
			return false;		
		
		return true;
	}
	
	private boolean readDataFile(String dfName)
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(dfName));
	
			String[] fLinetokens = (in.readLine()).split(ID3Main.splitChar);
			
			if (fLinetokens.length==2)
			{
				nInstances = Integer.parseInt(fLinetokens[0]);
				nFeatures = Integer.parseInt(fLinetokens[1]);
							
				int[][] dataSet = new int[nInstances][nFeatures];

				String sLine;
				for (int row = 0; row < nInstances; row++)
				{
					sLine = in.readLine();
					fLinetokens = sLine.split(ID3Main.splitChar);
	
					if (fLinetokens.length != nFeatures) throw new Exception("Exception: Feature is missing!");
						
					for (int col = 0; col < nFeatures; col++)
					{
						dataSet[row][col] = Integer.parseInt(fLinetokens[col]);
					}
				}
							
				for (int feature=0; feature<nFeatures; feature++)
				{
					int[] singleFeatureValues = new int[nInstances];
					
					for (int instance = 0; instance < nInstances; instance++)
					{
						singleFeatureValues[instance] = dataSet[instance][feature];
					}
					inputMatrix.add(singleFeatureValues);
				}
				
				return true;
			}
			else throw new Exception("Exception: input format is wrong!");
		}
		catch(Exception ex)
		{
			System.out.print(ex.getMessage());
			return false;
		}
	}
	
	public boolean readIntermediateFile(String ifName)
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(ifName));
		
			String lineText = in.readLine();
			while(lineText != null)
			{
				String[] tokens = lineText.split(ID3Main.splitChar);
				
				pNames.add(tokens[0]);			
				int numOfInsInPartition = tokens.length-1;
				
				int[] tempPartitionElements = new int[numOfInsInPartition];
				for (int instance = 0; instance < numOfInsInPartition; instance++)
				{
					tempPartitionElements[instance]= Integer.parseInt(tokens[instance+1]);
				}				
				pInstances.add(tempPartitionElements);
				
				lineText = in.readLine();
			}
			
			return true;
		}
		catch(Exception ex){
		      System.out.print(ex.getMessage());
		      return false;
		}	
	}

	public int getNewPartionIndex(int[] array, int val)
	{
		for (int index = 0; index < array.length; index++)
		{
			if (array[index] == val)
				return index;
		}		
		return ID3Main.iValue;
	}
}
