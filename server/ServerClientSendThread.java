import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;


/**
 * Class ServerClientSendThread
 * Thread to send result,file to client.
 * @author Raghav Babu
 * Date : 02/20/2016
 */
public class ServerClientSendThread implements Runnable{

	File dir;
	File f;
	String fileName;
	Input input;
	String ipAddress;
	int nodeLevel;
	int nodePos;
	String trailPath;

	public ServerClientSendThread(String fileName, Input input, String ipAddress,  int nodeLevel, int nodePos, String trailPath) {
		this.fileName = fileName;
		this.input = input;
		this.ipAddress = ipAddress;
		this.dir = ServerUtils.SERVER_DEST;
		this.nodeLevel = nodeLevel;
		this.nodePos = nodePos;
		this.trailPath = trailPath;

		f = new File(dir, fileName);
	}

	@Override
	public void run() {

		try {

			Socket serverFileSendSocket = null;
			BufferedReader br = null;

			if(input == Input.FILE )
			{
				System.out.println(ServerUtils.ServerName+ ": Sending to client : "+ipAddress);

				serverFileSendSocket = new Socket(ipAddress, 1300);		

				FileReader reader = new FileReader(f);
				br = new BufferedReader(reader);

				String str = null;

				boolean flag = false;
				String prevString = null;
				String nextString = null;
				DataOutputStream dos = null;

				while( ( str = br.readLine()) != null ){

					try{
						dos = new DataOutputStream(serverFileSendSocket.getOutputStream());
						prevString = nextString;
						nextString = str;

						if(!flag){
							nextString = str;
							str = input +":"+fileName+":"+trailPath;
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
				ServerUtils.filePopularityMap.put(fileName, (ServerUtils.filePopularityMap.get(fileName) + 1));
				System.out.println(ServerUtils.ServerName+ ": Popularity of file "+fileName+ " in this node : "+ ServerUtils.filePopularityMap.get(fileName));

				if(ServerUtils.filePopularityMap.get(fileName).equals(5) ){
					System.out.println(ServerUtils.ServerName+ ": Creating Replicas in child Nodes");
					Thread replicaThread = new Thread(new ReplicaManagerThread(fileName, nodeLevel, nodePos ) );
					replicaThread.start();
				}
				serverFileSendSocket.close();
			}
			else if(input == Input.REQ) {

				try{
					System.out.println(ServerUtils.ServerName+ ": Requesting parent server : "+ServerUtils.ipToValueMap.get(ipAddress));
					serverFileSendSocket = new Socket(ipAddress, 1234);		
					DataOutputStream dos = new DataOutputStream(serverFileSendSocket.getOutputStream());

					dos.writeBytes(Input.REQ+":"+fileName+":"+nodeLevel+":"+nodePos +":"+trailPath+'\n');

				}catch (Exception e){
					System.out.println("Exception while forwarding Request  "+e);
				}
				serverFileSendSocket.close();

			}


		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
