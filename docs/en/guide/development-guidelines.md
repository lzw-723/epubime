# Development Guidelines

## Code Style

- **Package Naming**: Use `fun.lzwi.epubime` as the base package name
- **Class Naming**: Follow Java standard naming conventions, using camelCase
- **Method Naming**: Semantic naming, using camelCase
- **Comments**: Use Chinese comments to explain functional intent rather than implementation details
- **Encoding Standard**: UTF-8 encoding
- **Static Analysis**: Perform static code analysis with SpotBugs tool to ensure code quality and avoid common defects

## Project Structure

```
epubime/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── fun/lzwi/epubime/
│   │           ├── cache/              # Cache management
│   │           │   └── EpubCacheManager.java # Cache manager
│   │           ├── epub/               # EPUB parsing core
│   │           │   ├── EpubBook.java    # EPUB book object
│   │           │   ├── EpubChapter.java # Chapter model
│   │           │   ├── EpubParser.java  # Main parser
│   │           │   ├── EpubResource.java# Resource model
│   │           │   └── Metadata.java    # Metadata model
│   │           ├── exception/          # Exception handling
│   │           │   ├── EpubFormatException.java
│   │           │   ├── EpubParseException.java
│   │           │   ├── EpubPathValidationException.java
│   │           │   ├── EpubResourceException.java
│   │           │   └── EpubZipException.java
│   │           └── zip/                # ZIP utilities
│   │               ├── PathValidator.java
│   │               ├── ZipFileManager.java
│   │               └── ZipUtils.java
│   └── test/
│       ├── java/
│       │   └── fun/lzwi/epubime/
│       │       ├── epub/               # EPUB parsing tests
│       │       │   ├── Epub33MetadataTest.java
│       │       │   ├── EpubBookTest.java
│       │       │   ├── EpubCacheTest.java
│       │       │   ├── EpubFormatExceptionTest.java
│       │       │   ├── EpubimeVsEpublibBenchmarkTest.java
│       │       │   ├── EpubParseExceptionEnhancedTest.java
│       │       │   ├── EpubParseExceptionTest.java
│       │       │   ├── EpubParserTest.java
│       │       │   ├── EpubPathValidationExceptionTest.java
│       │       │   ├── EpubResourceExceptionTest.java
│       │       │   ├── EpubResourceFallbackTest.java
│       │       │   ├── EpubResourceTest.java
│       │       │   ├── EpubZipExceptionTest.java
│       │       │   ├── MetadataTest.java
│       │       │   └── PerformanceBenchmarkTest.java
│       │       ├── zip/                # ZIP utilities tests
│       │       │   ├── PathValidatorTest.java
│       │       │   ├── ZipFileManagerIntegrationTest.java
│       │       │   ├── ZipFileManagerTest.java
│       │       │   └── ZipUtilsTest.java
│       │       └── ResUtils.java       # Test resource utilities
│       └── resources/
│           └── fun/lzwi/epubime/epub/ 《坟》鲁迅.epub # Example test file
├── pom.xml                            # Maven project configuration
├── .github/workflows/maven.yml        # CI/CD configuration
└── .gitignore                         # Git ignore file configuration
```

## Extension Guide

### Adding New Metadata Fields
1. Add fields and related methods in `Metadata.java`
2. Add parsing logic in `EpubParser.parseMetadata()`
3. Add corresponding test cases

### Supporting New EPUB Features
1. Analyze EPUB specification documents
2. Add corresponding parsing methods in `EpubParser`
3. Update model classes to support new features
4. Add comprehensive test cases

## Testing Guidelines

### Test Coverage
- **Minimum Requirements**: 80% instruction coverage, 100% class coverage
- **Test Files**: `*Test.java` naming convention
- **Test Resources**: Located in `src/test/resources/` directory
- **Testing Tools**: Provides `ResUtils` utility class for resource file access

### Test Types
- **Unit Testing**: Testing for individual classes or methods
- **Integration Testing**: Validation of interactions between multiple components
- **Performance Testing**: Validation of library's performance
- **Benchmark Testing**: Performance comparison with similar libraries