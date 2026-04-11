package fun.lzwi.epubime.zip;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Path Validator 安全测试类
 * 测试各种路径攻击场景
 */
public class PathValidatorTest {

    @Test
    public void testNormalPaths() {
        // 测试正常路径
        assertTrue(PathValidator.isPathSafe("", "normal/file.txt"));
        assertTrue(PathValidator.isPathSafe("", "file.html"));
        assertTrue(PathValidator.isPathSafe("", "dir/subdir/file.xml"));
        assertTrue(PathValidator.isPathSafe("base", "relative/path.txt"));
    }

    @Test
    public void testBasicPathTraversalAttacks() {
        // 测试基本路径遍历攻击
        assertFalse(PathValidator.isPathSafe("", "../../../etc/passwd"));
        assertFalse(PathValidator.isPathSafe("", "../config.ini"));
        assertFalse(PathValidator.isPathSafe("", "file.txt/../../secret"));
        assertFalse(PathValidator.isPathSafe("", ".."));
        assertFalse(PathValidator.isPathSafe("", "../"));
        assertFalse(PathValidator.isPathSafe("", "foo/../../bar"));
    }

    @Test
    public void testSingleURLEncodedAttacks() {
        // 测试单次 URL 编码的攻击
        assertFalse(PathValidator.isPathSafe("", "..%2Fetc%2Fpasswd"));
        assertFalse(PathValidator.isPathSafe("", "%2E%2E%2Fetc%2Fpasswd"));
        assertFalse(PathValidator.isPathSafe("", "%2e%2e%2fetc%2fpasswd"));
        assertFalse(PathValidator.isPathSafe("", "..%2F..%2Fetc%2Fpasswd"));
    }

    @Test
    public void testDoubleURLEncodedAttacks() {
        // 测试双重 URL 编码的攻击（之前版本的漏洞）
        assertFalse(PathValidator.isPathSafe("", "..%252F..%252Fetc%252Fpasswd"));
        assertFalse(PathValidator.isPathSafe("", "%252E%252E%252Fetc%252Fpasswd"));
        assertFalse(PathValidator.isPathSafe("", "..%252F..%252F..%252Fetc%252Fpasswd"));
    }

    @Test
    public void testTripleURLEncodedAttacks() {
        // 测试三重 URL 编码的攻击
        assertFalse(PathValidator.isPathSafe("", "..%25252F..%25252Fetc%25252Fpasswd"));
        assertFalse(PathValidator.isPathSafe("", "%25252E%25252E%25252Fetc"));
    }

    @Test
    public void testNullByteInjection() {
        // 测试空字节注入攻击
        assertFalse(PathValidator.isPathSafe("", "file.txt%00.jpg"));
        assertFalse(PathValidator.isPathSafe("", "file.txt\0.jpg"));
        assertFalse(PathValidator.isPathSafe("", "../../../etc/passwd%00.jpg"));
    }

    @Test
    public void testAbsolutePaths() {
        // 测试绝对路径拒绝
        assertFalse(PathValidator.isPathSafe("", "/etc/passwd"));
        assertFalse(PathValidator.isPathSafe("", "/var/log/syslog"));
        assertFalse(PathValidator.isPathSafe("", "C:\\Windows\\System32"));
        assertFalse(PathValidator.isPathSafe("", "D:/sensitive/file.txt"));
        assertFalse(PathValidator.isPathSafe("", "\\\\server\\share\\file"));
        assertFalse(PathValidator.isPathSafe("", "//server/share/file"));
    }

    @Test
    public void testEmptyAndNullInputs() {
        // 测试空值和 null 输入
        assertFalse(PathValidator.isPathSafe(null, "path"));
        assertFalse(PathValidator.isPathSafe("base", null));
        assertFalse(PathValidator.isPathSafe("", ""));
        assertFalse(PathValidator.isPathSafe("", "   "));
    }

    @Test
    public void testMixedEncodingAttacks() {
        // 测试混合编码攻击
        assertFalse(PathValidator.isPathSafe("", "..%2F..%252Fetc/passwd"));
        assertFalse(PathValidator.isPathSafe("", "..%2F%2E%2E%2Fetc"));
    }

