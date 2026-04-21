package com.green.eats.auth.application;

import com.green.eats.auth.application.model.UserPutReq;
import com.green.eats.auth.application.model.UserSigninReq;
import com.green.eats.auth.application.model.UserSigninRes;
import com.green.eats.auth.application.model.UserSignupReq;
import com.green.eats.auth.entity.User;
import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.JwtUser;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.common.security.JwtTokenManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;

    @PostMapping("/signup")
    public ResultResponse<?> signup( @RequestBody UserSignupReq req ) {
        log.info("req: {}", req);
        userService.signup( req );
        return ResultResponse.builder()
                .resultMessage( "회원가입 성공" )
                .resultData( 1 )
                .build();
    }

    @PostMapping("/signin")
    public ResultResponse<?> signin(HttpServletResponse res, @RequestBody UserSigninReq req ) {
        log.info("req: {}", req);

        User signedUser = userService.signin( req );

        //인증 쿠키
        JwtUser jwtUser = new JwtUser( signedUser.getId()
                                     , signedUser.getName()
                                     , signedUser.getEnumUserRole() );
        jwtTokenManager.issue(res, jwtUser);

        UserSigninRes resultData = UserSigninRes.builder()
                .id( signedUser.getId() )
                .name( signedUser.getName() )
                .build();

        return ResultResponse.builder()
                .resultMessage("로그인 성공")
                .resultData(resultData)
                .build();
    }

    @PutMapping
    public ResultResponse<?> updUser(@Valid @RequestBody UserPutReq req) {
        UserDto userDto = UserContext.get();
        log.info("userPutReq: {}, userDto: {}", req, userDto);
        userService.updUser(userDto.id(), req);
        return ResultResponse.builder()
                .resultMessage("수정 성공")
                .build();
    }

    @DeleteMapping
    public ResultResponse<?> updUser() {
        UserDto userDto = UserContext.get();
        log.info("userDto: {}", userDto);
        userService.delUser(userDto.id());
        return ResultResponse.builder()
                .resultMessage("삭제 성공")
                .build();
    }

    @PostMapping("/signout")
    public ResultResponse<?> signOut(HttpServletResponse res) {
        jwtTokenManager.signOut(res);
        return new ResultResponse<>("로그아웃 성공", 1);
    }

    @PostMapping("/reissue")
    public ResultResponse<?> reissue(HttpServletResponse res, HttpServletRequest req) {
        jwtTokenManager.reissue(req, res);
        return new ResultResponse<>("AT 재발행", null);
    }
}
