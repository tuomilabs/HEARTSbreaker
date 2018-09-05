package org.tuomilabs.heart.logging;

public class Logger {
    /**
     * 0 - debug
     * 1 - info
     * 2 - warning
     * 3 - error
     * 4 - none
     */
    private static int level = 1;

    public static int LEVEL_DEBUG = 0;
    public static int LEVEL_INFO = 1;
    public static int LEVEL_WARNING = 2;
    public static int LEVEL_ERROR = 3;
    public static int LEVEL_NONE = 4;


    public static void setLevel(int debugLevel) {
        level = debugLevel;
    }


    public static void info(Object text) {
        if (level <= LEVEL_INFO) {
            System.out.println("[INFO]: " + text.toString());
        }
    }
}
