package fla.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class GraphReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		URL fbUrl;

		try {
//			fbUrl = new URL("https://graph.facebook.com/452137104798374/feed?access_token=<YOUR TOKEN>&limit=1000");
//			URLConnection fb = fbUrl.openConnection();
//			BufferedReader in = new BufferedReader(new InputStreamReader(fb.getInputStream()));
			
			String inputLine;
			
			File inputFile = new File(args[0]);
			FileInputStream fis = new FileInputStream(inputFile);
			
			String jsonTxt = IOUtils.toString(fis);
			Object obj = JSONValue.parse(jsonTxt);
			JSONObject obj2 = (JSONObject) obj;
			JSONArray data = (JSONArray) obj2.get("data");
			int totalPosts = data.size();
			int emptyPosts = 0;
			
			for(int i = 0;i<data.size();i++)
			{
				JSONObject threadObj = (JSONObject) data.get(i);
				String message = (String) threadObj.get("message");
				JSONObject commentsObj = (JSONObject) threadObj.get("comments");
				long count = (Long) commentsObj.get("count");
				if(count == 0)
				{
					System.out.println(message);
					System.out.println("Next");
					emptyPosts++;
				}
				
			}
			System.out.println("Vals = " + totalPosts +  " , "  + emptyPosts);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}