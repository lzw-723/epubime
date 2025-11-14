package fun.lzwi.epubime.zip;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

public class PathValidatorTest {

    @Test
    public void testPathValidatorIsPathSafe() {
        // Test normal paths
        assertTrue(PathValidator.isPathSafe("", "normal/file.txt"));
        assertTrue(PathValidator.isPathSafe("", "file.html"));
        assertTrue(PathValidator.isPathSafe("", "dir/subdir/file.xml"));
        
        // Test path traversal attacks
        assertFalse(PathValidator.isPathSafe("", "../../../etc/passwd"));
        assertFalse(PathValidator.isPathSafe("", "../config.ini"));
        assertFalse(PathValidator.isPathSafe("", "file.txt/../../secret"));
        
        // Test boundary conditions
        assertTrue(PathValidator.isPathSafe("", ""));
        assertTrue(PathValidator.isPathSafe("", "."));
        assertFalse(PathValidator.isPathSafe("", ".."));
        assertFalse(PathValidator.isPathSafe("", "../"));
        
        // Test path traversal with URL encoding
        assertFalse(PathValidator.isPathSafe("", "..%2Fetc%2Fpasswd"));
        assertFalse(PathValidator.isPathSafe("", "%2E%2E%2Fetc%2Fpasswd"));
    }
    
    @Test
    public void testPathValidatorSanitizePath() {
        assertEquals("normal/file.txt", PathValidator.sanitizePath("normal/file.txt"));
        assertEquals("file.html", PathValidator.sanitizePath("file.html"));
        assertEquals("dir/subdir/file.xml", PathValidator.sanitizePath("dir/subdir/file.xml"));
        
        // Test cleaning leading and trailing slashes
        assertEquals("file.txt", PathValidator.sanitizePath("/file.txt"));
        assertEquals("file.txt", PathValidator.sanitizePath("//file.txt"));
        assertEquals("dir/file.txt", PathValidator.sanitizePath("dir/file.txt/"));
        assertEquals("dir/file.txt", PathValidator.sanitizePath("dir/file.txt//"));
        assertEquals("dir/file.txt", PathValidator.sanitizePath("/dir/file.txt/"));
        
        // Test null value handling
        assertNull(PathValidator.sanitizePath(null));
        assertEquals("", PathValidator.sanitizePath(""));
        assertEquals("", PathValidator.sanitizePath("   "));
    }
}