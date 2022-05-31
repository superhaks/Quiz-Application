package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitpoly.quizcomp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ManageQuestionsActivity extends Activity implements
		OnItemClickListener {

	ListView UI;
	ArrayList<String> data;
	ArrayList<String> ids;
	ArrayAdapter<String> adapter;
	String idOfQuestionToBeDeleted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage);

		UI = (ListView) findViewById(R.id.listView1);
		data = new ArrayList<String>();
		ids = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, R.layout.row, data);
		UI.setAdapter(adapter);
		registerForContextMenu(UI);

		UI.setOnItemClickListener(this);

		GetQuestions gq = new GetQuestions();
		gq.execute();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add("Edit");
		menu.add("Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (item.getTitle().toString().equals("Edit")) {
			Intent i1 = new Intent(this, AddOrEditActivity.class);
			i1.putExtra("id", ids.get(info.position));
			i1.putExtra("reason", "edit");
			startActivity(i1);
		} else {
			idOfQuestionToBeDeleted = ids.get(info.position);
			ids.remove(info.position);
			data.get(info.position);
			DeleteQuestion dq = new DeleteQuestion();
			dq.execute();
		}
		return super.onContextItemSelected(item);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent i = new Intent(this, AddOrEditActivity.class);
		i.putExtra("reason", "add");
		startActivity(i);
		return super.onOptionsItemSelected(item);
	}

	class GetQuestions extends AsyncTask<String, Void, String> {

		String myJSON;
		String tableName = "";

		@Override
		protected String doInBackground(String... params) {

			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpPost httppost;

			httppost = new HttpPost(
					"http://172.16.52.11/codes/readQuestionTitles.php");

			httppost.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			String result = null;
			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
			} catch (Exception e) {
				System.out.println("Error!");
			}

			finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception squish) {
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			myJSON = result;

			System.out.println("JSON: ");
			System.out.println(myJSON);

			try {
				JSONObject obj = new JSONObject(myJSON);
				JSONArray array = obj.getJSONArray("result");
				JSONObject innerObj;

				for (int i = 0; i < array.length(); i++) {
					innerObj = new JSONObject(array.get(i).toString());
					data.add(innerObj.getString("question"));
					ids.add(innerObj.getString("id"));
				}
				adapter.notifyDataSetChanged();

				System.out.println("ids: ");
				System.out.println(ids);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class DeleteQuestion extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {


			try {
				String link = "http://172.16.52.11/codes/deleteQuestion.php";

				String data = URLEncoder.encode("id", "UTF-8") + "="
						+ URLEncoder.encode(idOfQuestionToBeDeleted, "UTF-8");

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
			} catch (Exception e) {
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			adapter = new ArrayAdapter<String>(ManageQuestionsActivity.this, R.layout.row, data);
			UI.setAdapter(adapter);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent i1 = new Intent(this, ViewQuestionActivity.class);
		i1.putExtra("id", ids.get(position));
		startActivity(i1);

	}
}