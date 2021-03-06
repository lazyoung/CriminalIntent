package com.lazyoung.criminalintent;
import android.graphics.drawable.*;
import android.app.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;

public class PictureUtils { 
    /**
    * Get a BitmapDrawable from a local file that is scaled down
    * to fit the current Window size.
    */
    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity a, String path, int degree) {
         Display display = a.getWindowManager().getDefaultDisplay();
         float destWidth = display.getWidth();
         float destHeight = display.getHeight();
		 
         // Read in the dimensions of the image on disk
         BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
		     BitmapFactory.decodeFile(path, options);
		 
         float srcWidth = options.outWidth;
         float srcHeight = options.outHeight;

         int inSampleSize = 1;
         if (srcHeight > destHeight || srcWidth > destWidth) {
             if  (srcWidth > srcHeight) {
                 inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                 inSampleSize = Math.round(srcWidth / destWidth);
            }
         }

         options = new BitmapFactory.Options();
         options.inSampleSize = inSampleSize;
         Bitmap bitmap = BitmapFactory.decodeFile(path, options);
             
         Matrix matrix = new Matrix();
         matrix.postRotate(degree);

         Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
         return new BitmapDrawable(a.getResources(), bm);
    }
	
	public static void cleanImageView(ImageView imageView) {
		if (!(imageView.getDrawable() instanceof BitmapDrawable))
			return;

		// Clean up the view's image for the sake of memory
		BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
		b.getBitmap().recycle();
		imageView.setImageDrawable(null);
    }
}
