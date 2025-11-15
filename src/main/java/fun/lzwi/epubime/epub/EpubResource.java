package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubResourceException;
import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * EPUB Resource Model Class
 * Represents a single resource file in an EPUB e-book, such as images, CSS stylesheets, etc.
 */
public class EpubResource {
    private String id;
    private String type;
    private String href;
    private String properties;
    private String fallback; // Fallback resource ID for core media type fallback mechanism
    private byte[] data;
    private File epubFile; // EPUB file reference for streaming processing

    /**
     * Default constructor
     */
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
        this.properties = other.properties;
        this.fallback = other.fallback;
        this.epubFile = other.epubFile;
        if (other.data != null) {
            this.data = other.data.clone();
        }
    }

    /**
     * Get resource ID
     * @return resource ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set resource ID
     * @param id resource ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get resource type (MIME type)
     * @return resource type
     */
    public String getType() {
        return type;
    }

    /**
     * Set resource type (MIME type)
     * @param type resource type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get resource data
     * If data already exists, return directly, otherwise try to stream read from EPUB file
     * @return resource data byte array (caller should not modify the returned array)
     */
    public byte[] getData() {
        // If data already exists, return directly
        if (data != null) {
            return data; // Avoid cloning for performance; caller must not modify
        }

        // If there is an EPUB file reference, try to stream read data

        if (epubFile != null && href != null) {

            try {

                data = ZipUtils.getZipFileBytes(epubFile, href);

                return data; // Avoid cloning for performance; caller must not modify

            } catch (IOException e) {

                // Log the error for debugging (in a real application, you might want to use a proper logging framework)

                System.err.println("Warning: Failed to read resource data for " + href + " from EPUB file " + epubFile.getName() + ": " + e.getMessage());

                return null;

            }

        }

        return null;
    }

    /**
     * Set resource data
     * @param data resource data byte array
     */
    public void setData(byte[] data) {
        if (data != null) {
            this.data = data.clone();
        }
    }

    /**
     * Get resource file path
     * @return resource file path
     */
    public String getHref() {
        return href;
    }

    /**
     * Set resource file path
     * @param href resource file path
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Get resource properties
     * @return resource properties
     */
    public String getProperties() {
        return properties;
    }

    /**
     * Set resource properties
     * @param properties resource properties
     */
    public void setProperties(String properties) {
        this.properties = properties;
    }

    /**
     * Get fallback resource ID
     * @return fallback resource ID
     */
    public String getFallback() {
        return fallback;
    }

    /**
     * Set fallback resource ID
     * @param fallback fallback resource ID
     */
    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    /**
     * Get the final available resource based on the fallback chain
     * @param allResources all resource list
     * @return final available resource, return itself if no fallback
     */
    public EpubResource getFallbackResource(List<EpubResource> allResources) {
        if (fallback == null || fallback.isEmpty()) {
            return this;
        }
        
        // Find fallback resource
        EpubResource fallbackResource = allResources.stream()
                .filter(r -> fallback.equals(r.getId()))
                .findFirst()
                .orElse(null);
        
        // If fallback resource is found, recursively find its fallback resource
        if (fallbackResource != null) {
            return fallbackResource.getFallbackResource(allResources);
        }
        
        // If fallback resource is not found, return itself
        return this;
    }

    /**
     * Get EPUB file reference
     * @return EPUB file reference
     */
    public File getEpubFile() {
        return epubFile;
    }

    /**
     * Set EPUB file reference
     * @param epubFile EPUB file reference
     */
    public void setEpubFile(File epubFile) {
        this.epubFile = epubFile;
    }

    /**
     * Get resource input stream for streaming processing of large files
     * @return input stream
     * @throws IOException IO exception
     */
    public InputStream getInputStream() throws IOException {
        if (epubFile != null && href != null) {
            // Use ZipFileManager to optimize ZIP access
            return ZipUtils.getZipFileInputStream(epubFile, href);
        }
        return null;
    }
    
    /**
     * Load resource data in batch
     * @param resources resource list
     * @param epubFile EPUB file
     * @throws IOException IO exception
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

     * @param processor consumer function for processing resource content

     */

    public void processContent(Consumer<InputStream> processor) throws EpubResourceException {

        if (epubFile != null && href != null) {

            try {

                ZipUtils.processHtmlContent(epubFile, href, processor);

            } catch (IOException e) {

                throw new EpubResourceException("Failed to process resource content for " + href + " from EPUB file " + epubFile.getName(), 
                    epubFile.getName(), href, e);

            }

        }

    }
}