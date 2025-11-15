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
}