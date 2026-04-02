package com.neobyte8888.ecommerce.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.neobyte8888.ecommerce.common.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Nơi tập trung xử lý mọi ngoại lệ (Exception) của toàn bộ ứng dụng.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	//=====================404 NOT FOUND======================
    // 1. Xử lý lỗi Không tìm thấy dữ liệu (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
    	log.warn("Resource not found: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    // 2. Bắt lỗi khi User gọi sai URL API
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
    	log.warn("Đường dẫn API này không tồn tại trên hệ thống: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(), 
                "Đường dẫn API này không tồn tại trên hệ thống!"
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    //=====================404 BAD REQUEST======================
    // 1. Xử lý lỗi Logic nghiệp vụ (400 Bad Request)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
    	log.warn("Business exception: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    // 2. Xử lý lỗi User nhập thiếu/sai định dạng dữ liệu (Validation) (400 Bad Request)
    // Trả về chính xác field nào bị lỗi để Frontend bôi đỏ ô input đó.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Trích xuất từng trường bị lỗi từ Exception
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed: {}", errors);
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Dữ liệu đầu vào không hợp lệ",
                errors // Nhét Map các lỗi vào field 'data' của ApiResponse
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    

    //=====================401 UNAUTHORIZED======================
    // 1. Xử lý lỗi Xác thực/Bảo mật (401 Unauthorized)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
    	log.warn("Invalid credentials: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 2. Sai email hoặc mật khẩu
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
    	log.warn("Email hoặc mật khẩu không chính xác: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(), 
                "Email hoặc mật khẩu không chính xác!"
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    

    // XỬ LÝ LỖI TRANH CHẤP MUA HÀNG (RACE CONDITION)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleOptimisticLockingException(ObjectOptimisticLockingFailureException ex) {
        
        log.error("[RACE CONDITION] Đã chặn thành công một luồng tranh chấp dữ liệu đồng thời!");
        
        // Trả về HTTP 409 Conflict (Xung đột dữ liệu)
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.CONFLICT.value(), 
                "Xin lỗi, sản phẩm bạn chọn vừa có sự thay đổi về số lượng do người khác mua. Vui lòng thử lại!", 
                null
        ), HttpStatus.CONFLICT);
    }

    //=====================500 INTERNAL SERVER ERROR======================
    // 5. Chốt chặn cuối cùng: Xử lý các lỗi Hệ thống không lường trước được (500 Internal Server Error)
    // Tuyệt đối không trả stacktrace (ex.getMessage() hay NullPointerException) ra ngoài.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
        // Nên có 1 dòng log ở đây để dev vào server check log:
        // System.err.println("CRITICAL ERROR: " + ex.getMessage()); // Hoặc dùng Slf4j (nếu cấu hình)
    	// Quan trọng: log full stacktrace
        log.error("Unexpected system error occurred", ex.getMessage());
        
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau!" // Thông báo chung chung cho an toàn
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}