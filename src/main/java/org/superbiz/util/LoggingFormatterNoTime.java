package org.superbiz.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggingFormatterNoTime extends BaseLoggingFormatter {
    @Override
    public String format(LogRecord lr) {
        final String threadName = Thread.currentThread().getName();
        final String myMessage = lr.getThrown() != null ?
                String.format("%s\n%s", lr.getMessage(), formatStackTrace(lr.getThrown())) :
                lr.getMessage();
        return String.format("%4$-4s [%7$s] %2$s %5$s %6$s%n",
                lr.getMillis(), lr.getSourceClassName(), lr.getThreadID(), lr.getLevel(), lr.getSourceMethodName(),
                myMessage, threadName);
    }
}
