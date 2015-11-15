package eleanor.photobooth.Functions;

/**
 * Created by Eleanor on 2015/11/14.
 */
public enum PHOTOREQUESTCODE {
    NONE(0),
    PHOTORAPH(1),
    PHOTOZOOM(2),
    PHOTORESULT(3),
    ORIGINPHOTO(4),
    ORIGINPIC(5);

    private final int code;

    PHOTOREQUESTCODE(final int code) {
        this.code = code;
    }

    public int toInt(){
        return code;
    }

}
