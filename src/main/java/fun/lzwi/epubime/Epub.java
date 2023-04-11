package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Epub {

    private ZipFile zipFile;

    public Epub(File file) throws ZipException, IOException {
        zipFile = new ZipFile(file);
    }

    public Epub(InputStream stream) {
    }

    public Epub(String path) {
    }

    public Epub forEach(Consumer<ZipEntry> e) {
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
