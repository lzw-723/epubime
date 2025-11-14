# Metadata

`Metadata` 类代表 EPUB 书籍的元数据信息，包括标题、作者、出版商等标准元数据字段。

## 构造函数

```java
public Metadata()
```
创建一个新的 Metadata 实例。

## 主要方法

### getTitle()
```java
public String getTitle()
```
获取书籍标题。

返回:
- `String`: 书籍标题

### setTitle()
```java
public void setTitle(String title)
```
设置书籍标题。

参数:
- `title`: 书籍标题

### getCreator()
```java
public String getCreator()
```
获取书籍作者。

返回:
- `String`: 书籍作者

### setCreator()
```java
public void setCreator(String creator)
```
设置书籍作者。

参数:
- `creator`: 书籍作者

### getLanguage()
```java
public String getLanguage()
```
获取书籍语言。

返回:
- `String`: 书籍语言代码（如 "en"、"zh"）

### setLanguage()
```java
public void setLanguage(String language)
```
设置书籍语言。

参数:
- `language`: 书籍语言代码

### getPublisher()
```java
public String getPublisher()
```
获取书籍出版商。

返回:
- `String`: 书籍出版商

### setPublisher()
```java
public void setPublisher(String publisher)
```
设置书籍出版商。

参数:
- `publisher`: 书籍出版商

### getIdentifier()
```java
public String getIdentifier()
```
获取书籍标识符。

返回:
- `String`: 书籍标识符

### setIdentifier()
```java
public void setIdentifier(String identifier)
```
设置书籍标识符。

参数:
- `identifier`: 书籍标识符

### getDate()
```java
public Date getDate()
```
获取书籍出版日期。

返回:
- `Date`: 书籍出版日期

### setDate()
```java
public void setDate(Date date)
```
设置书籍出版日期。

参数:
- `date`: 书籍出版日期

### getDescription()
```java
public String getDescription()
```
获取书籍描述。

返回:
- `String`: 书籍描述

### setDescription()
```java
public void setDescription(String description)
```
设置书籍描述。

参数:
- `description`: 书籍描述

### getSubjects()
```java
public List<String> getSubjects()
```
获取书籍主题列表。

返回:
- `List<String>`: 书籍主题列表

### setSubjects()
```java
public void setSubjects(List<String> subjects)
```
设置书籍主题列表。

参数:
- `subjects`: 书籍主题列表

### addSubject()
```java
public void addSubject(String subject)
```
添加书籍主题。

参数:
- `subject`: 书籍主题

### getContributors()
```java
public List<String> getContributors()
```
获取书籍贡献者列表。

返回:
- `List<String>`: 书籍贡献者列表

### setContributors()
```java
public void setContributors(List<String> contributors)
```
设置书籍贡献者列表。

参数:
- `contributors`: 书籍贡献者列表

### addContributor()
```java
public void addContributor(String contributor)
```
添加书籍贡献者。

参数:
- `contributor`: 书籍贡献者

### getRights()
```java
public String getRights()
```
获取书籍版权信息。

返回:
- `String`: 书籍版权信息

### setRights()
```java
public void setRights(String rights)
```
设置书籍版权信息。

参数:
- `rights`: 书籍版权信息

### getCoverId()
```java
public String getCoverId()
```
获取封面图片的 ID。

返回:
- `String`: 封面图片的 ID

### setCoverId()
```java
public void setCoverId(String coverId)
```
设置封面图片的 ID。

参数:
- `coverId`: 封面图片的 ID

### getSeries()
```java
public String getSeries()
```
获取书籍系列信息。

返回:
- `String`: 书籍系列信息

### setSeries()
```java
public void setSeries(String series)
```
设置书籍系列信息。

参数:
- `series`: 书籍系列信息

### getSeriesIndex()
```java
public String getSeriesIndex()
```
获取书籍在系列中的索引。

返回:
- `String`: 书籍在系列中的索引

### setSeriesIndex()
```java
public void setSeriesIndex(String seriesIndex)
```
设置书籍在系列中的索引。

参数:
- `seriesIndex`: 书籍在系列中的索引

### getISBN()
```java
public String getISBN()
```
获取书籍 ISBN。

返回:
- `String`: 书籍 ISBN

### setISBN()
```java
public void setISBN(String isbn)
```
设置书籍 ISBN。

参数:
- `isbn`: 书籍 ISBN

### getUUID()
```java
public String getUUID()
```
获取书籍 UUID。

返回:
- `String`: 书籍 UUID

### setUUID()
```java
public void setUUID(String uuid)
```
设置书籍 UUID。

参数:
- `uuid`: 书籍 UUID

### getModifiedDate()
```java
public Date getModifiedDate()
```
获取书籍修改日期。

返回:
- `Date`: 书籍修改日期

### setModifiedDate()
```java
public void setModifiedDate(Date modifiedDate)
```
设置书籍修改日期。

参数:
- `modifiedDate`: 书籍修改日期

### getMetaAttributes()
```java
public Map<String, String> getMetaAttributes()
```
获取自定义元数据属性。

返回:
- `Map<String, String>`: 自定义元数据属性映射

### setMetaAttributes()
```java
public void setMetaAttributes(Map<String, String> metaAttributes)
```
设置自定义元数据属性。

参数:
- `metaAttributes`: 自定义元数据属性映射

### addMetaAttribute()
```java
public void addMetaAttribute(String key, String value)
```
添加自定义元数据属性。

参数:
- `key`: 属性键
- `value`: 属性值

### getDcTerms()
```java
public Map<String, String> getDcTerms()
```
获取 Dublin Core Terms 元数据。

返回:
- `Map<String, String>`: Dublin Core Terms 元数据映射

### setDcTerms()
```java
public void setDcTerms(Map<String, String> dcTerms)
```
设置 Dublin Core Terms 元数据。

参数:
- `dcTerms`: Dublin Core Terms 元数据映射

### addDcTerm()
```java
public void addDcTerm(String key, String value)
```
添加 Dublin Core Term 元数据。

参数:
- `key`: Term 键
- `value`: Term 值

### isValid()
```java
public boolean isValid()
```
检查元数据是否有效（至少包含标题）。

返回:
- `boolean`: 如果元数据有效则返回 true，否则返回 false

### toString()
```java
public String toString()
```
返回元数据的字符串表示。

返回:
- `String`: 元数据的字符串表示