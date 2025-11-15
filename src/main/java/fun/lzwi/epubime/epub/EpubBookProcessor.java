package fun.lzwi.epubime.epub;

/**
 * EPUB书籍处理器
 * 负责处理EpubBook相关的业务逻辑，遵循单一职责原则
 */
public class EpubBookProcessor {

    /**
     * 获取封面资源
     * @param book EPUB书籍
     * @return 封面资源，如果不存在返回null
     */
    public static EpubResource getCover(EpubBook book) {
        if (book == null || book.getResources().isEmpty()) {
            return null;
        }

        // 优先使用 properties="cover-image" 属性查找封面图片
        EpubResource coverResource = book.getResources().stream()
            .filter(r -> r.getProperties() != null && r.getProperties().contains("cover-image"))
            .findFirst()
            .orElse(null);

        // 如果没有找到带有 properties="cover-image" 的资源，尝试旧的 meta 标签方法
        if (coverResource == null && book.getMetadata().getCover() != null) {
            coverResource = book.getResources().stream()
                .filter(r -> r.getId().equals(book.getMetadata().getCover()))
                .findFirst()
                .orElse(null);
        }

        // 应用回退机制以获取最终可用的资源
        if (coverResource != null) {
            return coverResource.getFallbackResource(book.getResources());
        }

        return coverResource;
    }

    /**
     * 根据ID获取资源，自动应用回退机制
     * @param book EPUB书籍
     * @param resourceId 资源ID
     * @return 应用回退机制后的资源
     */
    public static EpubResource getResourceWithFallback(EpubBook book, String resourceId) {
        if (book == null || resourceId == null) {
            return null;
        }

        return book.getResources().stream()
                .filter(r -> resourceId.equals(r.getId()))
                .findFirst()
                .map(r -> r.getFallbackResource(book.getResources()))
                .orElse(null);
    }

    /**
     * 根据ID获取资源
     * @param book EPUB书籍
     * @param resourceId 资源ID
     * @return 资源对象
     */
    public static EpubResource getResource(EpubBook book, String resourceId) {
        if (book == null || resourceId == null) {
            return null;
        }

        return book.getResources().stream()
                .filter(r -> resourceId.equals(r.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 批量加载所有资源数据 - DEPRECATED: Use streaming methods instead to avoid memory issues
     * @param book EPUB书籍
     * @throws java.io.IOException 文件读取异常
     * @deprecated Use streaming processing to avoid loading all resources into memory
     */
    @Deprecated
    public static void loadAllResourceData(EpubBook book) throws java.io.IOException {
        if (book == null || book.getResources().isEmpty()) {
            return;
        }

        // 假设第一个资源有文件引用
        java.io.File epubFile = book.getResources().get(0).getEpubFile();
        if (epubFile != null) {
            EpubResource.loadResourceData(book.getResources(), epubFile);
        }
    }
}