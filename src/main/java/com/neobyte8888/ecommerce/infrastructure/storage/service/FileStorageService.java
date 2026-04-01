package com.neobyte8888.ecommerce.infrastructure.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	
	// Hàm nhận file và trả về TÊN file duy nhất sau khi lưu thành công
    String storeFile(MultipartFile file);

}
