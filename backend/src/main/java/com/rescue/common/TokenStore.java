package com.rescue.common;

import com.rescue.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    private final Map<String, User> store = new ConcurrentHashMap<>();
    private static final ThreadLocal<User> CURRENT = new ThreadLocal<>();

    public String create(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        store.put(token, user);
        return token;
    }

    public User get(String token) {
        return token == null ? null : store.get(token);
    }

    public void remove(String token) {
        if (token != null) store.remove(token);
    }

    public static void setCurrent(User u) { CURRENT.set(u); }
    public static User current() { return CURRENT.get(); }
    public static void clearCurrent() { CURRENT.remove(); }
}
