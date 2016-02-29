import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ValueToServerIPXmlParser {


	/**
	 * Class ValueToServerIPXmlParser
	 * parse XML file.
	 * @author Raghav Babu
	 * Date : 02/20/2016
	 */

	public Map<Integer, String> parseXML(){

		Map<Integer, String> hashValueToIpMap = new HashMap<Integer, String>();

		try {	
			File inputFile = new File("ValueToServerIP.xml");
			DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(inputFile);

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nodeList = doc.getElementsByTagName("Server_Node");

			for (int i = 0; i < nodeList.getLength(); i++) {

				Node nNode = nodeList.item(i);

				String id = null;
				String ipAddress = null;

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					id = eElement.getAttribute("id");
					ipAddress = eElement.getAttribute("IPAddress");
					hashValueToIpMap.put(Integer.parseInt(id), ipAddress);

					//System.out.println("Id : "  + id+ ", IP : "+ipAddress);
				}
			}
			NodeList clientList = doc.getElementsByTagName("Client_Node");

			for (int i = 0; i < clientList.getLength(); i++) {

				Node nNode = clientList.item(i);

				String ipAddress = null;

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					ipAddress = eElement.getAttribute("IPAddress");
					ServerUtils.CLIENT_IP = ipAddress;
					//System.out.println("Id : "  + id+ ", IP : "+ipAddress);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return hashValueToIpMap;
	}
}


