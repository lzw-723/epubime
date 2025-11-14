package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.cache.EpubCacheManager;

import java.io.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIP utility class
 * Provides operations for reading ZIP files, streaming processing, etc., supports caching and ZIP file handle reuse
 */
public class ZipUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * Get file list in ZIP file
     *
     * @param zipFile ZIP file
     * @return list of file names
     * @throws IOException IO exception
     */
    public static List<String> getZipFileList(File zipFile) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        try {
            return zip.stream().map(ZipEntry::getName).collect(java.util.stream.Collectors.toList());
        } finally {
            // Release ZIP file handle, reduce reference count
            ZipFileManager.getInstance().releaseZipFile();
        }
    }

    /**
     * Get text content in ZIP file
     *
     * @param zipFile  ZIP file
     * @param fileName file name
     * @return file content, returns null if it does not exist
     * @throws IOException IO exception
     */
    public static String getZipFileContent(File zipFile, String fileName) throws IOException {

        // Prevent directory traversal attacks

        if (!PathValidator.isPathSafe("", fileName)) {

            throw new java.io.IOException("Invalid file path: " + fileName);

        }


        // Try to get from cache

        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);

        String cachedContent = cache.getTextContent(fileName);

        if (cachedContent != null) {

            return cachedContent;

        }


        // Cache miss, read from ZIP file

        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);

        ZipEntry entry = zip.getEntry(fileName);

        if (entry == null) {

            // Release ZIP file handle, reduce reference count

            ZipFileManager.getInstance().releaseZipFile();

            return null;

        }

        try (InputStream in = zip.getInputStream(entry); BufferedReader reader =

                new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {

            String content = reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));

            // Cache result

            cache.setTextContent(fileName, content);

            return content;

        } finally {

            // Release ZIP file handle, reduce reference count

            ZipFileManager.getInstance().releaseZipFile();

        }

    }

    /**
     * Get byte array content in ZIP file
     *
     * @param zipFile  ZIP file
     * @param fileName file name
     * @return file content byte array, returns null if it does not exist
     * @throws IOException IO exception
     */
    public static byte[] getZipFileBytes(File zipFile, String fileName) throws IOException {

        // Prevent directory traversal attacks

        if (!PathValidator.isPathSafe("", fileName)) {

            throw new java.io.IOException("Invalid file path: " + fileName);

        }


        // Try to get from cache

        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);

        byte[] cachedData = cache.getBinaryContent(fileName);

        if (cachedData != null) {

            return cachedData.clone(); // Return clone to avoid modifying cache data

        }


        // Cache miss, read from ZIP file

        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);

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

            byte[] data = out.toByteArray();

            // Cache result

            cache.setBinaryContent(fileName, data.clone());

            return data;

        } finally {

            // Note: We don't close the ZIP file because it may be reused

        }

    }

    /**
     * Stream processing content in ZIP file to avoid loading entire file into memory
     *
     * @param zipFile   ZIP file
     * @param fileName  file name to process
     * @param processor consumer function to process input stream
     * @throws IOException IO exception
     */
    public static void processZipFileContent(File zipFile, String fileName, Consumer<InputStream> processor) throws IOException {

        // Prevent directory traversal attacks

        if (!PathValidator.isPathSafe("", fileName)) {

            throw new java.io.IOException("Invalid file path: " + fileName);

        }


        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);

        ZipEntry entry = zip.getEntry(fileName);

        if (entry == null) {

            // Release ZIP file handle, reduce reference count

            ZipFileManager.getInstance().releaseZipFile();

            return;

        }

        try (InputStream in = zip.getInputStream(entry)) {

            processor.accept(in);

        } finally {

            // Note: We don't close the ZIP file because it may be reused

        }

    }

    /**
     * Get input stream for specified file in ZIP file, for streaming processing
     *
     * @param zipFile  ZIP file
     * @param fileName file name to get
     * @return input stream, caller needs to close it
     * @throws IOException IO exception
     */
    public static InputStream getZipFileInputStream(File zipFile, String fileName) throws IOException {

        // Prevent directory traversal attacks

        if (!PathValidator.isPathSafe("", fileName)) {

            throw new java.io.IOException("Invalid file path: " + fileName);

        }


        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);

        ZipEntry entry = zip.getEntry(fileName);

        if (entry == null) {

            // Note: We can't close zip here because it may be reused

            return null;

        }

        return new ZipFileInputStream(zip.getInputStream(entry));

    }

    /**
     * Batch read multiple file contents using single ZIP file handle
     *
     * @param zipFile   ZIP file
     * @param fileNames list of file names to read
     * @return mapping from file name to content
     * @throws IOException IO exception
     */
    public static java.util.Map<String, String> getMultipleZipFileContents(File zipFile, List<String> fileNames) throws IOException {
        // Prevent directory traversal attacks
        for (String fileName : fileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new IOException("Invalid file path: " + fileName);
            }
        }

        java.util.Map<String, String> contents = new java.util.HashMap<>();
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry); BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
                    String content =
                            reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
                    contents.put(fileName, content);
                }
            } else {
                contents.put(fileName, null);
            }
        }
        // Release ZIP file handle, reduce reference count
        ZipFileManager.getInstance().releaseZipFile();
        return contents;
    }

    /**
     * Batch read multiple file byte arrays using single ZIP file handle
     *
     * @param zipFile   ZIP file
     * @param fileNames list of file names to read
     * @return mapping from file name to byte array
     * @throws IOException IO exception
     */
    public static java.util.Map<String, byte[]> getMultipleZipFileBytes(File zipFile, List<String> fileNames) throws IOException {
        // Prevent directory traversal attacks
        for (String fileName : fileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new IOException("Invalid file path: " + fileName);
            }
        }

        java.util.Map<String, byte[]> contents = new java.util.HashMap<>();
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry); ByteArrayOutputStream out =
                        new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    contents.put(fileName, out.toByteArray());
                }
            } else {
                contents.put(fileName, null);
            }
        }
        // Note: We don't close the ZIP file because it may be reused
        return contents;
    }

    /**
     * Stream processing HTML content to avoid loading entire file into memory
     *
     * @param zipFile      ZIP file
     * @param htmlFileName HTML file name
     * @param processor    consumer function to process HTML content
     * @throws IOException IO exception
     */
    public static void processHtmlContent(File zipFile, String htmlFileName, Consumer<InputStream> processor) throws IOException {
        // Prevent directory traversal attacks
        if (!PathValidator.isPathSafe("", htmlFileName)) {
            throw new IOException("Invalid file path: " + htmlFileName);
        }

        processZipFileContent(zipFile, htmlFileName, processor);
    }

    /**
     * Batch stream processing multiple HTML file contents
     *
     * @param zipFile       ZIP file
     * @param htmlFileNames HTML file name list
     * @param processor     consumer function to process each HTML content
     * @throws IOException IO exception
     */
    public static void processMultipleHtmlContents(File zipFile, List<String> htmlFileNames, BiConsumer<String,
            InputStream> processor) throws IOException {
        // Prevent directory traversal attacks
        for (String fileName : htmlFileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new IOException("Invalid file path: " + fileName);
            }
        }

        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : htmlFileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry)) {
                    processor.accept(fileName, in);
                } finally {
                    // Release ZIP file handle, reduce reference count
                }
            }
        }
        // Release ZIP file handle, reduce reference count
        ZipFileManager.getInstance().releaseZipFile();
    }

    /**
     * Custom input stream, automatically release ZIP file handle reference
     */
    private static class ZipFileInputStream extends InputStream {
        private final InputStream inputStream;

        public ZipFileInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return inputStream.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return inputStream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public void close() throws IOException {
            try {
                inputStream.close();
            } finally {
                // Release ZIP file handle reference when stream is closed
                ZipFileManager.getInstance().releaseZipFile();
            }
        }

        @Override
        public void mark(int readlimit) {
            inputStream.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            inputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }
    }
}