package fla.test;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class FormatJsonToPhpbb {


	String postDelimiter = "=========================================================";
	String questionDelimiter = "........................................";
	String messageDelimiter = "---------------------------------------------------------";
	boolean anonymize = true;
	String anonymousSubstituteName = "ANONYM";

	String anonymizeString(String input,String nameToBeAnonymized)
	{
		//extract name terms
		String nameTerms[] = nameToBeAnonymized.split(" ");

		String textTerms[] = input.split(" ");
		String output = null;
		//go through input, replace name term sequences with substitute
		boolean lastTermWasName = false;
		for(int i = 0; i < textTerms.length;i++)
		{
			boolean foundName = false;
			for(int j = 0; j < nameTerms.length;j++)
			{
				String processedTextTerm = textTerms[i].toLowerCase().replaceAll("[,.:;]+", "");
				//System.out.println(processedTextTerm);
				if(processedTextTerm.compareTo(nameTerms[j].toLowerCase()) == 0)
				{				
					foundName = true;
				}
			}
			if((foundName == true) && (anonymize == true))
			{
				if(lastTermWasName == false)
				{
					if(output == null)
					{
						output = anonymousSubstituteName;
					}
					else
					{
						output = output + " " + anonymousSubstituteName;
					}
				}
				lastTermWasName = true;
			}
			else
			{
				if(output == null)
				{
					output = textTerms[i];
				}
				else
				{
					output = output + " " + textTerms[i];
				}
				lastTermWasName = false;
			}
		}

		return output;
	}
	//output e.g., array("new data's \"post\"1","comme\nnt11","comment12","comment13"),array(
	String formatToPhpString(String inputFileName)
	{
		URL fbUrl;
		String acc = null;
		try {
			//			fbUrl = new URL("https://graph.facebook.com/452137104798374/feed?access_token=<YOUR TOKEN>&limit=1000");
			//			URLConnection fb = fbUrl.openConnection();
			//			BufferedReader in = new BufferedReader(new InputStreamReader(fb.getInputStream()));

			String inputLine;

			File inputFile = new File(inputFileName);
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
					String toDisplay =  "On " + createdTime.substring(0, 10) + ", [ " + asker + " ]: " + (question);

					System.out.println(anonymizeString(toDisplay, asker));
					if(acc == null)
					{
						acc = "";
					}
					else
					{
						acc += ",";
					}
					acc += "array(\"";
					acc += anonymizeString(StringEscapeUtils.escapeJava(question) + "     --" + "On " + createdTime.substring(0, 10) + ", [ " + asker + " ]\"",asker);

					JSONArray commentsData = (JSONArray) commentsObj.get("data");
					System.out.println(questionDelimiter);

					String mesgAcc = "";

					for(int j = 0;j<commentsData.size();j++)
					{
						mesgAcc += ",\"";
						JSONObject commentObj = (JSONObject) commentsData.get(j);
						String commenter = (String)((JSONObject)commentObj.get("from")).get("name");
						String commentCreatedTime = (String) commentObj.get("created_time");
						String comment = (String) commentObj.get("message");
						String rawDisplay =  "On " + commentCreatedTime.substring(0, 10) + ", [ " + commenter + " ]: " + comment;
						System.out.println(anonymizeString(rawDisplay, asker));
						mesgAcc += anonymizeString(StringEscapeUtils.escapeJava(comment) + "     --" + "On " + commentCreatedTime.substring(0, 10) + ", [ " + commenter + " ]\"",asker);
					}
					System.out.println(postDelimiter);
					acc += (mesgAcc + ")");
				}

				if(i > sampleSize)
				{
					//break;
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
		return acc;
	}

	public void FormatMultileFilesAndWrite(String[] args) throws IOException
	{
		String outputFileName = args[0];
		String prefix = "<?php \n $allArchive = array(";
		String suffix = "); \n?>";
		String outputAcc = null;
		for(int i = 1; i< args.length;i++)
		{
			if(outputAcc == null)
			{
				outputAcc = "";
			}
			else
			{
				outputAcc += ",";
			}
			outputAcc += ((new FormatJsonToPhpbb()).formatToPhpString(args[i]));
		}
		System.out.println("---xxxx-----");
		System.out.println(outputAcc);
		File file = new File(outputFileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(prefix + outputAcc + suffix);
		bw.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new FormatJsonToPhpbb().FormatMultileFilesAndWrite(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}