package com.common.as.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

public class BlendImageDecorator implements ImageDecorator {
	private final Bitmap mBlendBitmap;
	private final boolean mOnTop;

	public BlendImageDecorator(Context ctx, int id, boolean onTop) {
		mBlendBitmap = BitmapFactory.decodeResource(ctx.getResources(), id);
		mOnTop = onTop;
	}

	@Override
	public Bitmap decorateImage(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();

		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		if (mOnTop) {
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.drawBitmap(mBlendBitmap, 0, 0, null);
		} else {
			canvas.drawBitmap(mBlendBitmap, 0, 0, null);
			canvas.drawBitmap(bitmap, 0, 0, null);
		}

		return result;
	}
}
