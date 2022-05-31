package com.mitpoly.quizcomp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.mitpoly.quizcomp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity implements OnClickListener {

	String link = "http://172.16.52.11/codes/time_reader.php";
	
	int timeTaken;
	DatabaseHelper qd;
	TextView timerLabel;
	TextView questionCountLabel;
	TextView questionsLabel;
	RadioGroup group;
	RadioButton one, two, three, four;
	Button btn;
	ArrayList<Integer> numbers;
	String answerForCurrentQuestion;
	int questionCount, totalQuestions, marks = 0;
	int minutes = 30;
	String startTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		
		startTime = getIntent().getStringExtra("startTime");
		
		long maxTimeInMilliseconds = minutes * 60 * 1000;
		startTimer(maxTimeInMilliseconds, 1000);
		
		qd = new DatabaseHelper(this, "quiz_database", null, 1000);

		totalQuestions = qd.getnumberOfQuestions();
		numbers = new ArrayList<Integer>();
		for (int i = 0; i < totalQuestions; i++) {
			numbers.add(new Integer(i));
		}
		Collections.shuffle(numbers);
		
		timerLabel = (TextView) findViewById(R.id.textView1);
		questionCountLabel = (TextView) findViewById(R.id.textView2);
		questionsLabel = (TextView) findViewById(R.id.textView3);
		group = (RadioGroup) findViewById(R.id.radioGroup1);
		one = (RadioButton) findViewById(R.id.radio1);
		two = (RadioButton) findViewById(R.id.radio2);
		three = (RadioButton) findViewById(R.id.radio3);
		four = (RadioButton) findViewById(R.id.radio4);
		btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(this);
		loadQuestion(numbers.get(0));
	}

	public void loadQuestion(int questionNumber) {

		Cursor c = qd.getQuestion(questionNumber);
		c.moveToFirst();
		questionsLabel.setText(c.getString(c.getColumnIndex("question")));
		one.setText(c.getString(c.getColumnIndex("optionOne")));
		two.setText(c.getString(c.getColumnIndex("optionTwo")));
		three.setText(c.getString(c.getColumnIndex("optionThree")));
		four.setText(c.getString(c.getColumnIndex("optionFour")));
		answerForCurrentQuestion = c.getString(c
				.getColumnIndex("correctOption"));
		questionCount++;
		questionCountLabel.setText("Question count: " + questionCount + " / "
				+ totalQuestions);
	}

	@Override
	public void onClick(View v) {

		int id = group.getCheckedRadioButtonId();
		if (id == -1) {
			Toast.makeText(this, "You can't skip a question!",
					Toast.LENGTH_SHORT).show();
		} else {

			RadioButton checkedBtn = (RadioButton) findViewById(id);

			if (checkedBtn.getText().toString()
					.equals(answerForCurrentQuestion))
				marks++;
			group.clearCheck();
			if (questionCount == totalQuestions) {
				Intent i = new Intent(this, ResultActivity.class);
				i.putExtra("marks", marks);
				i.putExtra("timeTaken", (timeTaken%60));
				i.putExtra("startTime", startTime);
				startActivity(i);
			} else {
				loadQuestion(numbers.get(questionCount));
			}
		}
	}

	class QuizTimer extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}

	public void startTimer(final long finish, long tick) {
		CountDownTimer t = new CountDownTimer(finish, tick) {

			public void onTick(long millisUntilFinished) {
				long remainedSecs = millisUntilFinished / 1000;
				timerLabel.setText("Time remaining: " + (remainedSecs / 60)
						+ ":" + (remainedSecs % 60));
				timeTaken++;
			}

			public void onFinish() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						QuizActivity.this);
				builder.setTitle("Time's Up!");
				builder.setMessage("We're calculating results");
				builder.setCancelable(false);
				builder.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = new Intent(QuizActivity.this,
										ResultActivity.class);
								startActivity(i);
								cancel();
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		};
		t.start();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					QuizActivity.this);
			builder.setIcon(android.R.drawable.ic_menu_info_details);
			builder.setTitle("Warning!");
			builder.setMessage("You can't leave the quiz before you attempt and submit it!");
			builder.setPositiveButton("Okay",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
		return true;
	}
	
	class FetchTime extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			try {

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
				minutes = Integer.parseInt(sb.toString());
				return sb.toString();
			} catch (Exception e) {
				return new String("Something went wrong!\nPlease try again later.");
			}
		}
	}
}