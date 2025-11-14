# API Reference

This section provides complete API reference documentation for the EPUBime library. EPUBime is a pure Java library for parsing EPUB file format, supporting both EPUB 2 and EPUB 3 formats.

## Core Classes

- [EpubParser](/en/api/epub-parser) - EPUB file parser, responsible for parsing EPUB files and generating EpubBook objects
- [EpubBook](/en/api/epub-book) - Represents a parsed EPUB book, containing metadata, chapters, and resources
- [Metadata](/en/api/metadata) - Represents metadata information of an EPUB book
- [EpubChapter](/en/api/epub-chapter) - Represents a chapter in an EPUB book
- [EpubResource](/en/api/epub-resource) - Represents a resource file in an EPUB book

## Exception Handling

- [Exception Classes](/en/api/exceptions) - EPUBime's exception class hierarchy, all exceptions inherit from EpubException

## Overview

EPUBime's API design is simple and clear, with the main workflow as follows:

1. Use `EpubParser` to parse EPUB files
2. Get `EpubBook` object, which contains metadata, chapters, and resources
3. Access book metadata through `Metadata`
4. Access chapter content through `EpubChapter`
5. Access resource files through `EpubResource`

Select a class name from the left navigation bar to view detailed API documentation.