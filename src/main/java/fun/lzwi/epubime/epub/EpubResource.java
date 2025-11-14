package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * EPUB资源模型类
 * 表示EPUB电子书中的单个资源文件，如图片、CSS样式表等
 */
public class EpubResource {
    private String id;
    private String type;
    private String href;
    private byte[] data;
    private File epubFile; // EPUB文件引用用于流处理

    /**
     * 默认构造函数
     */
    public EpubResource() {
        // Default constructor
    }
    
    /**
     * 复制构造函数
     * @param other 要复制的EpubResource对象
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

    /**
     * 获取资源ID
     * @return 资源ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置资源ID
     * @param id 资源ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取资源类型（MIME类型）
     * @return 资源类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置资源类型（MIME类型）
     * @param type 资源类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取资源数据
     * 如果数据已存在则直接返回，否则尝试从EPUB文件中流式读取
     * @return 资源数据字节数组
     */
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

    /**
     * 设置资源数据
     * @param data 资源数据字节数组
     */
    public void setData(byte[] data) {
        if (data != null) {
            this.data = data.clone();
        }
    }

    /**
     * 获取资源文件路径
     * @return 资源文件路径
     */
    public String getHref() {
        return href;
    }

    /**
     * 设置资源文件路径
     * @param href 资源文件路径
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * 获取EPUB文件引用
     * @return EPUB文件引用
     */
    public File getEpubFile() {
        return epubFile;
    }

    /**
     * 设置EPUB文件引用
     * @param epubFile EPUB文件引用
     */
    public void setEpubFile(File epubFile) {
        this.epubFile = epubFile;
    }

    /**
     * 获取资源的输入流，用于大文件的流式处理
     * @return 输入流
     * @throws IOException IO异常
     */
    public InputStream getInputStream() throws IOException {
        if (epubFile != null && href != null) {
            // Use ZipFileManager to optimize ZIP access
            return ZipUtils.getZipFileInputStream(epubFile, href);
        }
        return null;
    }
    
    /**
     * 批量加载资源数据
     * @param resources 资源列表
     * @param epubFile EPUB文件
     * @throws IOException IO异常
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
     * 流式处理资源内容以避免将整个文件加载到内存中
     * @param processor 消费者函数，用于处理资源内容
     * @throws IOException IO异常
     */
    public void processContent(java.util.function.Consumer<InputStream> processor) throws IOException {
        if (epubFile != null && href != null) {
            ZipUtils.processHtmlContent(epubFile, href, processor);
        }
    }
}