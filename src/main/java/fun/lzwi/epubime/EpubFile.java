package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class EpubFile {

    private ZipFile zipFile;

    public EpubFile(File file) throws ZipException, IOException {
        zipFile = new ZipFile(file);
    }

    public EpubFile(InputStream stream) {
        // TODO: 从InputStream构造
    }

    public EpubFile(String path) {
        // TODO: 从path构造
    }

    public EpubFile forEach(Consumer<ZipEntry> e) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            e.accept(entry);
        }
        return this;
    }

    protected ZipEntry getEntry(String name) {
        return zipFile.getEntry(name);
    }

    protected InputStream getInputStream(String entry) throws IOException {
        return zipFile.getInputStream(getEntry(entry));
    }
}
