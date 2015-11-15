package eleanor.photobooth.Functions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;

import org.opencv.ml.Boost;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import eleanor.photobooth.R;

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

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(IMAGE_UNSPECIFIED);

        //intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);


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
    public Bitmap rescale_photo(Bitmap bm, int newWidth, int newHeight) {

        float height = bm.getHeight();
        float width = bm.getWidth();

        float scaleHeight = ((float) newHeight) / height;
        float scaleWidth = ((float) newWidth) / width;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newPhoto = Bitmap.createBitmap(bm, 0, 0, (int) width, (int) height, matrix, true);

        return newPhoto;
    }

    @Override
    public String save_photo(Bitmap bitmap){

//        Log.d("result file height", Integer.toString(bitmap.getHeight()));
//        Log.d("result file width", Integer.toString(bitmap.getWidth()));
//        bitmap = rescale_photo(bitmap, 50, 50);

        Date date = new Date();
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + Long.toString(date.getTime()) + ".png";
        File file = new File(fileName);
        FileOutputStream fout = null;
        try{
            file.createNewFile();
            fout = new FileOutputStream(file);
            boolean t = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
//            Log.d("result file write", Boolean.toString(t));
            fout.flush();
            fout.close();
        } catch (IOException e) {
            Log.d("result file write", "failed");
            e.printStackTrace();
        }

        Log.d("result file ", fileName);
        //f.delete();
        return fileName;
    }

    @Override
    public Bitmap get_photo(String fileName){
        if (fileName.equals(""))
            return null;

//        Log.d("result file ", fileName);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(fileName, options);

        if (bitmap == null) {
            Log.d("result", "bad bitmap");
            return null;
        }

//        Log.d("result bitmap width ", Integer.toString(bitmap.getWidth()));
//        Log.d("result bitmap height ", Integer.toString(bitmap.getHeight()));

        return bitmap;
    }

}
