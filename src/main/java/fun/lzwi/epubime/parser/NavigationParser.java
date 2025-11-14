package fun.lzwi.epubime.parser;

import fun.lzwi.epubime.epub.EpubChapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 导航解析器
 * 负责解析NCX和NAV文件中的导航信息
 */
public class NavigationParser {
    
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
        String selector = String.format("manifest>item[id=\"%s\"]", id);
        Element ncxItem = document.select(selector).first();
        
        if (ncxItem == null) {
            throw new IllegalArgumentException("NCX item not found in OPF manifest");
        }
        
        return opfDir + ncxItem.attr("href");
    }
    
    /**
     * 解析NCX目录内容
     *
     * @param tocContent NCX目录内容
     * @return 章节列表
     */
    public List<EpubChapter> parseNcx(String tocContent) {
        if (tocContent == null) {
            throw new IllegalArgumentException("NCX content cannot be null");
        }
        
        return Jsoup.parse(tocContent).select("navMap>navPoint").stream()
                .map(this::parseNcxNavPoint)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 解析NCX导航点
     *
     * @param navPoint 导航点元素
     * @return 章节对象
     */
    private EpubChapter parseNcxNavPoint(Element navPoint) {
        EpubChapter chapter = new EpubChapter();
        chapter.setTitle(navPoint.select("navLabel>text").text());
        chapter.setContent(navPoint.select("content").attr("src"));
        return chapter;
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
     * 解析NAV目录内容
     *
     * @param navContent NAV目录内容
     * @return 章节列表
     */
    public List<EpubChapter> parseNav(String navContent) {
        if (navContent == null) {
            throw new IllegalArgumentException("NAV content cannot be null");
        }
        
        Document doc = Jsoup.parse(navContent);
        List<EpubChapter> chapters = new ArrayList<>();
        
        // 首先查找epub:type为toc的nav元素（这是主导航）
        Element navElement = findNavElement(doc, "toc");
        
        if (navElement != null) {
            // 查找顶级ol或ul元素
            Elements topLists = navElement.select("> ol, > ul");
            
            for (Element list : topLists) {
                chapters.addAll(parseNavList(list));
            }
        }
        
        return chapters;
    }
    
    /**
     * 按导航类型解析NAV内容
     *
     * @param navContent NAV目录内容
     * @param navType 导航类型（如toc, landmarks, page-list等）
     * @return 章节列表
     */
    public List<EpubChapter> parseNavByType(String navContent, String navType) {
        if (navContent == null) {
            throw new IllegalArgumentException("NAV content cannot be null");
        }
        if (navType == null) {
            throw new IllegalArgumentException("Navigation type cannot be null");
        }
        
        Document doc = Jsoup.parse(navContent);
        List<EpubChapter> chapters = new ArrayList<>();
        
        // 查找特定类型的nav元素
        Element navElement = findNavElement(doc, navType);
        
        if (navElement != null) {
            // 查找顶级ol或ul元素
            Elements topLists = navElement.select("> ol, > ul");
            
            for (Element list : topLists) {
                chapters.addAll(parseNavList(list));
            }
        }
        
        return chapters;
    }
    
    /**
     * 查找指定类型的nav元素
     *
     * @param doc 文档对象
     * @param navType 导航类型
     * @return nav元素，如果未找到则返回null
     */
    private Element findNavElement(Document doc, String navType) {
        // 尝试不同的属性值格式
        Element navElement = doc.selectFirst("nav[epub:type= " + navType + "]");
        if (navElement == null) {
            navElement = doc.selectFirst("nav[epub:type='" + navType + "']");
        }
        if (navElement == null) {
            navElement = doc.selectFirst("nav[epub:type=\"" + navType + "\"]");
        }
        
        // 如果是toc类型且仍未找到，使用第一个nav元素
        if (navElement == null && "toc".equals(navType)) {
            navElement = doc.selectFirst("nav");
        }
        
        return navElement;
    }
    
    /**
     * 递归解析导航列表
     *
     * @param listElement ol或ul元素
     * @return 章节列表
     */
    private List<EpubChapter> parseNavList(Element listElement) {
        List<EpubChapter> chapters = new ArrayList<>();
        
        for (Element li : listElement.children()) {
            if (!"li".equalsIgnoreCase(li.tagName())) {
                continue;
            }
            
            EpubChapter chapter = null;
            Element link = li.selectFirst("a");
            
            if (link != null) {
                chapter = new EpubChapter();
                
                // 设置章节ID（如果存在）
                String id = link.attr("id");
                if (id != null && !id.isEmpty()) {
                    chapter.setId(id);
                }
                
                chapter.setTitle(link.text());
                chapter.setContent(link.attr("href"));
                
                // 处理epub:type属性
                String epubType = link.attr("epub:type");
                if (epubType != null && !epubType.isEmpty()) {
                    // 可以根据需要存储epub:type信息
                    // 这里不处理，但保留扩展的可能性
                }
            }
            
            // 查找嵌套列表（子章节）
            Elements nestedLists = li.select("> ol, > ul");
            
            for (Element nestedList : nestedLists) {
                if (chapter != null) {
                    List<EpubChapter> children = parseNavList(nestedList);
                    for (EpubChapter child : children) {
                        chapter.addChild(child);
                    }
                } else {
                    // 如果li标签没有a标签但有嵌套列表，解析这些嵌套列表
                    List<EpubChapter> children = parseNavList(nestedList);
                    chapters.addAll(children);
                }
            }
            
            // 如果当前li标签包含链接，添加到章节列表
            if (chapter != null) {
                chapters.add(chapter);
            }
        }
        
        return chapters;
    }
}