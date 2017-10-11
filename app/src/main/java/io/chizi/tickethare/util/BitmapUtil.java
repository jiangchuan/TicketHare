package io.chizi.tickethare.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class BitmapUtil {

    public static Bitmap getScaledBitmap(String imageFilePath, int targetW, int targetH) {
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath, bmOptions);

        return imageBitmap;
    }


    public static Bitmap scaleBitmap(Bitmap bm, int maxWidth, int maxHeight) {
        int bmOriginalWidth = bm.getWidth();
        int bmOriginalHeight = bm.getHeight();
        double originalWidthToHeightRatio = 1.0 * bmOriginalWidth / bmOriginalHeight;
        double originalHeightToWidthRatio = 1.0 * bmOriginalHeight / bmOriginalWidth;

        if (bmOriginalWidth > maxWidth || bmOriginalHeight > maxHeight) {
//            Log.v(LOG_TAG, String.format("RESIZING bitmap FROM %sx%s ", bmOriginalWidth, bmOriginalHeight));

            if (bmOriginalWidth > bmOriginalHeight) {
                bm = scaleDeminsFromWidth(bm, maxWidth, originalHeightToWidthRatio);
            } else {
                bm = scaleDeminsFromHeight(bm, maxHeight, originalWidthToHeightRatio);
            }

//            Log.v(LOG_TAG, String.format("RESIZED bitmap TO %sx%s ", bm.getWidth(), bm.getHeight()));
        }
        return bm;
    }

    public static Bitmap cropBitmapCenter(Bitmap bm, int screenWidth, int screenHeight,
                                          final float RATIO_ENLARGE,
                                          final float VERTICAL_RATIO_W, final float VERTICAL_RATIO_H,
                                          final float HORIZONTAL_RATIO_W, final float HORIZONTAL_RATIO_H) {
        int bmOriginalW = bm.getWidth();
        int bmOriginalH = bm.getHeight();
        float ratioEnlarge = RATIO_ENLARGE;
        float ratioW;
        float ratioH;

        if (screenHeight < screenWidth) {
            int t = screenHeight;
            screenHeight = screenWidth;
            screenWidth = t;
        }

        float screeRatio = (float) screenHeight / screenWidth;
        if (bmOriginalH > bmOriginalW) { // Portrait
            ratioW = VERTICAL_RATIO_W;
            ratioH = VERTICAL_RATIO_H;
            if ((float) bmOriginalH / bmOriginalW > screeRatio) {
                int cut = (int) (bmOriginalH - bmOriginalW * screeRatio) / 2;
                bmOriginalH -= 2 * cut;
                int locationW = (int) ((1 - ratioEnlarge / ratioW) * bmOriginalW / 2);
                int locationH = (int) ((1 - ratioEnlarge / ratioH) * bmOriginalH / 2);
                int bmW = bmOriginalW - 2 * locationW;
                int bmH = bmOriginalH - 2 * locationH;
                bm = Bitmap.createBitmap(bm, locationW, cut + locationH, bmW, bmH);
            } else {
                int cut = (int) (bmOriginalW - bmOriginalH / screeRatio) / 2;
                bmOriginalW -= 2 * cut;
                int locationW = (int) ((1 - ratioEnlarge / ratioW) * bmOriginalW / 2);
                int locationH = (int) ((1 - ratioEnlarge / ratioH) * bmOriginalH / 2);
                int bmW = bmOriginalW - 2 * locationW;
                int bmH = bmOriginalH - 2 * locationH;
                bm = Bitmap.createBitmap(bm, cut + locationW, locationH, bmW, bmH);
            }
        } else { // Landscape
            ratioW = HORIZONTAL_RATIO_W;
            ratioH = HORIZONTAL_RATIO_H;
            if ((float) bmOriginalW / bmOriginalH > screeRatio) {
                int cut = (int) (bmOriginalW - bmOriginalH * screeRatio) / 2;
                bmOriginalW -= 2 * cut;
                int locationW = (int) ((1 - ratioEnlarge / ratioW) * bmOriginalW / 2);
                int locationH = (int) ((1 - ratioEnlarge / ratioH) * bmOriginalH / 2);
                int bmW = bmOriginalW - 2 * locationW;
                int bmH = bmOriginalH - 2 * locationH;
                bm = Bitmap.createBitmap(bm, cut + locationW, locationH, bmW, bmH);
            } else {
                int cut = (int) (bmOriginalH - bmOriginalW / screeRatio) / 2;
                bmOriginalH -= 2 * cut;
                int locationW = (int) ((1 - ratioEnlarge / ratioW) * bmOriginalW / 2);
                int locationH = (int) ((1 - ratioEnlarge / ratioH) * bmOriginalH / 2);
                int bmW = bmOriginalW - 2 * locationW;
                int bmH = bmOriginalH - 2 * locationH;
                bm = Bitmap.createBitmap(bm, locationW, cut + locationH, bmW, bmH);
            }
        }
        return bm;
    }

    public static Bitmap scaleDeminsFromHeight(Bitmap bm, int maxHeight, int bmOriginalHeight, double originalWidthToHeightRatio) {
        int newHeight = (int) Math.max(maxHeight, bmOriginalHeight * .55);
        int newWidth = (int) (newHeight * originalWidthToHeightRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    public static Bitmap scaleDeminsFromHeight(Bitmap bm, int maxHeight, double originalWidthToHeightRatio) {
        int newHeight = maxHeight;
        int newWidth = (int) (newHeight * originalWidthToHeightRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    public static Bitmap scaleDeminsFromWidth(Bitmap bm, int maxWidth, int bmOriginalWidth, double originalHeightToWidthRatio) {
        //scale the width
        int newWidth = (int) Math.max(maxWidth, bmOriginalWidth * .75);
        int newHeight = (int) (newWidth * originalHeightToWidthRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    public static Bitmap scaleDeminsFromWidth(Bitmap bm, int maxWidth, double originalHeightToWidthRatio) {
        //scale the width
        int newWidth = maxWidth;
        int newHeight = (int) (newWidth * originalHeightToWidthRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    public static void setBitmapToImageView(ImageView imageView, Bitmap inBitmap) {
        /* Get the size of the ImageView */
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        setBitmapToImageView(imageView, targetW, targetH, inBitmap);
    }


    public static void setBitmapToImageView(ImageView imageView, int targetW, int targetH, Bitmap inBitmap) {
        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapUtil.scaleBitmap(inBitmap, targetW, targetH);
		/* Associate the Bitmap to the ImageView */
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }

}
