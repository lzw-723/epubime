package fun.lzwi.epubime;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;
import fun.lzwi.epubime.document.PackageDocument;

public class EpubFileTest {
    private static EpubFile epubFile;

    @Before
    public void setUp() throws ZipException, IOException, ParserConfigurationException, SAXException {
        epubFile = new EpubFile(Utils.getFile("《坟》鲁迅.epub"));
    }

    @Test
    public void testGetPackageDocument() {
        PackageDocument packageDocument = epubFile.getPackageDocument();
        assertNotNull(packageDocument);
    }

}
