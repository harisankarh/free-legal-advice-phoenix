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



public class FormatJson {

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

			String postDelimiter = "=========================================================";
			String questionDelimiter = "........................................";
			String messageDelimiter = "---------------------------------------------------------";

			File inputFile = new File(args[0]);
			FileInputStream fis = new FileInputStream(inputFile);

			String jsonTxt = IOUtils.toString(fis);
			Object obj = JSONValue.parse(jsonTxt);
			JSONObject obj2 = (JSONObject) obj;
			JSONArray data = (JSONArray) obj2.get("data");
			int totalPosts = data.size();
			int emptyPosts = 0;
			int sampleSize = 2;
			for(int i = 0;i<data.size();i++)
			{
				JSONObject threadObj = (JSONObject) data.get(i);
				String question = (String) threadObj.get("message");
				String asker = (String)((JSONObject)threadObj.get("from")).get("name");
				String createdTime = (String) threadObj.get("created_time");

				JSONObject commentsObj = (JSONObject) threadObj.get("comments");
				if(commentsObj != null)
				{
					System.out.println( "On " + createdTime.substring(0, 10) + ", [ " + asker + " ]: " + question);
					JSONArray commentsData = (JSONArray) commentsObj.get("data");
					System.out.println(questionDelimiter);
					for(int j = 0;j<commentsData.size();j++)
					{
						JSONObject commentObj = (JSONObject) commentsData.get(j);
						String commenter = (String)((JSONObject)commentObj.get("from")).get("name");
						String commentCreatedTime = (String) commentObj.get("created_time");
						String comment = (String) commentObj.get("message");
						System.out.println( "On " + commentCreatedTime.substring(0, 10) + ", [ " + commenter + " ]: " + comment);
					}
					System.out.println(postDelimiter);
				}

				if(i > sampleSize)
				{
//					break;
				}

			}
//			System.out.println("Vals = " + totalPosts +  " , "  + emptyPosts);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}