/*
 * Pore
 * Copyright (c) 2014-2015, Lapis <https://github.com/LapisBlue>
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package blue.lapis.pore.impl.entity;

import static org.spongepowered.api.data.manipulator.catalog.CatalogEntityData.DYEABLE_DATA;
import static org.spongepowered.api.data.manipulator.catalog.CatalogEntityData.SHEARED_DATA;

import blue.lapis.pore.converter.type.material.DyeColorConverter;
import blue.lapis.pore.converter.wrapper.WrapperConverter;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.spongepowered.api.entity.living.animal.Sheep;

public class PoreSheep extends PoreAnimals implements org.bukkit.entity.Sheep {

    public static PoreSheep of(Sheep handle) {
        return WrapperConverter.of(PoreSheep.class, handle);
    }

    protected PoreSheep(Sheep handle) {
        super(handle);
    }

    @Override
    public Sheep getHandle() {
        return (Sheep) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.SHEEP;
    }

    @Override
    public boolean isSheared() {
        return hasData(SHEARED_DATA) && getHandle().get(SHEARED_DATA).get().sheared().get();
    }

    @Override
    public void setSheared(boolean flag) {
        if (flag != isSheared()) {
            if (flag) {
                getHandle().offer(getHandle().getOrCreate(SHEARED_DATA).get().sheared().set(true));
            } else {
                getHandle().remove(SHEARED_DATA);
            }
        }
    }

    @Override
    public DyeColor getColor() {
        return DyeColorConverter.of(getHandle().get(DYEABLE_DATA).get().type().get());
    }

    @Override
    public void setColor(DyeColor color) {
        getHandle().get(DYEABLE_DATA).get().type().set(DyeColorConverter.of(color));
    }
}
