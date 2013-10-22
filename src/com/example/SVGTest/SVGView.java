package com.example.SVGTest;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.example.SVGTest.svg.SVG;
import com.example.SVGTest.svg.SVGBuilder;

public class SVGView extends View {

	public abstract static interface OnSVGViewInfoListener {
		public abstract void OnSVGViewInfo1(String info);

		public abstract void OnSVGViewInfo2(String info);
	}

	private OnSVGViewInfoListener mListener;

	public void setOnTestViewInfoListener(OnSVGViewInfoListener listener) {
		mListener = listener;
	}

	//	private static final String TAG = "SVGView";

	private SVG mSvg = null;

	private boolean mRunning = false;

	private DecimalFormat mDF = new DecimalFormat();
	private RectF mImageRect = new RectF();

	public SVGView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}

	public boolean isAvailable() {
		return (!mRunning);
	}

	public boolean load(int resId) {

		if (mRunning) {
			return false;
		}

		new AsyncTask<Integer, Void, Void>() {

			long loadTime;
			long buildTime;
			int fileSize;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				mRunning = true;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				mListener.OnSVGViewInfo1("size: " + mDF.format(fileSize) + "byte");
				mListener.OnSVGViewInfo1("load: " + mDF.format(loadTime) + "μs");
				mListener.OnSVGViewInfo1("build: " + mDF.format(buildTime) + "μs");

				float imageWidth = mSvg.getLimits().right;
				float imageHeight = mSvg.getLimits().bottom;

				float x = getWidth() / 2 - imageWidth / 2;
				float y = getHeight() / 2 - imageHeight / 2;

				mImageRect.set(x, y, x + imageWidth, y + imageHeight);

				invalidate();
				mRunning = false;
			}

			@Override
			protected Void doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				SVGBuilder builder = new SVGBuilder();

				long start = System.nanoTime();
				InputStream data = getResources().openRawResource(params[0]);
				long end = System.nanoTime();
				loadTime = (end - start) / 1000;

				try {
					fileSize = data.available();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				builder.readFromInputStream(data);

				start = System.nanoTime();
				mSvg = builder.build();
				end = System.nanoTime();
				buildTime = (end - start) / 1000;

				return null;
			}
		}.execute(resId);

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (null == mSvg) {
			return;
		}

		Picture picture = mSvg.getPicture();

		long start = System.nanoTime();
		canvas.drawPicture(picture, mImageRect);
		long end = System.nanoTime();
		long microseconds = (end - start) / 1000;

		mListener.OnSVGViewInfo2("draw: " + mDF.format(microseconds) + "μs");
	}

	private boolean mZooming = false;
	private float mOldDistZoom = 0f;
	private float mNewDistZoom = 0f;

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mRunning) {
			return true;
		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
				mZooming = false;
				mOldDistZoom = 1f;
				mNewDistZoom = 1f;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if (2 == event.getPointerCount()) {
					mOldDistZoom = spacing(event);
					mNewDistZoom = mOldDistZoom;
					mZooming = true;
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				if (mZooming && 2 == event.getPointerCount()) {
					mNewDistZoom = spacing(event);
					if (mOldDistZoom < mNewDistZoom) {
						zoom(true, mNewDistZoom - mOldDistZoom);
						mOldDistZoom = mNewDistZoom;
					} else if (mOldDistZoom > mNewDistZoom) {
						zoom(false, mOldDistZoom - mNewDistZoom);
						mOldDistZoom = mNewDistZoom;
					}
				}
				break;
		}

		return true;
	}

	public void zoom(boolean in, float delta) {

		float imageWidth = mImageRect.width();
		float imageHeight = mImageRect.height();

		if (in) {
			imageWidth += delta;
			imageHeight = (mSvg.getLimits().bottom * imageWidth) / mSvg.getLimits().right;
		} else {
			imageWidth -= delta;
			imageHeight = (mSvg.getLimits().bottom * imageWidth) / mSvg.getLimits().right;
		}

		if (imageWidth > 5000 || imageWidth < 50 || imageHeight > 5000 || imageHeight < 50) {
			return;
		}

		float x = getWidth() / 2 - imageWidth / 2;
		float y = getHeight() / 2 - imageHeight / 2;

		mImageRect.set(x, y, x + imageWidth, y + imageHeight);

		invalidate();
	}

	//	public static void logHeap() {
	//
	//		Double allocated = new Double(Debug.getNativeHeapAllocatedSize()) / new Double((1024));
	//		Double available = new Double(Debug.getNativeHeapSize()) / 1024.0;
	//		Double free = new Double(Debug.getNativeHeapFreeSize()) / 1024.0;
	//		DecimalFormat df = new DecimalFormat();
	//		df.setMaximumFractionDigits(2);
	//		df.setMinimumFractionDigits(2);
	//
	//		Log.d(TAG, "================================================================================");
	//		Log.d(TAG, "heap native: allocated " + df.format(allocated) + "KB of " + df.format(available) + "KB (" + df.format(free) + "KB free)");
	//		Log.d(TAG, "memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory() / 1024)) + "KB of " + df.format(new Double(Runtime.getRuntime().maxMemory() / 1024)) + "KB (" + df.format(new Double(Runtime.getRuntime().freeMemory() / 1024)) + "KB free)");
	//	}

}
