package org.superbiz.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;

public abstract class BaseLoggingFormatter extends Formatter{

    String formatStackTrace(Throwable thrown) {
        StringWriter sw = new StringWriter();
        thrown.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        return exceptionAsString;
    }
}
