package org.streambox.datasource.xmbox

import android.content.Context
import org.streambox.datasource.xmbox.config.XmboxConfig
import org.streambox.datasource.xmbox.config.XmboxConfigParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * XMBOX 视频源识别器
 * 负责识别和处理 XMBOX 格式的视频源配置
 */
class XmboxVideoSource(
    private val context: Context,
    private val extractor: VideoUrlExtractor = WebViewVideoUrlExtractor(context)
) {
    
    /**
     * 识别并解析视频源配置
     * 
     * @param configString 配置字符串（JSON 或 JavaScript）
     * @param name 源名称（可选）
     * @return 解析后的配置，如果解析失败则返回 null
     */
    suspend fun identify(configString: String, name: String = ""): XmboxConfig? {
        return withContext(Dispatchers.Default) {
            val config = XmboxConfigParser.parse(configString, name)
            if (config != null && XmboxConfigParser.isValid(config)) {
                config
            } else {
                null
            }
        }
    }
    
    /**
     * 从视频源配置中提取播放 URL
     * 
     * @param pageUrl 页面 URL 或视频 ID
     * @param config 视频源配置
     * @return 视频播放 URL，如果提取失败则返回 null
     */
    suspend fun extractPlayUrl(pageUrl: String, config: XmboxConfig): String? {
        return withContext(Dispatchers.Main) {
            try {
                extractor.extractVideoUrl(pageUrl, config)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * 验证配置是否有效
     */
    fun validateConfig(configString: String): Boolean {
        val config = XmboxConfigParser.parse(configString)
        return config != null && XmboxConfigParser.isValid(config)
    }
    
    companion object {
        /**
         * 创建实例
         */
        fun create(context: Context): XmboxVideoSource {
            return XmboxVideoSource(context)
        }
    }
}

