package fun.lzwi.epubime.api;

import fun.lzwi.epubime.epub.Metadata;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced Metadata with convenient methods and type-safe access
 * Provides utility methods for common metadata operations
 */
public class MetadataEnhanced {
    private final Metadata metadata;
    
    // Common date formats used in EPUB files
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };
    
    public MetadataEnhanced(Metadata metadata) {
        this.metadata = new Metadata(metadata);
    }
    
    /**
     * Get the primary title
     * @return primary title, or empty string if none
     */
    public String getTitle() {
        return Optional.ofNullable(metadata.getTitle()).orElse("");
    }
    
    /**
     * Get all titles
     * @return list of all titles
     */
    public List<String> getTitles() {
        return metadata.getTitles();
    }
    
    /**
     * Get the primary author/creator
     * @return primary author, or empty string if none
     */
    public String getAuthor() {
        return Optional.ofNullable(metadata.getCreator()).orElse("");
    }
    
    /**
     * Get all authors/creators
     * @return list of all authors
     */
    public List<String> getAuthors() {
        return metadata.getCreators();
    }
    
    /**
     * Get the primary language
     * @return primary language, or empty string if none
     */
    public String getLanguage() {
        return Optional.ofNullable(metadata.getLanguage()).orElse("");
    }
    
    /**
     * Get all languages
     * @return list of all languages
     */
    public List<String> getLanguages() {
        return metadata.getLanguages();
    }
    
    /**
     * Get the primary publisher
     * @return primary publisher, or empty string if none
     */
    public String getPublisher() {
        return Optional.ofNullable(metadata.getPublisher()).orElse("");
    }
    
    /**
     * Get all publishers
     * @return list of all publishers
     */
    public List<String> getPublishers() {
        return metadata.getPublishers();
    }
    
    /**
     * Get the primary identifier
     * @return primary identifier, or empty string if none
     */
    public String getIdentifier() {
        return Optional.ofNullable(metadata.getIdentifier()).orElse("");
    }
    
    /**
     * Get all identifiers
     * @return list of all identifiers
     */
    public List<String> getIdentifiers() {
        return metadata.getIdentifiers();
    }
    
    /**
     * Get the primary description
     * @return primary description, or empty string if none
     */
    public String getDescription() {
        return Optional.ofNullable(metadata.getDescription()).orElse("");
    }
    
    /**
     * Get all descriptions
     * @return list of all descriptions
     */
    public List<String> getDescriptions() {
        return metadata.getDescriptions();
    }
    
    /**
     * Get the primary date
     * @return primary date, or empty string if none
     */
    public String getDate() {
        return Optional.ofNullable(metadata.getDate()).orElse("");
    }
    
    /**
     * Get all dates
     * @return list of all dates
     */
    public List<String> getDates() {
        return metadata.getDates();
    }
    
    /**
     * Get parsed date as LocalDate
     * @return parsed date, or null if parsing fails
     */
    public LocalDate getParsedDate() {
        String dateStr = getDate();
        if (dateStr.isEmpty()) {
            return null;
        }
        
        // Try different date formats
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        
        return null;
    }
    
    /**
     * Get the rights information
     * @return rights, or empty string if none
     */
    public String getRights() {
        return Optional.ofNullable(metadata.getRights()).orElse("");
    }
    
    /**
     * Get all rights
     * @return list of all rights
     */
    public List<String> getRightsList() {
        return metadata.getRightsList();
    }
    
    /**
     * Get the source
     * @return source, or empty string if none
     */
    public String getSource() {
        return Optional.ofNullable(metadata.getSource()).orElse("");
    }
    
    /**
     * Get all sources
     * @return list of all sources
     */
    public List<String> getSources() {
        return metadata.getSources();
    }
    
    /**
     * Get the subject/genre
     * @return primary subject, or empty string if none
     */
    public String getSubject() {
        List<String> subjects = metadata.getSubjects();
        return subjects.isEmpty() ? "" : subjects.get(0);
    }
    
    /**
     * Get all subjects/genres
     * @return list of all subjects
     */
    public List<String> getSubjects() {
        return metadata.getSubjects();
    }
    
    /**
     * Get the type
     * @return primary type, or empty string if none
     */
    public String getType() {
        return Optional.ofNullable(metadata.getType()).orElse("");
    }
    
    /**
     * Get all types
     * @return list of all types
     */
    public List<String> getTypes() {
        return metadata.getTypes();
    }
    
    /**
     * Get the format
     * @return primary format, or empty string if none
     */
    public String getFormat() {
        return Optional.ofNullable(metadata.getFormat()).orElse("");
    }
    
    /**
     * Get all formats
     * @return list of all formats
     */
    public List<String> getFormats() {
        return metadata.getFormats();
    }
    
    /**
     * Get the cover ID
     * @return cover ID, or empty string if none
     */
    public String getCoverId() {
        return Optional.ofNullable(metadata.getCover()).orElse("");
    }
    
    /**
     * Get the modified date
     * @return modified date, or empty string if none
     */
    public String getModified() {
        return Optional.ofNullable(metadata.getModified()).orElse("");
    }
    
    /**
     * Get the unique identifier
     * @return unique identifier, or empty string if none
     */
    public String getUniqueIdentifier() {
        return Optional.ofNullable(metadata.getUniqueIdentifier()).orElse("");
    }
    
    /**
     * Get accessibility features
     * @return list of accessibility features
     */
    public List<String> getAccessibilityFeatures() {
        return metadata.getAccessibilityFeatures();
    }
    
    /**
     * Get accessibility hazards
     * @return list of accessibility hazards
     */
    public List<String> getAccessibilityHazards() {
        return metadata.getAccessibilityHazard();
    }
    
    /**
     * Get accessibility summary
     * @return accessibility summary, or empty string if none
     */
    public String getAccessibilitySummary() {
        return Optional.ofNullable(metadata.getAccessibilitySummary()).orElse("");
    }
    
    /**
     * Get layout property
     * @return layout, or empty string if none
     */
    public String getLayout() {
        return Optional.ofNullable(metadata.getLayout()).orElse("");
    }
    
    /**
     * Get orientation property
     * @return orientation, or empty string if none
     */
    public String getOrientation() {
        return Optional.ofNullable(metadata.getOrientation()).orElse("");
    }
    
    /**
     * Get spread property
     * @return spread, or empty string if none
     */
    public String getSpread() {
        return Optional.ofNullable(metadata.getSpread()).orElse("");
    }
    
    /**
     * Get viewport property
     * @return viewport, or empty string if none
     */
    public String getViewport() {
        return Optional.ofNullable(metadata.getViewport()).orElse("");
    }
    
    /**
     * Get media property
     * @return media, or empty string if none
     */
    public String getMedia() {
        return Optional.ofNullable(metadata.getMedia()).orElse("");
    }
    
    /**
     * Get flow property
     * @return flow, or empty string if none
     */
    public String getFlow() {
        return Optional.ofNullable(metadata.getFlow()).orElse("");
    }
    
    /**
     * Check if align-x-center is enabled
     * @return true if align-x-center is enabled
     */
    public boolean isAlignXCenter() {
        return metadata.isAlignXCenter();
    }
    
    /**
     * Check if the book has a cover specified
     * @return true if cover is specified
     */
    public boolean hasCover() {
        return metadata.getCover() != null && !metadata.getCover().isEmpty();
    }
    
    /**
     * Check if the book has accessibility features
     * @return true if accessibility features are present
     */
    public boolean hasAccessibilityFeatures() {
        return !metadata.getAccessibilityFeatures().isEmpty();
    }
    
    /**
     * Check if the book has accessibility hazards
     * @return true if accessibility hazards are specified
     */
    public boolean hasAccessibilityHazards() {
        return !metadata.getAccessibilityHazard().isEmpty();
    }
    
    /**
     * Check if the book has a description
     * @return true if description is present
     */
    public boolean hasDescription() {
        return !metadata.getDescriptions().isEmpty();
    }
    
    /**
     * Check if the book has subjects/genres
     * @return true if subjects are present
     */
    public boolean hasSubjects() {
        return !metadata.getSubjects().isEmpty();
    }
    
    /**
     * Get a formatted summary of the metadata
     * @return formatted metadata summary
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (!getTitle().isEmpty()) {
            summary.append("Title: ").append(getTitle()).append("\n");
        }
        
        if (!getAuthor().isEmpty()) {
            summary.append("Author: ").append(getAuthor()).append("\n");
        }
        
        if (!getLanguage().isEmpty()) {
            summary.append("Language: ").append(getLanguage()).append("\n");
        }
        
        if (!getPublisher().isEmpty()) {
            summary.append("Publisher: ").append(getPublisher()).append("\n");
        }
        
        if (!getDate().isEmpty()) {
            summary.append("Date: ").append(getDate()).append("\n");
        }
        
        if (hasSubjects()) {
            summary.append("Subjects: ").append(String.join(", ", getSubjects())).append("\n");
        }
        
        if (hasDescription()) {
            summary.append("Description: ").append(getDescription()).append("\n");
        }
        
        if (hasCover()) {
            summary.append("Cover: ").append(getCoverId()).append("\n");
        }
        
        return summary.toString().trim();
    }
    
    /**
     * Get the underlying Metadata instance
     * @return a copy of the original Metadata
     */
    public Metadata getOriginalMetadata() {
        return new Metadata(metadata);
    }
}