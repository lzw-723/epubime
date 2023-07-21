package fun.lzwi.epubime;

import java.io.IOException;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class EpubFileTest {
    @Before
    public void setUp() throws ZipException, IOException, ParserConfigurationException, SAXException {
        new EpubFile(Utils.getFile("《坟》鲁迅.epub"));
    }

    // @Test
    // public void testGetPackageDocument() throws IOException, ParserConfigurationException, SAXException {
    //     PackageDocument packageDocument = epubFile.getPackageDocument();
    //     assertNotNull(packageDocument);
    // }

}
