package com.common.as.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class MaskImageDecorator implements ImageDecorator {
	private final Bitmap mMaskBitmap;

	private final Paint mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public MaskImageDecorator(Context ctx, int id) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

		Bitmap source = BitmapFactory.decodeResource(ctx.getResources(), id, opts);
		mMaskBitmap = source.extractAlpha();
		source.recycle();

		mFillPaint.setColor(Color.RED);
		mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
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
		canvas.drawBitmap(mMaskBitmap, 0, 0, mFillPaint);
		canvas.drawBitmap(bitmap, 0, 0, mMaskPaint);

		return result;
	}
}
