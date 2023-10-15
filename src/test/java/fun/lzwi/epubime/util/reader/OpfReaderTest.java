package fun.lzwi.epubime.util.reader;

import fun.lzwi.Utils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class OpfReaderTest {
    private static OpfReader opfReader;

    @BeforeClass
    public static void beforeClass() throws IOException {
        opfReader = new OpfReader(Utils.getFile("content.opf"));
    }

    @Test
    public void testConstructors() throws ParserConfigurationException, SAXException, IOException {
        new OpfReader(Utils.getFile("content.opf"));
        new OpfReader(Utils.getFile("content.opf").getPath());
        new OpfReader(Files.newInputStream(Paths.get(Utils.getFile("content.opf").getPath())));
    }

    @Test
    public void testGetUniqueIdentifier() {
        assertEquals("正确读取unique-identifier", "BookId", opfReader.getUniqueIdentifier());
    }

    @Test
    public void testGetVersion() {
        assertEquals("正确读取version", "3.0", opfReader.getVersion());
    }
}
