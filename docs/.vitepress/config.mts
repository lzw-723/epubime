import { defineConfig } from 'vitepress'

export default defineConfig({
  base: '/',
  locales: {
    root: {
      label: '简体中文',
      lang: 'zh-CN',
      title: 'EPUBime',
      description: '一个纯 Java 库，用于解析 EPUB 文件格式',
      themeConfig: {
        nav: [
          { text: '首页', link: '/', activeMatch: '^/$|^/guide/' },
          { text: '快速开始', link: '/guide/quick-start' },
          { text: '使用指南', link: '/guide/' },
          { text: 'API 参考', link: '/api/' },
          { text: '更新日志', link: '/CHANGELOG' },
        ],
        sidebar: {
          '/guide/': [
            {
              text: '使用指南',
              items: [
                { text: '快速开始', link: '/guide/quick-start' },
                { text: '基础用法', link: '/guide/basic-usage' },
                { text: '高级功能', link: '/guide/advanced-features' },
                { text: '异常处理', link: '/guide/exception-handling' },
                 { text: '性能优化', link: '/guide/performance' },
                 { text: '基准测试', link: '/guide/benchmarking' },
                 { text: '开发指南', link: '/guide/development-guidelines' },
                 { text: 'EPUB2兼容性', link: '/guide/epub2-compatibility' },
                 { text: 'API使用指南', link: '/guide/api-usage-guide' },
                 { text: '错误处理', link: '/guide/error-handling' },
              ]
            }
          ],
          '/api/': [
            {
              text: '现代 API',
              items: [
                { text: 'EpubReader', link: '/api/epub-reader' },
                { text: 'EpubReaderConfig', link: '/api/epub-reader-config' },
                { text: 'AsyncEpubProcessor', link: '/api/async-processor' },
              ]
            },
            {
              text: '核心数据模型',
              items: [
                { text: 'EpubBook', link: '/api/epub-book' },
                { text: 'Metadata', link: '/api/metadata' },
                { text: 'EpubChapter', link: '/api/epub-chapter' },
                { text: 'EpubResource', link: '/api/epub-resource' },
              ]
            },
            {
              text: '专用处理器',
              items: [
                { text: 'EpubParser', link: '/api/epub-parser' },
                { text: 'EpubFileReader', link: '/api/epub-file-reader' },
                { text: 'EpubStreamProcessor', link: '/api/epub-stream-processor' },
                { text: 'EpubBookProcessor', link: '/api/epub-book-processor' },
              ]
            },
            {
              text: '增强功能',
              items: [
                { text: 'EpubBookEnhanced', link: '/api/epub-book-enhanced' },
                { text: 'MetadataEnhanced', link: '/api/metadata-enhanced' },
              ]
            },
            {
              text: '异常处理',
              items: [
                { text: '异常类', link: '/api/exceptions' },
              ]
            }
          ]
        },
        footer: {
          message: '基于 MIT 许可发布',
          copyright: 'Copyright © 2025 EPUBime 项目'
        },
      }
    },
    en: {
      label: 'English',
      lang: 'en-US',
      title: 'EPUBime',
      description: 'A pure Java library for parsing EPUB file format',
      themeConfig: {
        nav: [
          { text: 'Home', link: '/en/', activeMatch: '^/en/$|^/en/guide/' },
          { text: 'Quick Start', link: '/en/guide/quick-start' },
          { text: 'Guide', link: '/en/guide/' },
          { text: 'API Reference', link: '/en/api/' },
          { text: 'Changelog', link: '/en/CHANGELOG' },
        ],
        sidebar: {
          '/en/guide/': [
            {
              text: 'Guide',
              items: [
                { text: 'Quick Start', link: '/en/guide/quick-start' },
                { text: 'Basic Usage', link: '/en/guide/basic-usage' },
                { text: 'Advanced Features', link: '/en/guide/advanced-features' },
                { text: 'Exception Handling', link: '/en/guide/exception-handling' },
                 { text: 'Performance', link: '/en/guide/performance' },
                 { text: 'Benchmarking', link: '/en/guide/benchmarking' },
                 { text: 'Development Guidelines', link: '/en/guide/development-guidelines' },
                 { text: 'EPUB2 Compatibility', link: '/en/guide/epub2-compatibility' },
                 { text: 'API Usage Guide', link: '/en/guide/api-usage-guide' },
                 { text: 'Error Handling', link: '/en/guide/error-handling' },
              ]
            }
          ],
           '/en/api/': [
             {
               text: 'Modern API',
               items: [
                 { text: 'EpubReader', link: '/en/api/epub-reader' },
                 { text: 'EpubReaderConfig', link: '/en/api/epub-reader-config' },
                 { text: 'AsyncEpubProcessor', link: '/en/api/async-processor' },
               ]
             },
             {
               text: 'Core Data Models',
               items: [
                 { text: 'EpubBook', link: '/en/api/epub-book' },
                 { text: 'Metadata', link: '/en/api/metadata' },
                 { text: 'EpubChapter', link: '/en/api/epub-chapter' },
                 { text: 'EpubResource', link: '/en/api/epub-resource' },
               ]
             },
             {
               text: 'Dedicated Processors',
               items: [
                 { text: 'EpubParser', link: '/en/api/epub-parser' },
                 { text: 'EpubFileReader', link: '/en/api/epub-file-reader' },
                 { text: 'EpubStreamProcessor', link: '/en/api/epub-stream-processor' },
                 { text: 'EpubBookProcessor', link: '/en/api/epub-book-processor' },
               ]
             },
             {
               text: 'Enhanced Features',
               items: [
                 { text: 'EpubBookEnhanced', link: '/en/api/epub-book-enhanced' },
                 { text: 'MetadataEnhanced', link: '/en/api/metadata-enhanced' },
               ]
             },
             {
               text: 'Exception Handling',
               items: [
                 { text: 'Exception Classes', link: '/en/api/exceptions' },
               ]
             }
           ]
        },
        footer: {
          message: 'Released under the MIT License',
          copyright: 'Copyright © 2025 EPUBime Project'
        },
      }
    }
  },

  themeConfig: {
    socialLinks: [
      { icon: 'github', link: 'https://github.com/lzw-723/epubime' }
    ],

    localeLinks: {
      items: [
        { text: '简体中文', link: '/' },
        { text: 'English', link: '/en/' }
      ]
    }
  }
})