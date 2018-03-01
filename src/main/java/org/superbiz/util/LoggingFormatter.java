package org.superbiz.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggingFormatter extends Formatter {
    @Override
    public String format(LogRecord lr) {
        final String threadName = Thread.currentThread().getName();
        final String myMessage = lr.getThrown() != null ?
                String.format("%s\n%s", lr.getMessage(), formatStackTrace(lr.getThrown())) :
                lr.getMessage();
        return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-4s [%7$s] %2$s %5$s %6$s%n",
                lr.getMillis(), lr.getSourceClassName(), lr.getThreadID(), lr.getLevel(), lr.getSourceMethodName(),
                myMessage, threadName);
    }

    private String formatStackTrace(Throwable thrown) {
        StringWriter sw = new StringWriter();
        thrown.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        return exceptionAsString;
    }
}
