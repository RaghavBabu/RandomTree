import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;

/**
 * class ServerPeersReplicaThread to copy files in child nodes. replication purpose.
 * @author Raghav Babu
 * Date : 02/20/2016
 */
public class ServerPeersReplicaThread implements Runnable {

	String fileName;
	Input input;
	String childIpAddress;
	File dir;
	
	
	public ServerPeersReplicaThread(String fileName, Input input, String childIPAddress) {
		
		this.fileName = fileName;
		this.input = input;
		this.childIpAddress = childIPAddress;
		this.dir = ServerUtils.SERVER_DEST;
	}

	@Override
	public void run() {

		try {

			Socket serverFileSendSocket = null;

			serverFileSendSocket = new Socket(childIpAddress, 1234);		
			BufferedReader br = null;
			
			File f = new File(dir, fileName);
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
						str = input+":"+fileName;
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
			serverFileSendSocket.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
