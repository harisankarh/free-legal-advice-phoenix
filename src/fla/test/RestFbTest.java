package fla.test;

import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Page;
import com.restfb.types.User;

public class RestFbTest {

	public static void main(String[] args) {
		FacebookClient facebookClient;
		String accessToken = "CAACEdEose0cBAN1kBCNr1jkFbZAXAIBZBETdei1Od9YZCAT2q7PMkvslHI2mzLVEVc6jrEQ9LfBouq6K4hjTN4dzGXJR6dMcoSJCyeEewBWhExZCZB0RwUtQSGjDbu0LEsKvYRNFMrjz0VQMqe0DqOjxw1ghZC3hVyxYvZAt8AjxXiQhx0ZC7494i7DGHvREt9tV21wYBPAoexFZA6pYOZA7Y6";
		facebookClient = new DefaultFacebookClient(accessToken);

	
//		Connection<JsonObject> myFeed = facebookClient.fetchConnection("452137104798374/members", JsonObject.class, Parameter.with("limit", 5000));
//
//		int ctr = 0;
//		for (List<JsonObject> myFeedConnectionPage : myFeed)
//		{
//			for (JsonObject obj : myFeedConnectionPage)
//			{
//				System.out.println(obj);
//				System.out.println(obj.get("name"));
//				ctr++;
//			}
//		}
//		System.out.println(ctr);

		
	}

}
