package de.craften.plugins.educraft.environment;

import de.craften.plugins.educraft.EduCraft;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Iterator;
import java.util.List;

/**
 * Fire and griefing protection for environments.
 */
public class EnvironmentProtection implements Listener {
    /**
     * Checks if the given location is in any environment.
     *
     * @param location location to check
     * @return true if the given location is in any environment, false if not
     */
    private static boolean isInEnvironment(Location location) {
        for (EduCraftEnvironment environment : EduCraft.getPlugin(EduCraft.class).getEnvironments()) {
            if (environment.contains(location)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getBlockPlaced().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        if (!event.getPlayer().isOp() && isInEnvironment(block.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && !event.getPlayer().isOp() && isInEnvironment(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (isInEnvironment(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockForm(BlockFormEvent event) {
        if (isInEnvironment(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockDamage(BlockDamageEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFade(BlockFadeEvent event) {
        if (isInEnvironment(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (isInEnvironment(event.getBlock().getLocation()) && !isInEnvironment(event.getToBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        boolean first = isInEnvironment(event.getBlock().getLocation());
        for (Block b : event.getBlocks()) {
            if (isInEnvironment(b.getRelative(event.getDirection()).getLocation()) != first) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        boolean first = isInEnvironment(event.getBlock().getLocation());
        for (Block b : event.getBlocks()) {
            if (isInEnvironment(b.getRelative(event.getDirection()).getLocation()) != first) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStructureGrow(StructureGrowEvent event) {
        boolean inEnvironment = isInEnvironment(event.getLocation());
        List<BlockState> blocks = event.getBlocks();

        Iterator<BlockState> it = blocks.iterator();
        while (it.hasNext()) {
            if (isInEnvironment(it.next().getLocation()) != inEnvironment) {
                it.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (isInEnvironment(event.getLocation())) {
            event.setCancelled(false);

            List<Block> blocks = event.blockList();
            Iterator<Block> it = blocks.iterator();
            while (it.hasNext()) {
                if (!isInEnvironment(it.next().getLocation())) {
                    it.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!isInEnvironment(event.getBlock().getLocation())) {
            if (event.getPlayer() == null || !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        } else if (event.getIgnitingBlock() != null && isInEnvironment(event.getIgnitingBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!event.getRemover().isOp() && isInEnvironment(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!event.getDamager().isOp() && isInEnvironment(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getEgg().getLocation())) {
            event.setHatching(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Player && !event.getEntity().isOp() && isInEnvironment(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArmorStandManipulated(PlayerArmorStandManipulateEvent event) {
        if (!event.getPlayer().isOp() && isInEnvironment(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }
}
