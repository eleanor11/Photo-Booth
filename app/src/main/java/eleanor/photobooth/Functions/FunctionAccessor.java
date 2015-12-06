package eleanor.photobooth.Functions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import org.opencv.core.Mat;

/**
 * Created by Eleanor on 2015/11/14.
 */
public interface FunctionAccessor {

    Intent take_photo();
    Intent get_photo_from_album();
    Intent zoom_photo(Uri uri, boolean fixed);

    Bitmap rescale_photo(Bitmap photo, float scale);
    Bitmap rescale_photo(Bitmap photo, int newWidth, int newHeight);

    String save_photo(Bitmap photo);
    String save_photo(Bitmap photo, int opt);   //opt 0:ori, 1:tmp, other: date
    Bitmap get_photo(String fileName);

    Bitmap rotate_bitmap(Bitmap bitmap);

    Bitmap mirrorUp(Bitmap photo);
    Bitmap mirrorUp(Bitmap photo, int row);
    Bitmap mirrorLeft(Bitmap photo);
    Bitmap mirrorLeft(Bitmap photo, int col);
    Bitmap mirrorDown(Bitmap photo);
    Bitmap mirrorDown(Bitmap photo, int row);
    Bitmap mirrorRight(Bitmap photo);
    Bitmap mirrorRight(Bitmap photo, int col);

    Bitmap addLineRow(Bitmap photo);
    Bitmap addLineRow(Bitmap photo, int row);
    Bitmap addLineRow(Bitmap photo, int row, String color);
    Bitmap addLineCol(Bitmap photo);
    Bitmap addLineCol(Bitmap photo, int col);
    Bitmap addLineCol(Bitmap photo, int col, String color);

    Bitmap stretch(Bitmap photo);
    Bitmap stretch(Bitmap photo, float ratio);
    Bitmap stretch(Bitmap photo, int px, int py);
    Bitmap stretch(Bitmap photo, int px, int py, float ratio);
    Bitmap squeeze(Bitmap photo);
    Bitmap squeeze(Bitmap photo, float ratio);
    Bitmap squeeze(Bitmap photo, int px, int py);
    Bitmap squeeze(Bitmap photo, int px, int py, float ratio);

    Bitmap addCircle(Bitmap photo);
    Bitmap addCircle(Bitmap photo, float ratio);
    Bitmap addCircle(Bitmap photo, int px, int py);
    Bitmap addCircle(Bitmap photo, int px, int py, float ratio);
    Bitmap addCircle(Bitmap photo, int px, int py, float ratio, String color);

    Bitmap kaleidoscope(Bitmap photo);
    Bitmap kaleidoscope(Bitmap photo, float ratio);
    Bitmap kaleidoscope(Bitmap photo, int px, int py);
    Bitmap kaleidoscope(Bitmap photo, int px, int py, float ratio);

    Bitmap addPoint(Bitmap photo);
    Bitmap addPoint(Bitmap photo, int px, int py);
    Bitmap addPoint(Bitmap photo, int px, int py, String color);

}
