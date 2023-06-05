package fun.lzwi.epubime;

public class LoggerUtils {
    private static boolean enable = true;
    public static boolean isEnable() {
        return enable;
    }

    public static void setEnable(boolean enable) {
        LoggerUtils.enable = enable;
    }

    public static Logger from(Class<?> from) {
        return new Logger(from);
    }

    public static class Logger {
        private java.util.logging.Logger logger;

        public Logger(Class<?> from) {
            this.logger = java.util.logging.Logger.getLogger(from.getName());
        }

        public void info(String msg) {
            if (LoggerUtils.isEnable()) {
                logger.info(msg);
            }
        }
    }
}
