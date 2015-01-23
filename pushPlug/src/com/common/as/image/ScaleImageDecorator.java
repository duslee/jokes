package com.common.as.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class ScaleImageDecorator implements ImageDecorator {
	private final int mWidth, mHeight;
	private final Matrix mMatrix = new Matrix();

	public ScaleImageDecorator(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	public Bitmap decorateImage(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		final int bWidth = bitmap.getWidth();
		final int bHeight = bitmap.getHeight();

		float scale;
		float dx = 0, dy = 0;
		if (bWidth * mHeight > mWidth * bHeight) {
			scale = (float) mHeight / (float) bHeight;
			dx = (mWidth - bWidth * scale) * 0.5f;
		} else {
			scale = (float) mWidth / (float) bWidth;
			dy = (mHeight - bHeight * scale) * 0.5f;
		}

		mMatrix.reset();
		mMatrix.setScale(scale, scale);
		mMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

		Bitmap result = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);

		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(bitmap, mMatrix, null);

		return result;
	}
}
