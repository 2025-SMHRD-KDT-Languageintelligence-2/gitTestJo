package com.smhrd.teamjo.service;

import com.smhrd.teamjo.dto.KakaoPayReadyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@Service
public class KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com";

    public String kakaoPayReady(KakaoPayReadyRequest requestDto) {
        RestTemplate restTemplate = new RestTemplate();

        // ✅ 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK 3fcc5512de3f59d6a67f1bb7023bbdaf");

        // ✅ 파라미터 구성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME"); // 테스트용 CID
        params.add("partner_order_id", requestDto.getPartner_order_id());
        params.add("partner_user_id", requestDto.getPartner_user_id());
        params.add("item_name", requestDto.getItem_name());
        params.add("quantity", String.valueOf(requestDto.getQuantity()));
        params.add("total_amount", String.valueOf(requestDto.getTotal_amount()));
        params.add("tax_free_amount", String.valueOf(requestDto.getTax_free_amount()));
        params.add("approval_url", "http://localhost:8081/paySuccess");
        params.add("cancel_url", "http://localhost:8081/payCancel");
        params.add("fail_url", "http://localhost:8081/payFail");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            URI uri = new URI(HOST + "/v1/payment/ready");
            ResponseEntity<Map> response = restTemplate.postForEntity(uri, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody().get("next_redirect_pc_url").toString(); // ✅ 결제창 URL 반환
            }

        } catch (URISyntaxException e) {
            log.error("URI 형식 오류", e);
        } catch (Exception e) {
            log.error("카카오페이 요청 실패", e);
        }

        return "/payFail";
    }
}