    @Test
    public void testWindowsStyleAttacks() {
        // 测试 Windows 风格路径攻击
        assertFalse(PathValidator.isPathSafe("", "..\\..\\..\\etc\\passwd"));
        assertFalse(PathValidator.isPathSafe("", "..%5C..%5C..%5Cetc"));
        assertFalse(PathValidator.isPathSafe("", "..%255C..%255Cetc"));
    }

    @Test
    public void testSanitizePath() {
        // 测试路径清理
        assertEquals("file.txt", PathValidator.sanitizePath("/file.txt"));
        assertEquals("file.txt", PathValidator.sanitizePath("//file.txt"));
        assertEquals("dir/file.txt", PathValidator.sanitizePath("dir/file.txt/"));
        assertEquals("dir/file.txt", PathValidator.sanitizePath("dir/file.txt//"));
        assertEquals("dir/file.txt", PathValidator.sanitizePath("/dir/file.txt/"));
        
        // 测试 null 值处理
        assertNull(PathValidator.sanitizePath(null));
        
        // 测试空路径
        assertNull(PathValidator.sanitizePath(""));
        assertNull(PathValidator.sanitizePath("   "));
        
        // 测试 URL 编码清理
        assertEquals("normal/file.txt", PathValidator.sanitizePath("normal%2Ffile.txt"));
    }

    @Test
    public void testValidateAndNormalize() {
        // 测试验证和标准化
        String normalized = PathValidator.validateAndNormalize("", "normal/file.txt");
        assertNotNull(normalized);
        assertTrue(normalized.endsWith("normal" + java.io.File.separator + "file.txt"));
        
        // 测试攻击路径返回 null
        assertNull(PathValidator.validateAndNormalize("", "../../../etc/passwd"));
        assertNull(PathValidator.validateAndNormalize("", "..%2F..%2Fetc"));
        assertNull(PathValidator.validateAndNormalize("", "/absolute/path"));
    }

    @Test
    public void testEdgeCases() {
        // 测试边界情况
        
        // 单个点（当前目录）
        assertTrue(PathValidator.isPathSafe("", "."));
        assertTrue(PathValidator.isPathSafe("", "./file.txt"));
        
        // 包含点的正常路径
        assertTrue(PathValidator.isPathSafe("", "file.name.txt"));
        assertTrue(PathValidator.isPathSafe("", "dir.name/file.txt"));
        
        // 长路径
        String longPath = "a/b/c/d/e/f/g/h/i/j/k/l/m/n/o/p/q/r/s/t/u/v/w/x/y/z/file.txt";
        assertTrue(PathValidator.isPathSafe("", longPath));
        
        // 特殊字符（非 URL 编码）
        assertTrue(PathValidator.isPathSafe("", "file-name.txt"));
        assertTrue(PathValidator.isPathSafe("", "file_name.txt"));
        assertTrue(PathValidator.isPathSafe("", "file name.txt"));
    }

    @Test
    public void testPathWithBasePath() {
        // 测试带基础路径的情况
        assertTrue(PathValidator.isPathSafe("/safe/base", "relative/file.txt"));
        assertTrue(PathValidator.isPathSafe("/safe/base", "subdir/file.txt"));
        
        // 尝试逃逸基础路径
        assertFalse(PathValidator.isPathSafe("/safe/base", "../../../etc/passwd"));
        assertFalse(PathValidator.isPathSafe("/safe/base", "..%2F..%2F..%2Fetc%2Fpasswd"));
    }

    @Test
    public void testUnicodeAndSpecialCharacters() {
        // 测试 Unicode 和特殊字符
        // 中文路径应该是安全的
        assertTrue(PathValidator.isPathSafe("", "中文/文件.txt"));
        assertTrue(PathValidator.isPathSafe("", "目录/子目录/文件.xml"));
        
        // URL 编码的中文字符
        assertTrue(PathValidator.isPathSafe("", "%E4%B8%AD%E6%96%87/%E6%96%87%E4%BB%B6.txt"));
    }
}
