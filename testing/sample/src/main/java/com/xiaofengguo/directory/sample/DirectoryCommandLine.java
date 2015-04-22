package com.xiaofengguo.directory.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;

public class DirectoryCommandLine {
	private static final Logger logger = Logger
			.getLogger("com.xiaofengguo.directory.sample.DirectoryCommandLine");

	private static String CLIENT_ID = "336775904940-jjmrjdsuvekv8nbaot9h53farkupkjrn.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "bU1m1AhdwHjwp49stNJqUkPi";

	private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

	public static void main(String[] args) throws IOException {
		logger.info("xfguo 0");
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		logger.info("xfguo 1");
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
				Arrays.asList(DirectoryScopes.ADMIN_DIRECTORY_USER))
				.setAccessType("online").setApprovalPrompt("auto").build();

		logger.info("xfguo 2");
		String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI)
				.build();
		System.out
				.println("Please open the following URL in your browser then type the authorization code:");
		System.out.println("  " + url);
		System.out.println("Enter authorization code:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();
		logger.info("xfguo 3");

		GoogleTokenResponse response = flow.newTokenRequest(code)
				.setRedirectUri(REDIRECT_URI).execute();
		GoogleCredential credential = new GoogleCredential()
				.setFromTokenResponse(response);

		logger.info("xfguo 4");
		// Create a new authorized API client
		Directory service = new Directory.Builder(httpTransport, jsonFactory,
				credential).setApplicationName("DirectoryCommandLine").build();

		List<User> allUsers = new ArrayList<User>();
		Directory.Users.List request = service.users().list()
				.setCustomer("my_customer");

		// Get all users
		do {
			try {
				Users currentPage = request.execute();
				allUsers.addAll(currentPage.getUsers());
				request.setPageToken(currentPage.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);

		// Print all users
		for (User currentUser : allUsers) {
			System.out.println(currentUser.getPrimaryEmail());
		}
	}
}
