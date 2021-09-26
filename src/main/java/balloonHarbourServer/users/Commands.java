package balloonHarbourServer.users;

public enum Commands {
    ;
    public static final int LOGIN = 0x01;
    public static final int LOGOUT = 0x02;
    public static final int DISCONNECT = 0x03;
    public static final int MESSAGE = 0x04;
    public static final int PRINT_ERROR = 0x05;
    public static final int SUCCESSFULLY_LOGGED_IN = 0x06;
    public static final int WRONG_PASSWORD = 0x07;
    public static final int NOT_REGISTERED = 0x08;
    public static final int SUCCESSFULLY_REGISTERED = 0x09;
    public static final int KEY = 0x0A;
}