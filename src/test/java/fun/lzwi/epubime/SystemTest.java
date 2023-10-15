package fun.lzwi.epubime;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class SystemTest {
    @Test
    public void testEpubs() throws IOException, ParserConfigurationException, SAXException {
        // src\test\resources\epub
        // 此目录存放收集自互联网的epub文件，可能涉及版权问题，故不上传
        File dir = Utils.getFile("epub");
        if (dir == null) {
            return;
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith(".epub")) {
                testBook(file);
            }
        }
    }

    public void testBook(File file) throws IOException, ParserConfigurationException, SAXException {
        EpubReader reader = new EpubReader(new EpubFile(file));
        Epub epub = reader.read();
        assertNotNull(String.format("读取：%s失败", file.getName()), epub);
    }
}
