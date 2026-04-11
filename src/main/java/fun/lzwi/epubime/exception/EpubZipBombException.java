package fun.lzwi.epubime.exception;

/**
 * ZIP Bomb 异常
 * 
 * <p>当检测到潜在的 ZIP Bomb 攻击时抛出，包括：</p>
 * <ul>
 *   <li>文件大小超过限制</li>
 *   <li>压缩比异常（过度压缩）</li>
 *   <li>ZIP 条目数量过多</li>
 *   <li>解压后大小超过限制</li>
 * </ul>
 * 
 * @see fun.lzwi.epubime.zip.ZipBombProtection
 */
public class EpubZipBombException extends EpubZipException {
    
    private static final long serialVersionUID = 1L;
    
    private final String attackType;
    private final long expectedLimit;
    private final long actualValue;

    /**
     * 构造 ZIP Bomb 异常
     * 
     * @param message 错误消息
     * @param attackType 攻击类型
     * @param expectedLimit 预期限制
     * @param actualValue 实际值
     */
    public EpubZipBombException(String message, String attackType, 
                                long expectedLimit, long actualValue) {
        super(message, "unknown", "unknown", null);
        this.attackType = attackType;
        this.expectedLimit = expectedLimit;
        this.actualValue = actualValue;
    }

    /**
     * 构造 ZIP Bomb 异常（带原因）
     * 
     * @param message 错误消息
     * @param attackType 攻击类型
     * @param expectedLimit 预期限制
     * @param actualValue 实际值
     * @param cause 原因异常
     */
    public EpubZipBombException(String message, String attackType, 
                                long expectedLimit, long actualValue, Throwable cause) {
        super(message, "unknown", "unknown", cause);
        this.attackType = attackType;
        this.expectedLimit = expectedLimit;
        this.actualValue = actualValue;
    }

    /**
     * 获取攻击类型
     * 
     * @return 攻击类型
     */
    public String getAttackType() {
        return attackType;
    }

    /**
     * 获取预期限制
     * 
     * @return 预期限制
     */
    public long getExpectedLimit() {
        return expectedLimit;
    }

    /**
     * 获取实际值
     * 
     * @return 实际值
     */
    public long getActualValue() {
        return actualValue;
    }

    @Override
    public String getMessage() {
        return String.format("ZIP Bomb detected [%s]: %s (limit: %d, actual: %d)", 
                           attackType, super.getMessage(), expectedLimit, actualValue);
    }
}
