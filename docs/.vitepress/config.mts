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
                { text: 'API使用指南', link: '/guide/api-usage-guide' },
                { text: '错误处理', link: '/guide/error-handling' },
              ]
            }
          ],
          '/api/': [
            {
              text: 'API 参考',
              items: [
                { text: 'EpubParser', link: '/api/epub-parser' },
                { text: 'EpubBook', link: '/api/epub-book' },
                { text: 'Metadata', link: '/api/metadata' },
                { text: 'EpubChapter', link: '/api/epub-chapter' },
                { text: 'EpubResource', link: '/api/epub-resource' },
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
                { text: 'API Usage Guide', link: '/en/guide/api-usage-guide' },
                { text: 'Error Handling', link: '/en/guide/error-handling' },
              ]
            }
          ],
          '/en/api/': [
            {
              text: 'API Reference',
              items: [
                { text: 'EpubParser', link: '/en/api/epub-parser' },
                { text: 'EpubBook', link: '/en/api/epub-book' },
                { text: 'Metadata', link: '/en/api/metadata' },
                { text: 'EpubChapter', link: '/en/api/epub-chapter' },
                { text: 'EpubResource', link: '/en/api/epub-resource' },
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