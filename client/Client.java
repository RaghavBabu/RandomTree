
import java.util.Scanner;

/**
 * Class Client
 * Client program to send request to server and get response.
 * @author Raghav Babu
 * Date : 02/20/2016
 */
public class Client {

	public static void main(String[] args) {

		Client client = new Client();
		new ClientUtils();

		ClientUtils.NO_OF_SERVERS = Integer.parseInt(args[1]);
		ClientUtils.TOTAL_NO_OF_SERVERS = Integer.parseInt(args[2]);

		int fileCount = args[0].split(",").length;
		String[] fileNames = new String[fileCount];

		for(int i = 0; i < fileCount ; i++){

			fileNames[i] = args[0].split(",")[i];

			ClientUtils.files.add(fileNames[i]);

			client.insertFileintoRespectiveRootNodes(fileNames[i]);	

		}
		Scanner scan = new Scanner(System.in);

		Thread clientRecvThread = new Thread(new ClientRecvThread());
		clientRecvThread.start();

		System.out.println("Enter a file name to request from Server :");
		
		while(scan.hasNext()){

			String fileName = null;

			//choose a valid fileName.
			while(true){
				
				try{
					fileName = scan.next();
				}catch (Exception e) {
					System.out.println("Choose a valid fileName among : "+ClientUtils.files);
					continue;
				}

				if(fileName.equals("e")){
					Thread clientThread = new Thread(new ClientSendThread(fileName, -1, Input.EXIT));
					clientThread.start();

					try {
						clientThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					System.out.println( "-------- Closing Client and Servers -------");
					System.exit(1);
				}


				if(ClientUtils.files.contains(fileName)){
					break;
				}else{
					System.out.println("Choose a valid fileName among : "+ClientUtils.files);
					continue;
				}


			}
			
			//List<Integer > leafNodes = client.sendRequestToLeafNode(fileName);
			//System.out.println("Leaf Nodes : "+leafNodes);

			//enter a valid input leaf node position.
			String leafNodePos = null;
			System.out.println("Choose a leaf node position to request file from : ");

			while(true){

				try {
					leafNodePos = scan.next();
					
					if(ClientUtils.checkIfValidLeafNodePosition(Integer.parseInt(leafNodePos) ) || Integer.parseInt(leafNodePos) == -1  )
						break;
					else{
						System.out.println("Choose a leaf node position to request file from 0 to "+ClientUtils.getValidLeafNodePos()+" exclusive");
						continue;
					}
					
				}catch (Exception e) {
					System.out.println("Enter a valid integer leaf node position between 0 to "+ClientUtils.getValidLeafNodePos()+" exclusive");
					continue;
				}
				
			}

			int leafNode = Integer.parseInt(leafNodePos);

			if(leafNode == -1){
				Thread clientThread = new Thread(new ClientSendThread(fileName, leafNode, Input.EXIT));
				clientThread.start();
			}
			else{
				Thread clientThread = new Thread(new ClientSendThread(fileName, leafNode, Input.REQ));
				clientThread.start();
			}

			System.out.println("Enter a file name to request from Server :");
		}
	}
	/*
	 * insert files into root nodes.
	 */
	private void insertFileintoRespectiveRootNodes(String fileName) {

		Thread clientThread = new Thread(new ClientSendThread(fileName, Input.FILE));
		clientThread.start();

	}
}
