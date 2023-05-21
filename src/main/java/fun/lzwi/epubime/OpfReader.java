package fun.lzwi.epubime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class OpfReader {
    private InputStream opfi;
    private String version;
    private String uniqueIdentifier;

    public OpfReader(InputStream opfi) throws ParserConfigurationException, SAXException, IOException {
        this.opfi = opfi;
        init();
    }

    public String getVersion() {
        return version;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public OpfReader(File opf) throws ParserConfigurationException, SAXException, IOException {
        this.opfi = new FileInputStream(opf);
        init();
    }

    public OpfReader(String path) throws ParserConfigurationException, SAXException, IOException {
        this.opfi = new FileInputStream(new File(path));
        init();
    }

    private void init() throws ParserConfigurationException, SAXException, IOException {
        Node pkg = OpfUtils.getPackage(opfi);
        version = OpfUtils.getVersion(pkg);
        uniqueIdentifier = OpfUtils.getUniqueIdentifier(pkg);
    }
}
