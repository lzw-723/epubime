package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.exception.EpubResourceException;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * EPUB流处理器
 * 专门负责流式处理EPUB内容，遵循单一职责原则
 */
public class EpubStreamProcessor {
    private final EpubFileReader fileReader;

    /**
     * 构造函数
     * @param epubFile EPUB文件
     */
    public EpubStreamProcessor(File epubFile) {
        this.fileReader = new EpubFileReader(epubFile);
    }

    /**
     * 流式处理HTML章节内容
     * @param htmlFileName HTML文件名
     * @param processor 处理函数
     * @throws BaseEpubException 处理异常
     * @throws EpubPathValidationException 路径验证异常
     */
    public void processHtmlChapter(String htmlFileName, java.util.function.Consumer<InputStream> processor)
            throws BaseEpubException, EpubPathValidationException {
        fileReader.processHtmlChapterContent(htmlFileName, processor);
    }

    /**
     * 流式处理多个HTML章节内容
     * @param htmlFileNames HTML文件名列表
     * @param processor 处理函数
     * @throws BaseEpubException 处理异常
     * @throws EpubPathValidationException 路径验证异常
     */
    public void processMultipleHtmlChapters(List<String> htmlFileNames,
                                           BiConsumer<String, InputStream> processor)
            throws BaseEpubException, EpubPathValidationException {
        fileReader.processMultipleHtmlChapters(htmlFileNames, processor);
    }

    /**
     * 流式处理书籍的所有章节
     * @param book EPUB书籍
     * @param processor 处理函数
     * @throws BaseEpubException 处理异常
     */
    public void processBookChapters(EpubBook book, BiConsumer<EpubChapter, InputStream> processor)
            throws BaseEpubException {
        List<EpubChapter> chapters = book.getChapters();

        for (EpubChapter chapter : chapters) {
            try {
                processHtmlChapter(chapter.getContent(), inputStream -> processor.accept(chapter, inputStream));
            } catch (Exception e) {
                throw new EpubResourceException("Failed to process chapter: " + chapter.getContent(),
                                                book.getResources().isEmpty() ? null :
                                                book.getResources().get(0).getEpubFile().getName(),
                                                chapter.getContent(), e);
            }
        }
    }

    /**
     * 流式处理书籍的所有资源（图片、CSS等）
     * @param book EPUB书籍
     * @param processor 处理函数，接收资源和对应的输入流
     * @throws BaseEpubException 处理异常
     */
    public void processBookResources(EpubBook book, BiConsumer<EpubResource, InputStream> processor)
            throws BaseEpubException {
        List<EpubResource> resources = book.getResources();

        for (EpubResource resource : resources) {
            try {
                // 跳过章节内容，只处理其他资源
                if (!isChapterResource(resource, book)) {
                    resource.processContent(inputStream -> processor.accept(resource, inputStream));
                }
            } catch (Exception e) {
                throw new EpubResourceException("Failed to process resource: " + resource.getHref(),
                                                resource.getEpubFile().getName(),
                                                resource.getHref(), e);
            }
        }
    }

    /**
     * 判断资源是否为章节内容
     * @param resource 资源
     * @param book 书籍
     * @return 如果是章节内容返回true
     */
    private boolean isChapterResource(EpubResource resource, EpubBook book) {
        return book.getChapters().stream()
                .anyMatch(chapter -> chapter.getContent() != null &&
                           chapter.getContent().equals(resource.getHref()));
    }

    /**
     * 流式处理单个资源文件
     * @param resourceHref 资源文件路径
     * @param processor 处理函数
     * @throws BaseEpubException 处理异常
     * @throws EpubPathValidationException 路径验证异常
     */
    public void processResource(String resourceHref, java.util.function.Consumer<InputStream> processor)
            throws BaseEpubException, EpubPathValidationException {
        fileReader.processResourceContent(resourceHref, processor);
    }
}