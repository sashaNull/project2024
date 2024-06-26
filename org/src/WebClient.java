import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class WebClient {
	
	private String host;
	private int port;
	
	public WebClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Make an HTTP request to the RESTful API at the object's host:port
	 * The request will be of the form http://[host]:[port]/[resource]?
	 * followed by key=value& for each of the entries in the queryParams map.
	 * @return the JSON object returned by the API if successful, null if unsuccessful
	 */
	public String makeRequest(String resource, Map<String, Object> queryParams) {
		
		try {
		
			String request = "http://" + host + ":" + port + resource + "?";
			
			for (String key : queryParams.keySet()) {
				request += key + "=" + queryParams.get(key).toString().replaceAll(" ", "%20") + "&";
				
			}
			
			URL url = new URL(request);
		    url.openConnection(); 
	    	Scanner in = new Scanner(url.openStream());
	    	String response = "";
	    	while (in.hasNext()) {
	    		String line = in.nextLine();
	    		response += line;
	    	}

	    	//System.out.println("Web Client response: " + response);
	    	
	    	in.close();

	    	return response;
			
			
		}
		catch (Exception e) {
			return null;
		}
		
	}


}
