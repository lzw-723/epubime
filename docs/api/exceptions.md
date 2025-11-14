# 异常类

EPUBime 提供了完整的异常处理体系，所有异常都继承自 `EpubException` 基类。

## 继承关系

```
Exception
 └── EpubException
     ├── EpubParseException      (解析异常)
     ├── EpubFormatException     (格式异常)
     ├── EpubPathValidationException (路径验证异常)
     ├── EpubResourceException   (资源异常)
     └── EpubZipException        (ZIP 异常)
```

## EpubException

`EpubException` 是所有 EPUB 相关异常的基类。

### 构造函数

```java
public EpubException(String message)
```
创建一个新的 EpubException 实例。

参数:
- `message`: 异常消息

```java
public EpubException(String message, Throwable cause)
```
创建一个新的 EpubException 实例。

参数:
- `message`: 异常消息
- `cause`: 原始异常

```java
public EpubException(String fileName, String path, String message)
```
创建一个新的 EpubException 实例，包含文件名和路径信息。

参数:
- `fileName`: 文件名
- `path`: 文件路径
- `message`: 异常消息

```java
public EpubException(String fileName, String path, String message, Throwable cause)
```
创建一个新的 EpubException 实例，包含文件名、路径信息和原始异常。

参数:
- `fileName`: 文件名
- `path`: 文件路径
- `message`: 异常消息
- `cause`: 原始异常

### 主要方法

#### getFileName()
```java
public String getFileName()
```
获取相关文件的名称。

返回:
- `String`: 文件名

#### getPath()
```java
public String getPath()
```
获取相关文件的路径。

返回:
- `String`: 文件路径

#### getFullMessage()
```java
public String getFullMessage()
```
获取包含文件名和路径的完整错误消息。

返回:
- `String`: 完整错误消息

## EpubParseException

解析异常，用于处理 EPUB 文件解析过程中的错误。

### 构造函数

```java
public EpubParseException(String message)
```
创建一个新的 EpubParseException 实例。

参数:
- `message`: 异常消息

```java
public EpubParseException(String fileName, String path, String message)
```
创建一个新的 EpubParseException 实例，包含文件名和路径信息。

参数:
- `fileName`: 文件名
- `path`: 文件路径
- `message`: 异常消息

```java
public EpubParseException(String fileName, String path, String message, Throwable cause)
```
创建一个新的 EpubParseException 实例，包含文件名、路径信息和原始异常。

参数:
- `fileName`: 文件名
- `path`: 文件路径
- `message`: 异常消息
- `cause`: 原始异常

## EpubFormatException

格式异常，用于处理 EPUB 格式不符合规范的情况。

### 构造函数

```java
public EpubFormatException(String message)
```
创建一个新的 EpubFormatException 实例。

参数:
- `message`: 异常消息

```java
public EpubFormatException(String fileName, String path, String message)
```
创建一个新的 EpubFormatException 实例，包含文件名和路径信息。

参数:
- `fileName`: 文件名
- `path`: 文件路径
- `message`: 异常消息

```java
public EpubFormatException(String fileName, String path, String message, Throwable cause)
```
创建一个新的 EpubFormatException 实例，包含文件名、路径信息和原始异常。

参数:
- `fileName`: 文件名
- `path`: 文件路径
- `message`: 异常消息
- `cause`: 原始异常

## EpubPathValidationException

路径验证异常，用于防止目录遍历攻击。

### 构造函数

```java
public EpubPathValidationException(String path, String message)
```
创建一个新的 EpubPathValidationException 实例。

参数:
- `path`: 无效路径
- `message`: 异常消息

```java
public EpubPathValidationException(String path, String message, Throwable cause)
```
创建一个新的 EpubPathValidationException 实例。

参数:
- `path`: 无效路径
- `message`: 异常消息
- `cause`: 原始异常

## EpubResourceException

资源异常，用于处理资源文件访问错误。

### 构造函数

```java
public EpubResourceException(String message)
```
创建一个新的 EpubResourceException 实例。

参数:
- `message`: 异常消息

```java
public EpubResourceException(String path, String message)
```
创建一个新的 EpubResourceException 实例，包含资源路径信息。

参数:
- `path`: 资源路径
- `message`: 异常消息

```java
public EpubResourceException(String path, String message, Throwable cause)
```
创建一个新的 EpubResourceException 实例，包含资源路径和原始异常。

参数:
- `path`: 资源路径
- `message`: 异常消息
- `cause`: 原始异常

## EpubZipException

ZIP 异常，用于处理 ZIP 文件操作错误。

### 构造函数

```java
public EpubZipException(String message)
```
创建一个新的 EpubZipException 实例。

参数:
- `message`: 异常消息

```java
public EpubZipException(String fileName, String message)
```
创建一个新的 EpubZipException 实例，包含文件名信息。

参数:
- `fileName`: 文件名
- `message`: 异常消息

```java
public EpubZipException(String fileName, String message, Throwable cause)
```
创建一个新的 EpubZipException 实例，包含文件名和原始异常。

参数:
- `fileName`: 文件名
- `message`: 异常消息
- `cause`: 原始异常

## 使用示例

```java
try {
    EpubParser parser = new EpubParser(new File("book.epub"));
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    // 处理解析异常
    System.err.println("解析错误: " + e.getFullMessage());
    e.printStackTrace();
} catch (EpubFormatException e) {
    // 处理格式异常
    System.err.println("格式错误: " + e.getFullMessage());
} catch (EpubPathValidationException e) {
    // 处理路径验证异常
    System.err.println("路径验证失败: " + e.getMessage() + ", 路径: " + e.getPath());
} catch (EpubResourceException e) {
    // 处理资源异常
    System.err.println("资源错误: " + e.getMessage() + ", 路径: " + e.getPath());
} catch (EpubZipException e) {
    // 处理 ZIP 异常
    System.err.println("ZIP 错误: " + e.getFullMessage());
} catch (Exception e) {
    // 处理其他异常
    System.err.println("未预期的错误: " + e.getMessage());
    e.printStackTrace();
}
```