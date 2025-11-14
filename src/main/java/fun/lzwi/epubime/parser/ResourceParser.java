package fun.lzwi.epubime.parser;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源解析器
 * 负责解析OPF文件中的资源信息
 */
public class ResourceParser {
    
    private final File epubFile;
    
    /**
     * 构造函数
     *
     * @param epubFile EPUB文件（可以为null，但某些功能将不可用）
     */
    public ResourceParser(File epubFile) {
        this.epubFile = epubFile;
    }
    
    /**
     * 解析OPF内容中的资源文件列表
     *
     * @param opfContent OPF文件内容
     * @param opfDir OPF文件目录
     * @return 资源文件列表
     */
    public List<EpubResource> parseResources(String opfContent, String opfDir) {
        if (opfContent == null) {
            throw new IllegalArgumentException("OPF content cannot be null");
        }
        if (opfDir == null) {
            throw new IllegalArgumentException("OPF directory cannot be null");
        }
        
        // 如果epubFile为null，跳过缓存
        if (epubFile != null) {
            EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
            String cacheKey = "resources:" + opfContent.hashCode() + ":" + opfDir;
            
            @SuppressWarnings("unchecked")
            List<EpubResource> cachedResult = (List<EpubResource>) cache.getParsedResult(cacheKey);
            
            if (cachedResult != null) {
                return new ArrayList<>(cachedResult);
            }
        }
        
        Document document = Jsoup.parse(opfContent);
        List<EpubResource> resources = new ArrayList<>();
        
        for (Element item : document.select("manifest>item")) {
            EpubResource resource = createResource(item, opfDir);
            resources.add(resource);
        }
        
        // 如果epubFile不为null，缓存结果
        if (epubFile != null) {
            EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
            String cacheKey = "resources:" + opfContent.hashCode() + ":" + opfDir;
            cache.setParsedResult(cacheKey, new ArrayList<>(resources));
        }
        
        return resources;
    }
    
    /**
     * 根据manifest元素创建资源对象
     *
     * @param item manifest元素
     * @param opfDir OPF文件目录
     * @return 资源对象
     */
    private EpubResource createResource(Element item, String opfDir) {
        EpubResource resource = new EpubResource();
        
        resource.setId(item.attr("id"));
        resource.setHref(opfDir + item.attr("href"));
        resource.setType(item.attr("media-type"));
        
        String properties = item.attr("properties");
        resource.setProperties(properties.isEmpty() ? null : properties);
        
        String fallback = item.attr("fallback");
        resource.setFallback(fallback.isEmpty() ? null : fallback);
        
        // 设置EPUB文件引用，用于按需流式加载资源
        resource.setEpubFile(epubFile);
        
        return resource;
    }
    
    /**
     * 从OPF内容中获取NCX文件路径
     *
     * @param opfContent OPF文件内容
     * @param opfDir OPF文件目录
     * @return NCX文件路径
     */
    public String getNcxPath(String opfContent, String opfDir) {
        if (opfContent == null) {
            throw new IllegalArgumentException("OPF content cannot be null");
        }
        
        Document document = Jsoup.parse(opfContent);
        String id = document.select("spine").attr("toc");
        
        if (id.isEmpty()) {
            throw new IllegalArgumentException("No NCX reference found in spine element");
        }
        
        String selector = String.format("manifest>item[id=\"%s\"]", id);
        Element ncxItem = document.select(selector).first();
        
        if (ncxItem == null) {
            throw new IllegalArgumentException("NCX item not found in OPF manifest with id: " + id);
        }
        
        return opfDir + ncxItem.attr("href");
    }
    
    /**
     * 从OPF内容中获取NAV文件路径
     *
     * @param opfContent OPF文件内容
     * @param opfDir OPF文件目录
     * @return NAV文件路径，如果不存在则返回null
     */
    public String getNavPath(String opfContent, String opfDir) {
        if (opfContent == null) {
            throw new IllegalArgumentException("OPF content cannot be null");
        }
        
        Element navItem = Jsoup.parse(opfContent).select("manifest>item[properties=\"nav\"]").first();
        if (navItem != null) {
            return opfDir + navItem.attr("href");
        }
        return null;
    }
    
    /**
     * 从OPF内容中获取封面资源ID
     *
     * @param opfContent OPF文件内容
     * @return 封面资源ID，如果未找到则返回null
     */
    public String getCoverResourceId(String opfContent) {
        if (opfContent == null) {
            throw new IllegalArgumentException("OPF content cannot be null");
        }
        
        Document document = Jsoup.parse(opfContent);
        
        // 首先尝试查找properties="cover-image"的资源
        Element coverItem = document.select("manifest>item[properties=\"cover-image\"]").first();
        if (coverItem != null) {
            return coverItem.attr("id");
        }
        
        // 然后尝试查找meta name="cover"的资源
        Element metaCover = document.select("metadata>meta[name=\"cover\"]").first();
        if (metaCover != null) {
            return metaCover.attr("content");
        }
        
        return null;
    }
}