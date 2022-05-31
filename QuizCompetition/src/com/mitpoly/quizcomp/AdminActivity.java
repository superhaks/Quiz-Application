package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.mitpoly.quizcomp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AdminActivity extends Activity implements OnClickListener {

	String passwordSetByMe = "";
	String timerSetByMe = "";
	Button b1, b2, b3, b4, b5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin);

		b1 = (Button) findViewById(R.id.button1);
		b2 = (Button) findViewById(R.id.button2);
		b3 = (Button) findViewById(R.id.button3);
		b4 = (Button) findViewById(R.id.button4);
		b5 = (Button) findViewById(R.id.button5);

		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);
		b5.setOnClickListener(this);

		FetchPassword fp = new FetchPassword();
		fp.execute();
		
		FetchTimer ft = new FetchTimer();
		ft.execute();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button1:
			AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
			a_builder.setTitle("Set password");

			View dialogBox = getLayoutInflater().inflate(
					R.layout.custom_dialog_a, null);

			a_builder.setView(dialogBox);

			final EditText box = (EditText) dialogBox
					.findViewById(R.id.editText2);
			final EditText cbox = (EditText) dialogBox
					.findViewById(R.id.editText1);

			box.setText(passwordSetByMe);
			cbox.setText(passwordSetByMe);

			a_builder.setPositiveButton("Set",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							if (box.getText().toString()
									.equals(cbox.getText().toString())) {
								passwordSetByMe = box.getText().toString();
								UpdatePassword up = new UpdatePassword();
								up.execute();
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
			break;

		case R.id.button2:
			Intent i = new Intent(AdminActivity.this,
					ManageQuestionsActivity.class);
			startActivity(i);
			break;

		case R.id.button3:
			
			Intent i1 = new Intent(AdminActivity.this,
					ExportResult.class);
			startActivity(i1);
			
			break;	
			
		case R.id.button4:
			Intent i2 = new Intent(AdminActivity.this,
					ManageSubjectsActivity.class);
			startActivity(i2);
			break;
			
		case R.id.button5:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Set timer");

			View dBox = getLayoutInflater().inflate(R.layout.timer, null);

			builder.setView(dBox);

			final EditText timeBox = (EditText) dBox
					.findViewById(R.id.editText2);

			timeBox.setText(timerSetByMe);

			builder.setPositiveButton("Set",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							timerSetByMe = timeBox.getText().toString();
							UpdateTimer ut = new UpdateTimer();
							ut.execute();
							arg0.dismiss();
						}
					});

			builder.setNegativeButton("Back",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
						}
					});

			AlertDialog alertBox = builder.create();
			alertBox.show();
			break;
		}
	}

	class FetchPassword extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/authenticator.php";

				URL url = new URL(link);
				URLConnection conn = url.openConnection();

				conn.setDoOutput(true);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));

				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line);
					break;
				}
				timerSetByMe = sb.toString();
				return sb.toString();
			} catch (Exception e) {
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
	}
	
	class FetchTimer extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/time_reader.php";

				URL url = new URL(link);
				URLConnection conn = url.openConnection();

				conn.setDoOutput(true);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));

				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line);
					break;
				}
				timerSetByMe = sb.toString();
				return sb.toString();
			} catch (Exception e) {
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
	}

	class UpdatePassword extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/password_uploader.php";

				String data = "&"
						+ URLEncoder.encode("passwordSetByMe", "UTF-8") + "="
						+ URLEncoder.encode(passwordSetByMe, "UTF-8");

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
				passwordSetByMe = sb.toString();
				return sb.toString();
			} catch (Exception e) {
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
	}
	
	class UpdateTimer extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {

				String link = "http://172.16.52.11/codes/timer_uploader.php";

				String data = "&"
						+ URLEncoder.encode("timerSetByMe", "UTF-8") + "="
						+ URLEncoder.encode(timerSetByMe, "UTF-8");

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
				timerSetByMe = sb.toString();
				return sb.toString();
			} catch (Exception e) {
				return new String(
						"Something went wrong!\nPlease try again later.");
			}
		}
	}
}