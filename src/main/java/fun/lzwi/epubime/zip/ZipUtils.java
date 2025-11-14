package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.cache.EpubCacheManager;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

public class ZipUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static List<String> getZipFileList(File zipFile) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        try {
            return zip.stream().map(ZipEntry::getName).collect(java.util.stream.Collectors.toList());
        } finally {
            // Note: We don't close the ZIP file because it may be reused
        }
    }

    public static String getZipFileContent(File zipFile, String fileName) throws IOException {
        // Try to get from cache
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        String cachedContent = cache.getTextContentCache().get(fileName);
        if (cachedContent != null) {
            return cachedContent;
        }
        
        // Cache miss, read from ZIP file
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        if (entry == null) {
            return null;
        }
        try (InputStream in = zip.getInputStream(entry); BufferedReader reader =
                new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
            String content = reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
            // Cache result
            cache.getTextContentCache().put(fileName, content);
            return content;
        } finally {
            // Note: We don't close the ZIP file because it may be reused
        }
    }

    public static byte[] getZipFileBytes(File zipFile, String fileName) throws IOException {
        // Try to get from cache
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        byte[] cachedData = cache.getBinaryContentCache().get(fileName);
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
            cache.getBinaryContentCache().put(fileName, data.clone());
            return data;
        } finally {
            // Note: We don't close the ZIP file because it may be reused
        }
    }

    /**
     * Stream process content in ZIP file to avoid loading entire file into memory
     * @param zipFile ZIP file
     * @param fileName File name to process
     * @param processor Consumer function to process input stream
     * @throws IOException
     */
    public static void processZipFileContent(File zipFile, String fileName, Consumer<InputStream> processor) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        if (entry == null) {
            return;
        }
        try (InputStream in = zip.getInputStream(entry)) {
            processor.accept(in);
        } finally {
            // Note: We don't close the ZIP file because it may be reused
        }
    }

    /**
     * Get input stream for specified file in ZIP file for streaming processing
     * @param zipFile ZIP file
     * @param fileName File name to get
     * @return Input stream, caller needs to be responsible for closing
     * @throws IOException
     */
    public static InputStream getZipFileInputStream(File zipFile, String fileName) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        if (entry == null) {
            // Note: We can't close zip here because it may be reused
            return null;
        }
        return new ZipFileInputStream(zip, zip.getInputStream(entry));
    }

    /**
     * Batch read multiple file contents using single ZIP file handle
     * @param zipFile ZIP file
     * @param fileNames List of file names to read
     * @return Mapping from file names to contents
     * @throws IOException
     */
    public static java.util.Map<String, String> getMultipleZipFileContents(File zipFile, List<String> fileNames) throws IOException {
        java.util.Map<String, String> contents = new java.util.HashMap<>();
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry); BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
                    String content = reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
                    contents.put(fileName, content);
                }
            } else {
                contents.put(fileName, null);
            }
        }
        // Note: We don't close the ZIP file because it may be reused
        return contents;
    }

    /**
     * Batch read byte arrays of multiple files using single ZIP file handle
     * @param zipFile ZIP file
     * @param fileNames List of file names to read
     * @return Mapping from file names to byte arrays
     * @throws IOException
     */
    public static java.util.Map<String, byte[]> getMultipleZipFileBytes(File zipFile, List<String> fileNames) throws IOException {
        java.util.Map<String, byte[]> contents = new java.util.HashMap<>();
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry);
                     ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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
     * Stream process HTML content to avoid loading entire file into memory
     * @param zipFile ZIP file
     * @param htmlFileName HTML file name
     * @param processor Consumer function to process HTML content
     * @throws IOException
     */
    public static void processHtmlContent(File zipFile, String htmlFileName, Consumer<InputStream> processor) throws IOException {
        processZipFileContent(zipFile, htmlFileName, processor);
    }

    /**
     * Batch stream process multiple HTML file contents
     * @param zipFile ZIP file
     * @param htmlFileNames HTML file name list
     * @param processor Consumer function to process each HTML content
     * @throws IOException
     */
    public static void processMultipleHtmlContents(File zipFile, List<String> htmlFileNames, 
                                                   BiConsumer<String, InputStream> processor) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : htmlFileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry)) {
                    processor.accept(fileName, in);
                } finally {
                    // Note: We don't close the ZIP file because it may be reused
                }
            }
        }
    }

    /**
     * Custom input stream that automatically closes ZipFile
     */
    private static class ZipFileInputStream extends InputStream {
        private final ZipFile zipFile;
        private final InputStream inputStream;

        public ZipFileInputStream(ZipFile zipFile, InputStream inputStream) {
            this.zipFile = zipFile;
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
                // Note: We don't close zipFile here because it may be reused by other operations
                // ZipFile closing is managed uniformly by external manager
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