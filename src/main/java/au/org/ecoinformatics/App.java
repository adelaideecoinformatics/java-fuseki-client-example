package au.org.ecoinformatics;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class App {
	
	private static final String endpoint = "http://beast.ecoinformatics.org.au:3030/ds/query";
	
	public static void main(String[] args) {
		new App().run();
	}

	private void run() {
		try {
			String query1 = getQuery("query1.rq");
			List<String[]> someRecords = performQuery(query1);
			String firstSubject = someRecords.get(0)[0];
			String query2Template = getQuery("query2.rq");
			String query2 = query2Template.replace("%SUBJECT_PLACEHOLDER%", "<" + firstSubject + ">");
			List<String[]> finalOutcome = performQuery(query2);
			System.out.println(String.format("The subject '%s' is of type '%s'", firstSubject, finalOutcome.get(0)[0]));
		} catch (Throwable e) {
			throw new RuntimeException("Something exploded!", e);
		}
	}
	
	private List<String[]> performQuery(String query) throws Throwable {
		String urlEncodedQuery = URLEncoder.encode(query, "UTF-8");
		try {
			final boolean nobodyLikesYouHeader = true;
			List<String[]> result = postForCsv("query=" + urlEncodedQuery, nobodyLikesYouHeader);
//			for (String[] curr : csv) {
//				System.out.println(Arrays.toString(curr));
//			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Failed to perform query", e);
		}
	}

	private String getQuery(String fileName) {
		InputStream sparqlIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("au/org/ecoinformatics/" + fileName);
		OutputStream out = new ByteArrayOutputStream();
		try {
			copy(sparqlIS, out);
			return out.toString();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read file " + fileName, e);
		}
	}

	private List<String[]> postForCsv(String formData, boolean isDiscardHeader) throws Exception {
//		final String acceptMime = "application/sparql-results+json"; // JSON
		final String acceptMime = "text/csv"; // CSV
		List<String[]> result = new LinkedList<>();
		URL obj = new URL(endpoint);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept", acceptMime);
		con.setRequestProperty("Accept-Language", "en-AU,en-GB;q=0.8,en-US;q=0.6,en;q=0.4");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(formData);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		boolean isSuccess = responseCode == 200;
		if (!isSuccess) {
			throw new RuntimeException("Failed to perform query. HTTP status code = " + responseCode);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		boolean isFirst = true;
		while ((inputLine = in.readLine()) != null) {
			if (isDiscardHeader && isFirst) {
				isFirst = false;
				continue;
			}
			result.add(inputLine.split(","));
		}
		in.close();
		return result;
	}

	public static final int BUFFER_SIZE = 4096;
	public static int copy(InputStream in, OutputStream out) throws IOException {
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}
}
