package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitpoly.quizcomp.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class ViewQuestionActivity extends Activity {

	String link = "http://172.16.52.11/codes/getSelectedQuestion.php";
	
	TextView qLabel;
	TextView aLabel;
	TextView bLabel;
	TextView cLabel;
	TextView dLabel;
	TextView correctLabel;
	String idOfQuestion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_questions);

		idOfQuestion = getIntent().getStringExtra("id");
		
		System.out.println("Received: " + idOfQuestion);
		
		qLabel = (TextView) findViewById(R.id.question);
		aLabel = (TextView) findViewById(R.id.optiona);
		bLabel = (TextView) findViewById(R.id.optionb);
		cLabel = (TextView) findViewById(R.id.optionc);
		dLabel = (TextView) findViewById(R.id.optiond);
		correctLabel = (TextView) findViewById(R.id.correct);

		GetQuestions gq = new GetQuestions();
		gq.execute();

	}

	class GetQuestions extends AsyncTask<String, Void, String> {

		String myJSON;
		String tableName = "";

		@Override
		protected String doInBackground(String... params) {

			try {

				String data = URLEncoder.encode("idOfQuestion", "UTF-8") + "="
						+ URLEncoder.encode(idOfQuestion, "UTF-8");

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
			super.onPostExecute(result);

			myJSON = result;

			try {
				JSONObject obj = new JSONObject(myJSON);
				JSONArray array = obj.getJSONArray("result");
				JSONObject innerObj;

				for (int i = 0; i < array.length(); i++) {
					innerObj = new JSONObject(array.get(i).toString());
					qLabel.setText(innerObj.getString("question"));
					aLabel.setText(innerObj.getString("optionA"));
					bLabel.setText(innerObj.getString("optionB"));
					cLabel.setText(innerObj.getString("optionC"));
					dLabel.setText(innerObj.getString("optionD"));
					correctLabel.setText(innerObj.getString("correctAnswer"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
