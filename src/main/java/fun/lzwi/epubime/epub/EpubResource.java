package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EpubResource {
    private String id;
    private String type;
    private String href;
    private byte[] data;
    private File epubFile; // EPUB file reference for streaming processing

    public EpubResource() {
        // Default constructor
    }
    
    /**
     * Copy constructor
     * @param other EpubResource object to copy
     */
    public EpubResource(EpubResource other) {
        this.id = other.id;
        this.type = other.type;
        this.href = other.href;
        this.epubFile = other.epubFile;
        if (other.data != null) {
            this.data = other.data.clone();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        // If data already exists, return directly
        if (data != null) {
            return data.clone();
        }
        
        // If there is an EPUB file reference, try to stream read data
        if (epubFile != null && href != null) {
            try {
                data = ZipUtils.getZipFileBytes(epubFile, href);
                return data != null ? data.clone() : null;
            } catch (IOException e) {
                // Stream read failed, return null
                return null;
            }
        }
        
        return null;
    }

    public void setData(byte[] data) {
        if (data != null) {
            this.data = data.clone();
        }
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public File getEpubFile() {
        return epubFile;
    }

    public void setEpubFile(File epubFile) {
        this.epubFile = epubFile;
    }

    /**
     * Get input stream for resource for streaming processing of large files
     * @return Input stream
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        if (epubFile != null && href != null) {
            // Use ZipFileManager to optimize ZIP access
            return ZipUtils.getZipFileInputStream(epubFile, href);
        }
        return null;
    }
    
    /**
     * Batch load resource data
     * @param resources Resource list
     * @param epubFile EPUB file
     * @throws IOException
     */
    public static void loadResourceData(List<EpubResource> resources, File epubFile) throws IOException {
        // Collect all resource paths that need to be loaded
        List<String> hrefs = new java.util.ArrayList<>();
        for (EpubResource resource : resources) {
            if (resource.epubFile != null && resource.href != null) {
                hrefs.add(resource.href);
            }
        }
        
        // Use ZIP file stream reuse mechanism to read all resource data at once
        Map<String, byte[]> resourceData = ZipUtils.getMultipleZipFileBytes(epubFile, hrefs);
        
        // Set data to corresponding resource objects
        for (EpubResource resource : resources) {
            if (resource.href != null) {
                byte[] data = resourceData.get(resource.href);
                if (data != null) {
                    resource.setData(data);
                }
            }
        }
    }
    
    /**
     * Stream process resource content to avoid loading entire file into memory
     * @param processor Consumer function to process resource content
     * @throws IOException
     */
    public void processContent(java.util.function.Consumer<InputStream> processor) throws IOException {
        if (epubFile != null && href != null) {
            ZipUtils.processHtmlContent(epubFile, href, processor);
        }
    }
}