package fun.lzwi.epubime.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * ZIP 输入流包装器
 * 自动管理 ZipFile 句柄的生命周期，防止资源泄漏
 * 
 * 使用示例：
 * <pre>
 * try (ZipManagedInputStream is = ZipManagedInputStream.open(zipFile, path)) {
 *     // 使用输入流
 *     byte[] data = is.readAllBytes();
 * } // 流关闭时会自动释放 ZipFile 句柄
 * </pre>
 */
public class ZipManagedInputStream extends InputStream {
    
    private final InputStream delegate;
    private boolean closed = false;

    /**
     * 创建由 ZipFileManager 管理的输入流
     * 
     * @param zipFile ZIP 文件
     * @param fileName 文件名
     * @return 受管理的输入流
     * @throws IOException IO 异常
     */
    public static ZipManagedInputStream open(File zipFile, String fileName) throws IOException {
        InputStream is = ZipOperations.getZipInputStream(zipFile, fileName);
        if (is == null) {
            return null;
        }
        return new ZipManagedInputStream(is);
    }

    /**
     * 私有构造函数
     * 
     * @param delegate 实际的输入流
     */
    private ZipManagedInputStream(InputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        return delegate.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        return delegate.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        return delegate.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        return delegate.skip(n);
    }

    @Override
    public int available() throws IOException {
        if (closed) {
            return 0;
        }
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            try {
                delegate.close();
            } finally {
                // 释放 ZipFile 句柄
                ZipOperations.releaseZipFile();
                closed = true;
            }
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (!closed) {
            delegate.mark(readlimit);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return !closed && delegate.markSupported();
    }
}
