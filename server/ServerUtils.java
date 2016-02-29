import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class ServerUtils.
 * @author Raghav Babu
 * Date : 02/15/2016.
 */

public class ServerUtils {

	public static int NO_OF_SERVERS;
	public static int TOTAL_NO_OF_SERVERS;
	public static  File SERVER_DEST;
	public static File CLIENT_DEST;
	public static String CLIENT_IP;
	public static Map<Integer, String> valueToIpMap;
	public static Map<String,Integer> ipToValueMap;
	public static Map<String,Integer> filePopularityMap;
	public static String ServerName;

	/*
	 * Constructor.
	 */
	public ServerUtils(){
		valueToIpMap = new ValueToServerIPXmlParser().parseXML();
		ipToValueMap = fill();
		ServerUtils.CLIENT_DEST = new File("Client_dest");
		filePopularityMap = new ConcurrentHashMap<String, Integer>();

		try {
			SERVER_DEST = new File("Server_"+ ServerUtils.
					ipToValueMap.get(InetAddress.getLocalHost().getHostAddress().toString())+"_dest");
			ServerName = "Server_"+ ipToValueMap.get(InetAddress.getLocalHost().getHostAddress().toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Fill the ip to Hash Value Map.
	 */
	private Map<String, Integer> fill() {

		ipToValueMap = new HashMap<String, Integer>();

		for(Entry<Integer, String> e : valueToIpMap.entrySet()){
			ipToValueMap.put(e.getValue(), e.getKey());
		}
		return ipToValueMap;
	}

	/*
	 * returns the hash code for the input File Name.
	 * @param fileName
	 * @return hash Value.
	 */
	private static int getHashCodesForFile(String fileName, int i , int j) {

		int hashVal = 0;
		for (int l = 0; l < fileName.length(); l++)
			hashVal = (31 * hashVal + fileName.charAt(l) + i + j) % ServerUtils.TOTAL_NO_OF_SERVERS;

		return hashVal;
	}

	/*
	 * If hash value already present, find a new hash Value by adding a random number to it and taking modulo again.
	 * @param hashVal
	 * @return int.
	 */
	private static int hash(int hashVal) {

		int random = 31;
		hashVal = (hashVal + random) % ServerUtils.TOTAL_NO_OF_SERVERS;
		return hashVal;
	}

	/*
	 * Get Parent node.
	 */
	public static int getParentNode(String fileName, int nodeLevel, int nodePos) {

		int parentNode = ServerUtils.getHashCodeForFileWithLevelAndPosition(fileName, nodeLevel, nodePos);

		System.out.println(ServerUtils.ServerName+ ": Parent Node Level and Position : "+nodeLevel+","+nodePos+ " Parent Node  : "+parentNode);

		return parentNode;
	}

	/*
	 * Get all child nodes for this parent.
	 * @param val
	 * @param fileName
	 * @return
	 */
	public static int[] getChildNodes(String fileName, int nodeLevel, int nodePos) {

		int maxLevel = ServerUtils.getMaximumPossibleLevel();

		if(nodeLevel + 1 > maxLevel)
			return null;
		
		else {
			int[] childs = new int[2];

			childs[0] = ServerUtils.getHashCodeForFileWithLevelAndPosition(fileName, nodeLevel + 1, (2 * nodePos));
			childs[1] = ServerUtils.getHashCodeForFileWithLevelAndPosition(fileName, nodeLevel + 1, (2 * nodePos) + 1);
			return childs;
		}

	}

	/*
	 * To check if leaf node position is present in leaf node level of that tree. 
	 * @param leafNode
	 * @return
	 */
	public static boolean checkIfValidLeafNodePosition(int leafNodePosition) {

		int currentLevel = getMaximumPossibleLevel();

		int maxNodes = (int) Math.pow(2, currentLevel);

		if(leafNodePosition < maxNodes)
			return true;
		else
			return false;
	}


	/*
	 * Get Valid leaf node position max Count in leaf level.
	 */
	public static int getValidLeafNodePos() {

		int n = ServerUtils.NO_OF_SERVERS;
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

		//System.out.println("Computing unique hash vale to map to a node for : "+fileName+ ",level : "+level+",pos : "+pos);
		Set<Integer> fileNodeHashValues = new LinkedHashSet<Integer>();
		int hashVal = 0;

		for(int i = 0; i <= level; i++){

			for(int j = 0 ; j < Math.pow(2, i) ; j++){

				hashVal = getHashCodesForFile(fileName, i, j);
				
				while(!fileNodeHashValues.add(hashVal)){
					hashVal = hash(hashVal);
				}
		
				if( j == pos && i == level){
					/*System.out.println("Computed unique hash value to map to a node for : "+fileName+ ", level : "+level+", pos : "+pos 
							+" Hash value : "+hashVal);*/
					return hashVal;
				}
			}

		}
		//System.out.println("Hash value for node found "+hashVal);
		return hashVal;

	}

	/*
	 * Get maximum possible level for the given number of servers.
	 */
	public static int getMaximumPossibleLevel() {

		int n = ServerUtils.NO_OF_SERVERS;
		int currentLevel = 0;
		int maxLevel = -1;

		while(Math.pow(2, currentLevel) < n){
			currentLevel += 1;
			maxLevel += 1;
		}

		return maxLevel;
	}


}


