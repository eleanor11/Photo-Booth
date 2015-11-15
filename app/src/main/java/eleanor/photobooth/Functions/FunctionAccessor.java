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

    Bitmap rescale_photo(Bitmap photo, int newWidth, int newHeight);

    String save_photo(Bitmap photo);
    Bitmap get_photo(String fileName);

    Mat convert_to_mat(Bitmap bmp);
    Bitmap convert_to_bitmap(Mat mat);

    Mat mirror(Mat photo);

}
