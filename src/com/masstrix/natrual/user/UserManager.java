package com.masstrix.natrual.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UserManager handles the cache data for all {@link User} classes.
 * When a file is loaded it should be put into cache and used from
 * this class.
 *
 * @see User
 */
public final class UserManager {

    private static final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();

    static {
        for (Player player : Bukkit.getOnlinePlayers()) {
            add(User.load(player.getUniqueId()));
        }
    }

    /**
     * Add a new player into cache for later acceptability.
     *
     * @param user who is being added into the map.
     */
    public static void add(User user) {
        if (!users.containsKey(user.getUniqueId()))
            users.put(user.getUniqueId(), user);
    }

    /**
     * Return if the user is stored in cache.
     *
     * @param user uuid of the player.
     * @return if user is in cache.
     */
    public static boolean contains(UUID user) {
        return users.containsKey(user);
    }

    /**
     * Remove a player from cache. When removing the player from cache there data
     * will be saved for later access into a data file.
     *
     * @param user uuid of the player who is being removed.
     */
    public static void remove(UUID user) {
        if (contains(user)) {
            users.get(user).save();
            users.remove(user);
        }
    }

    /**
     * Return a players {@link User data}. This will never return null. If there is no
     * user that exists in record or cache a new user will be created and returned.
     * Otherwise a cached will be returned.
     *
     * @param user uuid of the player to fetch.
     * @return the players user data.
     */
    public static User get(UUID user) {
        if (contains(user)) return users.get(user);
        else {
            User u = User.load(user);
            add(u);
            return u;
        }
    }

    /**
     * @return all cached users.
     */
    public static Collection<User> getAll() {
        return users.values();
    }
}
