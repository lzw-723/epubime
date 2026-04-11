package fun.lzwi.epubime.zip;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Path validation utility class
 * Used to prevent directory traversal attacks (Path Traversal Attack)
 * 
 * <p>Security features:</p>
 * <ul>
 *   <li>Multiple rounds of URL decoding to prevent double/triple encoding attacks</li>
 *   <li>Protection against null bytes injection</li>
 *   <li>Protection against Windows and Unix path traversal</li>
 *   <li>Absolute path rejection</li>
 *   <li>Strict normalization and validation</li>
 * </ul>
 */
public class PathValidator {
    
    /**
     * Maximum number of decoding iterations to prevent infinite loops
     */
    private static final int MAX_DECODING_ITERATIONS = 5;
    
    /**
     * Validates whether a file path is safe to prevent directory traversal attacks
     * 
     * <p>This method performs comprehensive security checks:</p>
     * <ol>
     *   <li>Null and empty path rejection</li>
     *   <li>Multiple rounds of URL decoding (prevents ..%252F attacks)</li>
     *   <li>Null byte removal (prevents %00 attacks)</li>
     *   <li>Absolute path rejection</li>
     *   <li>Path traversal detection (.. components)</li>
     *   <li>Normalization and base path verification</li>
     * </ol>
     * 
     * @param basePath base path (can be empty string for ZIP internal paths)
     * @param relativePath relative path to validate
     * @return true if the path is safe, false otherwise
     */
    public static boolean isPathSafe(String basePath, String relativePath) {
        // Step 1: Null and empty checks
        if (basePath == null || relativePath == null) {
            return false;
        }
        
        if (relativePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Step 2: Multiple rounds of URL decoding to prevent encoding attacks
            // e.g., ..%252F..%252F -> ..%2F..%2F -> ../../
            relativePath = decodeMultipleTimes(relativePath);
        } catch (Exception e) {
            // If decoding fails, reject the path
            return false;
        }
        
        // Step 3: Check for null bytes (can be used to bypass security checks)
        if (relativePath.contains("\0") || relativePath.contains("%00")) {
            return false;
        }
        
        // Step 4: Reject absolute paths
        if (isAbsolutePath(relativePath)) {
            return false;
        }
        
        try {
            // Step 5: Normalize paths
            Path base = Paths.get(basePath.isEmpty() ? "." : basePath).toAbsolutePath().normalize();
            Path resolved = base.resolve(relativePath).toAbsolutePath().normalize();
            
            // Step 6: Check if the resolved path is under the base path
            // This prevents directory traversal attacks like ../../../etc/passwd
            if (!resolved.startsWith(base)) {
                return false;
            }
            
            // Step 7: Additional check - verify no ".." components remain after normalization
            // This catches edge cases in path resolution
            String resolvedPath = resolved.toString();
            if (resolvedPath.contains("..")) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            // If path normalization fails, reject the path
            return false;
        }
    }
    
    /**
     * Decodes URL-encoded string multiple times to prevent encoding attacks
     * 
     * <p>Attackers may use multiple encoding layers to bypass security:</p>
     * <ul>
     *   <li>Single encoding: ..%2F..%2F</li>
     *   <li>Double encoding: ..%252F..%252F</li>
     *   <li>Triple encoding: ..%25252F..%25252F</li>
     * </ul>
     * 
     * This method decodes until the string stabilizes or max iterations reached.
     * 
     * @param encoded the URL-encoded string
     * @return fully decoded string
     * @throws UnsupportedEncodingException if UTF-8 is not supported
     */
    private static String decodeMultipleTimes(String encoded) throws UnsupportedEncodingException {
        String decoded = encoded;
        
        for (int i = 0; i < MAX_DECODING_ITERATIONS; i++) {
            String newDecoded = URLDecoder.decode(decoded, "UTF-8");
            
            // If decoding didn't change the string, we're done
            if (newDecoded.equals(decoded)) {
                break;
            }
            
            decoded = newDecoded;
        }
        
        return decoded;
    }
    
    /**
     * Checks if a path is absolute
     * 
     * @param path path to check
     * @return true if the path is absolute
     */
    private static boolean isAbsolutePath(String path) {
        // Check Unix-style absolute paths
        if (path.startsWith("/")) {
            return true;
        }
        
        // Check Windows-style absolute paths (C:\, C:/, \\server\share)
        if (path.length() >= 2) {
            char firstChar = path.charAt(0);
            char secondChar = path.charAt(1);
            
            // C: or C\
            if (Character.isLetter(firstChar) && (secondChar == ':' || secondChar == '\\')) {
                return true;
            }
            
            // UNC path
            if (path.startsWith("\\\\") || path.startsWith("//")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Sanitizes a path string by removing potential directory traversal characters
     * 
     * <p>This method should be used with caution. It's better to reject invalid paths
     * rather than trying to sanitize them, as sanitization may introduce vulnerabilities.</p>
     * 
     * @param path path string
     * @return sanitized path
     */
    public static String sanitizePath(String path) {
        if (path == null) {
            return null;
        }
        
        try {
            // Decode URL-encoded characters (multiple rounds)
            path = decodeMultipleTimes(path);
        } catch (UnsupportedEncodingException e) {
            // If decoding fails, return null to indicate failure
            return null;
        }
        
        // Remove null bytes
        path = path.replace("\0", "");
        
        // Remove leading and trailing whitespace
        path = path.trim();
        
        // Reject empty paths
        if (path.isEmpty()) {
            return null;
        }
        
        // Remove leading slashes
        while (path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1);
        }
        
        // Remove trailing slashes (but preserve if it's just "/")
        while (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        
        // Reject if path became empty after sanitization
        if (path.isEmpty()) {
            return null;
        }
        
        return path;
    }
    
    /**
     * Strictly validates a path and returns a normalized version if safe
     * 
     * @param basePath base path
     * @param relativePath relative path to validate
     * @return normalized path if safe, null otherwise
     */
    public static String validateAndNormalize(String basePath, String relativePath) {
        if (!isPathSafe(basePath, relativePath)) {
            return null;
        }
        
        try {
            String decodedPath = decodeMultipleTimes(relativePath);
            Path base = Paths.get(basePath.isEmpty() ? "." : basePath).toAbsolutePath().normalize();
            Path resolved = base.resolve(decodedPath).toAbsolutePath().normalize();
            
            return resolved.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
