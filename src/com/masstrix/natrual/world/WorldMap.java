package com.masstrix.natrual.world;

import com.masstrix.natrual.Logger;
import com.masstrix.natrual.NaturalEnvironment;
import com.masstrix.natrual.util.FileUtil;
import org.bukkit.Location;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldMap {

    private static final String FILE = NaturalEnvironment.getInstance().getDataFolder().getAbsolutePath() + "/worlds/";

    private ConcurrentHashMap<ChunkLocation, PointMap> chunks = new ConcurrentHashMap<>();
    private String world;
    private JSONObject settings;

    private List<ChunkLocation> buffer = new ArrayList<>();

    public WorldMap(String world) {
        this.world = world;
    }

    /**
     * @return the worlds name.
     */
    public String getName() {
        return world;
    }

    /**
     * @param x x position of chunk.
     * @param z z position of chunk.
     * @return if the chunks data is loaded in cache.
     */
    public boolean isLoaded(int x, int z) {
        return chunks.containsKey(new ChunkLocation(x, z));
    }

    /**
     * @return the size of loaded chunks.
     */
    public int getSize() {
        return chunks.size();
    }

    /**
     * Fetch a chunks data. If the data has not been loaded or generated, that will be done.
     * Otherwise a cached version of the chunks data will be returned.
     *
     * @param x x location of the chunk.
     * @param z z location of the chunk.
     * @return a chinks <code>PointMap</code> data.
     */
    public PointMap fetch(int x, int z) {
        ChunkLocation key = new ChunkLocation(x, z);
        if (!chunks.containsKey(key) && !buffer.contains(key)) {
            String worldPath = FILE + world;
            String chunkPath = worldPath + "/data/" + getFileName(x, z);

            //File file = new File(chunkPath);

            /*if (file.exists()) {
                try {
                    byte[] data = FileUtil.readData(chunkPath);
                    ByteArrayInputStream stream = new ByteArrayInputStream(data);
                    ObjectInputStream in = new ObjectInputStream(stream);
                    //double[][][] points = (double[][][]) in.read();

                    PointMap map = new PointMap(x, z);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else*/ if (!buffer.contains(key)) {
                buffer.add(key);
                new ChunkMapGenerator(this, x, z).start();
                if (NaturalEnvironment.getInstance().isDebugEnabled())
                    Logger.info(String.format("Added chunk [%d %d %s] to buffer", x, z, world));
            }
            //}
        }
        return chunks.getOrDefault(key, null);
    }

    synchronized void setChunkData(int x, int z, double[][][] data) {
        ChunkLocation key = new ChunkLocation(x, z);
        chunks.put(key, new PointMap(x, z, data));
        buffer.remove(key);
    }

    /**
     * Unload a chunk's data from cache.
     *
     * @param x x location of chunk.
     * @param z z location of chunk.
     */
    public void unload(int x, int z) {
        ChunkLocation key = new ChunkLocation(x, z);
        if (chunks.containsKey(key)) {
            save(chunks.get(key));
            chunks.remove(key);
        }
    }

    /*
     * Save a point map's data to file.
     */
    private void save(PointMap map) {
        new PointMapSaver(map).start();
    }

    /**
     * Return the mean temperature value for a specific location. This works by getting the closest
     * 4 world data points from the required {@link PointMap}'s.
     *
     * @param location location of returned point.
     * @return the temperature for the given location.
     */
    public double getTemperature(Location location) {
        return location == null || location.getWorld().getName().equals(this.getName()) ? 0 :
                getTemperature(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Return the mean temperature value for a specific location. This works by getting the closest
     * 4 world data points from the required {@link PointMap}'s.
     *
     * @param x x location.
     * @param y y location.
     * @param z z location.
     * @return the temperature for the given location.
     */
    public double getTemperature(double x, double y, double z) {
        int xRound = PointMap.getPointRound(x), zRound = PointMap.getPointRound(z);
        int chunkX = (int) Math.round(x) % 16, chunkZ = (int) Math.round(z) % 16;

        List<PointMap> chunks = new ArrayList<>();
        List<Double> points = new ArrayList<>();

        if (chunkX >= 14) {
            chunks.add(fetch((int) x + 1, (int) z));
        }

        if (chunkZ >= 14) {
            chunks.add(fetch((int) x + 1, (int) z));
        }

        for (PointMap map : chunks) {

        }

        double total = 0D;
        for (double d : points) total += d;
        return total / points.size();
    }

    /*
     * PointMapSaver saves a single PointMap to file.
     */
    private class PointMapSaver extends Thread {

        private PointMap map;

        PointMapSaver(PointMap map) {
            this.map = map;
            setName("PointMapSaver #" + this.getId());
        }

        @Override
        public void run() {
            String worldPath = FILE + world;
            String configPath = worldPath + "/settings.json";

            try {
                File file = new File(worldPath);
                if (!file.exists()) {
                    Files.createDirectories(Paths.get(worldPath));
                }

                File config = new File(configPath);
                if (config.createNewFile()) {
                    FileWriter writer = new FileWriter(config);
                    try {
                        Map<String, Object> vals = new HashMap<>();
                        vals.put("lava", 100);
                        vals.put("water", 50);

                        JSONObject object = new JSONObject(vals);
                        writer.write(object.toJSONString());
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        writer.flush();
                        writer.close();
                    }
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(stream);
                out.writeObject(Arrays.deepToString(map.getPoints()));
                FileUtil.asyncWriteData(worldPath + "/data/" + getFileName(map.getX(), map.getZ()), stream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileName(int x, int z) {
        return String.format("%d_%d", x, z) + ".dat";
    }
}
