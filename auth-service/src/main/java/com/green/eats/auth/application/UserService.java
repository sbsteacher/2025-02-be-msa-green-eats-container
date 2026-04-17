package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserPutReq;
import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import com.green.eats.common.constants.UserEventType;
import com.green.eats.common.model.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void signup(UserSignupReq req) {
        String hashedPassword = passwordEncoder.encode(req.getPassword());

        //회원가입 시켜주세요. 제발~
        User newUser = new User();
        newUser.setEmail( req.getEmail() );
        newUser.setPassword( hashedPassword );
        newUser.setName( req.getName() );
        newUser.setAddress( req.getAddress() );
        newUser.setEnumUserRole( req.getUserRole() );

        userRepository.save(newUser);

        UserEvent userEvent = UserEvent.builder()
                                        .userId( newUser.getId() )
                                        .name( newUser.getName() )
                                        .eventType( UserEventType.CREATE )
                                        .build();

        kafkaTemplate.send("user-topic", String.valueOf(newUser.getId()), userEvent)
                .whenComplete((result, ex) -> {
            if (ex == null) {
                // 성공 시 로그
                log.info("✅ [Kafka Success] Topic: {}, Offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().offset());
            } else {
                // 실패 시 로그
                log.error("❌ [Kafka Failure] 원인: {}", ex.getMessage());
            }
        });
    }

    public User signin(UserSigninReq req) {
        User signedUser = userRepository.findByEmail( req.getEmail() );
        log.info("signedUser: {}", signedUser);
        if(signedUser == null || !passwordEncoder.matches( req.getPassword(), signedUser.getPassword() )) {
            notFoundUser();
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
                .eventType( UserEventType.UPDATE )
                .build();

        kafkaTemplate.send("user-topic", String.valueOf(user.getId()), userEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        // 성공 시 로그
                        log.info("✅ [Kafka Success] Topic: {}, Offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().offset());
                    } else {
                        // 실패 시 로그
                        log.error("❌ [Kafka Failure] 원인: {}", ex.getMessage());
                    }
                });
    }

    private void notFoundUser() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디, 비밀번호를 확인해 주세요.");
    }
}
