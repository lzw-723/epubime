package fun.lzwi.epubime.epub;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fun.lzwi.epubime.exception.EpubResourceException;
import fun.lzwi.epubime.zip.ZipManagedInputStream;
import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * Get resource data - DEPRECATED: Use getInputStream() for streaming to avoid loading entire file into memory
     * If data already exists, return directly, otherwise try to stream read from EPUB file
     * 优化: 减少冗余clone, ZipUtils.getZipFileBytes已返回防御性clone
     * @return resource data byte array
     * @throws IOException if reading from EPUB file fails
     * @deprecated Use streaming methods instead to avoid memory issues with large files
     */
    @Deprecated
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "返回byte[]引用是有意的设计，调用方需要访问资源数据")
    public byte[] getData() throws IOException {
        // If data already exists, return directly
        if (data != null) {
            // 优化: data已经是resource的私有数据,直接返回无需clone
            // 如果调用方需要修改,应该自行clone
            return data;
        }

        // If there is an EPUB file reference, try to stream read data
        if (epubFile != null && href != null) {
            // 优化: ZipUtils.getZipFileBytes已返回防御性clone,直接赋值无需再clone
            data = ZipUtils.getZipFileBytes(epubFile, href);
            return data;
        }

        return null;
    }

    /**
     * Set resource data
     * 优化: 克隆传入数据以保护内部状态，但调用方应避免重复克隆
     * @param data resource data byte array
     */
    public void setData(byte[] data) {
        if (data != null) {
            // 优化: 保留clone保护内部状态，但文档说明调用方不应重复克隆
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
     * Get the final available resource based on the fallback chain.
     * Uses cycle detection to prevent infinite recursion when resources
     * reference each other as fallbacks.
     * @param allResources all resource list
     * @return final available resource, return itself if no fallback or cycle detected
     */
    public EpubResource getFallbackResource(List<EpubResource> allResources) {
        return getFallbackResource(allResources, new HashSet<String>());
    }

    /**
     * Internal recursive method with cycle detection.
     * @param allResources all resource list
     * @param visited set of already visited resource IDs to detect cycles
     * @return final available resource
     */
    private EpubResource getFallbackResource(List<EpubResource> allResources, Set<String> visited) {
        if (fallback == null || fallback.isEmpty()) {
            return this;
        }

        // Cycle detection: if we've already visited this fallback ID, break the cycle
        if (visited.contains(fallback)) {
            return this;
        }
        visited.add(fallback);

        // Find fallback resource
        EpubResource fallbackResource = null;
        for (EpubResource r : allResources) {
            if (r.getId() != null && fallback.equals(r.getId())) {
                fallbackResource = r;
                break;
            }
        }

        // If fallback resource is found, recursively find its fallback resource
        if (fallbackResource != null) {
            return fallbackResource.getFallbackResource(allResources, visited);
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
     * 获取资源输入流，用于流式处理大型文件。
     * 
     * <p><b>重要：</b>使用后必须关闭返回的输入流，以释放底层 ZIP 文件句柄。
     * 推荐使用 try-with-resources 语句：</p>
     * 
     * <pre>
     * try (InputStream is = resource.getInputStream()) {
     *     // 处理输入流
     *     byte[] data = is.readAllBytes();
     * } // 自动关闭流并释放 ZIP 句柄
     * </pre>
     * 
     * <p>或者使用更安全的方法 {@link #processContent(java.util.function.Consumer)}，
     * 该方法会自动管理资源的生命周期。</p>
     * 
     * @return 输入流
     * @throws IOException 如果未设置 EPUB 文件引用或读取失败
     * @see #processContent(java.util.function.Consumer)
     * @see fun.lzwi.epubime.zip.ZipManagedInputStream
     */
    public InputStream getInputStream() throws IOException {
        if (epubFile != null && href != null) {
            // 使用 ZipManagedInputStream 自动管理 ZIP 句柄生命周期
            return ZipManagedInputStream.open(epubFile, href);
        }
        throw new IOException("Cannot get input stream: epubFile or href is not set (id=" + id + ")");
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
                ZipUtils.processZipFileContent(epubFile, href, processor);
            } catch (IOException e) {
                throw new EpubResourceException("Failed to process resource content for " + href + " from EPUB file " + epubFile.getName(),
                    epubFile.getName(), href, e);
            }
        }
    }
}
