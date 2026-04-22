package com.green.eats.auth.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.eats.auth.application.model.UserPutReq;
import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import com.green.eats.auth.exception.UserErrorCode;
import com.green.eats.common.constants.UserEventType;
import com.green.eats.common.exception.BusinessException;
import com.green.eats.common.model.UserEvent;
import com.green.eats.common.outbox.Outbox;
import com.green.eats.common.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void signup(UserSignupReq req) {
        String hashedPassword = passwordEncoder.encode(req.getPassword());

        //회원가입 시켜주세요. 제발~
        User newUser = new User();
        newUser.setEmail( req.getEmail() );
        newUser.setPassword( hashedPassword );
        newUser.setName( req.getName() );
        newUser.setAddress( req.getAddress() );
        newUser.setEnumUserRole( req.getUserRole() );
        newUser.setIsDel( false );

        userRepository.save(newUser);

        UserEvent userEvent = UserEvent.builder()
                                        .userId( newUser.getId() )
                                        .name( newUser.getName() )
                                        .eventType( UserEventType.USER_CREATED)
                                        .build();

        kafkaSend(userEvent);
    }

    public User signin(UserSigninReq req) {
        User signedUser = userRepository.findByEmail( req.getEmail() );
        log.info("signedUser: {}", signedUser);
        if(signedUser == null || !passwordEncoder.matches( req.getPassword(), signedUser.getPassword() )) {
            notFoundUserAndNotMatchedPassword();
        }
        return signedUser;
    }

    @Transactional
    public void updUser(Long userId, UserPutReq req) {
        User user = userRepository.findById(userId).orElseThrow();

        user.setName( req.getName() );
        user.setAddress( req.getAddress() );

        UserEvent userEvent = UserEvent.builder()
                .userId( user.getId() )
                .name( user.getName() )
                .eventType( UserEventType.USER_UPDATED)
                .build();

        kafkaSend(userEvent);
    }

    @Transactional
    public void delUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setIsDel( true );

        UserEvent userEvent = UserEvent.builder()
                .userId( user.getId() )
                .name( user.getName() )
                .eventType( UserEventType.USER_DELETED)
                .build();

        kafkaSend(userEvent);
    }

    private void kafkaSend(UserEvent userEvent) {
        String payload = objectMapper.writeValueAsString(userEvent);
        Outbox outbox = Outbox.builder()
                .topic("user-topic")
                .aggregateId( userEvent.getUserId() )
                .eventType( userEvent.getEventType().name() )
                .payload( payload )
                .build();

        outboxRepository.save(outbox);

//        kafkaTemplate.send("user-topic", String.valueOf(userEvent.getUserId()), userEvent)
//                .whenComplete((result, ex) -> {
//                    if (ex == null) {
//                        // 성공 시 로그
//                        log.info("✅ [Kafka Success] Topic: {}, Offset: {}",
//                                result.getRecordMetadata().topic(),
//                                result.getRecordMetadata().offset());
//                    } else {
//                        // 실패 시 로그
//                        log.error("❌ [Kafka Failure] 원인: {}", ex.getMessage());
//                    }
//                });
    }

    private void notFoundUserAndNotMatchedPassword() {
        //throw new BusinessException(UserErrorCode.CHECK_EMAIL_PASSWORD);
        throw BusinessException.of(UserErrorCode.CHECK_EMAIL_PASSWORD);
    }
}
