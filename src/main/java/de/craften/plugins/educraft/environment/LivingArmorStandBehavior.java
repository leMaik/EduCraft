package de.craften.plugins.educraft.environment;

import de.craften.plugins.managedentities.ManagedEntityBase;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

/**
 * A behavior that adds arms to armor stands and adds animations.
 */
public class LivingArmorStandBehavior implements de.craften.plugins.managedentities.behavior.Behavior {
    private double count = 0;
    private ItemStack itemInHand;

    @Override
    public void tick(ManagedEntityBase managedEntityBase) {
        Entity entity = managedEntityBase.getEntity();
        if (entity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) entity;
            if (!armorStand.hasArms()) {
                armorStand.setArms(true);
            }
            if (armorStand.hasBasePlate()) {
                armorStand.setBasePlate(false);
            }

            count++;
            armorStand.setLeftArmPose(new EulerAngle(
                    Math.toRadians(3 * Math.sin(2 * Math.PI / 180 * count)),
                    0,
                    Math.toRadians(5 * Math.cos(2 * Math.PI / 120 * count) - 5)
            ));

            if (itemInHand == null) {
                if (armorStand.getItemInHand() != null) {
                    armorStand.setItemInHand(null);
                }

                armorStand.setRightArmPose(new EulerAngle(
                        Math.toRadians(3 * Math.cos(2 * Math.PI / 180 * count)),
                        0,
                        Math.toRadians(-5 * Math.cos(2 * Math.PI / 120 * count) + 5)
                ));
            } else {
                if (armorStand.getItemInHand() != itemInHand) {
                    armorStand.setItemInHand(itemInHand);
                }

                armorStand.setRightArmPose(new EulerAngle(
                        Math.toRadians(-30 + 3 * Math.cos(2 * Math.PI / 180 * count)),
                        0,
                        Math.toRadians(-5 * Math.cos(2 * Math.PI / 120 * count) + 5)
                ));
            }
        }
    }

    public void setItemInHand(ItemStack itemInHand) {
        this.itemInHand = itemInHand;
    }
}
