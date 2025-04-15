package com.smhrd.teamjo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder  // ✅ Lombok의 빌더 패턴 활성화
public class KakaoPayReadyRequest {
    private String partner_order_id;
    private String partner_user_id;
    private String item_name;
    private int quantity;
    private int total_amount;
    private int tax_free_amount;
}
