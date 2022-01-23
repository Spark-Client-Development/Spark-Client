package me.wallhacks.spark.util.objects;

public class AuthException extends Exception {
    private static final long serialVersionUID = 1L;
    private String text;

    public AuthException(String s) {
        super(s);
        text = s;
    }

    public String getText() {
        return text;
    }
}
