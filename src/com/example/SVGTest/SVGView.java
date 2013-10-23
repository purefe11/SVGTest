package com.example.SVGTest;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.example.SVGTest.svg.SVG;

public class SVGView extends View {

	public abstract static interface OnSVGViewInfoListener {
		public abstract void didSVGViewDraw(String time);
	}

	private OnSVGViewInfoListener mListener;

	public void setOnTestViewInfoListener(OnSVGViewInfoListener listener) {
		mListener = listener;
	}

	//	private static final String TAG = "SVGView";

	private SVG mSvg = null;

	private DecimalFormat mDF = new DecimalFormat();
	private RectF mImageRect = new RectF();

	public SVGView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}

	public void setSVG(SVG svg) {
		// TODO Auto-generated method stub

		mSvg = svg;

		float imageWidth = mSvg.getLimits().right;
		float imageHeight = mSvg.getLimits().bottom;

		float x = getWidth() / 2 - imageWidth / 2;
		float y = getHeight() / 2 - imageHeight / 2;

		mImageRect.set(x, y, x + imageWidth, y + imageHeight);

		invalidate();
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

		mListener.didSVGViewDraw("Draw: " + mDF.format(microseconds) + "μs");
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
}
