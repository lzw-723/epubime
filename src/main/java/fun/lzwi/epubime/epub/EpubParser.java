package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.zip.ZipUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpubParser {
    public static final String CONTAINER_FILE_PATH = "META-INF/container.xml";
    private File epubFile;

    public EpubParser(File epubFile) {
        this.epubFile = epubFile;
    }

    protected static String readEpubContent(File epubFile, String path) throws EpubParseException {
        try {
            return ZipUtils.getZipFileContent(epubFile, path);
        } catch (IOException e) {
            throw new EpubParseException("Failed to read EPUB file", e);
        }
    }


    /**
     * 获取根文件路径
     *
     * @param containerContent 容器文件内容
     * @return 根文件路径
     */
    protected static String getRootFilePath(String containerContent) {
        int start = containerContent.indexOf("full-path=\"");
        int end = containerContent.indexOf("\"", start + 11);
        return containerContent.substring(start + 11, end);
    }

    /**
     * 获取根文件目录
     *
     * @param rootFilePath 根文件路径
     * @return 根文件目录
     */
    protected static String getRootFileDir(String rootFilePath) {
        int start = rootFilePath.lastIndexOf("/");
        return rootFilePath.substring(0, start + 1);
    }

    protected static Metadata parseMetadata(String opfContent) {
        Objects.requireNonNull(opfContent);
        Metadata metadata = new Metadata();
        Jsoup.parse(opfContent, Parser.xmlParser()).select("metadata").forEach(meta -> {
            meta.children().forEach(child -> {
                switch (child.tagName()) {
                    case "dc:title":
                        metadata.setTitle(child.text());
                        break;
                    case "dc:creator":
                        metadata.setCreator(child.text());
                        break;
                    case "dc:language":
                        metadata.setLanguage(child.text());
                        break;
                    case "dc:identifier":
                        metadata.setIdentifier(child.text());
                        break;
                    case "dc:publisher":
                        metadata.setPublisher(child.text());
                        break;
                    case "dc:date":
                        metadata.setDate(child.text());
                        break;
                    case "dc:description":
                        metadata.setDescription(child.text());
                        break;
                    case "dc:subject":
                        metadata.addSubject(child.text());
                        break;
                    case "dc:type":
                        metadata.setType(child.text());
                        break;
                    case "dc:format":
                        metadata.setFormat(child.text());
                        break;
                    case "dc:source":
                        metadata.setSource(child.text());
                        break;
                    //                    case "dc:relation":

                    //                        metadata.addRelation(child.text());

                    //                        break;

                    //                    case "dc:coverage":

                    //                        metadata.addCoverage(child.text());

                    //                        break;
                    case "dc:rights":
                        metadata.setRights(child.text());
                        break;
                    case "dc:contributor":
                        metadata.addContributor(child.text());
                        break;
                    case "meta":
                        if (child.attr("name").equals("cover")) {
                            metadata.setCover(child.attr("content"));
                        } else if (child.attr("property").equals("dcterms:rightsHolder")) {
                            metadata.setRightsHolder(child.text());
                        } else if (child.attr("property").equals("dcterms:modified")) {
                            metadata.setModified(child.text());
                        }
                        break;
                    default:
                        break;
                }
            });
        });
        return metadata;
    }

    protected static String getNcxPath(String opfContent, String opfDir) {
        Objects.requireNonNull(opfContent);
        Document document = Jsoup.parse(opfContent);
        String id = document.select("spine").attr("toc");
        String selector = String.format("manifest>item[id=\"%s\"]", id);
        Element ncxItem = document.select(selector).first();
        return opfDir + ncxItem.attr("href");
    }

    protected static List<EpubChapter> parseNcx(String tocContent) {
        Objects.requireNonNull(tocContent);
        return Jsoup.parse(tocContent).select("navMap>navPoint").stream().map(navPoint -> {
            EpubChapter chapter = new EpubChapter();
            chapter.setTitle(navPoint.select("navLabel>text").text());
            chapter.setContent(navPoint.select("content").attr("src"));
            return chapter;
        }).collect(java.util.stream.Collectors.toList());
    }

    protected static String getNavPath(String opfContent, String opfDir) {
        Objects.requireNonNull(opfContent);
        Element navItem = Jsoup.parse(opfContent).select("manifest>item[properties=\"nav\"]").first();
        if (navItem != null) {
            return opfDir + navItem.attr("href");
        }
        return null;
    }

    protected static List<EpubChapter> parseNav(String navContent) {
        Objects.requireNonNull(navContent);
        return Jsoup.parse(navContent).select("nav>ol>li>a, nav>ul>li>a").stream().map(a -> {
            EpubChapter chapter = new EpubChapter();
            chapter.setTitle(a.text());
            chapter.setContent(a.attr("href"));
            return chapter;
        }).collect(java.util.stream.Collectors.toList());
    }

    protected static List<EpubResource> parseResources(String opfContent, String opfDir, File epubFile) throws EpubParseException {
        Objects.requireNonNull(opfContent);
        Document document = Jsoup.parse(opfContent);
        List<EpubResource> resources = new ArrayList<>();
        
        for (Element item : document.select("manifest>item")) {
            EpubResource res = new EpubResource();
            res.setId(item.attr("id"));
            res.setHref(opfDir + item.attr("href"));
            res.setType(item.attr("media-type"));
            try {
                res.setData(ZipUtils.getZipFileBytes(epubFile, res.getHref()));
                resources.add(res);
            } catch (IOException e) {
                throw new EpubParseException("Failed to parse resource: " + res.getHref(), e);
            }
        }
        return resources;
    }

    // 解析EPUB文件并返回EpubBook对象
    public EpubBook parse() throws EpubParseException {
        EpubBook book = new EpubBook();
        // 解析META-INF/container.xml文件，获取EPUB文件的根目录
        String container = readEpubContent(epubFile, CONTAINER_FILE_PATH);
        // 解析OPF文件，获取元数据、章节列表和资源列表
        Objects.requireNonNull(container);
        String opfPath = getRootFilePath(container);
        String opfDir = getRootFileDir(opfPath);
        String opfContent = readEpubContent(epubFile, opfPath);

        // 元数据
        Metadata metadata = parseMetadata(opfContent);
        book.setMetadata(metadata);

        // 解析章节文件，获取章节内容
        // epub3 支持两种格式，ncx 和 nav
        // 优先解析 ncx
        String ncxPath = getNcxPath(opfContent, opfDir);
        String ncxContent = readEpubContent(epubFile, ncxPath);
        List<EpubChapter> ncx = parseNcx(ncxContent);
        book.setNcx(ncx);
        // 解析 nav
        String navPath = getNavPath(opfContent, opfDir);
        // nav 可能不存在
        if (navPath != null) {
            String navContent = readEpubContent(epubFile, navPath);
            List<EpubChapter> nav = parseNav(navContent);
            book.setNav(nav);
        }

        // 解析资源文件，获取资源数据
        List<EpubResource> resources = parseResources(opfContent, opfDir, epubFile);
        book.setResources(resources);
        return book;
    }
}