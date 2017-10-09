package com.masstrix.natrual.user;

import com.masstrix.natrual.Logger;
import com.masstrix.natrual.NaturalEnvironment;
import com.masstrix.natrual.events.PlayerDehydrateEvent;
import com.masstrix.natrual.util.FileUtil;
import com.masstrix.natrual.world.PointMap;
import com.masstrix.natrual.world.WorldDataHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

public class User {

    private static final String FILE_PATH = NaturalEnvironment.getInstance().getDataFolder().getAbsolutePath() + "/players/";
    private static Random random = new Random();

    private double walked = 0, temp = 0;
    private int thirst = 20, thirstD = 0;
    private UUID uuid;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public User(UUID uuid, int thirst, double walked, int thirstD, double temp) {
        this.uuid = uuid;
        this.thirst = thirst > 20 ? 20 : thirst;
        this.walked = walked;
        this.thirstD = thirstD;
        this.temp = temp;
    }

    /**
     * @return the players UUID.
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * @return the players thirst level from 0 to 20.
     */
    public int getThirst() {
        return thirst;
    }

    /**
     * @return the players last updated temperature.
     */
    public double getTemp() {
        return temp;
    }

    public double updateTemperature() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return this.temp;
        Chunk chunk = player.getLocation().getChunk();
        WorldDataHandler dataHandler = NaturalEnvironment.getInstance().getChunkData();
        PointMap map = dataHandler.fetchChunk(player.getWorld(), chunk.getX(), chunk.getZ());

