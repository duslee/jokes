package com.common.as.image;

import android.graphics.Bitmap;

public class QueueImageDecorator implements ImageDecorator {
	private final ImageDecorator[] mDecorators;

	public QueueImageDecorator(ImageDecorator... decorators) {
		mDecorators = decorators;
	}

	@Override
	public Bitmap decorateImage(Bitmap bitmap) {
		for (ImageDecorator decorator : mDecorators) {
			bitmap = decorator.decorateImage(bitmap);
		}
		return bitmap;
	}
}
