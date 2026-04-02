package com.neobyte8888.ecommerce.modules.payment.provider;

import com.neobyte8888.ecommerce.modules.payment.entity.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MockVnPayProvider implements PaymentGatewayProvider {

    @Override
    public String generatePaymentUrl(Payment payment) {
        // Ở môi trường Production, đoạn này sẽ là 50 dòng code 
        // mã hóa chữ ký điện tử và gọi HTTP Client sang server VNPAY.
        // Ở môi trường Dev, ta ghép chuỗi đơn giản để Frontend có URL bấm thử.

        String baseUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        
        // VNPAY yêu cầu truyền mã giao dịch duy nhất (Ta dùng chính UUID của Payment)
        String vnpTxnRef = payment.getId().toString(); 
        
        // VNPAY yêu cầu số tiền phải nhân lên 100 lần (VD: 10,000 VNĐ -> gửi đi 1,000,000)
        String vnpAmount = payment.getAmount().multiply(new BigDecimal("100")).toBigInteger().toString(); 
        
        String vnpOrderInfo = "Thanh_toan_don_hang_ShoppyApp_ID_" + payment.getOrder().getId();

        // Nối chuỗi tạo URL giả lập
        return baseUrl + "?vnp_TxnRef=" + vnpTxnRef 
                       + "&vnp_Amount=" + vnpAmount 
                       + "&vnp_OrderInfo=" + vnpOrderInfo;
    }

    @Override
    public String getProviderName() {
        return "VNPAY"; // Khớp với Enum PaymentProvider.VNPAY
    }
}