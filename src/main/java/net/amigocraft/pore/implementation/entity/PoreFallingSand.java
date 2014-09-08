package net.amigocraft.pore.implementation.entity;

import net.amigocraft.pore.implementation.entity.PoreEntity;
import org.bukkit.Material;
import org.bukkit.entity.FallingSand;


 
public class PoreFallingSand extends PoreEntity implements FallingSand {

    @Override
    public Material getMaterial() {
        return null;
    }

    @Override
    public int getBlockId() {
        return 0;
    }

    @Override
    public byte getBlockData() {
        return 0;
    }

    @Override
    public boolean getDropItem() {
        return false;
    }

    @Override
    public void setDropItem(boolean drop) {

    }
}