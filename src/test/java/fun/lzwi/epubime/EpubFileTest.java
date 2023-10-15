package fun.lzwi.epubime;

import fun.lzwi.Utils;
import org.junit.Before;

import java.io.IOException;

public class EpubFileTest {
    @Before
    public void setUp() throws IOException {
        new EpubFile(Utils.getFile("《坟》鲁迅.epub"));
    }

    // @Test
    // public void testGetPackageDocument() throws IOException, ParserConfigurationException, SAXException {
    //     PackageDocument packageDocument = epubFile.getPackageDocument();
    //     assertNotNull(packageDocument);
    // }

}
