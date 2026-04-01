package com.neobyte8888.ecommerce.infrastructure.storage.service.impl;

import com.neobyte8888.ecommerce.exception.BusinessException;
import com.neobyte8888.ecommerce.infrastructure.storage.service.FileStorageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    // Lấy config đường dẫn từ application.yml
    private final Path rootLocation;

    // Khởi tạo thư mục lưu trữ ngay khi Bean được tạo
    public FileStorageServiceImpl(@Value("${storage.location}") String location) {
        this.rootLocation = Paths.get(location);
        try {
            // Tự động tạo thư mục 'uploads' nếu chưa tồn tại
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new BusinessException("Lỗi hệ thống: Không thể khởi tạo thư mục lưu trữ file.");
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            // 1. Validate File trống
            if (file.isEmpty()) {
                throw new BusinessException("Vui lòng chọn file để upload.");
            }

            // 2. Validate Định dạng File (MIME Type) - Chốt chặn bảo mật cực quan trọng
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new BusinessException("Định dạng file không hợp lệ. Chỉ chấp nhận JPG (image/jpeg) hoặc PNG (image/png).");
            }

            // 3. Sinh tên file duy nhất (UUID)
            // Lấy đuôi file gốc (vd: jpg, png)
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileExtension = originalFilename.substring(lastDotIndex); // Lấy bao gồm dấu chấm (vd: .jpg)
            }
            
            // Tạo tên file mới: UUID + đuôi gốc (vd: 550e8400-e29b-41d4-a716-446655440000.jpg)
            // Việc đổi tên file giúp chặn hoàn toàn Path Traversal tấn công hệ thống file
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 4. Kiểm tra an toàn đường dẫn (đề phòng ký tự ..)
            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // Hacker cố tình dùng ký tự .. để lưu ra ngoài thư mục uploads
                throw new BusinessException("Đường dẫn file không an toàn.");
            }

            // 5. Lưu file vào ổ cứng Server
            try (InputStream inputStream = file.getInputStream()) {
                // StandardCopyOption.REPLACE_EXISTING: Ghi đè nếu trùng tên (UUID thì hiếm khi trùng)
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Trả về tên file duy nhất để lưu vào DB
            return newFilename;

        } catch (IOException e) {
            throw new BusinessException("Lỗi hệ thống khi lưu file: " + e.getMessage());
        }
    }
}
