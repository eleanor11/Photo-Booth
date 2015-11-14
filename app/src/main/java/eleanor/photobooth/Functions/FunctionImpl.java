package eleanor.photobooth.Functions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Eleanor on 2015/11/14.
 */
public class FunctionImpl implements FunctionAccessor {

    @Override
    public Intent take_photo(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));

        return intent;
    }

    @Override
    public Intent get_photo_from_album(){
        String IMAGE_UNSPECIFIED = "image/*";

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);

        return intent;
    }

    @Override
    public Intent zoom_photo(Uri uri, boolean fixed){
        String IMAGE_UNSPECIFIED = "image/*";

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);

        intent.putExtra("crop", "true");

        if (fixed) {
            // aspectX aspectY
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY
            intent.putExtra("outputX", 100);
            intent.putExtra("outputY", 100);
        }
        intent.putExtra("return-data", true);

        return intent;

    }

    @Override
    public Bitmap compress_photo(Bitmap oldPhoto, boolean fixed, int size){
        int newWidth = oldPhoto.getWidth() * 3;
        int newHeight = oldPhoto.getHeight() * 3;
        if (fixed){
            newWidth = size;
            newHeight = size;
        }

        float width = oldPhoto.getWidth();
        float height = oldPhoto.getHeight();

        Matrix matrix = new Matrix();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newPhoto = Bitmap.createBitmap(oldPhoto, 0, 0, (int) width, (int) height, matrix, true);

        return newPhoto;

    }

}
