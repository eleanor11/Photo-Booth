package eleanor.photobooth.Functions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Eleanor on 2015/11/14.
 */
public interface FunctionAccessor {

    Intent take_photo();
    Intent get_photo_from_album();
    Intent zoom_photo(Uri uri, boolean fixed);
    Bitmap compress_photo(Bitmap photo, boolean fixed, int size);

}
