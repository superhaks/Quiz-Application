package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mitpoly.quizcomp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultActivity extends Activity {

	String link = "http://172.16.52.11/codes/uploadResult.php";
	
	LinearLayout results;
	int marks;
	TextView label;
	AlertDialog.Builder builder;
	AlertDialog alert;
	int timeTaken;
	String startTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		label = (TextView) findViewById(R.id.textView1);
		marks = getIntent().getIntExtra("marks", -1);
		timeTaken = getIntent().getIntExtra("timeTaken", -1);
		startTime = getIntent().getStringExtra("startTime");
		label.setText("You scored " + marks + " marks\nin " + timeTaken + " seconds");
		results = (LinearLayout) findViewById(R.id.results);
		
		uploadInformation();
	}

	public void uploadInformation() {
		MyTask mt = new MyTask();
		mt.execute();
	}

	class MyTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			
			try {

				SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
				SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
				String timing = time.format(new Date()).toString();
				String day = date.format(new Date()).toString();

				String data = URLEncoder.encode("score", "UTF-8") + "="
						+ URLEncoder.encode("Marks: " + String.valueOf(marks), "UTF-8");
				data += "&" + URLEncoder.encode("name", "UTF-8") + "="
						+ URLEncoder.encode(FormActivity.name, "UTF-8");
				data += "&" + URLEncoder.encode("number", "UTF-8") + "="
						+ URLEncoder.encode(FormActivity.number, "UTF-8");
				data += "&" + URLEncoder.encode("subject", "UTF-8") + "="
						+ URLEncoder.encode(FormActivity.subject, "UTF-8");
				data += "&" + URLEncoder.encode("timing", "UTF-8") + "="
						+ URLEncoder.encode(timing, "UTF-8");
				data += "&" + URLEncoder.encode("day", "UTF-8") + "="
						+ URLEncoder.encode(day, "UTF-8");
				data += "&" + URLEncoder.encode("timeTaken", "UTF-8") + "="
						+ URLEncoder.encode(timeTaken + " seconds", "UTF-8");
				data += "&" + URLEncoder.encode("startTime", "UTF-8") + "="
						+ URLEncoder.encode(startTime, "UTF-8");
				
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
				return sb.toString();
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			results.setVisibility(View.INVISIBLE);
		}
	}
}