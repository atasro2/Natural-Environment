package com.masstrix.natrual.world;

import com.masstrix.natrual.NaturalEnvironment;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data handler for each worlds data. All points for world temperature is stored and handled
 * from this handler.
 */
public final class WorldDataHandler {

    private static final ConcurrentHashMap<String, WorldMap> WORLDS = new ConcurrentHashMap<>();

    /*
    * Start a runnable that runs every 10 minutes to force clear any unused WORLDS.
    * If a chunk is not removed from error or miss use of interfacing the plugin,
    * this will clean those rodge worlds.
    * */
    static {
        final int TIME = (20 * 60) * 10;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<String, WorldMap> entry : WORLDS.entrySet()) {
                    if (entry.getValue().getSize() == 0)
                        WORLDS.remove(entry.getKey());
                }
            }
        }.runTaskTimerAsynchronously(NaturalEnvironment.getInstance(), TIME, TIME);
    }

    /**
     * Load a WORLDS data from file or generate a new chunk data file if once does
     * not exist yet. This is automatically ran when a chunk is loaded in a world.
     *
     * @param world what world the chunk is in.
     * @param x x position of chunk.
     * @param z z position of chunk.
     * @return the WORLDS {@link PointMap}.
     */
    public PointMap fetchChunk(World world, int x, int z) {
        if (world == null) return null;
        return fetchChunk(world.getName(), x, z);
    }

    /**
     * Load a WORLDS data from file or generate a new chunk data file if once does
     * not exist yet. This is automatically ran when a chunk is loaded in a world.
     *
     * @param world what world the chunk is in.
     * @param chunk what chink's data is being fetched.
     * @return the WORLDS {@link PointMap}.
     */
    public PointMap fetchChunk(World world, Chunk chunk) {
        if (world == null || chunk == null) return null;
        return fetchChunk(world.getName(), chunk.getX(), chunk.getZ());
    }

    /**
     * Load a WORLDS data from file or generate a new chunk data file if once does
     * not exist yet. This is automatically ran when a chunk is loaded in a world.
     *
     * @param world name of the WORLDS world its in.
     * @param x x position of chunk.
     * @param z z position of chunk.
     * @return the WORLDS {@link PointMap}.
     */
    public PointMap fetchChunk(String world, int x, int z) {
        if (!WORLDS.containsKey(world)) {
            WorldMap map = new WorldMap(world);
            WORLDS.put(world, map);
        }
        return WORLDS.get(world).fetch(x, z);
    }

    /**
     * Unload a WORLDS data from cache.
     *
     * @param world name of the WORLDS world its in.
     * @param x x position of chunk.
     * @param z z position of chunk.
     */
    public void unloadChunk(String world, int x, int z) {
        if (WORLDS.containsKey(world)) {
            WORLDS.get(world).unload(x, z);
        }
    }

    /**
     * @param world name of world.
     * @return if a world is loaded into cache.
     */
    public boolean conatins(String world) {
        return WORLDS.containsKey(world);
    }

    /**
     * @param world name of world the WORLDS located in.
     * @param x x position of chunk.
     * @param z z position of chunk.
     * @return if a specific chunk is loaded into cache.
     */
    public boolean contains(String world, int x, int z) {
        return WORLDS.containsKey(world) && WORLDS.get(world).isLoaded(x, z);
    }

    /**
     * @param world what worlds data is being returned.
     * @return a data map for the defined world or null if there is none loaded.
     */
    public WorldMap get(String world) {
        return WORLDS.getOrDefault(world, null);
    }

    public boolean isLoaded(Chunk chunk) {
        for (WorldMap map : WORLDS.values()) {
            if (map.isLoaded(chunk.getX(), chunk.getZ())) return true;
        }
        return false;
    }

    /**
     * Remove a world from cache.
     *
     * @param world what world is being removed.
     */
    public void remove(String world) {
        WORLDS.remove(world);
    }

    /**
     * Remove a world from cache.
     *
     * @param world what world is being removed.
     */
    public void remove(World world) {
        WORLDS.remove(world.getName());
    }

    /**
     * @return how many worlds are currently in use.
     */
    public int size() {
        return WORLDS.size();
    }

    /**
     * @return the total amount of WORLDS currently in use.
     */
    public int chunkSize() {
        int size = 0;
        for (WorldMap map : WORLDS.values()) {
            size += map.getSize();
        }
        return size;
    }
}
