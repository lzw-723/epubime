package fun.lzwi.epubime;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Util;

public class EpubCheckerTest {

    @Test
    public void testCheck() throws IOException, ParserConfigurationException, SAXException {
        File file = Util.getFile("《坟》鲁迅.epub");
        assertTrue(EpubChecker.check(file));
    }

}
