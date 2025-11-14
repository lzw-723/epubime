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
    private File epubFile; // 用于流式处理的EPUB文件引用

    public EpubResource() {
        // 默认构造函数
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
        // 如果已经有数据，直接返回
        if (data != null) {
            return data.clone();
        }
        
        // 如果有EPUB文件引用，尝试流式读取数据
        if (epubFile != null && href != null) {
            try {
                data = ZipUtils.getZipFileBytes(epubFile, href);
                return data != null ? data.clone() : null;
            } catch (IOException e) {
                // 流式读取失败，返回null
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
     * 获取资源的输入流，用于流式处理大文件
     * @return 输入流
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        if (epubFile != null && href != null) {
            return ZipUtils.getZipFileInputStream(epubFile, href);
        }
        return null;
    }
    
    /**
     * 批量加载资源数据
     * @param resources 资源列表
     * @param epubFile EPUB文件
     * @throws IOException
     */
    public static void loadResourceData(List<EpubResource> resources, File epubFile) throws IOException {
        // 收集所有需要加载的资源路径
        List<String> hrefs = new java.util.ArrayList<>();
        for (EpubResource resource : resources) {
            if (resource.epubFile != null && resource.href != null) {
                hrefs.add(resource.href);
            }
        }
        
        // 使用ZIP文件流重用机制一次性读取所有资源数据
        Map<String, byte[]> resourceData = ZipUtils.getMultipleZipFileBytes(epubFile, hrefs);
        
        // 将数据设置到对应的资源对象中
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
     * 流式处理资源内容，避免将整个文件加载到内存中
     * @param processor 处理资源内容的消费者函数
     * @throws IOException
     */
    public void processContent(java.util.function.Consumer<InputStream> processor) throws IOException {
        if (epubFile != null && href != null) {
            ZipUtils.processHtmlContent(epubFile, href, processor);
        }
    }
}