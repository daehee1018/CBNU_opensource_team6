package com.example.opensource_team6.user;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final List<User> users = new ArrayList<>();
    static {
        users.add(new User(1, "박영현"));
        users.add(new User(2, "비만"));
        users.add(new User(3, "정삼"));
        users.add(new User(4, "초 고도비만"));
        users.add(new User(5, "테스트1"));
        users.add(new User(6, "테스트2"));
    }

    public static List<User> getUsers() { return users; }

    public static User getUserById(int id) {
        for (User u : users) if (u.getId() == id) return u;
        return null;
    }

    public static User getUserByName(String name) {
        for (User u : users) if (u.getName().equals(name)) return u;
        return null;
    }
}
