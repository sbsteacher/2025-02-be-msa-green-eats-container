package com.green.eats.common.model;

import com.green.eats.common.constants.UserEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * User Service에서 발생한 변경 사항을 담는 이벤트 DTO
 * Kafka를 통해 타 서비스(Order 등)로 전달됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent implements Serializable {

    private Long userId;     // User Service의 PK (TSID)
    private String name;     // 사용자 이름 (한글 포함 시 인코딩 주의)

    /**
     * 이벤트 타입 구분
     * CREATE: 회원가입
     * UPDATE: 정보 수정
     * DELETE: 회원 탈퇴
     */
    private UserEventType eventType;
}
