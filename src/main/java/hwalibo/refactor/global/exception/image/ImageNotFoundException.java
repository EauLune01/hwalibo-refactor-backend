package hwalibo.refactor.global.exception.image;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message){
        super(message);
    }
}
