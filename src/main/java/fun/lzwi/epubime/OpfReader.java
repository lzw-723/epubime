package fun.lzwi.epubime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.bean.PackageInfo;

public class OpfReader {
    private InputStream opfi;
    private PackageInfo packageInfo;

    public OpfReader(InputStream opfi) throws ParserConfigurationException, SAXException, IOException {
        this.opfi = opfi;
        init();
    }

    public String getVersion() {
        return packageInfo.getVersion();
    }

    public String getUniqueIdentifier() {
        return packageInfo.getUniqueIdentifier();
    }

    public PackageInfo getPackageInfo() {
        return packageInfo.clone();
    }

    public OpfReader(File opf) {
        try {
            this.opfi = new FileInputStream(opf);
            init();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public OpfReader(String path) throws ParserConfigurationException, SAXException, IOException {
        this.opfi = new FileInputStream(new File(path));
        init();
    }

    private void init() throws ParserConfigurationException, SAXException, IOException {
        Node pkg = OpfUtils.getPackage(opfi);
        packageInfo = OpfUtils.getPackageInfo(pkg);
    }
}
