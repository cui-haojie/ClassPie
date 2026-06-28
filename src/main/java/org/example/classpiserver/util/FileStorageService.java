package org.example.classpiserver.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final Set<String> ATTACHMENT_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp",
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".zip", ".rar", ".7z", ".txt"
    );

    private final Path uploadRoot;

    public FileStorageService() {
        uploadRoot = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
    }

    public String saveAvatar(MultipartFile file, String account) throws IOException {
        validateFile(file, IMAGE_EXTENSIONS, 2 * 1024 * 1024);
        String ext = resolveExtension(file.getOriginalFilename(), IMAGE_EXTENSIONS);
        Path dir = uploadRoot.resolve("avatars");
        Files.createDirectories(dir);
        String safeName = sanitizeAccount(account) + ext;
        Path target = dir.resolve(safeName);
        Files.write(target, file.getBytes());
        return "/uploads/avatars/" + safeName;
    }

    public StoredFile saveHomeworkAttachment(MultipartFile file) throws IOException {
        validateFile(file, ATTACHMENT_EXTENSIONS, 10 * 1024 * 1024);
        String ext = resolveExtension(file.getOriginalFilename(), ATTACHMENT_EXTENSIONS);
        Path dir = uploadRoot.resolve("attachments");
        Files.createDirectories(dir);
        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dir.resolve(storedName);
        Files.write(target, file.getBytes());
        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        return new StoredFile("/uploads/attachments/" + storedName, originalName);
    }

    public StoredFile saveTopicImage(MultipartFile file) throws IOException {
        validateFile(file, IMAGE_EXTENSIONS, 5 * 1024 * 1024);
        String ext = resolveExtension(file.getOriginalFilename(), IMAGE_EXTENSIONS);
        Path dir = uploadRoot.resolve("topic-images");
        Files.createDirectories(dir);
        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dir.resolve(storedName);
        Files.write(target, file.getBytes());
        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        return new StoredFile("/uploads/topic-images/" + storedName, originalName);
    }

    public Path getUploadRoot() {
        return uploadRoot;
    }

    private void validateFile(MultipartFile file, Set<String> allowedExtensions, long maxBytes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的文件");
        }
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("文件大小超出限制");
        }
        String ext = resolveExtension(file.getOriginalFilename(), allowedExtensions);
        if (!allowedExtensions.contains(ext)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }
    }

    private String resolveExtension(String originalFilename, Set<String> allowedExtensions) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("文件缺少扩展名");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        if (!allowedExtensions.contains(ext)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }
        return ext;
    }

    private String sanitizeAccount(String account) {
        if (account == null || account.isBlank()) {
            return "unknown";
        }
        return account.replaceAll("[^a-zA-Z0-9@._-]", "_");
    }

    private String sanitizeOriginalName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "attachment";
        }
        String name = Paths.get(originalFilename).getFileName().toString();
        return name.length() > 200 ? name.substring(0, 200) : name;
    }

    public record StoredFile(String url, String originalName) {
    }
}
