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

import blue.lapis.pore.converter.data.AbstractDataValue;
import blue.lapis.pore.converter.data.DataTypeConverter;

import com.google.common.collect.ImmutableBiMap;
import org.spongepowered.api.data.manipulator.mutable.block.AxisData;
import org.spongepowered.api.data.manipulator.mutable.block.TreeData;
import org.spongepowered.api.data.type.TreeType;
import org.spongepowered.api.data.type.TreeTypes;
import org.spongepowered.api.util.Axis;

public final class LogDataConverter extends DataTypeConverter {

    @SuppressWarnings("rawtypes")
    private LogDataConverter() {
        converters.put(
                ImmutableBiMap.<AbstractDataValue, Byte>builder()
                        .put(new TreeDataValue(TreeTypes.OAK), (byte) 0)
                        .put(new TreeDataValue(TreeTypes.SPRUCE), (byte) 1)
                        .put(new TreeDataValue(TreeTypes.BIRCH), (byte) 2)
                        .put(new TreeDataValue(TreeTypes.JUNGLE), (byte) 3)
                        .build(),
                (byte) 2
        );
        applicableTypes.add(TreeData.class);
        converters.put(
                ImmutableBiMap.<AbstractDataValue, Byte>builder()
                        .put(new AxisDataValue(Axis.Y), (byte) 0)
                        .put(new AxisDataValue(Axis.X), (byte) 1)
                        .put(new AxisDataValue(Axis.Z), (byte) 2)
                        //TODO: add mapping for data value 3 (not yet in SpongeAPI)
                        .build(),
                (byte) 2
        );
        applicableTypes.add(AxisData.class);
    }

    static class TreeDataValue extends AbstractDataValue<TreeData, TreeType> {

        public TreeDataValue(TreeType value) {
            super(TreeData.class, value);
        }
    }

    static class AxisDataValue extends AbstractDataValue<AxisData, Axis> {

        public AxisDataValue(Axis value) {
            super(AxisData.class, value);
        }
    }
}
