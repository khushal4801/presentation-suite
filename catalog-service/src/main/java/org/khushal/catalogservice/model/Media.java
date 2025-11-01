package org.khushal.catalogservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "media")
public class Media {
    @Id
    private String id;  
    
    @Field("category_id")
    private String categoryId;
    
    @Field("folder_name")
    private String folderName;
    
    @Field("media_type")
    private MediaType mediaType;
    
    @Field("file_name")
    private String fileName;
    
    @Field("original_name")
    private String originalName;
    
    @Field("file_path")
    private String filePath;
    
    @Field("file_size")
    private Long fileSize;
    
    @Field("mime_type")
    private String mimeType;
    
    @Field("upload_date")
    private LocalDateTime uploadDate;
    
    @Field("sequence_number")
    private Integer sequenceNumber; // For ordering images in series
    
    // Constructors
    public Media() {
        this.uploadDate = LocalDateTime.now();
    }
    
    public Media(String categoryId, String folderName, MediaType mediaType, 
                String fileName, String originalName, String filePath, 
                Long fileSize, String mimeType) {
        this();
        this.categoryId = categoryId;
        this.folderName = folderName;
        this.mediaType = mediaType;
        this.fileName = fileName;
        this.originalName = originalName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    public MediaType getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
    
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
    
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    // Media Type Enum - Limited to Images and Videos only
    public enum MediaType {
        IMAGE("image"),
        VIDEO("video");
        
        private final String value;
        
        MediaType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static MediaType fromString(String value) {
            for (MediaType type : MediaType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown media type: " + value);
        }
        
        public static MediaType fromFileExtension(String extension) {
            String ext = extension.toLowerCase();
            if (ext.matches("(jpg|jpeg|png|gif|webp|bmp)")) {
                return IMAGE;
            } else if (ext.matches("(mp4|avi|mov|wmv|mkv|flv|webm)")) {
                return VIDEO;
            }
            throw new IllegalArgumentException("Unsupported file extension: " + extension);
        }
    }
}
