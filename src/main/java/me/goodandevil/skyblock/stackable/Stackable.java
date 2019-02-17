package me.goodandevil.skyblock.stackable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.UUID;

public class Stackable {

    private UUID uuid;
    private Island island;

    private Location location;
    private Material material;
    private Integer size = 2;


    public Stackable(Location location, Material material) {
        this.island = SkyBlock.getInstance().getIslandManager().getIslandAtLocation(location);
        this.uuid = UUID.randomUUID();
        this.location = location;
        this.material = material;
        updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
        save();
    }

    public Stackable(UUID uuid, Location location, Material material, int size) {
        this.island = SkyBlock.getInstance().getIslandManager().getIslandAtLocation(location);
        this.uuid = uuid;
        this.location = location;
        this.material = material;
        this.size = size;
        updateDisplay();
        save();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Island getIsland() {
        return island;
    }

    public Location getLocation() {
        return location.clone();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        save();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
        updateDisplay();
    }

    public void addOne() {
        this.size ++;
        updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
        save();
    }

    public void takeOne() {
        this.size --;
        updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        save();
    }

    private void updateDisplay() {
        removeDisplay();
        Location dropLocation = location.clone().add(.5,1,.5);

        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(dropLocation, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setSmall(true);
        if (NMSUtil.getVersionNumber() > 8) {
            as.setMarker(true);
        }
        as.setBasePlate(true);
        as.setHelmet(new ItemStack(material));
        as.setCustomName(WordUtils.capitalize(material.name().toLowerCase()).replace("_", " ") + "s: " + size);
        as.setCustomNameVisible(true);

    }

    void removeDisplay() {
        for (Entity entity : location.getWorld().getNearbyEntities(getLocation().add(.5,.55,.5), .1,.5,.1)) {
            if (entity.getType() != EntityType.ARMOR_STAND) continue;
            entity.remove();
        }
    }

    private void save() {
        File configFile = new File(SkyBlock.getInstance().getDataFolder().toString() + "/island-data");
        FileManager.Config config = SkyBlock.getInstance().getFileManager().getConfig(new File(configFile, island.getOwnerUUID() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        ConfigurationSection section = configLoad.createSection("Stackables." + getUuid().toString());
        section.set("Location", getLocation());
        section.set("Material", getMaterial().name());
        section.set("Size", getSize());
    }
}