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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ManageSubjectsActivity extends Activity {

	ListView UI;
	ArrayList<String> data;
	ArrayAdapter<String> adapter;
	String oldSubject;
	String newSubject;
	String subjectToAdd;
	String subjectToRemove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage);

		GetSubjects gs = new GetSubjects();
		gs.execute();

		UI = (ListView) findViewById(R.id.listView1);
		data = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, R.layout.row, data);
		UI.setAdapter(adapter);
		registerForContextMenu(UI);
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

		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		oldSubject = data.get(info.position);
		newSubject = "";

		if (item.getTitle().toString().equals("Edit")) {
			AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
			a_builder.setTitle("Edit Subject");

			View dialogBox = getLayoutInflater().inflate(
					R.layout.custom_dialog, null);

			a_builder.setView(dialogBox);

			final EditText box = (EditText) dialogBox
					.findViewById(R.id.editText2);

			box.setHint("");
			box.setInputType(InputType.TYPE_CLASS_TEXT);
			box.setText(oldSubject);

			a_builder.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							if (!box.getText().toString().equals("")) {

								newSubject = box.getText().toString();
								data.set(data.indexOf(oldSubject), newSubject);
								adapter.notifyDataSetChanged();

								UpdateSubject us = new UpdateSubject();
								us.execute();

								arg0.dismiss();
							} else {
								arg0.dismiss();
							}
						}
					});

			a_builder.setNegativeButton("Back",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
						}
					});

			AlertDialog alert = a_builder.create();
			alert.show();
		} else {

			subjectToRemove = data.get(info.position);
			RemoveSubject rs = new RemoveSubject();
			rs.execute();
			data.remove(subjectToRemove);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
		a_builder.setTitle("Add Subject");

		View dialogBox = getLayoutInflater().inflate(R.layout.custom_dialog,
				null);

		a_builder.setView(dialogBox);

		final EditText box = (EditText) dialogBox.findViewById(R.id.editText2);

		box.setHint("Enter subject name");
		box.setInputType(InputType.TYPE_CLASS_TEXT);

		a_builder.setPositiveButton("Add",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (!box.getText().toString().equals("")) {

							subjectToAdd = box.getText().toString();
							data.add(subjectToAdd);

							AddSubject as = new AddSubject();
							as.execute();

							adapter.notifyDataSetChanged();

							arg0.dismiss();
						} else {
							arg0.dismiss();
						}
					}
				});

		a_builder.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				});

		AlertDialog alert = a_builder.create();
		alert.show();

		return super.onOptionsItemSelected(item);
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

	class UpdateSubject extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/subject_updater.php";

				String data = URLEncoder.encode("oldSubject", "UTF-8") + "="
						+ URLEncoder.encode(oldSubject, "UTF-8");
				data += "&" + URLEncoder.encode("newSubject", "UTF-8") + "="
						+ URLEncoder.encode(newSubject, "UTF-8");

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
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
	}

	class AddSubject extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/addSubject.php";

				String data = URLEncoder.encode("subjectToAdd", "UTF-8") + "="
						+ URLEncoder.encode(subjectToAdd, "UTF-8");

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
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
	}

	class RemoveSubject extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/removeSubject.php";

				String data = URLEncoder.encode("subjectToRemove", "UTF-8")
						+ "=" + URLEncoder.encode(subjectToRemove, "UTF-8");

				System.out.println("Im removing: " + subjectToRemove);
				
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
				e.printStackTrace();
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
		}
	}
}