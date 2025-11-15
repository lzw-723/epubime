package fun.lzwi.epubime.epub;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * EPUB元数据模型类
 *
 * <p>
 * <p>
 * 表示EPUB电子书的元数据信息，包括标题、作者、出版商等
 */


public class Metadata {


    private final List<String> titles;


    private final List<String> creators;


    private final List<String> contributors;


    private final List<String> publishers;


    private final List<String> identifiers;


    private final List<String> subjects;


    private final List<String> dates;


    private final List<String> languages;


    private final List<String> descriptions;


    private final List<String> rightsList;


    private final List<String> types;


    private final List<String> formats;


    private final List<String> sources;


    private String modified;


    private String rightsHolder;


    private String cover;


    // EPUB 3.3 可访问性元数据

    private final List<String> accessibilityFeatures;


    private final List<String> accessibilityHazard;


    private final List<String> accessibilitySummary;


    // 渲染属性



    private String layout;





    private String orientation;





    private String spread;





    private String viewport;





    private String media;





    private String flow;





    private boolean alignXCenter;





    // unique-identifier属性

    private String uniqueIdentifier;





    /**

     * 获取封面资源ID

     *

     * @return 封面资源ID

     */


    public String getCover() {


        return cover;


    }


    /**
     * 设置封面资源ID
     *
     * @param cover 封面资源ID
     */


    public void setCover(String cover) {


        this.cover = cover;


    }


    /**
     * 默认构造函数
     */


    public Metadata() {


        titles = new java.util.ArrayList<>();


        creators = new java.util.ArrayList<>();


        contributors = new java.util.ArrayList<>();


        publishers = new java.util.ArrayList<>();


        identifiers = new java.util.ArrayList<>();


        subjects = new java.util.ArrayList<>();


        dates = new java.util.ArrayList<>();


        languages = new java.util.ArrayList<>();


        descriptions = new java.util.ArrayList<>();


        rightsList = new java.util.ArrayList<>();


        types = new java.util.ArrayList<>();


        formats = new java.util.ArrayList<>();


        sources = new java.util.ArrayList<>();


        accessibilityFeatures = new java.util.ArrayList<>();


        accessibilityHazard = new java.util.ArrayList<>();


        accessibilitySummary = new java.util.ArrayList<>();





        this.uniqueIdentifier = null;

        this.viewport = null;

        this.media = null;

        this.flow = null;

        this.alignXCenter = false;





    }


    /**
     * 复制构造函数
     *
     * @param metadata 要复制的元数据对象
     */


    public Metadata(Metadata metadata) {


        this.titles = new ArrayList<>(List.copyOf(metadata.titles));


        this.creators = new ArrayList<>(List.copyOf(metadata.creators));


        this.contributors = new ArrayList<>(List.copyOf(metadata.contributors));


        this.publishers = new ArrayList<>(List.copyOf(metadata.publishers));


        this.identifiers = new ArrayList<>(List.copyOf(metadata.identifiers));


        this.subjects = new ArrayList<>(List.copyOf(metadata.subjects));


        this.dates = new ArrayList<>(List.copyOf(metadata.dates));


        this.languages = new ArrayList<>(List.copyOf(metadata.languages));


        this.descriptions = new ArrayList<>(List.copyOf(metadata.descriptions));


        this.rightsList = new ArrayList<>(List.copyOf(metadata.rightsList));


        this.types = new ArrayList<>(List.copyOf(metadata.types));


        this.formats = new ArrayList<>(List.copyOf(metadata.formats));


        this.sources = new ArrayList<>(List.copyOf(metadata.sources));


        this.modified = metadata.modified;


        this.rightsHolder = metadata.rightsHolder;


        this.cover = metadata.cover;


        this.accessibilityFeatures = new ArrayList<>(List.copyOf(metadata.accessibilityFeatures));


        this.accessibilityHazard = new ArrayList<>(List.copyOf(metadata.accessibilityHazard));


        this.accessibilitySummary = new ArrayList<>(List.copyOf(metadata.accessibilitySummary));


        this.layout = metadata.layout;


        this.orientation = metadata.orientation;


        this.spread = metadata.spread;





        this.uniqueIdentifier = metadata.uniqueIdentifier;





        this.viewport = metadata.viewport;





        this.media = metadata.media;





        this.flow = metadata.flow;





        this.alignXCenter = metadata.alignXCenter;





    }

