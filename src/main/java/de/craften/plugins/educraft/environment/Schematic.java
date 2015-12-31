package de.craften.plugins.educraft.environment;

import jnbt.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A schematic file that can be loaded and restored at a given location.
 */
public class Schematic {
    private final int width;
    private final int length;
    private final int height;
    private final Map<Integer, Map<String, Tag>> idxEntitiesMap;
    private final byte[] blocks;
    private final byte[] data;

    /**
     * Loads the schematic from the given file.
     *
     * @param schematic schematic file
     * @throws IOException if the schematic file cannot be loaded
     */
    public Schematic(File schematic) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(schematic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        NBTInputStream nis = new NBTInputStream(fis);
        CompoundTag master = (CompoundTag) nis.readTag();
        nis.close();
        Map<String, Tag> masterMap = master.getValue();

        width = ((ShortTag) masterMap.get("Width")).getValue();
        length = ((ShortTag) masterMap.get("Length")).getValue();
        height = ((ShortTag) masterMap.get("Height")).getValue();

        blocks = ((ByteArrayTag) masterMap.get("Blocks")).getValue();
        data = ((ByteArrayTag) masterMap.get("Data")).getValue();

        // get tile entities
        List<Tag> entities = ((ListTag) masterMap.get("TileEntities")).getValue();
        idxEntitiesMap = new TreeMap<>();
        for (Tag tag : entities) {
            Map<String, Tag> cmpMap = ((CompoundTag) tag).getValue();
            int x = ((IntTag) cmpMap.get("x")).getValue();
            int y = ((IntTag) cmpMap.get("y")).getValue();
            int z = ((IntTag) cmpMap.get("z")).getValue();

            int blockIndex = (y * length + z) * width + x;
            idxEntitiesMap.put(blockIndex, cmpMap);
        }
    }

    /**
     * Gets the width of this schematic (along the x-axis).
     *
     * @return width of this schematic
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of this schematic (along the y-axis).
     *
     * @return height of this schematic
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the length of this schematic (along the z-axis).
     *
     * @return length of this schematic
     */
    public int getLength() {
        return length;
    }

    /**
     * Restores this schematic at the given location.
     *
     * @param location location of the bottom north-east corner of the schematic
     */
    public void restoreAt(Location location) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                for (int z = 0; z < getLength(); z++) {
                    restoreBlock(x, y, z, location);
                }
            }
        }
    }

    /**
     * Restores the given block.
     *
     * @param x      relative x coordinate of the block
     * @param y      relative y coordinate of the block
     * @param z      relative z coordinate of the block
     * @param origin location of the bottom north-east corner of the schematic
     */
    private void restoreBlock(int x, int y, int z, Location origin) {
        int index = (y * getLength() + z) * getWidth() + x;
        Block block = origin.getWorld().getBlockAt(origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z);

        if (blocks.length > index) {
            short id = (short) (blocks[index] & 0xFF);
            //noinspection deprecation
            block.setTypeIdAndData(id, data[index], false);

            if (id == 63 || id == 68) { // sign
                Map<String, Tag> tileEntity = idxEntitiesMap.get(index);
                if (tileEntity != null) {
                    Sign sign = (Sign) block.getState();
                    for (int i = 0; i < 4; i++) {
                        String raw = ((StringTag) tileEntity.get("Text" + (i + 1))).getValue();
                        if (raw.equals("\"\"")) {
                            sign.setLine(i, "");
                        } else {
                            sign.setLine(i, raw.replace("{\"extra\":[\"", "").replace("\"],\"text\":\"\"}", ""));
                        }
                    }
                    sign.update(true);
                }
            }
        } else {
            block.setType(Material.AIR);
        }
    }
}
