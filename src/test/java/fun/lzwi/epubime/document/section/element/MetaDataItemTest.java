package fun.lzwi.epubime.document.section.element;

import static org.junit.Assume.assumeTrue;

import org.junit.Test;

public class MetaDataItemTest {

    @Test
    public void testToString() {
        assumeTrue(!new MetaDataItem().toString().isEmpty());
    }
}
