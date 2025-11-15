package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.exception.EpubZipException;
import fun.lzwi.epubime.zip.PathValidator;
import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * EPUB文件读取器
 * 负责读取EPUB文件中的内容，遵循单一职责原则
 */
public class EpubFileReader {
    public final File epubFile;

    /**
     * 构造函数
     * @param epubFile EPUB文件
     */
    public EpubFileReader(File epubFile) {
        if (epubFile == null) {
            throw new IllegalArgumentException("EPUB file cannot be null");
        }
        this.epubFile = epubFile;
    }

    /**
     * 读取EPUB文件中指定路径的内容
     * @param path 文件路径
     * @return 文件内容
     * @throws EpubZipException 读取异常
     * @throws EpubPathValidationException 路径验证异常
     */
    public String readContent(String path) throws EpubZipException, EpubPathValidationException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", path)) {
            throw new EpubPathValidationException("Invalid file path: " + path, path);
        }

        try {
            return ZipUtils.getZipFileContent(epubFile, path);
        } catch (IOException e) {
            throw new EpubZipException("Failed to read EPUB file content", epubFile, path, e);
        }
    }

    /**
     * 流式处理HTML章节内容，避免将整个文件加载到内存中
     * @param htmlFileName HTML文件名
     * @param processor 处理HTML内容的消费者函数
     * @throws EpubZipException 处理异常
     * @throws EpubPathValidationException 路径验证异常
     */
    public void processHtmlChapterContent(String htmlFileName, Consumer<InputStream> processor)
            throws EpubZipException, EpubPathValidationException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", htmlFileName)) {
            throw new EpubPathValidationException("Invalid file path: " + htmlFileName, htmlFileName);
        }

        try {
            ZipUtils.processHtmlContent(epubFile, htmlFileName, processor);
        } catch (IOException e) {
            throw new EpubZipException("Failed to process HTML chapter content", epubFile, htmlFileName, e);
        }
    }

    /**
     * 流式处理多个HTML章节内容
     * @param htmlFileNames HTML文件名列表
     * @param processor 处理每个HTML内容的消费者函数
     * @throws EpubZipException 处理异常
     * @throws EpubPathValidationException 路径验证异常
     */
    public void processMultipleHtmlChapters(java.util.List<String> htmlFileNames,
                                           BiConsumer<String, InputStream> processor)
            throws EpubZipException, EpubPathValidationException {
        // 防止目录遍历攻击
        for (String fileName : htmlFileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new EpubPathValidationException("Invalid file path: " + fileName, fileName);
            }
        }

        try {
            ZipUtils.processMultipleHtmlContents(epubFile, htmlFileNames, processor);
        } catch (IOException e) {
            throw new EpubZipException("Failed to process multiple HTML chapters", epubFile, "multiple files", e);
        }
    }

    /**
     * 流式处理资源内容（图片、CSS等），避免将整个文件加载到内存中
     * @param resourceFileName 资源文件名
     * @param processor 处理资源内容的消费者函数
     * @throws EpubZipException 处理异常
     * @throws EpubPathValidationException 路径验证异常
     */
    public void processResourceContent(String resourceFileName, Consumer<InputStream> processor)
            throws EpubZipException, EpubPathValidationException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", resourceFileName)) {
            throw new EpubPathValidationException("Invalid file path: " + resourceFileName, resourceFileName);
        }

        try {
            ZipUtils.processZipFileContent(epubFile, resourceFileName, processor);
        } catch (IOException e) {
            throw new EpubZipException("Failed to process resource content", epubFile, resourceFileName, e);
        }
    }
}