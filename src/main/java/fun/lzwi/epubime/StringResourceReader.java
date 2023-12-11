package fun.lzwi.epubime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StringResourceReader {
    public String read(Resource res) throws IOException {
        InputStreamReader iReader = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8);
        try (BufferedReader bReader = new BufferedReader(iReader)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bReader.readLine()) != null) {
                sb.append(line);
            }
            bReader.close();
            iReader.close();
            return sb.toString();
        }
    }
}
