package fun.lzwi.epubime.epub;


import java.util.Collections;

import java.util.List;


/**
 * EPUB元数据模型类
 * <p>
 * 表示EPUB电子书的元数据信息，包括标题、作者、出版商等
 */

public class Metadata {


    private String title;


    private String creator;


    private final List<String> contributors;


    private String publisher;


    private String identifier;


    private final List<String> subjects;


    private String date;


    private String language;


    private String description;


    private String rights;


    private String type;


    private String format;


    private String source;


    private String modified;


    private String rightsHolder;


    private String cover;


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

        contributors = new java.util.ArrayList<>();

        subjects = new java.util.ArrayList<>();

    }


    /**
     * 复制构造函数
     *
     * @param metadata 要复制的元数据对象
     */

    public Metadata(Metadata metadata) {


        this.title = metadata.title;


        this.creator = metadata.creator;


        this.contributors = metadata.contributors;


        this.publisher = metadata.publisher;


        this.identifier = metadata.identifier;


        this.subjects = metadata.subjects;


        this.date = metadata.date;


        this.language = metadata.language;


        this.description = metadata.description;


        this.rights = metadata.rights;


        this.type = metadata.type;


        this.format = metadata.format;


        this.source = metadata.source;


        this.modified = metadata.modified;


        this.rightsHolder = metadata.rightsHolder;


        this.cover = metadata.cover;


    }

    /**
     * 获取格式信息
     *
     * @return 格式信息
     */

    public String getFormat() {

        return format;

    }


    /**
     * 设置格式信息
     *
     * @param format 格式信息
     */

    public void setFormat(String format) {

        this.format = format;

    }


    /**
     * 获取类型信息
     *
     * @return 类型信息
     */

    public String getType() {

        return type;

    }


    /**
     * 设置类型信息
     *
     * @param type 类型信息
     */

    public void setType(String type) {

        this.type = type;

    }


    /**
     * 获取标识符
     *
     * @return 标识符
     */

    public String getIdentifier() {

        return identifier;

    }


    /**
     * 设置标识符
     *
     * @param identifier 标识符
     */

    public void setIdentifier(String identifier) {

        this.identifier = identifier;

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

        return subjects.get(0);

    }


    /**
     * 获取描述信息
     *
     * @return 描述信息
     */

    public String getDescription() {

        return description;

    }


    /**
     * 设置描述信息
     *
     * @param description 描述信息
     */

    public void setDescription(String description) {

        this.description = description;

    }


    /**
     * 获取权利信息
     *
     * @return 权利信息
     */

    public String getRights() {

        return rights;

    }


    /**
     * 设置权利信息
     *
     * @param rights 权利信息
     */

    public void setRights(String rights) {

        this.rights = rights;

    }


    /**
     * 获取标题
     *
     * @return 标题
     */

    public String getTitle() {

        return title;

    }


    /**
     * 设置标题
     *
     * @param title 标题
     */

    public void setTitle(String title) {

        this.title = title;

    }


    /**
     * 获取创建者（作者）
     *
     * @return 创建者
     */

    public String getCreator() {

        return creator;

    }


    /**
     * 设置创建者（作者）
     *
     * @param creator 创建者
     */

    public void setCreator(String creator) {

        this.creator = creator;

    }


    /**
     * 获取出版商
     *
     * @return 出版商
     */

    public String getPublisher() {

        return publisher;

    }


    /**
     * 设置出版商
     *
     * @param publisher 出版商
     */

    public void setPublisher(String publisher) {

        this.publisher = publisher;

    }


    /**
     * 获取日期
     *
     * @return 日期
     */

    public String getDate() {

        return date;

    }


    /**
     * 设置日期
     *
     * @param date 日期
     */

    public void setDate(String date) {

        this.date = date;

    }


    /**
     * 获取语言
     *
     * @return 语言
     */

    public String getLanguage() {

        return language;

    }


    /**
     * 设置语言
     *
     * @param language 语言
     */

    public void setLanguage(String language) {

        this.language = language;

    }


    /**
     * 获取来源信息
     *
     * @return 来源信息
     */

    public String getSource() {

        return source;

    }


    /**
     * 设置来源信息
     *
     * @param source 来源信息
     */

    public void setSource(String source) {

        this.source = source;

    }


    /**
     * 获取第一个贡献者
     *
     * @return 第一个贡献者
     */

    public String getContributor() {

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


}

