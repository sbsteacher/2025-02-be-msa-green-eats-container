package com.green.eats.common.outbox;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private Long aggregateId;    // 관련 도메인의 PK

    private String eventType;    // 예: "USER_CREATED"

    @Column(columnDefinition = "TEXT")
    private String payload;      // JSON 데이터

    private OutboxStatus status;       // INIT, SENT, FAILED
    private LocalDateTime createdAt;

    @Builder
    public Outbox(String topic, Long aggregateId, String eventType, String payload) {
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxStatus.INIT;
        this.createdAt = LocalDateTime.now();
    }
}
