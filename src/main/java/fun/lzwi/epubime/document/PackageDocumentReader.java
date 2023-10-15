package fun.lzwi.epubime.document;

import java.io.InputStream;

public class PackageDocumentReader {
    private final InputStream in;

    public PackageDocumentReader(InputStream in) {
        super();
        this.in = in;
    }

    public PackageDocument read(){
        return PackageDocumentUtils.getPackageDocument(PackageDocumentUtils.getPackageElement(in));
    }
}
