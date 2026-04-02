package com.neobyte8888.ecommerce.util;

import com.neobyte8888.ecommerce.exception.BusinessException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HmacUtils {

    // Thuật toán tạo chữ ký điện tử HMAC-SHA256
    public static String calculateHmacSha256(String data, String secretKey) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKeySpec);
            byte[] hashBytes = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Chuyển mảng byte thành chuỗi Hex (Hệ thập lục phân)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new BusinessException("Lỗi hệ thống khi mã hóa chữ ký điện tử: " + e.getMessage());
        }
    }
}