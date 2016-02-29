
/**
 * Class ReplicaManagerThread
 * Thread to create replica in child servers.
 * @author Raghav Babu
 *	Date : 02/20/2016
 */
public class ReplicaManagerThread implements Runnable {

	String fileName;
	int nodeLevel;
	int nodePos;
	
	public ReplicaManagerThread(String fileName, int nodeLevel, int nodePos) {
		this.fileName = fileName;
		this.nodeLevel = nodeLevel;
		this.nodePos = nodePos;
	}

	@Override
	public void run() {
		createReplicasInChildNodes(fileName, nodeLevel, nodePos);
	}
	
	/*
	 * Create replicas of that file in child nodes. 
	 */
	public void createReplicasInChildNodes(String fileName, int nodeLevel, int nodePos) {

			int[] childs = null;
				
				try {
					childs = ServerUtils.getChildNodes(fileName, nodeLevel, nodePos);

					for(int child : childs){
						
						System.out.println("REPLICATING file "+fileName+ " in child Node : "+child);
						
						String childIpAddress = ServerUtils.valueToIpMap.get(child);
						Thread thread = new Thread(new ServerPeersReplicaThread(fileName,Input.FILE, childIpAddress));
						thread.start();
						
						thread.join();
					}
					
				}catch(Exception e){
					System.out.println("Leaf Nodes don't have any childs, so replication won't happen.");
				}

	}

}
