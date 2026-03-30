package com.neobyte8888.ecommerce.common;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtils {
    
    // Hàm chuyển đổi chuỗi tiếng Việt có dấu thành không dấu và nối bằng dấu gạch ngang
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) return "";
        
        // Map riêng Đ/đ
        input = input.replace("Đ", "D").replace("đ", "d");
        
        // Loại bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        
        // Chuyển thành chữ thường, thay khoảng trắng bằng gạch ngang, xóa ký tự đặc biệt
        return slug.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "") // Xóa ký tự đặc biệt
                   .replaceAll("\\s+", "-")         // Thay khoảng trắng bằng dấu gạch ngang
                   .replaceAll("-+", "-");          // Xóa các dấu gạch ngang liền nhau
    }
}