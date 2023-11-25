package fun.lzwi.epubime.document;

import java.io.InputStream;

public class PackageDocumentReader {
    private final InputStream in;
    private String entry = "";

    public PackageDocumentReader(InputStream in) {
        super();
        this.in = in;
    }

    public PackageDocumentReader(InputStream in, String entry) {
        super();
        this.in = in;
        this.entry = entry;
    }

    public PackageDocument read() {
        PackageDocument packageDocument = PackageDocumentUtils
                .getPackageDocument(PackageDocumentUtils.getPackageElement(in));
        packageDocument.setHref(entry);
        return packageDocument;
    }
}
