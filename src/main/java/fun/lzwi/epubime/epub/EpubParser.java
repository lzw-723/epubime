package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.zip.ZipUtils;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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
        Jsoup.parse(opfContent).select("metadata").forEach(meta -> {
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
                        }
                        break;
                    default:
                        break;
                }
            });
        });
        return metadata;
    }

    protected static String getTocPath(String opfContent, String opfDir) {
        Objects.requireNonNull(opfContent);
        AtomicReference<String> tocPath = new AtomicReference<>();
        Jsoup.parse(opfContent).select("manifest").forEach(manifest -> {
            manifest.children().forEach(child -> {
                if (child.attr("id").equals("ncx")) {
                    tocPath.set(child.attr("href"));
                }
            });
        });
        return opfDir + tocPath.get();
    }

    protected static List<EpubChapter> parseChapters(String tocContent) {
        Objects.requireNonNull(tocContent);
        List<EpubChapter> chapters = new ArrayList<>();
        Jsoup.parse(tocContent).select("navMap").forEach(navMap -> {
            navMap.children().forEach(child -> {
                if (child.tagName().equals("navpoint")) {
                    EpubChapter chapter = new EpubChapter();
                    child.children().forEach(navPoint -> {
                        switch (navPoint.tagName()) {
                            case "navlabel":
                                chapter.setTitle(navPoint.text());
                                break;
                            case "content":
                                String contentPath = navPoint.attr("src");
                                chapter.setContent(contentPath);
                                break;
                            default:
                                break;
                        }
                    });
                    chapters.add(chapter);
                }
            });
        });
        return chapters;
    }

    protected static List<EpubResource> parseResources(String opfContent, String opfDir, File epubFile) {
        Objects.requireNonNull(opfContent);
        List<EpubResource> resources = new ArrayList<>();
        Jsoup.parse(opfContent).select("manifest").forEach(manifest -> {
            manifest.children().forEach(child -> {
                if (child.tagName().equals("item")) {
                    EpubResource res = new EpubResource();
                    res.setId(child.attr("id"));
                    res.setHref(opfDir + child.attr("href"));
                    res.setType(child.attr("media-type"));
                    try {
                        res.setData(ZipUtils.getZipFileBytes(epubFile, res.getHref()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    resources.add(res);
                }
            });
        });
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
        String opfContent = readEpubContent(epubFile, opfPath);
        // 元数据
        book.setMetadata(parseMetadata(opfContent));
        // 解析章节文件，获取章节内容
        String tocPath = getTocPath(opfContent, getRootFileDir(opfPath));
        String tocContent = readEpubContent(epubFile, tocPath);
        List<EpubChapter> chapters = parseChapters(tocContent);
        book.setChapters(chapters);
        // 解析资源文件，获取资源数据
        List<EpubResource> resources = parseResources(opfContent, getRootFileDir(opfPath), epubFile);
        book.setResources(resources);
        return book;
    }
}
