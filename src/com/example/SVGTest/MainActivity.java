package com.example.SVGTest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.SVGTest.SVGView.OnSVGViewInfoListener;

public class MainActivity extends Activity implements OnSVGViewInfoListener {

	private SVGView mSvgView = null;
	private EditText mEditText1 = null;
	private EditText mEditText2 = null;
	private RadioButton mCheckedRadioButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		mSvgView = (SVGView)findViewById(R.id.testView);
		mEditText1 = (EditText)findViewById(R.id.editText1);
		mEditText2 = (EditText)findViewById(R.id.editText2);

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

		mSvgView.load(R.raw.svg1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	OnClickListener OnRadioClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!mSvgView.isAvailable()) {
				mCheckedRadioButton.setChecked(true);
				return;
			}

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

			mEditText1.setText("");
			mEditText2.setText("");
			mSvgView.load(resId);
		}

	};

	@Override
	public void OnSVGViewInfo1(String info) {
		// TODO Auto-generated method stub
		mEditText1.append(info + "\n");

	}

	@Override
	public void OnSVGViewInfo2(String info) {
		// TODO Auto-generated method stub
		mEditText2.append(info + "\n");

	}

}
