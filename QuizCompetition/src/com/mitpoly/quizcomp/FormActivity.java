package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitpoly.quizcomp.R;
import com.mitpoly.quizcomp.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FormActivity extends Activity implements OnClickListener {

	EditText nameBox, numberBox;
	Spinner dropdown;
	Button submitBtn;
	TextView formTitle;
	AlertDialog.Builder builder;
	AlertDialog alert;
	boolean isConnected = false;
	View connectionView;
	TextView connectionTV;
	IsNetworkAvailable isa;
	ImageButton reloadB;
	ProgressBar pB1;
	static String name, number, subject;
	String passwordSetByAdmin = "";
	ArrayList<String> data;
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.form);
		
		formTitle = (TextView) findViewById(R.id.textView1);
		nameBox = (EditText) findViewById(R.id.editText1);
		numberBox = (EditText) findViewById(R.id.editText2);
		dropdown = (Spinner) findViewById(R.id.spinner1);
		submitBtn = (Button) findViewById(R.id.button1);
		connectionView = (View) findViewById(R.id.ColorRL);
		connectionTV = (TextView) findViewById(R.id.textView5);
		reloadB = (ImageButton) findViewById(R.id.imageButton1);
		pB1 = (ProgressBar) findViewById(R.id.progressBar1);
		
		submitBtn.setOnClickListener(this);
		reloadB.setOnClickListener(this);
		
		data = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
		dropdown.setAdapter(adapter);	
		
		IsNetworkAvailable isa = new IsNetworkAvailable();
		isa.execute();
		
		//do{
			//if(IsConnected == true){
				
			//}
		//}while(IsConnected == false);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.button1 :	FormActivity.name = nameBox.getText().toString();
								if (!name.equals("")) {
								FormActivity.number = numberBox.getText().toString();
								if (!number.equals("") && number.length() == 10) {
								FormActivity.subject = dropdown.getSelectedItem().toString();
								
								
									
						
										AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
										a_builder.setTitle("Authentication");
						
										View dialogBox = getLayoutInflater().inflate(
												R.layout.custom_dialog, null);
						
										a_builder.setView(dialogBox);
						
										final EditText box = (EditText) dialogBox.findViewById(R.id.editText2);
						
										a_builder.setPositiveButton("Login",
												new DialogInterface.OnClickListener() {
						
													@Override
													public void onClick(DialogInterface arg0, int arg1) {
						
														if (box.getText().toString().equals(passwordSetByAdmin)) {
															arg0.dismiss();
															Intent i = new Intent(FormActivity.this, ExpandableListAppActivity.class);
															startActivity(i);
														} else {
															Toast.makeText(FormActivity.this, "Login failed!", 1000).show();
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
										Toast.makeText(this, "Enter Number", Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
								}
		
		case R.id.imageButton1 :	try{
											Toast.makeText(this, "Trying...", Toast.LENGTH_SHORT).show();
											isa.execute();
										}catch(Exception e){
											e.printStackTrace();
										}
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
				passwordSetByAdmin = sb.toString();
				return sb.toString();
			} catch (Exception e) {
				return new String("Something went wrong!\nPlease try again later.");
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
	
	class IsNetworkAvailable extends AsyncTask<Void, Void, String>  {
		ConnectionStatus cs = new ConnectionStatus();
		@Override
		protected String doInBackground(Void... arg0) {
				try{
					
					cs.connecting();
					
					ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
					NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
					
					if (netInfo.isConnected()) {
						
						cs.networkA();
						
						FetchPassword fp = new FetchPassword();
						fp.execute();
						
						GetSubjects gs = new GetSubjects();
						gs.execute();
							isConnected = true;
					} else {
							
							cs.networkNA();
							
							isConnected = false;
						}
				}catch (Exception e) {
					
				}
			return null;
		}
	}
	
	public class ConnectionStatus{
		void connected(){
			connectionView.setBackgroundColor(Color.parseColor("#9CCC65")); //GREEN
			connectionTV.setText("   Connected !");
			reloadB.setVisibility(View.INVISIBLE);
			pB1.setVisibility(View.INVISIBLE);
		}
		void notConnected(){
			connectionView.setBackgroundColor(Color.parseColor("#FF5252")); //RED
			connectionTV.setText("   Not connected !");
			reloadB.setVisibility(View.VISIBLE);
			pB1.setVisibility(View.INVISIBLE);
		}
		void connecting(){
			connectionView.setBackgroundColor(Color.parseColor("#FFEA00")); //YELLOW
			connectionTV.setText("   Connecting...");
			reloadB.setVisibility(View.INVISIBLE);
			pB1.setVisibility(View.VISIBLE);
		}
		void networkNA(){
			connectionView.setBackgroundColor(Color.parseColor("#9E9E9E")); //GREY
			connectionTV.setText("   Network not available.");
			reloadB.setVisibility(View.VISIBLE);
			pB1.setVisibility(View.INVISIBLE);
		}
		void networkA(){
			connectionView.setBackgroundColor(Color.parseColor("#9CCC65")); //GREEN
			connectionTV.setText("   Network Available !");
			reloadB.setVisibility(View.INVISIBLE);
			pB1.setVisibility(View.INVISIBLE);
		}
	}
}