
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Class DistributedServer
 * @author Raghav Babu
 *  Date : 02/20/2016
 */
public class DistributedServer {

	/**
	 * Main Function.
	 * @param args
	 */
	public static void main(String[] args) {

		DistributedServer server = new DistributedServer();
		new ServerUtils();
		
		ServerUtils.NO_OF_SERVERS = Integer.parseInt(args[0]);
		ServerUtils.TOTAL_NO_OF_SERVERS = Integer.parseInt(args[1]);


		Thread serverThread = new Thread(new ServerRecvThread(server));
		serverThread.start();

	}

	/*
	 * passing the request to parent. 
	 * input is fileName, client IP and leaf node position in tree.
	 */
	public void passReqToParent(String fileName, String clientIPAddress, int nodeLevel, int nodePos, String trailPath) {

		int parent = 0;
		int parentLevel = 0;
		int parentNodePos = -1;
		
		try {
			System.out.println("Current Ip : "+InetAddress.getLocalHost().getHostAddress().toString());

			parentLevel = nodeLevel - 1;
			parentNodePos = (int) Math.floor(nodePos / 2);

			parent = ServerUtils.getParentNode(fileName, parentLevel, parentNodePos);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		String parentIpAddress = ServerUtils.valueToIpMap.get(parent);
		System.out.println("Parent : "+parent+" | Parent IP : "+parentIpAddress+" | client IP : "+clientIPAddress);

		Thread thread = new Thread(new ServerClientSendThread(fileName, Input.REQ, parentIpAddress, parentLevel, parentNodePos, trailPath));
		thread.start();
	}

	/*
	 * sends theFile to client.
	 */
	public void sendFileToClient(File dir, String fileName, String clientIPAddress, int nodeLevel, int nodePos, String trailPath) {

		Thread thread = new Thread(new ServerClientSendThread(fileName, Input.FILE, clientIPAddress, nodeLevel, nodePos,trailPath));
		thread.start();
	}


	/*
	 * serve request from the client.
	 */
	public void serveRequest(String fileName, File dir, String clientIpAddress, int nodeLevel, int nodePos, String trailPath) {

		boolean present = checkIfFilePresentInDirectory(fileName, dir);
		
		trailPath = trailPath + ServerUtils.getHashCodeForFileWithLevelAndPosition(fileName, nodeLevel, nodePos);

		if(present){
			sendFileToClient(dir,fileName,clientIpAddress, nodeLevel, nodePos, trailPath);
		}
		else{
			trailPath  = trailPath + "-";
			passReqToParent(fileName, clientIpAddress, nodeLevel, nodePos, trailPath);
		}
	}

	/*
	 * check if file is present in directory. 
	 * @param fileName
	 * @param dir
	 * @return
	 */
	public boolean checkIfFilePresentInDirectory(String fileName, File dir) {

		if(dir.exists()){

			File[] files = dir.listFiles();

			for(File file : files){

				if(file.getName().equals(fileName) )
					return true;
			}
		}
		return false;
	}

}
