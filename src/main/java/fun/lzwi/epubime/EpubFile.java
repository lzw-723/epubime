package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class EpubFile {
    private final ZipFile zipFile;


    public EpubFile(File file) throws IOException {
        zipFile = new ZipFile(file);
    }

    protected InputStream getInputStream(String entry) throws IOException {
        return zipFile.getInputStream(zipFile.getEntry(entry));
    }

}
