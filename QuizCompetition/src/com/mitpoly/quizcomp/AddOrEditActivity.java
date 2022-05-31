package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitpoly.quizcomp.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddOrEditActivity extends Activity {

	String reason;
	String idOfQuestion;

	ArrayList<String> data;
	ArrayAdapter<String> adapter;

	Spinner sub;
	
	EditText qBox;
	EditText aBox;
	EditText bBox;
	EditText cBox;
	EditText dBox;
	EditText answerBox;

	String question, optionA, optionB, optionC, optionD, correctAnswer,
			subject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.addoredit);

		sub = (Spinner) findViewById(R.id.spinner1);
		qBox = (EditText) findViewById(R.id.editText1);
		aBox = (EditText) findViewById(R.id.editText2);
		bBox = (EditText) findViewById(R.id.editText3);
		cBox = (EditText) findViewById(R.id.editText4);
		dBox = (EditText) findViewById(R.id.editText5);
		answerBox = (EditText) findViewById(R.id.editText6);

		data = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data);
		sub.setAdapter(adapter);

		reason = getIntent().getStringExtra("reason").toString();

		GetSubjects gs = new GetSubjects();
		gs.execute();

		if (reason.equals("edit")) {
			idOfQuestion = getIntent().getStringExtra("id").toString();
			GetDetailedQuestion gdq = new GetDetailedQuestion();
			gdq.execute();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.addoredit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (reason.equals("edit")) {
			EditQuestion eq = new EditQuestion();
			eq.execute();
			Toast.makeText(this, "Update this question", 1000).show();
		} else {
			AddQuestion aq = new AddQuestion();
			aq.execute();
			Toast.makeText(this, "Question Added!", 1000).show();
		}
		return super.onOptionsItemSelected(item);
	}

	class GetDetailedQuestion extends AsyncTask<String, Void, String> {

		String myJSON;
		String tableName = "";

		@Override
		protected String doInBackground(String... params) {

			try {
				String link = "http://172.16.52.11/codes/getSelectedQuestion.php";

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
					qBox.setText(innerObj.getString("question"));
					aBox.setText(innerObj.getString("optionA"));
					bBox.setText(innerObj.getString("optionB"));
					cBox.setText(innerObj.getString("optionC"));
					dBox.setText(innerObj.getString("optionD"));
					answerBox.setText(innerObj.getString("correctAnswer"));
					sub.setSelection(data.indexOf(innerObj.getString("subject")
							.toString()));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class GetSubjects extends AsyncTask<Void, Void, String> {

		String subjects;

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/getSubjects.php";

				URL url = new URL(link);
				URLConnection conn = url.openConnection();

				conn.setDoOutput(true);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));

				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
					break;
				}
				subjects = sb.toString();
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
				JSONObject obj = new JSONObject(subjects);
				JSONArray array = obj.getJSONArray("result");
				JSONObject innerObj;

				for (int i = 0; i < array.length(); i++) {
					innerObj = new JSONObject(array.get(i).toString());
					data.add(innerObj.getString("name"));
				}
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class AddQuestion extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				question = qBox.getText().toString();
				optionA = aBox.getText().toString();
				optionB = bBox.getText().toString();
				optionC = cBox.getText().toString();
				optionD = dBox.getText().toString();
				correctAnswer = answerBox.getText().toString();
				subject = sub.getSelectedItem().toString();
				
				System.out.println(question);
				System.out.println(optionA);
				System.out.println(optionB);
				System.out.println(optionC);
				System.out.println(optionD);
				System.out.println(correctAnswer);
				System.out.println(subject);
				
				
				String link = "http://172.16.52.11/codes/addQuestion.php";

				String data = URLEncoder.encode("question", "UTF-8") + "="
						+ URLEncoder.encode(question, "UTF-8");
				data += "&" + URLEncoder.encode("optionA", "UTF-8") + "="
						+ URLEncoder.encode(optionA, "UTF-8");
				data += "&" + URLEncoder.encode("optionB", "UTF-8") + "="
						+ URLEncoder.encode(optionB, "UTF-8");
				data += "&" + URLEncoder.encode("optionC", "UTF-8") + "="
						+ URLEncoder.encode(optionC, "UTF-8");
				data += "&" + URLEncoder.encode("optionD", "UTF-8") + "="
						+ URLEncoder.encode(optionD, "UTF-8");
				data += "&" + URLEncoder.encode("correctAnswer", "UTF-8") + "="
						+ URLEncoder.encode(correctAnswer, "UTF-8");
				data += "&" + URLEncoder.encode("subject", "UTF-8") + "="
						+ URLEncoder.encode(subject, "UTF-8");

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
					//break;
				}
				System.out.println("Response: " + sb.toString());
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			qBox.setText("");
			aBox.setText("");
			bBox.setText("");
			cBox.setText("");
			dBox.setText("");
			answerBox.setText("");
		}
	}

	class EditQuestion extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String newQuestion = qBox.getText().toString();
				String newOptionA= aBox.getText().toString();
				String newOptionB = bBox.getText().toString();
				String newOptionC = cBox.getText().toString();
				String newOptionD = dBox.getText().toString();
				String newCorrectAnswer = answerBox.getText().toString();
				String newSubject = sub.getSelectedItem().toString();
				
				System.out.println(newQuestion);
				System.out.println(newOptionA);
				System.out.println(newOptionB);
				System.out.println(newOptionC);
				System.out.println(newOptionD);
				System.out.println(newCorrectAnswer);
				System.out.println(newSubject);
				
				String link = "http://172.16.52.11/codes/updateQuestion.php";

				String data = URLEncoder.encode("question", "UTF-8") + "="
						+ URLEncoder.encode(newQuestion, "UTF-8");
				data += "&" + URLEncoder.encode("optionA", "UTF-8") + "="
						+ URLEncoder.encode(newOptionA, "UTF-8");
				data += "&" + URLEncoder.encode("optionB", "UTF-8") + "="
						+ URLEncoder.encode(newOptionB, "UTF-8");
				data += "&" + URLEncoder.encode("optionC", "UTF-8") + "="
						+ URLEncoder.encode(newOptionC, "UTF-8");
				data += "&" + URLEncoder.encode("optionD", "UTF-8") + "="
						+ URLEncoder.encode(newOptionD, "UTF-8");
				data += "&" + URLEncoder.encode("correctAnswer", "UTF-8") + "="
						+ URLEncoder.encode(newCorrectAnswer, "UTF-8");
				data += "&" + URLEncoder.encode("subject", "UTF-8") + "="
						+ URLEncoder.encode(newSubject, "UTF-8");
				data += "&" + URLEncoder.encode("idOfQuestion", "UTF-8") + "="
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
					//break;
				}
				System.out.println("Response: " + sb.toString());
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			qBox.setText("");
			aBox.setText("");
			bBox.setText("");
			cBox.setText("");
			dBox.setText("");
			answerBox.setText("");
		}
	}
}