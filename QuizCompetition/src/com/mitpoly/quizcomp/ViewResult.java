package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitpoly.quizcomp.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewResult extends Activity {
	
	String link = "http://172.16.52.11/codes/getresults.php";
	
	ArrayList<String> data;
	ArrayAdapter<String> adapter;
	ListView UI;
	String results;
	String subject;
	String quizDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewresults);
		
		subject = getIntent().getStringExtra("subject");
		quizDay = getIntent().getStringExtra("quizDay");
		
		UI = (ListView) findViewById(R.id.listView1);
		data = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
		UI.setAdapter(adapter);
		
		GetResult gr = new GetResult();
		gr.execute();
	}
	
	class GetResult extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String data = URLEncoder.encode("subject", "UTF-8") + "="
						+ URLEncoder.encode(subject, "UTF-8");
				data += "&" + URLEncoder.encode("quizDay", "UTF-8") + "="
						+ URLEncoder.encode(quizDay, "UTF-8");
				
				URL url = new URL(link);
				URLConnection conn = url.openConnection();

				conn.setDoOutput(true);

				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());

				wr.write(data);
				wr.flush();
				
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));

				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line);
					break;
				}
				results = sb.toString();
				return sb.toString();
			} catch (Exception e) {
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			try {
				JSONObject obj = new JSONObject(results);
				JSONArray array = obj.getJSONArray("result");
				JSONObject innerObj;

				String name; String score; String timing;
				String day; String timeTaken; String startTime;
				
				
				for (int i = 0; i < array.length(); i++) {
					innerObj = new JSONObject(array.get(i).toString());
					score = innerObj.getString("score");
					name =  innerObj.getString("name");
					timing = innerObj.getString("timing");
					day = innerObj.getString("day");
					timeTaken = innerObj.getString("timeTaken");
					startTime = innerObj.getString("startTime");
					
					data.add(name + " : " + score + " : " + timeTaken + "\nDuration: " + startTime + " to " + timing + ".\nDate: " + day);
				}
				adapter.notifyDataSetChanged();
				
				createPDF();
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void createPDF() {
		try {
            
            Document document = new Document(PageSize.A2);
            String filename = Environment.getExternalStorageDirectory() + "/result_"+ new SimpleDateFormat("hh_mm_ss").format(new java.util.Date()).toString() +".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
                    
            Paragraph p = new Paragraph("Score Sheet\n\n\nSubject: " + subject + "\n\nDate: " + quizDay + "\n\n\n\n");
            p.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p);
            
            PdfPTable table = new PdfPTable(1);
            table.addCell("Score Sheet");
            
            for (int i = 0; i < data.size(); i++) {
                table.addCell(data.get(i));
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            System.out.println("Error!" + e.getMessage());
        }
	}
}