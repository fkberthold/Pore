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
package blue.lapis.pore.converter.data.block;

import blue.lapis.pore.Pore;
import blue.lapis.pore.converter.data.AbstractDataValue;
import blue.lapis.pore.converter.data.DataConverter;
import blue.lapis.pore.converter.data.DataTypeConverter;
import blue.lapis.pore.converter.data.block.type.BigMushroomDataConverter;
import blue.lapis.pore.converter.data.block.type.BrickDataConverter;
import blue.lapis.pore.converter.data.block.type.Leaves2DataConverter;
import blue.lapis.pore.converter.data.block.type.LeavesDataConverter;
import blue.lapis.pore.converter.data.block.type.Log2DataConverter;
import blue.lapis.pore.converter.data.block.type.LogDataConverter;
import blue.lapis.pore.converter.data.block.type.PlanksDataConverter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import org.spongepowered.api.world.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockDataConverter implements DataConverter<Location> {

    public static final BlockDataConverter INSTANCE = new BlockDataConverter();

    private static final Map<Class<?>, DataTypeConverter> CONVERTER_OBJECTS = Maps.newHashMap();

    @SuppressWarnings("ConstantConditions")
    private static final Map<BlockType, DataTypeConverter> CONVERTER_MAP =
            ImmutableMap.<BlockType, DataTypeConverter>builder()
                    .put(BlockTypes.BROWN_MUSHROOM_BLOCK, getConverter(BigMushroomDataConverter.class))
                    .put(BlockTypes.RED_MUSHROOM_BLOCK, getConverter(BigMushroomDataConverter.class))
                    .put(BlockTypes.LEAVES, getConverter(LeavesDataConverter.class))
                    .put(BlockTypes.LEAVES2, getConverter(Leaves2DataConverter.class))
                    .put(BlockTypes.LOG, getConverter(LogDataConverter.class))
                    .put(BlockTypes.LOG2, getConverter(Log2DataConverter.class))
                    .put(BlockTypes.PLANKS, getConverter(PlanksDataConverter.class))
                    .put(BlockTypes.STONEBRICK, getConverter(BrickDataConverter.class))
                    .build();

    private BlockDataConverter() {
    }

    private static DataTypeConverter getConverter(Class<?> clazz) {
        if (CONVERTER_OBJECTS.containsKey(clazz)) {
            return CONVERTER_OBJECTS.get(clazz);
        } else {
            Exception e; // f--- Java 6 yo
            try {
                Constructor<?> c = clazz.getDeclaredConstructors()[0];
                c.setAccessible(true);
                return (DataTypeConverter) c.newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                e = ex;
            }
            Pore.getLogger().error("Failed to instantiate " + clazz.getName());
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public byte getDataValue(Collection<DataManipulator<?, ?>> manipulators, BlockType target) {
        final DataTypeConverter converter = getConverter(target);
        return converter.of(manipulators.stream().filter(m -> m != null).filter(m -> {
            try {
                Class<? extends DataManipulator<?, ?>> clazz = (Class<? extends DataManipulator<?, ?>>)
                        Class.forName(m.getClass().getName().split("\\$")[0]);
                return converter.getApplicableDataTypes().contains(clazz);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                return false;
            }
        }).collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    public byte getDataValue(BlockState target) {
        // Not sure why the compiler has an issue with this without the cast. I blame generics for being terrible.
        return getDataValue((Collection) target.getManipulators(), target.getType());
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public byte getDataValue(Location target) {
        return getDataValue(target.getContainers(), target.getBlockType());
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setDataValue(Location target, byte dataValue) {
        DataTypeConverter converter = getConverter(target.getBlockType());
        Collection<AbstractDataValue> data = converter.of(dataValue);
        data.stream().filter(datum -> datum.getValue() != AbstractDataValue.ABSENT).forEach(datum -> {
            DataManipulator dm = (DataManipulator) target.getOrCreate(datum.getDataClass()).get();
            if (dm instanceof VariantData) {
                ((VariantData) dm).type().set(datum.getValue());
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private DataTypeConverter getConverter(BlockType target) {
        if (!CONVERTER_MAP.containsKey(target)) {
            throw new IllegalArgumentException("Cannot convert data for block type " + target.getName());
        }
        return CONVERTER_MAP.get(target);
    }

}
