package com.clj.jaf.utils;

public class JEventIdUtil {
    public static final boolean Debug_Or_Release_App = true;
    private static int indexEvent = 0;
    private static final int CoreEvent = getNextEvent();
    public static final int Success = getNextEvent();
    public static final int Failure = getNextEvent();
    public static final int Before = getNextEvent();
    public static final int Cancel = getNextEvent();
    public static final int Update = getNextEvent();
    public static final int Work = getNextEvent();
    public static final int InProgress = getNextEvent();
    public static final int NetworkChange = getNextEvent();

    public static int getNextEvent() {
        return indexEvent++;
    }
}
