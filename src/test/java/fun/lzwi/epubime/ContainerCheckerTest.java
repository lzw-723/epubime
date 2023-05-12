package fun.lzwi.epubime;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Util;

public class ContainerCheckerTest {
    @Test
    public void testCheckContainer() throws SAXException, IOException, ParserConfigurationException {
        assertTrue(ContainerChecker.checkContainer(Util.getFile("container.xml")));
    }

    @Test
    public void testCheckRootFiles() throws ParserConfigurationException, SAXException, IOException {
        assertTrue(ContainerChecker.checkRootFiles(Util.getFile("container.xml")));
    }
}
