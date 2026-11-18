/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockSand
extends BlockFalling {
    public static final PropertyEnum<EnumType> field_176504_a = PropertyEnum.func_177709_a("variant", EnumType.class);

    public BlockSand() {
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176504_a, EnumType.SAND));
    }

    @Override
    public int func_180651_a(IBlockState iBlockState) {
        return iBlockState.func_177229_b(field_176504_a).func_176688_a();
    }

    @Override
    public void func_149666_a(CreativeTabs creativeTabs, NonNullList<ItemStack> nonNullList) {
        for (EnumType enumType : EnumType.values()) {
            nonNullList.add(new ItemStack(this, 1, enumType.func_176688_a()));
        }
    }

    @Override
    public MapColor func_180659_g(IBlockState iBlockState, IBlockAccess iBlockAccess, BlockPos blockPos) {
        return iBlockState.func_177229_b(field_176504_a).func_176687_c();
    }

    @Override
    public IBlockState func_176203_a(int n) {
        return this.func_176223_P().func_177226_a(field_176504_a, EnumType.func_176686_a(n));
    }

    @Override
    public int func_176201_c(IBlockState iBlockState) {
        return iBlockState.func_177229_b(field_176504_a).func_176688_a();
    }

    @Override
    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer(this, field_176504_a);
    }

    @Override
    public int func_189876_x(IBlockState iBlockState) {
        EnumType enumType = iBlockState.func_177229_b(field_176504_a);
        return enumType.func_189865_a();
    }

    public static enum EnumType implements IStringSerializable
    {
        SAND(0, "sand", "default", MapColor.field_151658_d, -2370656),
        RED_SAND(1, "red_sand", "red", MapColor.field_151676_q, -5679071);

        private static final EnumType[] field_176695_c;
        private final int field_176692_d;
        private final String field_176693_e;
        private final MapColor field_176690_f;
        private final String field_176691_g;
        private final int field_189866_h;

        private EnumType(int n2, String string2, String string3, MapColor mapColor, int n3) {
            this.field_176692_d = n2;
            this.field_176693_e = string2;
            this.field_176690_f = mapColor;
            this.field_176691_g = string3;
            this.field_189866_h = n3;
        }

        public int func_189865_a() {
            return this.field_189866_h;
        }

        public int func_176688_a() {
            return this.field_176692_d;
        }

        public String toString() {
            return this.field_176693_e;
        }

        public MapColor func_176687_c() {
            return this.field_176690_f;
        }

        public static EnumType func_176686_a(int n) {
            if (n < 0 || n >= field_176695_c.length) {
                n = 0;
            }
            return field_176695_c[n];
        }

        @Override
        public String func_176610_l() {
            return this.field_176693_e;
        }

        public String func_176685_d() {
            return this.field_176691_g;
        }

        static {
            field_176695_c = new EnumType[EnumType.values().length];
            EnumType[] enumTypeArray = EnumType.values();
            int n = enumTypeArray.length;
            for (int i = 0; i < n; ++i) {
                EnumType enumType;
                EnumType.field_176695_c[enumType.func_176688_a()] = enumType = enumTypeArray[i];
            }
        }
    }
}

