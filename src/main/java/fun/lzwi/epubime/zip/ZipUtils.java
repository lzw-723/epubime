package fun.lzwi.epubime.zip;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    // 解压ZIP文件
    public static void unzip(File zipFile, File destDir) throws IOException {
        ZipFile zip = new ZipFile(zipFile);

        // 遍历ZIP文件中的每个条目
        zip.stream().forEach(entry -> {
            // 创建目标文件
            File targetFile = new File(destDir, entry.getName());
            // 如果条目是目录，则创建目录
            if (entry.isDirectory()) {
                targetFile.mkdirs();
            }
            // 如果条目是文件，则写入文件
            else {
                // 创建目标文件的父目录
                targetFile.getParentFile().mkdirs();
                try (InputStream in = zip.getInputStream(entry); OutputStream out =
                        Files.newOutputStream(targetFile.toPath())) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        zip.close();
    }

    public static List<String> getZipFileList(File zipFile) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            return zip.stream().map(ZipEntry::getName).collect(java.util.stream.Collectors.toList());
        }
    }

    public static String getZipFileContent(File zipFile, String fileName) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry == null) {
                return null;
            }
            try (InputStream in = zip.getInputStream(entry); BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
                return reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
            }
        }
    }

    public static byte[] getZipFileBytes(File zipFile, String fileName) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry == null) {
                return null;
            }
            try (InputStream in = zip.getInputStream(entry);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                return out.toByteArray();
            }
        }
    }
}
