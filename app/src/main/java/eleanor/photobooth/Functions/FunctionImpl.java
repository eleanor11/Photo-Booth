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
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.*;
import org.opencv.video.*;
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


    /*
    * mirror
    * */
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


    /*
    * add line
    * */

    double white[] = {255.0, 255.0, 255.0, 255.0};
    double red[] = {255.0, 0.0, 0.0, 255.0};

    @Override
    public Bitmap addLineRow(Bitmap bitmap) {
        return addLineRow(bitmap, bitmap.getHeight() / 2, "");
    }
    @Override
    public Bitmap addLineRow(Bitmap bitmap, int row) {
        return addLineRow(bitmap, row, "");
    }
    @Override
    public Bitmap addLineRow(Bitmap bitmap, int row, String color){
        Mat photo = convert_to_mat(bitmap);

        double[] line = white;
        if (color.equals("red")) {
            line = red;
        }

        int sj = Math.max(0, row - 2);
        int ej = Math.min(row + 2, photo.rows());

        for (int i = 0; i < photo.cols(); i++) {
            for (int j = sj; j < ej; j++){
                photo.put(j, i, line);
            }
        }

        return convert_to_bitmap(photo);
    }
    @Override
    public Bitmap addLineCol(Bitmap bitmap) {
        return addLineCol(bitmap, bitmap.getWidth() / 2, "");
    }
    @Override
    public  Bitmap addLineCol(Bitmap bitmap, int col) {
        return addLineCol(bitmap, col, "");
    }
    @Override
    public Bitmap addLineCol(Bitmap bitmap, int col, String color){
        Mat photo = convert_to_mat(bitmap);

        double[] line = white;
        if (color.equals("red")) {
            line = red;
        }

        int sj = Math.max(0, col - 2);
        int ej = Math.min(col + 2, photo.cols());

        for (int i = 0; i < photo.rows(); i++) {
            for (int j = sj; j < ej; j++) {
                photo.put(i, j, line);
            }
        }

        return convert_to_bitmap(photo);
    }


    /*
    * stretch & squeeze
    * */

    int scaleDefault = 500;         //default

    @Override
    public Bitmap stretch(Bitmap bitmap){
        return stretch(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scaleDefault);
    }
    @Override
    public Bitmap stretch(Bitmap bitmap, int scale){
        return stretch(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scale);
    }
    @Override
    public Bitmap stretch(Bitmap bitmap, int px, int py){
        return stretch(bitmap, px, py, scaleDefault);
    }
    @Override
    public Bitmap stretch(Bitmap bitmap, int px, int py, int scale){
        Mat photo = convert_to_mat(bitmap);

        int width = photo.cols();
        int height = photo.rows();
        Mat newPhoto = new Mat(photo.size(), photo.type());
        photo.copyTo(newPhoto);

        byte[] tmp = new byte[4];

//        Log.d("photo_booth", "stretch " + Integer.toString(width) + " " + Integer.toString(height) + " " + Integer.toString(px) + " " + Integer.toString(py)) ;;
//        Mat mc = new MatOfPoint(center);
        int sy = Math.max(0, py - scale);
        int ey = Math.min(py + scale, height);
        for (int y = sy; y < ey; y++) {
//            Log.d("photo_booth", "pointy " + Integer.toString(y));
            int dx = (int) Math.sqrt((double) ((scale * scale) - (y -py) * (y - py)));
            int sx = Math.max(0, px - dx);
            int ex = Math.min(px + dx, width);
            for (int x = sx; x < ex; x++) {
//                Mat sb = new Mat();           //so many mats need several seconds to solve
//                Core.subtract(new MatOfPoint(new Point(x, y)), mc, sb);
//                int dis = (int) Core.norm(sb);
                int dis = (int) Math.sqrt((double) ((x - px) * (x - px) + (y - py) * (y - py)));
                int newX = (int) ((x - px) * dis / scale + px);
                int newY = (int) ((y - py) * dis / scale + py);

                if (newX < width && newX >= 0 && newY < height && newY >= 0) {
                    photo.get(newY, newX, tmp);
                    newPhoto.put(y, x, tmp);
                }

//                if (dis < scale) {
//                    Log.d("photo_booth", "put");
//
//                    int newX = (int) ((x - px) * dis / scale + px);
//                    int newY = (int) ((y - py) * dis / scale + py);
//
//                    if (newX < width && newY < height) {
//                        newPhoto.put(y, x, photo.get(newY, newX));
//                    }
//
//                }
            }
        }

//        Log.d("photo_booth", "stretch end");

        return convert_to_bitmap(newPhoto);
    }
    @Override
    public Bitmap squeeze(Bitmap bitmap){
        return squeeze(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scaleDefault);
    }
    @Override
    public Bitmap squeeze(Bitmap bitmap, int scale){
        return squeeze(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scale);
    }
    @Override
    public Bitmap squeeze(Bitmap bitmap, int px, int py){
        return squeeze(bitmap, px, py, scaleDefault);
    }
    @Override
    public Bitmap squeeze(Bitmap bitmap, int px, int py, int scale){
        Mat photo = convert_to_mat(bitmap);

        int width = photo.cols();
        int height = photo.rows();
        Mat newPhoto = new Mat(photo.size(), photo.type());
        photo.copyTo(newPhoto);

        byte[] tmp = new byte[4];

        int sy = Math.max(0, py - scale);
        int ey = Math.min(py + scale, height);
        for (int y = sy; y < ey; y++) {

            int dx = (int) Math.sqrt((double) ((scale * scale) - (y -py) * (y - py)));
            int sx = Math.max(0, px - dx);
            int ex = Math.min(px + dx, width);

            for (int x = sx; x < ex; x++) {

                double dis = Math.sqrt((double) ((x - px) * (x - px) + (y - py) * (y - py)));
                int r = (int) (Math.sqrt(dis) * 16);

                double theta = Math.atan2((float)(y - py), (float)(x - px));
                int newX = (int) (r * Math.cos(theta)) + px;
                int newY = (int) (r * Math.sin(theta)) + py;

                if (newX < width && newX >= 0 && newY < height && newY >= 0) {
                    photo.get(newY, newX, tmp);
                    newPhoto.put(y, x, tmp);
                }

            }
        }

        return convert_to_bitmap(newPhoto);
    }


    /*
    * add cercle
    * */
    @Override
    public Bitmap addCircle(Bitmap bitmap){
        return addCircle(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scaleDefault, "");
    }

    @Override
    public Bitmap addCircle(Bitmap bitmap, int scale){
        return addCircle(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scale, "");
    }
    @Override
    public Bitmap addCircle(Bitmap bitmap, int px, int py){
        return addCircle(bitmap, px, py, scaleDefault, "");
    }
    @Override
    public Bitmap addCircle(Bitmap bitmap, int px, int py, int scale){
        return addCircle(bitmap, px, py, scale, "");
    }
    @Override
    public Bitmap addCircle(Bitmap bitmap, int px, int py, int scale, String color){
        Mat photo = convert_to_mat(bitmap);

        double[] circle = white;
        if (color.equals("red")) {
            circle = red;
        }

        int scale1 = scale - 2;
        int scale2 = scale + 2;

        int sy = Math.max(0, py - scale);
        int ey = Math.min(py + scale, photo.rows());
        for (int y = sy; y < ey; y++) {
            int dx1 = (int) Math.sqrt((double) ((scale1 * scale1) - (y -py) * (y - py)));
            int dx2 = (int) Math.sqrt((double) ((scale2 * scale2) - (y -py) * (y - py)));

            int sx,ex;
            sx = Math.max(0, px - dx2);
            ex = Math.max(0, px - dx1);
            for (int x = sx; x < ex; x++) {
                photo.put(y, x, circle);
            }
            sx = Math.min(px + dx1, photo.cols());
            ex = Math.min(px + dx2, photo.cols());
            for (int x = sx; x < ex; x++) {
                photo.put(y, x, circle);
            }

        }

        return convert_to_bitmap(photo);
    }

    /*
    * kaleidoscope
    * */

    private float triangle(float x) {
        float r = x % 1.0f;
        return 2.0f * (r < 0.5 ? r : 1 - r);
    }

    @Override
    public Bitmap kaleidoscope(Bitmap bitmap){
        return kaleidoscope(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scaleDefault);
    }
    @Override
    public Bitmap kaleidoscope(Bitmap bitmap, int scale){
        return kaleidoscope(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, scale);
    }
    @Override
    public Bitmap kaleidoscope(Bitmap bitmap, int px, int py){
        return kaleidoscope(bitmap, px, py, scaleDefault);
    }
    @Override
    public Bitmap kaleidoscope(Bitmap bitmap, int px, int py, int scale){
        Mat photo = convert_to_mat(bitmap);

        Mat newPhoto = new Mat(photo.size(),photo.type());
        photo.copyTo(newPhoto);

        double angle = Math.PI / 4;
        double angle2 = Math.PI / 4;
        int sides = 10;
        byte[] tmp = new byte[4];
        
        for (int y = 0; y < photo.height(); y++) {
            for (int x = 0; x < photo.width(); x++) {
                int dx = x - px;
                int dy = y - py;
                double r = Math.sqrt(dx * dx + dy * dy);
                double theta = Math.atan2(dy, dx) - angle - angle2;
                theta = triangle( (float) (theta / Math.PI * sides * 0.5));

                double c = Math.cos(theta);
                double radiusc = scale / c;
                r = radiusc * triangle((float) (r / radiusc));

                theta += angle;

                double xx = r * Math.cos(theta) + px;
                double yy = r * Math.sin(theta) + py;
                xx = Math.max(0, Math.min(xx, photo.width() - 1));
                yy = Math.max(0, Math.min(yy, photo.height() - 1));

                double x1 = Math.floor(xx);
                double y1 = Math.floor(yy);
                double p = xx - x1;
                double q = yy - y1;

//                // TODO: 2015/12/6  
//                newPhoto.put(x, y, )

            }
        }

        return convert_to_bitmap(photo);
    }

}