        if (map == null) return this.temp;
        double point = map.getWorldValue(player.getLocation());
        temp = point;
        if (temp > 250 && player.getFireTicks() != 0) player.setFireTicks(20 * 30);
        return point;
    }

    /**
     * Update the players temperature value. This method will get all nearby blocks, biomes
     * items and other affecting elements and calculate the temperature fo where the player is
     * standing currently. This method will cause issues with large player counts and is currently
     * planned to be updated to a new system.
     *
     * @return the new temperature of the player.
     */
    @Deprecated
    public int updateTemp() {
        return (int) this.temp;
        /*Player player = Bukkit.getPlayer(uuid);
        if (player == null) return (int) temp;
        double temp_to = getAmbientTemperature();
        int b = 0;

        //TODO clean & tidy method
        //TODO implement new temperature system.

        for (Entity e : player.getNearbyEntities(5, 5, 5)) {
            if (e instanceof ArmorStand) {
                CampFire fire = CampFire.get((ArmorStand) e);
                if (fire == null) continue;
                temp_to += TemperatureValue.CAMPFIRE.getAttr() / (e.getLocation().distance(player.getLocation()) / 2);
                break;
            }
        }

        ItemStack[] armor = player.getInventory().getArmorContents();

        ItemStack hand_l = player.getInventory().getItemInOffHand();
        ItemStack hand_r = player.getInventory().getItemInMainHand();

        for (TemperatureValue tv : TemperatureValue.values()) {
            if (tv.getType() == TemperatureType.ARMOR) {
                for (ItemStack s : armor) {
                    if (s != null) {
                        if (s.containsEnchantment(EnchantmentsManager.HYDRATION)) {
                            double lvl = s.getEnchantmentLevel(EnchantmentsManager.HYDRATION) * 2.35;
                            if (temp_to > 40) {
                                temp_to /= (Math.pow(1.00001D + (lvl / 100000D), (temp_to * 1.03)));
                            }
                        }
                        Object o = CustomStack.getNBTTag(s, "hydration.level");
                        if (o != null) {
                            NBTTagInt tag = (NBTTagInt) o;
                            int i = tag.e();
                            if (i == 3) {
                                if (temp_to > 40) temp_to /= (Math.pow(1.00101, (temp_to * 1.03)));
                            } else if (i == 2) {
                                if (temp_to > 40) temp_to /= (Math.pow(1.0004, (temp_to * 1.03)));
                            } else {
                                if (temp_to > 40) temp_to /= (Math.pow(1.0002, (temp_to * 1.03)));
                            }
                            continue;
                        }
                    }

                    if (s != null && s.getType().toString().equals(tv.toString())) {
                        temp_to += tv.getAttr();
                    }
                }
            } else if (tv.getType() == TemperatureType.ITEM) {
                if (hand_l != null && hand_l.getType().toString().toLowerCase().contains(tv.getName()))
                    temp_to += tv.getAttr();
                if (hand_r != null && hand_r.getType().toString().toLowerCase().contains(tv.getName()))
                    temp_to += tv.getAttr();
            }
        }

        World world = player.getWorld();

        long time = world.getTime();
        if (world.getEnvironment() == World.Environment.NORMAL) temp_to -= time > 12000L ? (int) (time / 1000) : 0;

        temp_to += b;

        if (world.isThundering()) temp_to -= 5;

        if (temp != temp_to) {
            double v = (temp - temp_to);
            if (temp_to > 0 || temp_to <= 0) temp -= v / 40;
            else temp += v / 40;
        }
        save();
        return (int) temp_to;
    }

    public double getAmbientTemperature() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return this.temp;
        double temperature = 0;

        List<TemperatureValue> biomes = new ArrayList<>();
        Map<TemperatureValue, Double> factors = new HashMap<>();

        for (Block m : WorldUtil.getNearbyBlocks(player.getLocation(), 7)) {
            String y = m.getType().name().toLowerCase();
            String bi = m.getBiome().toString().replaceAll("MUTATED_", "");

            for (TemperatureValue tv : TemperatureValue.values()) {
                if (!biomes.contains(tv)
                        && tv.getType() == TemperatureType.BIOME
                        && bi.contains(tv.toString())) {
                    temperature += tv.getAttr();
                    biomes.add(tv);
                    continue;
                }
                if (tv.getType() != TemperatureType.BLOCK) continue;
                if (!y.contains(tv.getName())) continue;
                if (factors.containsKey(tv)) {
                    if (factors.get(tv) > m.getLocation().distance(player.getLocation()))
                        factors.put(tv, m.getLocation().distance(player.getLocation()));
                    continue;
                }
                factors.put(tv, m.getLocation().distance(player.getLocation()));
            }
        }

        for (Map.Entry<TemperatureValue, Double> entry : factors.entrySet()) {
            temperature = entry.getKey().getAttr() / (entry.getValue() / 2);
        }
        return temperature;*/
    }

    /**
     * Hydrate the players h20. Values can be between -20 and 20.
     *
     * @param v how much to hydrate the player ranging from -20 to 20.
     */
    public void hydrate(int v) {
        int hydration = this.thirst + v > 20 ? 20 : this.thirst + v < 0 ? 0 : this.thirst + v;
        PlayerDehydrateEvent event = new PlayerDehydrateEvent(Bukkit.getPlayer(this.uuid),
                this.thirst, hydration);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.thirst = event.getTo() > 20 ? 20 : event.getTo() < 0 ? 0 : event.getTo();
        if (v < 0) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getWorld().spawnParticle(Particle.WATER_SPLASH, player.getEyeLocation(), 15, 0, 0, 0, 0);
            }
        }
        save();
    }

    public void addDistance(double d) {
        this.walked += d;
        if (walked >= thirstD) {
            walked = 0;
            thirstD = random.nextInt(50) + 50;
            hydrate(-1);
        }
    }

    public void reset() {
        hydrate(100);
        walked = 0;
        thirstD = random.nextInt(50) + 50;
        temp = updateTemperature();
    }

    /**
     * Save the players data to a file in the plugins folder for later access.
     */
    public void save() {
        Map<String, Object> data = new HashMap<>();
        data.put("temp", temp);
        data.put("thirst", thirst);
        data.put("walked", walked);

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(data);

            FileUtil.writeData(FILE_PATH + this.uuid + ".dat", stream.toByteArray());
        } catch (IOException e) {
            Logger.info("Failed to save player " + this.uuid);
            e.printStackTrace();
        }
    }

    /**
     * Load a players {@link User class}. If the player has not logged in before or the file
     * is missing a new version will be generated and saved. Otherwise the file will be loaded
     * and returned as the users data.
     *
     * @param uuid who is being loaded.
     * @return the users data class.
     */
    public static User load(UUID uuid) {
        try {
            File reader = new File(FILE_PATH + uuid + ".dat");
            if (!reader.exists()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return null;
                else {
                    User user = new User(uuid, 20, 0, 0, 0);
                    user.save();
                    return user;
                }
            }
            byte[] file = FileUtil.readData(FILE_PATH + uuid + ".dat");
            ByteArrayInputStream stream = new ByteArrayInputStream(file);
            ObjectInputStream in = new ObjectInputStream(stream);
            Map<String, Object> data = (Map<String, Object>) in.readObject();
            if (data != null) {
                double temp = data.containsKey("temp") ? (double) data.get("temp") : 0;
                int thirst = data.containsKey("thirst") ? (int) data.get("thirst") : 20;
                double walked = data.containsKey("walked") ? (double) data.get("walked") : 0D;
                return new User(uuid, thirst, walked, 0, temp);
            }
        } catch (IOException e) {
            Logger.info("Failed to fetch player " + uuid);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
