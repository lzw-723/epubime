# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- 添加专业基准测试
- 实现真正的流式处理
- 简化过度复杂的异常处理系统
- 优化API设计

### Changed
- 升级JUnit到5.x版本
- 优化缓存策略提高并发性能
- 减少不必要的对象创建
- 移除未使用的代码
- 重构类职责，遵循单一职责原则
- 调整SimpleEpubException的名称
- 清理无用文件
- 更新文档
- 整理文档
- 提升测试覆盖率

### Fixed
- 修复内存基准测试异常
- 修复jacoco
- 修复SpotBugs
- 修复调试输出过多的问题
- 修复测试并保持java8兼容