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

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
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
        return save_photo(bitmap, -1);
    }

    @Override
    public String save_photo(Bitmap bitmap, int opt){

//        Log.d("result file height", Integer.toString(bitmap.getHeight()));
//        Log.d("result file width", Integer.toString(bitmap.getWidth()));
//        bitmap = rescale_photo(bitmap, 50, 50);

        String fileName = Environment.getExternalStorageDirectory().getPath() + "/";
        if (opt == 0){
            fileName += "photo_booth_ori.jpg";
        }
        else if (opt == 1) {
            fileName += "photo_booth_tmp.jpg";
        }
        else {
            Date date = new Date();
            fileName += Long.toString(date.getTime()) + ".jpg";
        }
        File file = new File(fileName);
        FileOutputStream fout = null;
        try{
            file.createNewFile();
            fout = new FileOutputStream(file);
            boolean t = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
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


    private Mat convert_to_mat(Bitmap bmp){
        Mat ImageMat = new Mat ( bmp.getHeight(), bmp.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, ImageMat);

        return ImageMat;
    }


    private Bitmap convert_to_bitmap(Mat mat){
        Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);;
        Utils.matToBitmap(mat, resultBitmap);

        return resultBitmap;
    }

    @Override
    public Bitmap rotate_bitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public Bitmap mirrorUp(Bitmap bitmap) {
        return mirrorUp(bitmap, bitmap.getHeight() / 2);
    }

    @Override
    public Bitmap mirrorUp(Bitmap bitmap, int row) {

        Mat photo = convert_to_mat(bitmap);

        Rect roi = new Rect(0, 0, photo.width(), row);
        Mat p1 = new Mat(photo, roi);
        Mat p2 = new Mat();
        Core.flip(p1, p2, 0);

        Mat newPhoto = new Mat(photo.rows(), photo.cols(), photo.type());
        Log.d("photo_booth", Integer.toString(newPhoto.width()) + " " + Integer.toString(newPhoto.height()));
        Log.d("photo_booth", "row: " + Integer.toString(row));

        int start = 0;
        Mat submit;
        while (start + row + row <= newPhoto.rows()) {
            submit = newPhoto.rowRange(start, start + row);
            p1.copyTo(submit);
            submit = newPhoto.rowRange(start + row, start + row + row);
            p2.copyTo(submit);
            start += row + row;
            Log.d("photo_booth", "start: " + Integer.toString(start));
        }
        if (start < newPhoto.rows()) {
            roi = new Rect(0, 0, p1.width(), Math.min(row, newPhoto.rows() - start));
            p1 = new Mat(p1, roi);
            p1.copyTo(newPhoto.rowRange(start, start + p1.rows()));
            start += p1.rows();
            Log.d("photo_booth", "start: " + Integer.toString(start));
            Log.d("photo_booth", "row: " + Integer.toString(p1.rows()));
        }
        if (start < newPhoto.rows()) {
            roi = new Rect(0, 0, p2.width(), Math.min(row, newPhoto.rows() - start));
            p2 = new Mat(p2, roi);
            p2.copyTo(newPhoto.rowRange(start, start + p2.rows()));
            start += p2.rows();
            Log.d("photo_booth", "start: " + Integer.toString(start));
            Log.d("photo_booth", "row: " + Integer.toString(p2.rows()));
        }

        return convert_to_bitmap(newPhoto);
    }

    @Override
    public Bitmap mirrorDown(Bitmap bitmap) {
        return mirrorDown(bitmap, bitmap.getHeight() / 2);
    }

    @Override
    public Bitmap mirrorDown(Bitmap bitmap, int row) {

        Mat photo = convert_to_mat(bitmap);

        Rect roi = new Rect(0, photo.height() - row, photo.width(), row);
        Mat p1 = new Mat(photo, roi);
        Mat p2 = new Mat();
        Core.flip(p1, p2, 0);

        Mat newPhoto = new Mat(photo.rows(), photo.cols(), photo.type());

        int end = newPhoto.rows();
        Mat submit;
        while (end - row - row >= 0) {
            submit = newPhoto.rowRange(end - row, end);
            p1.copyTo(submit);
            submit = newPhoto.rowRange(end - row - row, end - row);
            p2.copyTo(submit);
            end -= row + row;
        }
        if (end > 0) {
            roi = new Rect(0, Math.max(0, row - end), p1.width(), Math.min(row, end));
            p1 = new Mat(p1, roi);
            p1.copyTo(newPhoto.rowRange(end - p1.rows(), end));
            end -= p1.rows();
        }
        if (end > 0) {
            roi = new Rect(0, Math.max(0, row - end), p1.width(), Math.min(row, end));
            p2 = new Mat(p2, roi);
            p2.copyTo(newPhoto.rowRange(end - p2.rows(), end));
            end -= p2.rows();
        }


        return convert_to_bitmap(newPhoto);
    }

    @Override
    public Bitmap mirrorLeft(Bitmap bitmap) {
        return mirrorLeft(bitmap, bitmap.getWidth() / 2);
    }

    @Override
    public Bitmap mirrorLeft(Bitmap bitmap, int col) {

        Mat photo = convert_to_mat(bitmap);

        Rect roi = new Rect(0, 0, col, photo.height());
        Mat p1 = new Mat(photo, roi);
        Mat p2 = new Mat();
        Core.flip(p1, p2, 1);

        Mat newPhoto = new Mat(photo.rows(), photo.cols(), photo.type());

        int start = 0;
        Mat submit;
        while (start + col + col <= newPhoto.cols()) {
            submit = newPhoto.colRange(start, start + col);
            p1.copyTo(submit);
            submit = newPhoto.colRange(start + col, start + col + col);
            p2.copyTo(submit);
            start += col + col;
        }
        if (start < newPhoto.cols()) {
            roi = new Rect(0, 0, Math.min(col, newPhoto.cols() - start), p1.height());
            p1 = new Mat(p1, roi);
            p1.copyTo(newPhoto.colRange(start, start + p1.cols()));
            start += p1.cols();
        }
        if (start < newPhoto.cols()) {
            roi = new Rect(0, 0, Math.min(col, newPhoto.cols() - start), p2.height());
            p2 = new Mat(p2, roi);
            p2.copyTo(newPhoto.colRange(start, start + p2.cols()));
            start += p2.cols();
        }

        return convert_to_bitmap(newPhoto);
    }

    @Override
    public Bitmap mirrorRight(Bitmap bitmap) {
        return mirrorRight(bitmap, bitmap.getWidth() / 2);
    }

    @Override
    public Bitmap mirrorRight(Bitmap bitmap, int col) {

        Mat photo = convert_to_mat(bitmap);

        Rect roi = new Rect(photo.width() - col, 0, col, photo.height());
        Mat p1 = new Mat(photo, roi);
        Mat p2 = new Mat();
        Core.flip(p1, p2, 1);

        Mat newPhoto = new Mat(photo.rows(), photo.cols(), photo.type());

        int end = newPhoto.cols();
        Mat submit;
        while (end - col - col >= 0) {
            submit = newPhoto.colRange(end - col, end);
            p1.copyTo(submit);
            submit = newPhoto.colRange(end - col - col, end - col);
            p2.copyTo(submit);
            end -= col + col;
        }
        if (end > 0) {
            roi = new Rect(Math.max(0, col - end), 0, Math.min(col, end), p1.height());
            p1 = new Mat(p1, roi);
            p1.copyTo(newPhoto.colRange(end - p1.cols(), end));;
            end -= p1.cols();
        }
        if (end > 0) {
            roi = new Rect(Math.max(0, col - end), 0, Math.min(col, end), p2.height());
            p2 = new Mat(p2, roi);
            p2.copyTo(newPhoto.colRange(end - p2.cols(), end));
            end -= p2.cols();
        }

        return convert_to_bitmap(newPhoto);
    }

    @Override
    public Bitmap addLineRow(Bitmap bitmap) {
        return addLineRow(bitmap, -1, "");
    }
    @Override
    public Bitmap addLineRow(Bitmap bitmap, int row) {
        return addLineRow(bitmap, row, "");
    }
    @Override
    public Bitmap addLineRow(Bitmap bitmap, int row, String color){
        Mat photo = convert_to_mat(bitmap);

        if (row == -1) {
            row = photo.rows() / 2;
        }

        double[] line = new double[4];
        if (color.equals("red")) {
            line[0] = 255.0;
            line[1] = 0.0;
            line[2] = 0.0;
            line[3] = 255.0;
        }
        else {
            line[0] = 0.0;
            line[1] = 255.0;
            line[2] = 255.0;
            line[3] = 255.0;
        }

        for (int i = 0; i < photo.cols(); i++) {
            photo.put(row - 2, i, line);
            photo.put(row - 1, i, line);
            photo.put(row, i, line);
            photo.put(row + 1, i, line);
            photo.put(row + 2, i, line);
        }

        return convert_to_bitmap(photo);
    }
    @Override
    public Bitmap addLineCol(Bitmap bitmap) {
        return addLineCol(bitmap, -1, "");
    }
    @Override
    public  Bitmap addLineCol(Bitmap bitmap, int col) {
        return addLineCol(bitmap, col, "");
    }
    @Override
    public Bitmap addLineCol(Bitmap bitmap, int col, String color){
        Mat photo = convert_to_mat(bitmap);

        if (col == -1) {
            col = photo.cols() / 2;
        }

        double[] line = new double[4];
        if (color.equals("red")) {
            line[0] = 255.0;
            line[1] = 0.0;
            line[2] = 0.0;
            line[3] = 255.0;
        }
        else {
            line[0] = 0.0;
            line[1] = 255.0;
            line[2] = 255.0;
            line[3] = 255.0;
        }

        for (int i = 0; i < photo.rows(); i++) {
            photo.put(i, col - 2, line);
            photo.put(i, col - 1, line);
            photo.put(i, col, line);
            photo.put(i, col + 1, line);
            photo.put(i, col + 2, line);
        }

        return convert_to_bitmap(photo);
    }


}
