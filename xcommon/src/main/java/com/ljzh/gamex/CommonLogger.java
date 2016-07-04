package com.ljzh.gamex;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonLogger {
    private static Logger logger;

    static {
        DOMConfigurator.configure("log4j.xml");
        logger = LoggerFactory.getLogger("Logger");
    }

    public static void info(String s, Object... objects) {
        logger.info(String.format(s, objects));
    }

    public static boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public static void debug(String s, Object...objects) {
        logger.debug(String.format(s, objects));
    }

    public static void debug(Throwable throwable, String s, Object... objects) {
        logger.debug(String.format(s, objects), throwable);
    }

    public static void error(String s, Object... objects) {
        logger.error(String.format(s, objects));
    }

    public static void error(Throwable throwable, String s, Object... objects) {
        logger.error(String.format(s, objects), throwable);
    }

    public static void main(String[] args) {
        CommonLogger.info("info");
        CommonLogger.info("isDebugEnabled:" + CommonLogger.isDebugEnabled());
        CommonLogger.debug("debug");
        CommonLogger.error("error");
    }
}
