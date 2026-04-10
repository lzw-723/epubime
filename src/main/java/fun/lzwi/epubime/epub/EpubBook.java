package fun.lzwi.epubime.epub;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * EPUB涔︾睄妯″瀷绫? * 琛ㄧず涓€涓畬鏁寸殑EPUB鐢靛瓙涔︼紝鍖呭惈鍏冩暟鎹€佺珷鑺傚拰璧勬簮鏂囦欢
 */
public class EpubBook {

    private Metadata metadata;
    private String version; // EPUB鐗堟湰
    private List<EpubChapter> ncx = new ArrayList<>();
    private List<EpubChapter> nav = new ArrayList<>();
    private List<EpubChapter> landmarks = new ArrayList<>(); // 鍦版爣瀵艰埅
    private List<EpubChapter> pageList = new ArrayList<>(); // 椤甸潰鍒楄〃瀵艰埅
    private List<EpubResource> resources = new ArrayList<>();

    /**
     * 榛樿鏋勯€犲嚱鏁?     */
    public EpubBook() {
        // Default constructor
    }

    /**
     * 澶嶅埗鏋勯€犲嚱鏁帮紝鐢ㄤ簬缂撳瓨
     * @param other 瑕佸鍒剁殑EpubBook瀵硅薄
     */
    public EpubBook(EpubBook other) {
        this.version = other.version;
        if (other.metadata != null) {
            this.metadata = new Metadata(other.metadata);
        }
        if (other.ncx != null) {
            this.ncx = new ArrayList<>(other.ncx.size());
            for (EpubChapter chapter : other.ncx) {
                this.ncx.add(new EpubChapter(chapter));
            }
        }
        if (other.nav != null) {
            this.nav = new ArrayList<>(other.nav.size());
            for (EpubChapter chapter : other.nav) {
                this.nav.add(new EpubChapter(chapter));
            }
        }
        if (other.landmarks != null) {
            this.landmarks = new ArrayList<>(other.landmarks.size());
            for (EpubChapter chapter : other.landmarks) {
                this.landmarks.add(new EpubChapter(chapter));
            }
        }
        if (other.pageList != null) {
            this.pageList = new ArrayList<>(other.pageList.size());
            for (EpubChapter chapter : other.pageList) {
                this.pageList.add(new EpubChapter(chapter));
            }
        }
        if (other.resources != null) {
            this.resources = new ArrayList<>(other.resources.size());
            for (EpubResource resource : other.resources) {
                this.resources.add(new EpubResource(resource));
            }
        }
    }

    /**
     * 鑾峰彇NCX鐩綍绔犺妭鍒楄〃
     * @return NCX鐩綍绔犺妭鍒楄〃锛堜笉鍙慨鏀癸級
     */
    public List<EpubChapter> getNcx() {
        return Collections.unmodifiableList(ncx);
    }

    /**
     * 璁剧疆NCX鐩綍绔犺妭鍒楄〃
     * @param ncx NCX鐩綍绔犺妭鍒楄〃
     */
    public void setNcx(List<EpubChapter> ncx) {
        this.ncx = new ArrayList<>(ncx);
    }

    /**
     * 鑾峰彇NAV鐩綍绔犺妭鍒楄〃
     * @return NAV鐩綍绔犺妭鍒楄〃锛堜笉鍙慨鏀癸級
     */
    public List<EpubChapter> getNav() {
        return Collections.unmodifiableList(nav);
    }

    /**
     * 璁剧疆NAV鐩綍绔犺妭鍒楄〃
     * @param nav NAV鐩綍绔犺妭鍒楄〃
     */
    public void setNav(List<EpubChapter> nav) {
        this.nav = new ArrayList<>(nav);
    }

    /**
     * 鑾峰彇鍦版爣瀵艰埅绔犺妭鍒楄〃
     * @return 鍦版爣瀵艰埅绔犺妭鍒楄〃锛堜笉鍙慨鏀癸級
     */
    public List<EpubChapter> getLandmarks() {
        return Collections.unmodifiableList(landmarks);
    }

    /**
     * 璁剧疆鍦版爣瀵艰埅绔犺妭鍒楄〃
     * @param landmarks 鍦版爣瀵艰埅绔犺妭鍒楄〃
     */
    public void setLandmarks(List<EpubChapter> landmarks) {
        this.landmarks = new ArrayList<>(landmarks);
    }

    /**
     * 鑾峰彇椤甸潰鍒楄〃瀵艰埅绔犺妭鍒楄〃
     * @return 椤甸潰鍒楄〃瀵艰埅绔犺妭鍒楄〃锛堜笉鍙慨鏀癸級
     */
    public List<EpubChapter> getPageList() {
        return Collections.unmodifiableList(pageList);
    }

