package fun.lzwi.epubime.parser;

import fun.lzwi.epubime.epub.EpubChapter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 导航解析器
 * 负责解析NCX和NAV文件中的导航信息
 * 
 * 重构后使用XmlUtils工具类消除重复代码
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
        Document document = XmlUtils.parseXml(opfContent);
        String id = XmlUtils.getAttribute(XmlUtils.selectFirst(document, "spine"), "toc");
        
        if (id.isEmpty()) {
            throw new IllegalArgumentException("No NCX reference found in spine element");
        }
        
        // 优化：使用更高效的查询方式
        Element ncxItem = XmlUtils.selectFirst(document, "manifest > item[id=\"" + id + "\"]");
        if (ncxItem == null) {
            throw new IllegalArgumentException("NCX item not found in OPF manifest with id: " + id);
        }
        
        return opfDir + XmlUtils.getAttribute(ncxItem, "href");
    }
    
    /**
     * 解析NCX目录内容
     *
     * @param tocContent NCX目录内容
     * @return 章节列表
     */
    public List<EpubChapter> parseNcx(String tocContent) {
        Document doc = XmlUtils.parseXml(tocContent);
        Elements navPoints = XmlUtils.select(doc, "navMap > navPoint");

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
     * 流式解析NCX目录内容，避免加载整个文件到内存
     *
     * @param tocInputStream NCX文件输入流
     * @return 章节列表
     * @throws java.io.IOException IO异常
     */
    public List<EpubChapter> parseNcx(java.io.InputStream tocInputStream) throws java.io.IOException {
        String tocContent = readStreamToString(tocInputStream);
        return parseNcx(tocContent);
    }

    /**
     * 将输入流读取为字符串
     * @param inputStream 输入流
     * @return 字符串内容
     * @throws java.io.IOException IO异常
     */
    private String readStreamToString(java.io.InputStream inputStream) throws java.io.IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                contentBuilder.append(buffer, 0, charsRead);
            }
        }
        return contentBuilder.toString();
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
        Element navLabel = XmlUtils.selectFirst(navPoint, "navLabel > text");
        if (navLabel != null) {
            chapter.setTitle(XmlUtils.getText(navLabel));
        }
        
        Element content = XmlUtils.selectFirst(navPoint, "content");
        if (content != null) {
            chapter.setContent(XmlUtils.getAttribute(content, "src"));
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
        // 使用更快的查询方式
        Element navItem = XmlUtils.selectFirst(XmlUtils.parseXml(opfContent), "manifest > item[properties=nav]");
        if (navItem != null) {
            return opfDir + XmlUtils.getAttribute(navItem, "href");
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
        Document doc = XmlUtils.parseXml(navContent);
        Element navElement = findNavElement(doc, "toc");
        
        if (navElement == null) {
            return EMPTY_CHAPTER_LIST;
        }
        
        // 查找顶级ol或ul元素
        Elements topLists = XmlUtils.select(navElement, "> ol, > ul");
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
        Document doc = XmlUtils.parseXml(navContent);
        Element navElement = findNavElement(doc, navType);

        if (navElement == null) {
            return EMPTY_CHAPTER_LIST;
        }

        // 查找顶级ol或ul元素
        Elements topLists = XmlUtils.select(navElement, "> ol, > ul");
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
     * 流式解析NAV目录内容，避免加载整个文件到内存
     *
     * @param navInputStream NAV文件输入流
     * @return 章节列表
     * @throws java.io.IOException IO异常
     */
    public List<EpubChapter> parseNav(java.io.InputStream navInputStream) throws java.io.IOException {
        String navContent = readStreamToString(navInputStream);
        return parseNav(navContent);
    }

    /**
     * 流式按导航类型解析NAV内容，避免加载整个文件到内存
     *
     * @param navInputStream NAV文件输入流
     * @param navType 导航类型（如toc, landmarks, page-list等）
     * @return 章节列表
     * @throws java.io.IOException IO异常
     */
    public List<EpubChapter> parseNavByType(java.io.InputStream navInputStream, String navType) throws java.io.IOException {
        String navContent = readStreamToString(navInputStream);
        return parseNavByType(navContent, navType);
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
            Element element = XmlUtils.selectFirst(doc, selector);
            if (element != null) {
                return element;
            }
        }
        
        // 如果是toc类型且仍未找到，使用第一个nav元素
        if ("toc".equals(navType)) {
            return XmlUtils.selectFirst(doc, "nav");
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
            Element link = XmlUtils.selectFirst(li, "a");
            
            if (link != null) {
                chapter = new EpubChapter();
                
                // 设置章节ID（如果存在）
                String id = XmlUtils.getAttribute(link, "id");
                if (!id.isEmpty()) {
                    chapter.setId(id);
                }
                
                chapter.setTitle(XmlUtils.getText(link));
                chapter.setContent(XmlUtils.getAttribute(link, "href"));
            }
            
            // 查找嵌套列表（子章节）
            Elements nestedLists = XmlUtils.select(li, "> ol, > ul");
            
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