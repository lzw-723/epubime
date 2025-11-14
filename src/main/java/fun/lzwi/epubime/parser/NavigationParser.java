package fun.lzwi.epubime.parser;

import fun.lzwi.epubime.epub.EpubChapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 导航解析器
 * 负责解析NCX和NAV文件中的导航信息
 */
public class NavigationParser {
    
    // 预分配的空列表，避免重复创建
    private static final List<EpubChapter> EMPTY_CHAPTER_LIST = new ArrayList<>();
    
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
        
        // 优化：使用更高效的查询方式
        Element ncxItem = document.selectFirst("manifest > item[id=\"" + id + "\"]");
        if (ncxItem == null) {
            throw new IllegalArgumentException("NCX item not found in OPF manifest with id: " + id);
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
        
        // 使用更快的解析配置
        Document doc = Jsoup.parse(tocContent, "", Parser.xmlParser());
        Elements navPoints = doc.select("navMap > navPoint");
        
        if (navPoints.isEmpty()) {
            return EMPTY_CHAPTER_LIST;
        }
        
        // 预分配列表容量，避免动态扩容
        List<EpubChapter> chapters = new ArrayList<>(navPoints.size());
        
        for (Element navPoint : navPoints) {
            chapters.add(parseNcxNavPoint(navPoint));
        }
        
        return chapters;
    }
    
    /**
     * 解析NCX导航点
     *
     * @param navPoint 导航点元素
     * @return 章节对象
     */
    private EpubChapter parseNcxNavPoint(Element navPoint) {
        EpubChapter chapter = new EpubChapter();
        
        // 优化：使用更高效的查询方式
        Element navLabel = navPoint.selectFirst("navLabel > text");
        if (navLabel != null) {
            chapter.setTitle(navLabel.text());
        }
        
        Element content = navPoint.selectFirst("content");
        if (content != null) {
            chapter.setContent(content.attr("src"));
        }
        
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
        
        // 使用更快的查询方式
        Element navItem = Jsoup.parse(opfContent).selectFirst("manifest > item[properties=nav]");
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
        
        Document doc = Jsoup.parse(navContent, "", Parser.xmlParser());
        Element navElement = findNavElement(doc, "toc");
        
        if (navElement == null) {
            return EMPTY_CHAPTER_LIST;
        }
        
        // 查找顶级ol或ul元素
        Elements topLists = navElement.select("> ol, > ul");
        if (topLists.isEmpty()) {
            return EMPTY_CHAPTER_LIST;
        }
        
        List<EpubChapter> chapters = new ArrayList<>();
        
        for (Element list : topLists) {
            chapters.addAll(parseNavList(list));
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
        
        Document doc = Jsoup.parse(navContent, "", Parser.xmlParser());
        Element navElement = findNavElement(doc, navType);
        
        if (navElement == null) {
            return EMPTY_CHAPTER_LIST;
        }
        
        // 查找顶级ol或ul元素
        Elements topLists = navElement.select("> ol, > ul");
        if (topLists.isEmpty()) {
            return EMPTY_CHAPTER_LIST;
        }
        
        List<EpubChapter> chapters = new ArrayList<>();
        
        for (Element list : topLists) {
            chapters.addAll(parseNavList(list));
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
        // 使用更快的查询方式，避免多次查询
        String[] selectors = {
            "nav[epub:type=\"" + navType + "\"]",
            "nav[epub:type='" + navType + "']",
            "nav[epub:type=\"" + navType + "\"]"
        };
        
        for (String selector : selectors) {
            Element element = doc.selectFirst(selector);
            if (element != null) {
                return element;
            }
        }
        
        // 如果是toc类型且仍未找到，使用第一个nav元素
        if ("toc".equals(navType)) {
            return doc.selectFirst("nav");
        }
        
        return null;
    }
    
    /**
     * 递归解析导航列表
     *
     * @param listElement ol或ul元素
     * @return 章节列表
     */
    private List<EpubChapter> parseNavList(Element listElement) {
        List<EpubChapter> chapters = new ArrayList<>();
        
        // 优化：直接遍历子元素，避免选择器开销
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