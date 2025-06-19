package com.example.opensource_team6.user;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FollowManager {
    private static final String PREF = "FollowPrefs";

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    private static Set<String> getSet(Context ctx, String key) {
        return new HashSet<>(prefs(ctx).getStringSet(key, new HashSet<>()));
    }

    private static void saveSet(Context ctx, String key, Set<String> set) {
        prefs(ctx).edit().putStringSet(key, set).apply();
    }

    public static Set<Integer> getFollowers(Context ctx, int userId) {
        Set<String> raw = getSet(ctx, "followers_" + userId);
        Set<Integer> result = new HashSet<>();
        for (String s : raw) result.add(Integer.parseInt(s));
        return result;
    }

    public static Set<Integer> getFollowings(Context ctx, int userId) {
        Set<String> raw = getSet(ctx, "following_" + userId);
        Set<Integer> result = new HashSet<>();
        for (String s : raw) result.add(Integer.parseInt(s));
        return result;
    }

    private static void setFollowers(Context ctx, int userId, Set<Integer> set) {
        Set<String> raw = new HashSet<>();
        for (int id : set) raw.add(String.valueOf(id));
        saveSet(ctx, "followers_" + userId, raw);
    }

    private static void setFollowings(Context ctx, int userId, Set<Integer> set) {
        Set<String> raw = new HashSet<>();
        for (int id : set) raw.add(String.valueOf(id));
        saveSet(ctx, "following_" + userId, raw);
    }

    public static boolean isFollowing(Context ctx, int userId, int targetId) {
        return getFollowings(ctx, userId).contains(targetId);
    }

    public static void follow(Context ctx, int userId, int targetId) {
        Set<Integer> followings = getFollowings(ctx, userId);
        Set<Integer> followers = getFollowers(ctx, targetId);
        if (followings.add(targetId)) {
            followers.add(userId);
            setFollowings(ctx, userId, followings);
            setFollowers(ctx, targetId, followers);
        }
    }

    public static void unfollow(Context ctx, int userId, int targetId) {
        Set<Integer> followings = getFollowings(ctx, userId);
        Set<Integer> followers = getFollowers(ctx, targetId);
        if (followings.remove(targetId)) {
            followers.remove(userId);
            setFollowings(ctx, userId, followings);
            setFollowers(ctx, targetId, followers);
        }
    }
}
