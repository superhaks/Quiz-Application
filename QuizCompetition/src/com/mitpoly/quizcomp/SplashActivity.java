package com.mitpoly.quizcomp;

import com.mitpoly.quizcomp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;

public class SplashActivity extends Activity implements OnClickListener {

	Button btn;
	View text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		btn = (Button) findViewById(R.id.button1);
		text = (View) findViewById(R.id.textView1);
		btn.setOnClickListener(this);
		registerForContextMenu(btn);
		Animation a = AnimationUtils.loadAnimation(this, R.anim.fade);
		Animation b = AnimationUtils.loadAnimation(this, R.anim.fade);
		b.setStartOffset(1500);
		b.setDuration(1000);
		
		text.startAnimation(a);
		btn.startAnimation(b);
		
		a.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) { }
			
			@Override
			public void onAnimationRepeat(Animation animation) { }
			
			@Override
			public void onAnimationEnd(Animation animation) {
				//label.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onClick(View v) {

		Intent i = new Intent(SplashActivity.this, FormActivity.class);
		startActivity(i);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add("Login");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
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
						if (box.getText().toString().equals("admin")) {
							arg0.dismiss();
							Intent i = new Intent(SplashActivity.this, AdminActivity.class);
							startActivity(i);
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
		return super.onContextItemSelected(item);
	}
}