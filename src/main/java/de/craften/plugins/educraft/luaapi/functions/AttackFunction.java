package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to attack the entities in front of the entity.
 */
public class AttackFunction extends EduCraftApiFunction {
    private static final double DAMAGE = 8; //same as diamond sword

    @Override
    protected void beforeExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
    }

    @Override
    public Varargs execute(Varargs varargs) {
        for (Entity entity : getApi().getEntitiesAhead()) {
            if (entity instanceof Damageable) {
                ((Damageable) entity).damage(DAMAGE);
            }
        }

        return LuaValue.NIL;
    }

    @Override
    protected void afterExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);
    }
}
