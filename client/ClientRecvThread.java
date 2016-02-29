

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class ClientRecvThread to receive result from server.
 * @author Raghav Babu
 * Date : 02/20/2016
 */
public class ClientRecvThread implements Runnable {

	private InetSocketAddress boundPort = null;
	private final int port = 1300;
	private ServerSocket clientRecvSocket;
	File dir;
	File file;
	String str;
	FileWriter fw = null;
	BufferedWriter bw = null;
	
	 public ClientRecvThread() {
		this.dir = ClientUtils.CLIENT_DEST;
	}
	
	@Override
	public void run() {
		
		try {

			initServerSocket();

			while(true) {

				Socket connectionSocket = clientRecvSocket.accept();
				boolean flag = false;
				
				BufferedReader br = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

				String str = null;
				String fileName = null;
				String trailPath = null;
				
				if(!flag){

					str = br.readLine();

					String[] inputString = str.split(":");
					 fileName = inputString[1];
					 trailPath = inputString[2];
					//System.out.println(fileName);
					
						if (!dir.exists()) {

							if (dir.mkdir()) {
								System.out.println("Client Dest Directory is created!");
							} 

							else {
								System.out.println("Client Dest Directory creation failure!");
							}
						}

						file = new File(dir, fileName);
						fw = new FileWriter(file);
						flag = true;
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
					String ip = connectionSocket.getInetAddress().toString();
					System.out.println("Received file "+fileName+" from "+ClientUtils.ipToValueMap.get(ip.substring(1,ip.length())));
					System.out.println("File Retrieval path : "+trailPath);
			}	
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
			clientRecvSocket = new ServerSocket(port);

			if (clientRecvSocket.isBound())
			{
				System.out.println("Client bound to data port " + clientRecvSocket.getLocalPort() + " and is ready for receiving...");
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to initiate socket.");
		}

	}

}
