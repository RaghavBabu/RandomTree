import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Class ClientUtils which holds client utility methods.
 * @author Raghav Babu
 *  Date : 02/20/2016
 */
public class ClientUtils {

	public static int NO_OF_SERVERS;
	public static int TOTAL_NO_OF_SERVERS;
	public static File CLIENT_DEST;
	public static String CLIENT_IP;
	public static Map<Integer, String> valueToIpMap;
	public static Map<String,Integer> ipToValueMap;
	public static List<String> files;

	public ClientUtils(){
		valueToIpMap = new ValueToServerIPXmlParser().parseXML();
		ipToValueMap = fill();
		ClientUtils.CLIENT_DEST = new File("Client_dest");
		files = new ArrayList<String>();
	}

	private Map<String, Integer> fill() {

		ipToValueMap = new HashMap<String, Integer>();

		for(Entry<Integer, String> e : valueToIpMap.entrySet()){
			ipToValueMap.put(e.getValue(), e.getKey());
		}
		return ipToValueMap;
	}

	/*
	 * returns the hash codes for the input File Name.
	 * @param fileName
	 * @return hash Value.
	 */
	private static int getHashCodesForFile(String fileName, int i , int j) {
		
		int hashVal = 0 ;
		for (int l = 0; l < fileName.length(); l++)
			hashVal = (31 * hashVal + fileName.charAt(l) + i + j) % ClientUtils.TOTAL_NO_OF_SERVERS;

		return hashVal;
	}


	/*
	 * To check if leaf node position is present in leaf node level of that tree. 
	 * @param leafNode
	 * @return
	 */
	public static boolean checkIfValidLeafNodePosition(int leafNodePosition) {

		int currentLevel = getMaximumPossibleLevel();

		int maxNodes = (int) Math.pow(2, currentLevel);

		if(leafNodePosition >= 0 && leafNodePosition < maxNodes)
			return true;
		else
			return false;
	}

	/*
	 * Get Valid leaf node position max Count in leaf level.
	 */
	public static int getValidLeafNodePos() {

		int n = ClientUtils.NO_OF_SERVERS;
		int maxNodes = 0;
		int currentLevel = 0;

		while(Math.pow(2, currentLevel) < n){

			maxNodes = 0;

			for(int j = 0 ; j < Math.pow(2, currentLevel); j++){
				maxNodes += 	1;
			}
			currentLevel += 1;
		}
		return maxNodes;
	}

	/*
	 * returns the hash code for the input File Name.
	 * @param fileName
	 * @return hash Value.
	 */
	public static int getHashCodeForFileWithLevelAndPosition(String fileName, int level , int pos) {

		System.out.println("Computing unique hash vale to map to a node for : "+fileName+ ",level : "+level+", pos : "+pos);
		
		Set<Integer> fileNodeHashValues = new LinkedHashSet<Integer>();
		int hashVal = 0;
		boolean flag = false;
		
		for(int i = 0; i <= level; i++){

			for(int j = 0 ; j < Math.pow(2, i) ; j++){

				 hashVal = getHashCodesForFile(fileName, i, j);

				while(!fileNodeHashValues.add(hashVal)){
					hashVal = hash(hashVal);
				}
				if( j == pos && i == level){
					System.out.println("Hash value for node found "+hashVal);
					return hashVal;
				}
			}
			
		}
		return hashVal;

	}

	/*
	 * If hash value already present, find a new hash Value by adding a random number to it and taking modulo again.
	 * @param hashVal
	 * @return int.
	 */
	private static int hash(int hashVal) {
		
		int random = 31;
		hashVal = (hashVal + random) % ClientUtils.TOTAL_NO_OF_SERVERS;
		return hashVal;
	}

	/*
	 * Get maximum possible level for the given number of servers.
	 */
	public static int getMaximumPossibleLevel() {
		
		int n = ClientUtils.NO_OF_SERVERS;
		int currentLevel = 0;
		int maxLevel = -1;

		while(Math.pow(2, currentLevel) < n){
			currentLevel += 1;
			maxLevel += 1;
		}
		
		return maxLevel;
	}


}
