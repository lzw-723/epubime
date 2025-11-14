# Metadata

`Metadata` class represents the metadata information of an EPUB book, including standard metadata fields such as title, author, publisher, etc.

## Constructor

```java
public Metadata()
```
Creates a new Metadata instance.

## Main Methods

### getTitle()
```java
public String getTitle()
```
Gets book title.

Returns:
- `String`: Book title

### setTitle()
```java
public void setTitle(String title)
```
Sets book title.

Parameters:
- `title`: Book title

### getCreator()
```java
public String getCreator()
```
Gets book author.

Returns:
- `String`: Book author

### setCreator()
```java
public void setCreator(String creator)
```
Sets book author.

Parameters:
- `creator`: Book author

### getLanguage()
```java
public String getLanguage()
```
Gets book language.

Returns:
- `String`: Book language code (e.g., "en", "zh")

### setLanguage()
```java
public void setLanguage(String language)
```
Sets book language.

Parameters:
- `language`: Book language code

### getPublisher()
```java
public String getPublisher()
```
Gets book publisher.

Returns:
- `String`: Book publisher

### setPublisher()
```java
public void setPublisher(String publisher)
```
Sets book publisher.

Parameters:
- `publisher`: Book publisher

### getIdentifier()
```java
public String getIdentifier()
```
Gets book identifier.

Returns:
- `String`: Book identifier

### setIdentifier()
```java
public void setIdentifier(String identifier)
```
Sets book identifier.

Parameters:
- `identifier`: Book identifier

### getDate()
```java
public Date getDate()
```
Gets book publication date.

Returns:
- `Date`: Book publication date

### setDate()
```java
public void setDate(Date date)
```
Sets book publication date.

Parameters:
- `date`: Book publication date

### getDescription()
```java
public String getDescription()
```
Gets book description.

Returns:
- `String`: Book description

### setDescription()
```java
public void setDescription(String description)
```
Sets book description.

Parameters:
- `description`: Book description

### getSubjects()
```java
public List<String> getSubjects()
```
Gets book subject list.

Returns:
- `List<String>`: Book subject list

### setSubjects()
```java
public void setSubjects(List<String> subjects)
```
Sets book subject list.

Parameters:
- `subjects`: Book subject list

### addSubject()
```java
public void addSubject(String subject)
```
Adds book subject.

Parameters:
- `subject`: Book subject

### getContributors()
```java
public List<String> getContributors()
```
Gets book contributor list.

Returns:
- `List<String>`: Book contributor list

### setContributors()
```java
public void setContributors(List<String> contributors)
```
Sets book contributor list.

Parameters:
- `contributors`: Book contributor list

### addContributor()
```java
public void addContributor(String contributor)
```
Adds book contributor.

Parameters:
- `contributor`: Book contributor

### getRights()
```java
public String getRights()
```
Gets book rights information.

Returns:
- `String`: Book rights information

### setRights()
```java
public void setRights(String rights)
```
Sets book rights information.

Parameters:
- `rights`: Book rights information

### getCoverId()
```java
public String getCoverId()
```
Gets cover image ID.

Returns:
- `String`: Cover image ID

### setCoverId()
```java
public void setCoverId(String coverId)
```
Sets cover image ID.

Parameters:
- `coverId`: Cover image ID

### getSeries()
```java
public String getSeries()
```
Gets book series information.

Returns:
- `String`: Book series information

### setSeries()
```java
public void setSeries(String series)
```
Sets book series information.

Parameters:
- `series`: Book series information

### getSeriesIndex()
```java
public String getSeriesIndex()
```
Gets book's index in series.

Returns:
- `String`: Book's index in series

### setSeriesIndex()
```java
public void setSeriesIndex(String seriesIndex)
```
Sets book's index in series.

Parameters:
- `seriesIndex`: Book's index in series

### getISBN()
```java
public String getISBN()
```
Gets book ISBN.

Returns:
- `String`: Book ISBN

### setISBN()
```java
public void setISBN(String isbn)
```
Sets book ISBN.

Parameters:
- `isbn`: Book ISBN

### getUUID()
```java
public String getUUID()
```
Gets book UUID.

Returns:
- `String`: Book UUID

### setUUID()
```java
public void setUUID(String uuid)
```
Sets book UUID.

Parameters:
- `uuid`: Book UUID

### getModifiedDate()
```java
public Date getModifiedDate()
```
Gets book modification date.

Returns:
- `Date`: Book modification date

### setModifiedDate()
```java
public void setModifiedDate(Date modifiedDate)
```
Sets book modification date.

Parameters:
- `modifiedDate`: Book modification date

### getMetaAttributes()
```java
public Map<String, String> getMetaAttributes()
```
Gets custom metadata attributes.

Returns:
- `Map<String, String>`: Custom metadata attributes map

### setMetaAttributes()
```java
public void setMetaAttributes(Map<String, String> metaAttributes)
```
Sets custom metadata attributes.

Parameters:
- `metaAttributes`: Custom metadata attributes map

### addMetaAttribute()
```java
public void addMetaAttribute(String key, String value)
```
Adds custom metadata attribute.

Parameters:
- `key`: Attribute key
- `value`: Attribute value

### getDcTerms()
```java
public Map<String, String> getDcTerms()
```
Gets Dublin Core Terms metadata.

Returns:
- `Map<String, String>`: Dublin Core Terms metadata map

### setDcTerms()
```java
public void setDcTerms(Map<String, String> dcTerms)
```
Sets Dublin Core Terms metadata.

Parameters:
- `dcTerms`: Dublin Core Terms metadata map

### addDcTerm()
```java
public void addDcTerm(String key, String value)
```
Adds Dublin Core Term metadata.

Parameters:
- `key`: Term key
- `value`: Term value

### isValid()
```java
public boolean isValid()
```
Checks if metadata is valid (at least contains title).

Returns:
- `boolean`: Returns true if metadata is valid, otherwise false

### toString()
```java
public String toString()
```
Returns string representation of metadata.

Returns:
- `String`: String representation of metadata