package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    }

    public User signin(UserSigninReq req) {
        User signedUser = userRepository.findByEmail( req.getEmail() );
        log.info("signedUser: {}", signedUser);
        if(signedUser == null || !passwordEncoder.matches( req.getPassword(), signedUser.getPassword() )) {
            notFoundUser();
        }
        return signedUser;
    }

    private void notFoundUser() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디, 비밀번호를 확인해 주세요.");
    }
}