    /**
     * 璁剧疆椤甸潰鍒楄〃瀵艰埅绔犺妭鍒楄〃
     * @param pageList 椤甸潰鍒楄〃瀵艰埅绔犺妭鍒楄〃
     */
    public void setPageList(List<EpubChapter> pageList) {
        this.pageList = new ArrayList<>(pageList);
    }

    /**
     * 鑾峰彇涓昏绔犺妭鍒楄〃锛屼紭鍏堜娇鐢∟AV鐩綍锛圗PUB3鏍囧噯锛夛紝濡傛灉NAV涓虹┖鍒欎娇鐢∟CX鐩綍锛堝悜鍚庡吋瀹癸級
     * @return 绔犺妭鍒楄〃锛堜笉鍙慨鏀癸級
     */
    public List<EpubChapter> getChapters() {
        if (!nav.isEmpty()) {
            return getNav();
        }
        return getNcx();
    }

    /**
     * 鑾峰彇鍏冩暟鎹壇鏈?     * @return 鍏冩暟鎹壇鏈紝濡傛灉鍏冩暟鎹湭璁剧疆鍒欒繑鍥瀗ull
     */
    public Metadata getMetadata() {
        return metadata != null ? new Metadata(metadata) : null;
    }

    /**
     * 璁剧疆鍏冩暟鎹?     * @param metadata 鍏冩暟鎹璞★紝涓簄ull鏃跺皢娓呯┖鍏冩暟鎹?     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata != null ? new Metadata(metadata) : null;
    }

    /**
     * 鑾峰彇EPUB鐗堟湰
     * @return EPUB鐗堟湰瀛楃涓?     */
    public String getVersion() {
        return version;
    }

    /**
     * 璁剧疆EPUB鐗堟湰
     * @param version EPUB鐗堟湰瀛楃涓?     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 鑾峰彇璧勬簮鏂囦欢鍒楄〃
     * @return 璧勬簮鏂囦欢鍒楄〃锛堜笉鍙慨鏀癸級
     */
    public List<EpubResource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    /**
     * 璁剧疆璧勬簮鏂囦欢鍒楄〃
     * @param resources 璧勬簮鏂囦欢鍒楄〃
     */
    public void setResources(List<EpubResource> resources) {
        this.resources = new ArrayList<>(resources);
    }

    /**
     * 鑾峰彇灏侀潰璧勬簮
     * @return 灏侀潰璧勬簮瀵硅薄锛屽鏋滀笉瀛樺湪杩斿洖null
     */
    public EpubResource getCover() {
        return EpubBookProcessor.getCover(this);
    }

    /**
     * 鏍规嵁ID鑾峰彇璧勬簮锛岃嚜鍔ㄥ簲鐢ㄥ洖閫€鏈哄埗
     * @param resourceId 璧勬簮ID
     * @return 搴旂敤鍥為€€鏈哄埗鍚庣殑璧勬簮锛屽鏋滀笉瀛樺湪杩斿洖null
     */
    public EpubResource getResourceWithFallback(String resourceId) {
        return EpubBookProcessor.getResourceWithFallback(this, resourceId);
    }

    /**
     * 鏍规嵁ID鑾峰彇璧勬簮
     * @param resourceId 璧勬簮ID
     * @return 璧勬簮瀵硅薄锛屽鏋滀笉瀛樺湪杩斿洖null
     */
    public EpubResource getResourceById(String resourceId) {
        return EpubBookProcessor.getResource(this, resourceId);
    }

    /**
     * 鎵归噺鍔犺浇鎵€鏈夎祫婧愭暟鎹?     * @throws IOException 鏂囦欢璇诲彇寮傚父
     * @deprecated 浣跨敤娴佸紡澶勭悊閬垮厤灏嗘墍鏈夎祫婧愬姞杞藉埌鍐呭瓨
     */
    @Deprecated
    public void loadAllResourceData() throws IOException {
        EpubBookProcessor.loadAllResourceData(this);
    }

    /**
     * 娴佸紡澶勭悊HTML绔犺妭鍐呭锛岄伩鍏嶅皢鏁翠釜鏂囦欢鍔犺浇鍒板唴瀛?     * @param processor 澶勭悊HTML鍐呭鐨勬秷璐硅€呭嚱鏁?     * @throws fun.lzwi.epubime.exception.EpubParseException 瑙ｆ瀽寮傚父
     */
    public void processHtmlChapter(BiConsumer<EpubChapter, InputStream> processor)
            throws fun.lzwi.epubime.exception.EpubParseException {
        if (resources.isEmpty()) {
            return;
        }
        java.io.File epubFile = resources.get(0).getEpubFile();
        if (epubFile == null) {
            return;
        }
        try {
            EpubStreamProcessor processorObj = new EpubStreamProcessor(epubFile);
            processorObj.processBookChapters(this, processor);
        } catch (Exception e) {
            throw new fun.lzwi.epubime.exception.EpubParseException("Failed to process chapters: " + e.getMessage(), e);
        }
    }
}
