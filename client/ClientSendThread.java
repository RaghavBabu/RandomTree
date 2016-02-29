import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.util.Map.Entry;


/**
 * Class ClientSendThread
 * Sends files to the Server root initially.
 * @author Raghav Babu
 * @version 5-Oct-2015
 */

public class ClientSendThread implements Runnable {

	String fileName;
	Integer leafNodePos;
	Input input;

	public ClientSendThread(String fileName, Input input){
		this.fileName = fileName;
		this.input = input;
	}

	public ClientSendThread(String fileName,Integer leafNodePos, Input input){
		this.fileName = fileName;
		this.leafNodePos = leafNodePos;
		this.input = input;
	}

	public void run(){

		try {

			Socket clientSocket = null;

			if(input.equals(Input.FILE) )
			{
				int node = ClientUtils.getHashCodeForFileWithLevelAndPosition(fileName, 0, 0);
				System.out.println("Inserting file "+fileName+" in root Node "+node);
				
				String ipAddress = ClientUtils.valueToIpMap.get(node);

				clientSocket = new Socket(ipAddress, 1234);		
				BufferedReader br = null;

				File f = new File(fileName);

				FileReader reader = new FileReader(f);
				br = new BufferedReader(reader);

				String str = null;

				boolean flag = false;
				String prevString = null;
				String nextString = null;
				DataOutputStream dos = null;

				while( ( str = br.readLine()) != null ){

					try{
						dos = new DataOutputStream(clientSocket.getOutputStream());
						prevString = nextString;
						nextString = str;

						if(!flag){
							nextString = str;
							str = Input.FILE+":"+fileName;
							flag = true;
						}else{
							str = prevString;
						}
						//System.out.println(str);
						dos.writeBytes(str + '\n');

					}catch (Exception e){
						System.out.println("Exception while transfering file  "+e);
					}
				}

				dos.writeBytes(nextString + '\n');
				clientSocket.close();
			}
			else if(input.equals(Input.REQ)){

				try{

					//picking random leaf node.
					//int leafNode = leafNodes.get(new Random().nextInt(leafNodes.size()));
					
					int maxLevel = ClientUtils.getMaximumPossibleLevel();
					System.out.println("Chosen Leaf Node Position : "+leafNodePos+", Max Level in Random Tree : "+maxLevel );
					
					int leafNode = ClientUtils.getHashCodeForFileWithLevelAndPosition(fileName, maxLevel, leafNodePos);
					System.out.println("Chosen Leaf Node : "+leafNode);
					
				    String ipAddress = ClientUtils.valueToIpMap.get(leafNode);
				    System.out.println("Chosen Leaf Node IP Address: "+ipAddress);
				    
					clientSocket = new Socket(ipAddress, 1234);		
					DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

					dos.writeBytes(Input.REQ+":"+fileName+":"+maxLevel+":"+leafNodePos +'\n');

					clientSocket.close();
				}catch (Exception e){
					System.out.println("Exception while forwarding request  ");
					e.printStackTrace();
				}
			}
			else if(input.equals(Input.EXIT) )
			{

				for(Entry<Integer, String > e : ClientUtils.valueToIpMap.entrySet()){
					try{
						clientSocket = new Socket(e.getValue(), 1234);		
						DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
						dos.writeBytes(Input.EXIT+":"+e.getKey()+'\n');
						clientSocket.close();

					}catch (Exception ex){
						System.out.println("Exception while forwarding  exit Request  "+ex);
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
}
