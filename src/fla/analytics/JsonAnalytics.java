package fla.analytics;


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
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class JsonAnalytics {


	String postDelimiter = "=========================================================";
	String questionDelimiter = "........................................";
	String messageDelimiter = "---------------------------------------------------------";
	boolean anonymize = true;
	String anonymousSubstituteName = "ANONYM";

	long totalPostCount = 0;
	long emptyPostCount = 0;
	long totalReplyCount = 0;
	long totalWords = 0;
	HashMap<String, Long> wordCounts = new HashMap();

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
	/**
	 * process each input file
	 * @param inputFileName
	 * @return
	 */
	String formatToPhpAndUpdateStat(String inputFileName)
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
			int sampleSize = 2;
			//iterating for each post
			for(int i = 0;i<data.size();i++)
			{
				totalPostCount++;
				JSONObject threadObj = (JSONObject) data.get(i);
				String question = (String) threadObj.get("message");
				String forStats = question;
				//updateCountStats(question);
				String asker = (String)((JSONObject)threadObj.get("from")).get("name");
				String createdTime = (String) threadObj.get("created_time");

				JSONObject commentsObj = (JSONObject) threadObj.get("comments");
				if(commentsObj != null)
				{
					String toDisplay =  "On " + createdTime.substring(0, 10) + ", [ " + asker + " ]: " + (question);

					//System.out.println(anonymizeString(toDisplay, asker));
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
					//System.out.println(questionDelimiter);

					String mesgAcc = "";

					for(int j = 0;j<commentsData.size();j++)
					{
						totalReplyCount++;
						mesgAcc += ",\"";
						JSONObject commentObj = (JSONObject) commentsData.get(j);
						String commenter = (String)((JSONObject)commentObj.get("from")).get("name");
						String commentCreatedTime = (String) commentObj.get("created_time");
						String comment = (String) commentObj.get("message");
						forStats += (" " + comment); 
						//updateCountStats(comment);
						String rawDisplay =  "On " + commentCreatedTime.substring(0, 10) + ", [ " + commenter + " ]: " + comment;
						//System.out.println(anonymizeString(rawDisplay, asker));
						mesgAcc += anonymizeString(StringEscapeUtils.escapeJava(comment) + "     --" + "On " + commentCreatedTime.substring(0, 10) + ", [ " + commenter + " ]\"",asker);
					}
					updateCountStatsForPost(forStats);
					//System.out.println(postDelimiter);
					acc += (mesgAcc + ")");
				}
				else
				{
					emptyPostCount++;
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
		String outputDirName = args[0];
		String outputFileName = outputDirName + "/tempout1.txt";
		String statsFile = outputDirName + "/stats.txt";

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
			outputAcc += formatToPhpAndUpdateStat(args[i]);
		}
		System.out.println("---xxxx-----");
		//System.out.println(outputAcc);
		BufferedWriter bw  = createAndGetBufWriter(outputFileName);		
		bw.write(prefix + outputAcc + suffix);
		bw.close();

		BufferedWriter bwStat = createAndGetBufWriter(statsFile);
		printAllStats(bwStat);
	}

	void printAllStats(BufferedWriter statBw) throws IOException
	{
		statBw.write("totalPostCount\tTotal posts:\t" + (int) totalPostCount + "\n");
		statBw.write("totalReplyCount\tTotal replies:\t" + (int) totalReplyCount + "\n");
		statBw.write("emptyPostCount\tPosts with no replies:\t" + (int) emptyPostCount + "\n");

		HashMap sortedWordCount = sortByValue(wordCounts);
		for ( Object entry1 : sortedWordCount.entrySet()) {
			Map.Entry<String, Long> entry = (Map.Entry<String, Long>) entry1;
			String key = entry.getKey();
			Long value = entry.getValue();
			double predD = (((double)value/(double)totalPostCount)*100.0);
			//DecimalFormat df = new DecimalFormat("#0.");
			statBw.write("wordCount\t" + key + "\t" + Math.round(predD) + "\n" );
		}

		statBw.close();
	}
	static HashMap sortByValue(HashMap map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		HashMap result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry)it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	} 

	StopWords stopWords = new StopWords();
	void updateCountStatsForPost(String input)
	{
		if(input == null)
		{
			return;
		}
		HashSet<String> seenWords = new HashSet<String>(); 
		String inputCpy = input.replaceAll("[!?,]", "");
		String[] terms = inputCpy.split("\\s+");
		//String terms[] = input.split(" ");
		for(int i = 0; i < terms.length ; i++)
		{
			totalWords++;
			String term = terms[i].toLowerCase().replace(".", "");;
			if(stopWords.isStopWord(term))
			{
				continue;
			}
			if(seenWords.contains(term))
			{
				continue;
			}
			else
			{
				seenWords.add(term);
			}
			if(wordCounts.get(term) == null)
			{
				wordCounts.put(term, new Long(1));
			}
			else
			{
				Long oldCount = wordCounts.get(term);
				wordCounts.put(term, oldCount + 1);
			}
		}
	}

	BufferedWriter createAndGetBufWriter(String opFileName) throws IOException
	{
		File file = new File(opFileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		return bw;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new JsonAnalytics().FormatMultileFilesAndWrite(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}