    /**
     * 获取标题列表
     *
     * @return 标题列表（不可修改）
     */


    public List<String> getTitles() {


        return Collections.unmodifiableList(titles);


    }


    /**
     * 获取主标题（第一个标题）
     *
     * @return 主标题
     */


    public String getTitle() {


        return titles.isEmpty() ? null : titles.get(0);


    }


    /**
     * 添加标题
     *
     * @param title 标题
     */


    public void addTitle(String title) {


        this.titles.add(title);


    }


    /**
     * 设置标题（清空现有标题并添加新标题）
     *
     * @param title 标题
     */


    public void setTitle(String title) {


        this.titles.clear();


        this.titles.add(title);


    }


    /**
     * 获取创建者列表
     *
     * @return 创建者列表（不可修改）
     */


    public List<String> getCreators() {


        return Collections.unmodifiableList(creators);


    }


    /**
     * 获取主创建者（第一个创建者）
     *
     * @return 主创建者
     */


    public String getCreator() {


        return creators.isEmpty() ? null : creators.get(0);


    }


    /**
     * 添加创建者
     *
     * @param creator 创建者
     */


    public void addCreator(String creator) {


        this.creators.add(creator);


    }


    /**
     * 设置创建者（清空现有创建者并添加新创建者）
     *
     * @param creator 创建者
     */


    public void setCreator(String creator) {


        this.creators.clear();


        this.creators.add(creator);


    }


    /**
     * 获取出版商列表
     *
     * @return 出版商列表（不可修改）
     */


    public List<String> getPublishers() {


        return Collections.unmodifiableList(publishers);


    }


    /**
     * 获取第一个出版商
     *
     * @return 第一个出版商
     */


    public String getPublisher() {


        return publishers.isEmpty() ? null : publishers.get(0);


    }


    /**
     * 添加出版商
     *
     * @param publisher 出版商
     */


    public void addPublisher(String publisher) {


        this.publishers.add(publisher);


    }


    /**
     * 设置出版商（清空现有出版商并添加新出版商）
     *
     * @param publisher 出版商
     */


    public void setPublisher(String publisher) {


        this.publishers.clear();


        this.publishers.add(publisher);


    }


    /**
     * 获取标识符列表
     *
     * @return 标识符列表（不可修改）
     */


    public List<String> getIdentifiers() {


        return Collections.unmodifiableList(identifiers);


    }


    /**
     * 获取第一个标识符
     *
     * @return 第一个标识符
     */


    public String getIdentifier() {


        return identifiers.isEmpty() ? null : identifiers.get(0);


    }


    /**
     * 添加标识符
     *
     * @param identifier 标识符
     */


    public void addIdentifier(String identifier) {


        this.identifiers.add(identifier);


    }


    /**
     * 设置标识符（清空现有标识符并添加新标识符）
     *
     * @param identifier 标识符
     */


    public void setIdentifier(String identifier) {


        this.identifiers.clear();


        this.identifiers.add(identifier);


    }


    /**
     * 获取格式列表
     *
     * @return 格式列表（不可修改）
     */


    public List<String> getFormats() {


        return Collections.unmodifiableList(formats);


    }


    /**
     * 获取第一个格式
     *
     * @return 第一个格式
     */


    public String getFormat() {


        return formats.isEmpty() ? null : formats.get(0);


    }


    /**
     * 添加格式
     *
     * @param format 格式
     */


    public void addFormat(String format) {


        this.formats.add(format);


    }


    /**
     * 设置格式（清空现有格式并添加新格式）
     *
     * @param format 格式
     */


    public void setFormat(String format) {


        this.formats.clear();


        this.formats.add(format);


    }


    /**
     * 获取类型列表
     *
     * @return 类型列表（不可修改）
     */


    public List<String> getTypes() {


        return Collections.unmodifiableList(types);


    }


    /**
     * 获取第一个类型
     *
     * @return 第一个类型
     */


    public String getType() {


        return types.isEmpty() ? null : types.get(0);


    }


    /**
     * 添加类型
     *
     * @param type 类型
     */


    public void addType(String type) {


        this.types.add(type);


    }


    /**
     * 设置类型（清空现有类型并添加新类型）
     *
     * @param type 类型
     */


    public void setType(String type) {


        this.types.clear();


        this.types.add(type);


    }


    /**
     * 获取日期列表
     *
     * @return 日期列表（不可修改）
     */


    public List<String> getDates() {


        return Collections.unmodifiableList(dates);


    }


