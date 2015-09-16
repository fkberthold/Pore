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
package blue.lapis.pore.converter.data.block.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import blue.lapis.pore.converter.data.AbstractDataValue;
import blue.lapis.pore.converter.data.DataTypeConverter;
import blue.lapis.pore.converter.data.block.BlockDataConverter;
import blue.lapis.pore.impl.block.PoreBlock;

import org.bukkit.block.Block;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

// This is kind of a behemoth of a class. Lots and lots of overloading.
// Sorry for not documenting it, but honestly I don't have the patience for that right now.
@SuppressWarnings("rawtypes")
public class BTDCTestUtil {

    public static <T extends DataManipulator<T, ?>, V> void testSingleAbstraction(BlockType blockType,
                                                                               Class<T> dataClass, byte inputByte,
                                                                               V expectedValue, boolean invert)
            throws Exception {
        AbstractDataValue<T, V> output = new AbstractDataValue<>(dataClass, expectedValue);
        Collection<AbstractDataValue<T, V>> outputColl = Collections.singletonList(output);
        testAbstraction(blockType, inputByte, outputColl, invert);
    }

    public static <T extends DataManipulator<T, ?>, V> void testSingleAbstraction(BlockType blockType,
                                                                               Class<T> dataClass, byte rawData,
                                                                               V abstractedData) throws Exception {
        testSingleAbstraction(blockType, dataClass, rawData, abstractedData, false);
    }

    public static <T extends DataManipulator<T, ?>, V> void testSingleDeabstraction(BlockType blockType,
                                                                                 Class<T> dataClass, byte rawData,
                                                                                 V abstractedData, boolean invert)
            throws Exception {
        AbstractDataValue<T, V> input = new AbstractDataValue<>(dataClass, abstractedData);
        Collection<AbstractDataValue<T, V>> inputColl = Collections.singletonList(input);
        testDeabstraction(blockType, rawData, inputColl, invert);
    }

    public static <T extends DataManipulator<T, ?>, V> void testSingleDeabstraction(BlockType blockType,
                                                                                 Class<T> dataClass, byte rawData,
                                                                                 V abstractedData) throws Exception {
        testSingleDeabstraction(blockType, dataClass, rawData, abstractedData, false);
    }

    @SuppressWarnings("unchecked")
    public static void testAbstraction(BlockType blockType, byte rawData,
                                       Collection<? extends AbstractDataValue<? extends DataManipulator, ?>>
                                               abstractedData,
                                       boolean invert) throws Exception {
        Method getConverter = BlockDataConverter.class.getDeclaredMethod("getConverter", BlockType.class);
        getConverter.setAccessible(true);
        DataTypeConverter converter = (DataTypeConverter) getConverter.invoke(BlockDataConverter.INSTANCE, blockType);
        Collection<AbstractDataValue> derived = converter.of(rawData);
        if (invert) {
            assertFalse(derived.containsAll(abstractedData) && abstractedData.containsAll(derived));
        } else {
            assertTrue("Missing expected data values", derived.containsAll(abstractedData));
            assertTrue("Found unexpected data values", abstractedData.containsAll(derived));
        }
    }

    public static void testAbstraction(BlockType blockType, byte rawData,
                                       Collection<? extends AbstractDataValue<? extends DataManipulator, ?>>
                                               abstractedData)
            throws Exception {
        testAbstraction(blockType, rawData, abstractedData, false);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public static void testDeabstraction(BlockType blockType, byte rawData,
                                         Collection<? extends AbstractDataValue<? extends DataManipulator, ?>>
                                                 abstractedData,
                                         boolean invert) throws Exception {
        Location loc = new Location(mock(Extent.class), 0, 0, 0);
        when(loc.getBlockType()).thenReturn(blockType);

        Collection<DataManipulator<?, ?>> manipulators = abstractedData.stream()
                .map((Function<AbstractDataValue<? extends DataManipulator, ?>, DataManipulator<?, ?>>) datum -> {
            if (datum.getValue() == AbstractDataValue.ABSENT) {
                return null;
            }
            DataManipulator<?, ?> dm = (DataManipulator<?, ?>) mock(datum.getDataClass());
            if (dm instanceof VariantData) {
                Value value = mock(Value.class);
                when(value.get()).thenReturn(datum.getValue());
                when(((VariantData) dm).type()).thenReturn(value);
            }
            return dm;
        }).collect(Collectors.toList());

        when(loc.getContainers()).thenReturn(manipulators);
        Block block = PoreBlock.of(loc);
        if (invert) {
            assertNotEquals((long) rawData, (long) block.getData());
        } else {
            assertEquals(rawData, block.getData());
        }
    }

    @SuppressWarnings("unchecked")
    public static void testDeabstraction(BlockType blockType, byte rawData,
                                         Collection<? extends AbstractDataValue<? extends DataManipulator, ?>>
                                                 abstractedData)
            throws Exception {
        testDeabstraction(blockType, rawData, abstractedData, false);
    }

    public static void testConversion(BlockType blockType, byte rawData,
                                      Collection<? extends AbstractDataValue<? extends DataManipulator, ?>>
                                              abstractedData,
                                      boolean invert) throws Exception {
        testAbstraction(blockType, rawData, abstractedData, invert);
        testDeabstraction(blockType, rawData, abstractedData, invert);
    }

    public static void testConversion(BlockType blockType, byte rawData,
                                      Collection<? extends AbstractDataValue<? extends DataManipulator, ?>>
                                              abstractedData)
            throws Exception {
        testConversion(blockType, rawData, abstractedData, false);
    }

    public static <T extends DataManipulator<T, ?>, V> void testSingleConversion(BlockType blockType,
                                                                              Class<T> dataClass, byte rawData,
                                                                              V abstractedData, boolean invert)
            throws Exception {
        testSingleAbstraction(blockType, dataClass, rawData, abstractedData, invert);
        testSingleDeabstraction(blockType, dataClass, rawData, abstractedData, invert);
    }


    public static <T extends DataManipulator<T, ?>, V> void testSingleConversion(BlockType blockType,
                                                                              Class<T> dataClass, byte rawData,
                                                                              V abstractedData) throws Exception {
        testSingleConversion(blockType, dataClass, rawData, abstractedData, false);
    }
}
