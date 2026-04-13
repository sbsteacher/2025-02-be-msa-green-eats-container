package com.green.eats.common.auth;

import com.green.eats.common.model.UserDto;

public class UserContext {
    private static final ThreadLocal<UserDto> USER_HOLDER = new ThreadLocal<>();

    public static void set(UserDto user) { USER_HOLDER.set(user); }
    public static UserDto get() { return USER_HOLDER.get(); }
    public static void clear() { USER_HOLDER.remove(); }
}
