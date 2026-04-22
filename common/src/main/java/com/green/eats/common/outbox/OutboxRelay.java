package com.green.eats.common.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "key-serializer")
public class OutboxRelay {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 5초마다 실행 (간격은 조절 가능)
    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishEvents() {
        List<Outbox> waitingEvents = outboxRepository.findAll();

        for (Outbox outbox : waitingEvents) {
            try {
                // 실제 카프카 전송 (JSON 문자열 그대로 전송)
                kafkaTemplate.send(outbox.getTopic(), String.valueOf(outbox.getAggregateId()), outbox.getPayload())
                        .whenComplete((result, ex) -> {
                            log.info("result: {}, ex: {}", result, ex);
                            if (ex == null) {
                                // 성공 시 즉시 삭제
                                outboxRepository.deleteById(outbox.getId());

                            } else {
                                log.error("Relay 전송 실패: {}", ex.getMessage());
                                outbox.setStatus( OutboxStatus.FAILED );
                                outboxRepository.save(outbox);
                            }
                        });
            } catch (Exception e) {
                log.error("카프카 클라이언트 오류: {}", e.getMessage());
            }
        }
    }

}
