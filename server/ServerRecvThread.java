import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;




/**
 * Class ServerRecvThread to receive files from client and communication between servers.
 * Receives files from the client.
 * @author Raghav Babu
 * Date : 02/20/2016
 *
 */
public class ServerRecvThread implements Runnable {

	private DistributedServer server;
	private InetSocketAddress boundPort = null;
	private final int port = 1234;
	private ServerSocket serverSocket;
	File file;
	File dir;
	FileWriter fw = null;
	BufferedWriter bw = null;
	boolean exitFlag = false;
	String trailPath;

	public ServerRecvThread(DistributedServer server) {
		this.server = server;
		this.dir = ServerUtils.SERVER_DEST;
		this.trailPath = "";
	}

	public void run() {

		try {

			initServerSocket();

			while(true) {

				Socket connectionSocket = serverSocket.accept();
				boolean flag = false;
				
				
				BufferedReader br = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

				String str = null;
				
				if(!flag){

					str = br.readLine();

					String[] inputString = str.split(":");
					String fileorNodeName = inputString[1];
					int nodeLevel = 0;
					int nodePos = 0;
					String trailPath = null;
					
					if(inputString.length > 2 && inputString[2] != null &&  inputString[3] != null){
						nodeLevel = Integer.parseInt(inputString[2]);
						nodePos = Integer.parseInt(inputString[3]);
					}
					
					if(inputString.length > 4  && inputString[4] != null)
						trailPath = inputString[4];
					else
						trailPath = "";
					
					//System.out.println(fileName);
					
					if(inputString[0].equals(Input.FILE.toString() ) )
					{

						if (!dir.exists()) {

							if (dir.mkdir()) {
								System.out.println(ServerUtils.ServerName+ ": Directory is created!");
							} 

							else {
								System.out.println("Directory creation failure!!");
							}
						}

						file = new File(dir, fileorNodeName);
						System.out.println(ServerUtils.ServerName+ ": New File : "+fileorNodeName+ " added to directory : "+dir.getName());
						fw = new FileWriter(file);
						
						//initializing populartiy count.
						if(!ServerUtils.filePopularityMap.containsKey(fileorNodeName))
							ServerUtils.filePopularityMap.put(fileorNodeName, 0);
						
					}
					//request to get a file from leaf node.
					else if(inputString[0].equals(Input.REQ.toString() ) ){
						
						String ip = connectionSocket.getInetAddress().toString();
						
						if(ServerUtils.ipToValueMap.get(ip.substring(1,ip.length())) != null)
							System.out.println( ServerUtils.ServerName+ ": Received "+inputString[0]+" for "+fileorNodeName+ " from : "+ServerUtils.ipToValueMap.get(ip.substring(1, ip.length())) );
						else
							System.out.println(ServerUtils.ServerName+ ": Received "+inputString[0]+" for "+fileorNodeName+ " from client : "+ServerUtils.CLIENT_IP);
						
						//initializing populartiy count.
						if(!ServerUtils.filePopularityMap.containsKey(fileorNodeName))
							ServerUtils.filePopularityMap.put(fileorNodeName, 0);
						
						server.serveRequest(fileorNodeName, dir, ServerUtils.CLIENT_IP, nodeLevel, nodePos, trailPath);
					}
					
					// exit and close all servers.
					else if(inputString[0].equals(Input.EXIT.toString() )){
						System.out.println( ServerUtils.ServerName+ ": Closing server node : "+fileorNodeName);
						exitFlag = true;
						break;
					}
					
				}

					while( (str = br.readLine()) != null ) {

						bw = new BufferedWriter(fw);
						try {

							bw.write(str+"\n");
							bw.flush();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}	
				}
			serverSocket.close();
			if(exitFlag)
				System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}


	}


	/**
	 * method which initialized and bounds a server socket to a port.
	 * @return void.
	 */
	private void initServerSocket()
	{
		boundPort = new InetSocketAddress(port);
		try
		{
			serverSocket = new ServerSocket(port);

			if (serverSocket.isBound())
			{
				System.out.println("Server bound to data port " + serverSocket.getLocalPort() + " and is ready...");
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to initiate socket.");
		}

	}
}


