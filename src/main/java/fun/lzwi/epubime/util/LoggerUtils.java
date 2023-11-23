package fun.lzwi.epubime.util;

public class LoggerUtils {
    private static boolean enable = false;

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
        private Class<?> from;

        public Logger(Class<?> from) {
            this.from = from;
        }

        public void info(String msg) {
            if (enable) {
                System.err.println("info: " + msg + "\tat " + from.getSimpleName() + ".java");
            }
        }

        public void warn(String msg) {
            if (enable) {
                System.err.println("warn: " + msg + "\tat " + from.getSimpleName() + ".java");
            }
        }

        public void error(String msg) {
            if (enable) {
                System.err.println("error: " + msg + "\tat " + from.getSimpleName() + ".java");
            }
        }

    }
}
