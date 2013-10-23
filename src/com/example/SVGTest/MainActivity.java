package com.example.SVGTest;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.SVGTest.SVGView.OnSVGViewInfoListener;
import com.example.SVGTest.svg.SVG;
import com.example.SVGTest.svg.SVGBuilder;

public class MainActivity extends Activity implements OnSVGViewInfoListener {

	//	private static final String TAG = "MainActivity";

	private SVGView mSvgView = null;
	private TextView mTextView1 = null;
	private TextView mTextView2 = null;
	private RadioButton mCheckedRadioButton = null;
	private boolean mLoading = false;
	private DecimalFormat mDF = new DecimalFormat();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		mSvgView = (SVGView)findViewById(R.id.testView);
		mTextView1 = (TextView)findViewById(R.id.textView1);
		mTextView2 = (TextView)findViewById(R.id.textView2);

		mSvgView.setOnTestViewInfoListener(this);

		findViewById(R.id.radio0).setOnClickListener(OnRadioClickListener);
		findViewById(R.id.radio1).setOnClickListener(OnRadioClickListener);
		findViewById(R.id.radio2).setOnClickListener(OnRadioClickListener);
		findViewById(R.id.radio3).setOnClickListener(OnRadioClickListener);
		findViewById(R.id.radio4).setOnClickListener(OnRadioClickListener);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		load(R.raw.svg1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	OnClickListener OnRadioClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mLoading) {
				mCheckedRadioButton.setChecked(true);
				return;
			}

			System.gc();

			int resId = 0;

			switch (v.getId()) {
				case R.id.radio0:
					resId = R.raw.svg1;
					break;
				case R.id.radio1:
					resId = R.raw.svg2;
					break;
				case R.id.radio2:
					resId = R.raw.svg3;
					break;
				case R.id.radio3:
					resId = R.raw.svg4;
					break;
				case R.id.radio4:
					resId = R.raw.svg5;
					break;
			}

			mCheckedRadioButton = (RadioButton)v;

			mTextView1.setText("");
			mTextView2.setText("");
			load(resId);
		}

	};

	private void load(int resId) {

		if (mLoading) {
			return;
		}

		new AsyncTask<Integer, Void, SVG>() {
			long buildTime;
			int fileSize;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				mLoading = true;
			}

			@Override
			protected void onPostExecute(SVG svg) {
				// TODO Auto-generated method stub

				System.gc();

				mTextView1.setText("Size: " + mDF.format(fileSize) + "Byte\nBuild: " + mDF.format(buildTime) + "μs\nNative: " + mDF.format(Debug.getNativeHeapAllocatedSize() / 1024) + "KB\nDalvik: " + mDF.format(Runtime.getRuntime().totalMemory() / 1024 - Runtime.getRuntime().freeMemory() / 1024) + "KB");

				mSvgView.setSVG(svg);

				mLoading = false;
			}

			@Override
			protected SVG doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				SVGBuilder builder = new SVGBuilder();

				InputStream data = getResources().openRawResource(params[0]);

				try {
					fileSize = data.available();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				builder.readFromInputStream(data);

				long start = System.nanoTime();
				SVG svg = builder.build();
				long end = System.nanoTime();
				buildTime = (end - start) / 1000;

				return svg;
			}
		}.execute(resId);
	}

	@Override
	public void onSVGViewInfo(String info) {
		// TODO Auto-generated method stub
		mTextView2.setText(info);

	}

}
