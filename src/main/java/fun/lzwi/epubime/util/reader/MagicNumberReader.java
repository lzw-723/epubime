package fun.lzwi.epubime.util.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class MagicNumberReader {
    private static final char[] MAGIC_NUMBER_0 = new char[] { 'P', 'K', 0x003, 0x004 };
    private static final String MAGIC_NUMBER_30 = "mimetype";
    private static final String MAGIC_NUMBER_38 = "application/epub+zip";

    public static void read(File file, byte[] b, long pos, int len) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(pos);
            if (randomAccessFile.read(b, 0, len) == -1) {
                throw new IOException("读取文件失败");
            }
            randomAccessFile.close();
        }
    }

    public static boolean check0(File file) throws IOException {

        byte[] b = new byte[MAGIC_NUMBER_0.length];
        read(file, b, 0, MAGIC_NUMBER_0.length);
        for (int i = 0; i < b.length; i++) {
            if (b[i] != MAGIC_NUMBER_0[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean check30(File file) throws IOException {
        byte[] b = new byte[MAGIC_NUMBER_30.length()];
        read(file, b, 30, MAGIC_NUMBER_30.length());
        return new String(b, StandardCharsets.UTF_8).equals(MAGIC_NUMBER_30);
    }

    public static boolean check38(File file) throws IOException {
        byte[] b = new byte[MAGIC_NUMBER_38.length()];
        read(file, b, 38, MAGIC_NUMBER_38.length());
        return new String(b, StandardCharsets.UTF_8).equals(MAGIC_NUMBER_38);
    }
}
