package se.mah.af6589.assignment2;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Gustaf Bohlin on 04/10/2017.
 */

public class BitmapPackage {
    private String imageId;
    private Bitmap bitmap;

    public BitmapPackage(String imageId, Bitmap bitmap) {
        this.imageId = imageId;
        this.bitmap = bitmap;
    }

    public String getImageId() {
        return imageId;
    }

    public byte[] getBitmap() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