    /**
     * 获取第一个日期
     *
     * @return 第一个日期
     */


    public String getDate() {


        return dates.isEmpty() ? null : dates.get(0);


    }


    /**
     * 添加日期
     *
     * @param date 日期
     */


    public void addDate(String date) {


        this.dates.add(date);


    }


    /**
     * 设置日期（清空现有日期并添加新日期）
     *
     * @param date 日期
     */


    public void setDate(String date) {


        this.dates.clear();


        this.dates.add(date);


    }


    /**
     * 获取语言列表
     *
     * @return 语言列表（不可修改）
     */


    public List<String> getLanguages() {


        return Collections.unmodifiableList(languages);


    }


    /**
     * 获取第一个语言
     *
     * @return 第一个语言
     */


    public String getLanguage() {


        return languages.isEmpty() ? null : languages.get(0);


    }


    /**
     * 添加语言
     *
     * @param language 语言
     */


    public void addLanguage(String language) {


        this.languages.add(language);


    }


    /**
     * 设置语言（清空现有语言并添加新语言）
     *
     * @param language 语言
     */


    public void setLanguage(String language) {


        this.languages.clear();


        this.languages.add(language);


    }


    /**
     * 获取来源列表
     *
     * @return 来源列表（不可修改）
     */


    public List<String> getSources() {


        return Collections.unmodifiableList(sources);


    }


    /**
     * 获取第一个来源
     *
     * @return 第一个来源
     */


    public String getSource() {


        return sources.isEmpty() ? null : sources.get(0);


    }


    /**
     * 添加来源
     *
     * @param source 来源
     */


    public void addSource(String source) {


        this.sources.add(source);


    }


    /**
     * 设置来源（清空现有来源并添加新来源）
     *
     * @param source 来源
     */


    public void setSource(String source) {


        this.sources.clear();


        this.sources.add(source);


    }


    /**
     * 获取描述列表
     *
     * @return 描述列表（不可修改）
     */


    public List<String> getDescriptions() {


        return Collections.unmodifiableList(descriptions);


    }


    /**
     * 获取第一个描述
     *
     * @return 第一个描述
     */


    public String getDescription() {


        return descriptions.isEmpty() ? null : descriptions.get(0);


    }


    /**
     * 添加描述
     *
     * @param description 描述
     */


    public void addDescription(String description) {


        this.descriptions.add(description);


    }


    /**
     * 设置描述（清空现有描述并添加新描述）
     *
     * @param description 描述
     */


    public void setDescription(String description) {


        this.descriptions.clear();


        this.descriptions.add(description);


    }


    /**
     * 获取权利列表
     *
     * @return 权利列表（不可修改）
     */


    public List<String> getRightsList() {


        return Collections.unmodifiableList(rightsList);


    }


    /**
     * 获取第一个权利
     *
     * @return 第一个权利
     */


    public String getRights() {


        return rightsList.isEmpty() ? null : rightsList.get(0);


    }


    /**
     * 添加权利
     *
     * @param rights 权利
     */


    public void addRights(String rights) {


        this.rightsList.add(rights);


    }


    /**
     * 设置权利（清空现有权利并添加新权利）
     *
     * @param rights 权利
     */


    public void setRights(String rights) {


        this.rightsList.clear();


        this.rightsList.add(rights);


    }


    /**
     * 获取主题列表
     *
     * @return 主题列表（不可修改）
     */


    public List<String> getSubjects() {


        return Collections.unmodifiableList(subjects);


    }


    /**
     * 添加主题
     *
     * @param subject 主题
     */


    public void addSubject(String subject) {


        this.subjects.add(subject);


    }


    /**
     * 获取第一个主题
     *
     * @return 第一个主题
     */


    public String getSubject() {


        return subjects.isEmpty() ? null : subjects.get(0);


    }


    /**
     * 获取第一个贡献者
     *
     * @return 第一个贡献者
     */

    public String getContributor() {

        if (contributors.isEmpty()) {
            return null;
        }
        return contributors.get(0);

    }


    /**
     * 获取贡献者列表
     *
     * @return 贡献者列表（不可修改）
     */

    public List<String> getContributors() {

        return Collections.unmodifiableList(contributors);

    }


    /**
     * 添加贡献者
     *
     * @param contributor 贡献者
     */

    public void addContributor(String contributor) {

        this.contributors.add(contributor);

    }


