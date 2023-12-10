package fun.lzwi.epubime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BytesResourceReader {
    public byte[] read(Resource res) throws IOException {
        InputStream is = res.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int read;
        while ((read = is.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }
}
