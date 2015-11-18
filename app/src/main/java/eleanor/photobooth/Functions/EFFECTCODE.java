package eleanor.photobooth.Functions;

/**
 * Created by Eleanor on 2015/11/18.
 */
public enum EFFECTCODE {
    MIRROE(0),
    ORIGIN(4);
    private final int code;

    EFFECTCODE(final int code) {
        this.code = code;
    }

    public int toInt(){
        return code;
    }
}