    /**
     * 获取修改时间
     *
     * @return 修改时间
     */

    public String getModified() {

        return modified;

    }


    /**
     * 设置修改时间
     *
     * @param modified 修改时间
     */

    public void setModified(String modified) {

        this.modified = modified;

    }


    /**
     * 获取权利持有者
     *
     * @return 权利持有者
     */

    public String getRightsHolder() {

        return rightsHolder;

    }


    /**
     * 设置权利持有者
     *
     * @param rightsHolder 权利持有者
     */


    public void setRightsHolder(String rightsHolder) {


        this.rightsHolder = rightsHolder;


    }


    // 可访问性元数据相关方法


    /**
     * 获取可访问性特征列表
     *
     * @return 可访问性特征列表（不可修改）
     */


    public List<String> getAccessibilityFeatures() {


        return Collections.unmodifiableList(accessibilityFeatures);


    }


    /**
     * 添加可访问性特征
     *
     * @param feature 可访问性特征
     */


    public void addAccessibilityFeature(String feature) {


        this.accessibilityFeatures.add(feature);


    }


    /**
     * 获取可访问性危害列表
     *
     * @return 可访问性危害列表（不可修改）
     */


    public List<String> getAccessibilityHazard() {


        return Collections.unmodifiableList(accessibilityHazard);


    }


    /**
     * 添加可访问性危害
     *
     * @param hazard 可访问性危害
     */


    public void addAccessibilityHazard(String hazard) {


        this.accessibilityHazard.add(hazard);


    }


    /**
     * 获取可访问性摘要
     *
     * @return 可访问性摘要
     */


    public String getAccessibilitySummary() {


        return accessibilitySummary.isEmpty() ? null : accessibilitySummary.get(0);


    }


    /**
     * 添加可访问性摘要
     *
     * @param summary 可访问性摘要
     */


    public void addAccessibilitySummary(String summary) {


        this.accessibilitySummary.add(summary);


    }


    // 渲染属性相关方法


    /**
     * 获取布局属性
     *
     * @return 布局属性
     */


    public String getLayout() {


        return layout;


    }


    /**
     * 设置布局属性
     *
     * @param layout 布局属性
     */


    public void setLayout(String layout) {


        this.layout = layout;


    }


    /**
     * 获取方向属性
     *
     * @return 方向属性
     */


    public String getOrientation() {


        return orientation;


    }


    /**
     * 设置方向属性
     *
     * @param orientation 方向属性
     */


    public void setOrientation(String orientation) {


        this.orientation = orientation;


    }


    /**
     * 获取展开属性
     *
     * @return 展开属性
     */


    public String getSpread() {


        return spread;


    }


    /**

     * 设置展开属性

     *

     * @param spread 展开属性

     */





    public void setSpread(String spread) {





        this.spread = spread;





    }





    /**

     * 获取视口属性

     *

     * @return 视口属性

     */





    public String getViewport() {





        return viewport;





    }





    /**

     * 设置视口属性

     *

     * @param viewport 视口属性

     */





    public void setViewport(String viewport) {





        this.viewport = viewport;





    }





    /**

     * 获取媒体属性

     *

     * @return 媒体属性

     */





    public String getMedia() {





        return media;





    }





    /**

     * 设置媒体属性

     *

     * @param media 媒体属性

     */





    public void setMedia(String media) {





        this.media = media;





    }





    /**

     * 获取流动属性

     *

     * @return 流动属性

     */





    public String getFlow() {





        return flow;





    }





    /**

     * 设置流动属性

     *

     * @param flow 流动属性

     */





    public void setFlow(String flow) {





        this.flow = flow;





    }





    /**

     * 获取水平居中对齐属性

     *

     * @return 水平居中对齐属性

     */





    public boolean isAlignXCenter() {





        return alignXCenter;





    }





    /**

     * 设置水平居中对齐属性

     *

     * @param alignXCenter 水平居中对齐属性

     */





    public void setAlignXCenter(boolean alignXCenter) {





        this.alignXCenter = alignXCenter;





    }





    /**

     * 获取唯一标识符

     *

     * @return 唯一标识符

     */

    public String getUniqueIdentifier() {

        return uniqueIdentifier;

    }





    /**

     * 设置唯一标识符

     *

     * @param uniqueIdentifier 唯一标识符

     */

    public void setUniqueIdentifier(String uniqueIdentifier) {

        this.uniqueIdentifier = uniqueIdentifier;

    }


}


