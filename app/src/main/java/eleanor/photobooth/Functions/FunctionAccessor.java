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

    Bitmap rotate_bitmap(Bitmap bitmap);

    Bitmap mirrorUp(Bitmap photo);
    Bitmap mirrorLeft(Bitmap photo);
    Bitmap mirrorDown(Bitmap photo);
    Bitmap mirrorRight(Bitmap photo);

}
