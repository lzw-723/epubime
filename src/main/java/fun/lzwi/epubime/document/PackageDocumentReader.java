package fun.lzwi.epubime.document;

import java.io.InputStream;

public class PackageDocumentReader {
    private InputStream in;

    public PackageDocumentReader(InputStream in) {
        super();
        this.in = in;
    }

    public PackageDocument read(){
        PackageDocument packageDocument = PackageDocumentUtils.getPackageDocument(PackageDocumentUtils.getPackageElement(in));
        return packageDocument;
    }
}
