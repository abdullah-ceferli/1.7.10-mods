/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemClock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemKnowledgeBook;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemLilyPad;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemSimpleFoiled;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSpectralArrow;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class Item {
    public static final RegistryNamespaced<ResourceLocation, Item> field_150901_e = new RegistryNamespaced();
    private static final Map<Block, Item> field_179220_a = Maps.newHashMap();
    private static final IItemPropertyGetter field_185046_b = new IItemPropertyGetter(){

        @Override
        public float func_185085_a(ItemStack itemStack, @Nullable World world, @Nullable EntityLivingBase entityLivingBase) {
            return itemStack.func_77951_h() ? 1.0f : 0.0f;
        }
    };
    private static final IItemPropertyGetter field_185047_c = new IItemPropertyGetter(){

        @Override
        public float func_185085_a(ItemStack itemStack, @Nullable World world, @Nullable EntityLivingBase entityLivingBase) {
            return MathHelper.func_76131_a((float)itemStack.func_77952_i() / (float)itemStack.func_77958_k(), 0.0f, 1.0f);
        }
    };
    private static final IItemPropertyGetter field_185048_d = new IItemPropertyGetter(){

        @Override
        public float func_185085_a(ItemStack itemStack, @Nullable World world, @Nullable EntityLivingBase entityLivingBase) {
            return entityLivingBase == null || entityLivingBase.func_184591_cq() == EnumHandSide.RIGHT ? 0.0f : 1.0f;
        }
    };
    private static final IItemPropertyGetter field_185049_e = new IItemPropertyGetter(){

        @Override
        public float func_185085_a(ItemStack itemStack, @Nullable World world, @Nullable EntityLivingBase entityLivingBase) {
            return entityLivingBase instanceof EntityPlayer ? ((EntityPlayer)entityLivingBase).func_184811_cZ().func_185143_a(itemStack.func_77973_b(), 0.0f) : 0.0f;
        }
    };
    private final IRegistry<ResourceLocation, IItemPropertyGetter> field_185051_m = new RegistrySimple<ResourceLocation, IItemPropertyGetter>();
    protected static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID field_185050_h = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private CreativeTabs field_77701_a;
    protected static Random field_77697_d = new Random();
    protected int field_77777_bU = 64;
    private int field_77699_b;
    protected boolean field_77789_bW;
    protected boolean field_77787_bX;
    private Item field_77700_c;
    private String field_77774_bZ;

    public static int func_150891_b(Item item) {
        return item == null ? 0 : field_150901_e.func_148757_b(item);
    }

    public static Item func_150899_d(int n) {
        return field_150901_e.func_148754_a(n);
    }

    public static Item func_150898_a(Block block) {
        Item item = field_179220_a.get(block);
        return item == null ? Items.field_190931_a : item;
    }

    @Nullable
    public static Item func_111206_d(String string) {
        Item item = field_150901_e.func_82594_a(new ResourceLocation(string));
        if (item == null) {
            try {
                return Item.func_150899_d(Integer.parseInt(string));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return item;
    }

    public final void func_185043_a(ResourceLocation resourceLocation, IItemPropertyGetter iItemPropertyGetter) {
        this.field_185051_m.func_82595_a(resourceLocation, iItemPropertyGetter);
    }

    @Nullable
    public IItemPropertyGetter func_185045_a(ResourceLocation resourceLocation) {
        return this.field_185051_m.func_82594_a(resourceLocation);
    }

    public boolean func_185040_i() {
        return !this.field_185051_m.func_148742_b().isEmpty();
    }

    public boolean func_179215_a(NBTTagCompound nBTTagCompound) {
        return false;
    }

    public Item() {
        this.func_185043_a(new ResourceLocation("lefthanded"), field_185048_d);
        this.func_185043_a(new ResourceLocation("cooldown"), field_185049_e);
    }

    public Item func_77625_d(int n) {
        this.field_77777_bU = n;
        return this;
    }

    public EnumActionResult func_180614_a(EntityPlayer entityPlayer, World world, BlockPos blockPos, EnumHand enumHand, EnumFacing enumFacing, float f, float f2, float f3) {
        return EnumActionResult.PASS;
    }

    public float func_150893_a(ItemStack itemStack, IBlockState iBlockState) {
        return 1.0f;
    }

    public ActionResult<ItemStack> func_77659_a(World world, EntityPlayer entityPlayer, EnumHand enumHand) {
        return new ActionResult<ItemStack>(EnumActionResult.PASS, entityPlayer.func_184586_b(enumHand));
    }

    public ItemStack func_77654_b(ItemStack itemStack, World world, EntityLivingBase entityLivingBase) {
        return itemStack;
    }

    public int func_77639_j() {
        return this.field_77777_bU;
    }

    public int func_77647_b(int n) {
        return 0;
    }

    public boolean func_77614_k() {
        return this.field_77787_bX;
    }

    protected Item func_77627_a(boolean bl) {
        this.field_77787_bX = bl;
        return this;
    }

    public int func_77612_l() {
        return this.field_77699_b;
    }

    protected Item func_77656_e(int n) {
        this.field_77699_b = n;
        if (n > 0) {
            this.func_185043_a(new ResourceLocation("damaged"), field_185046_b);
            this.func_185043_a(new ResourceLocation("damage"), field_185047_c);
        }
        return this;
    }

    public boolean func_77645_m() {
        return this.field_77699_b > 0 && (!this.field_77787_bX || this.field_77777_bU == 1);
    }

    public boolean func_77644_a(ItemStack itemStack, EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return false;
    }

    public boolean func_179218_a(ItemStack itemStack, World world, IBlockState iBlockState, BlockPos blockPos, EntityLivingBase entityLivingBase) {
        return false;
    }

    public boolean func_150897_b(IBlockState iBlockState) {
        return false;
    }

    public boolean func_111207_a(ItemStack itemStack, EntityPlayer entityPlayer, EntityLivingBase entityLivingBase, EnumHand enumHand) {
        return false;
    }

    public Item func_77664_n() {
        this.field_77789_bW = true;
        return this;
    }

    public boolean func_77662_d() {
        return this.field_77789_bW;
    }

    public boolean func_77629_n_() {
        return false;
    }

    public Item func_77655_b(String string) {
        this.field_77774_bZ = string;
        return this;
    }

    public String func_77657_g(ItemStack itemStack) {
        return I18n.func_74838_a(this.func_77667_c(itemStack));
    }

    public String func_77658_a() {
        return "item." + this.field_77774_bZ;
    }

    public String func_77667_c(ItemStack itemStack) {
        return "item." + this.field_77774_bZ;
    }

    public Item func_77642_a(Item item) {
        this.field_77700_c = item;
        return this;
    }

    public boolean func_77651_p() {
        return true;
    }

    @Nullable
    public Item func_77668_q() {
        return this.field_77700_c;
    }

    public boolean func_77634_r() {
        return this.field_77700_c != null;
    }

    public void func_77663_a(ItemStack itemStack, World world, Entity entity, int n, boolean bl) {
    }

    public void func_77622_d(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    }

    public boolean func_77643_m_() {
        return false;
    }

    public EnumAction func_77661_b(ItemStack itemStack) {
        return EnumAction.NONE;
    }

    public int func_77626_a(ItemStack itemStack) {
        return 0;
    }

    public void func_77615_a(ItemStack itemStack, World world, EntityLivingBase entityLivingBase, int n) {
    }

    public void func_77624_a(ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag iTooltipFlag) {
    }

    public String func_77653_i(ItemStack itemStack) {
        return I18n.func_74838_a(this.func_77657_g(itemStack) + ".name").trim();
    }

    public boolean func_77636_d(ItemStack itemStack) {
        return itemStack.func_77948_v();
    }

    public EnumRarity func_77613_e(ItemStack itemStack) {
        if (itemStack.func_77948_v()) {
            return EnumRarity.RARE;
        }
        return EnumRarity.COMMON;
    }

    public boolean func_77616_k(ItemStack itemStack) {
        return this.func_77639_j() == 1 && this.func_77645_m();
    }

    protected RayTraceResult func_77621_a(World world, EntityPlayer entityPlayer, boolean bl) {
        float f = entityPlayer.field_70125_A;
        \u2603 = entityPlayer.field_70177_z;
        double \u26032 = entityPlayer.field_70165_t;
        double \u26033 = entityPlayer.field_70163_u + (double)entityPlayer.func_70047_e();
        double \u26034 = entityPlayer.field_70161_v;
        Vec3d \u26035 = new Vec3d(\u26032, \u26033, \u26034);
        \u2603 = MathHelper.func_76134_b(-\u2603 * ((float)Math.PI / 180) - (float)Math.PI);
        \u2603 = MathHelper.func_76126_a(-\u2603 * ((float)Math.PI / 180) - (float)Math.PI);
        \u2603 = -MathHelper.func_76134_b(-f * ((float)Math.PI / 180));
        \u2603 = MathHelper.func_76126_a(-f * ((float)Math.PI / 180));
        \u2603 = \u2603 * \u2603;
        \u2603 = \u2603;
        \u2603 = \u2603 * \u2603;
        double \u26036 = 5.0;
        Vec3d \u26037 = \u26035.func_72441_c((double)\u2603 * 5.0, (double)\u2603 * 5.0, (double)\u2603 * 5.0);
        return world.func_147447_a(\u26035, \u26037, bl, !bl, false);
    }

    public int func_77619_b() {
        return 0;
    }

    public void func_150895_a(CreativeTabs creativeTabs, NonNullList<ItemStack> nonNullList) {
        if (this.func_194125_a(creativeTabs)) {
            nonNullList.add(new ItemStack(this));
        }
    }

    protected boolean func_194125_a(CreativeTabs creativeTabs) {
        \u2603 = this.func_77640_w();
        return \u2603 != null && (creativeTabs == CreativeTabs.field_78027_g || creativeTabs == \u2603);
    }

    @Nullable
    public CreativeTabs func_77640_w() {
        return this.field_77701_a;
    }

    public Item func_77637_a(CreativeTabs creativeTabs) {
        this.field_77701_a = creativeTabs;
        return this;
    }

    public boolean func_82788_x() {
        return false;
    }

    public boolean func_82789_a(ItemStack itemStack, ItemStack itemStack2) {
        return false;
    }

    public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot entityEquipmentSlot) {
        return HashMultimap.create();
    }

    public static void func_150900_l() {
        Item.func_179214_a(Blocks.field_150350_a, new ItemAir(Blocks.field_150350_a));
        Item.func_179214_a(Blocks.field_150348_b, new ItemMultiTexture(Blocks.field_150348_b, Blocks.field_150348_b, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockStone.EnumType.func_176643_a(itemStack.func_77960_j()).func_176644_c();
            }
        }).func_77655_b("stone"));
        Item.func_179214_a(Blocks.field_150349_c, new ItemColored(Blocks.field_150349_c, false));
        Item.func_179214_a(Blocks.field_150346_d, new ItemMultiTexture(Blocks.field_150346_d, Blocks.field_150346_d, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockDirt.DirtType.func_176924_a(itemStack.func_77960_j()).func_176927_c();
            }
        }).func_77655_b("dirt"));
        Item.func_179216_c(Blocks.field_150347_e);
        Item.func_179214_a(Blocks.field_150344_f, new ItemMultiTexture(Blocks.field_150344_f, Blocks.field_150344_f, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockPlanks.EnumType.func_176837_a(itemStack.func_77960_j()).func_176840_c();
            }
        }).func_77655_b("wood"));
        Item.func_179214_a(Blocks.field_150345_g, new ItemMultiTexture(Blocks.field_150345_g, Blocks.field_150345_g, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockPlanks.EnumType.func_176837_a(itemStack.func_77960_j()).func_176840_c();
            }
        }).func_77655_b("sapling"));
        Item.func_179216_c(Blocks.field_150357_h);
        Item.func_179214_a(Blocks.field_150354_m, new ItemMultiTexture((Block)Blocks.field_150354_m, (Block)Blocks.field_150354_m, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockSand.EnumType.func_176686_a(itemStack.func_77960_j()).func_176685_d();
            }
        }).func_77655_b("sand"));
        Item.func_179216_c(Blocks.field_150351_n);
        Item.func_179216_c(Blocks.field_150352_o);
        Item.func_179216_c(Blocks.field_150366_p);
        Item.func_179216_c(Blocks.field_150365_q);
        Item.func_179214_a(Blocks.field_150364_r, new ItemMultiTexture(Blocks.field_150364_r, Blocks.field_150364_r, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockPlanks.EnumType.func_176837_a(itemStack.func_77960_j()).func_176840_c();
            }
        }).func_77655_b("log"));
        Item.func_179214_a(Blocks.field_150363_s, new ItemMultiTexture(Blocks.field_150363_s, Blocks.field_150363_s, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockPlanks.EnumType.func_176837_a(itemStack.func_77960_j() + 4).func_176840_c();
            }
        }).func_77655_b("log"));
        Item.func_179214_a(Blocks.field_150362_t, new ItemLeaves(Blocks.field_150362_t).func_77655_b("leaves"));
        Item.func_179214_a(Blocks.field_150361_u, new ItemLeaves(Blocks.field_150361_u).func_77655_b("leaves"));
        Item.func_179214_a(Blocks.field_150360_v, new ItemMultiTexture(Blocks.field_150360_v, Blocks.field_150360_v, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return (itemStack.func_77960_j() & 1) == 1 ? "wet" : "dry";
            }
        }).func_77655_b("sponge"));
        Item.func_179216_c(Blocks.field_150359_w);
        Item.func_179216_c(Blocks.field_150369_x);
        Item.func_179216_c(Blocks.field_150368_y);
        Item.func_179216_c(Blocks.field_150367_z);
        Item.func_179214_a(Blocks.field_150322_A, new ItemMultiTexture(Blocks.field_150322_A, Blocks.field_150322_A, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockSandStone.EnumType.func_176673_a(itemStack.func_77960_j()).func_176676_c();
            }
        }).func_77655_b("sandStone"));
        Item.func_179216_c(Blocks.field_150323_B);
        Item.func_179216_c(Blocks.field_150318_D);
        Item.func_179216_c(Blocks.field_150319_E);
        Item.func_179214_a(Blocks.field_150320_F, new ItemPiston(Blocks.field_150320_F));
        Item.func_179216_c(Blocks.field_150321_G);
        Item.func_179214_a(Blocks.field_150329_H, new ItemColored(Blocks.field_150329_H, true).func_150943_a(new String[]{"shrub", "grass", "fern"}));
        Item.func_179216_c(Blocks.field_150330_I);
        Item.func_179214_a(Blocks.field_150331_J, new ItemPiston(Blocks.field_150331_J));
        Item.func_179214_a(Blocks.field_150325_L, new ItemCloth(Blocks.field_150325_L).func_77655_b("cloth"));
        Item.func_179214_a(Blocks.field_150327_N, new ItemMultiTexture((Block)Blocks.field_150327_N, (Block)Blocks.field_150327_N, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockFlower.EnumFlowerType.func_176967_a(BlockFlower.EnumFlowerColor.YELLOW, itemStack.func_77960_j()).func_176963_d();
            }
        }).func_77655_b("flower"));
        Item.func_179214_a(Blocks.field_150328_O, new ItemMultiTexture((Block)Blocks.field_150328_O, (Block)Blocks.field_150328_O, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockFlower.EnumFlowerType.func_176967_a(BlockFlower.EnumFlowerColor.RED, itemStack.func_77960_j()).func_176963_d();
            }
        }).func_77655_b("rose"));
        Item.func_179216_c(Blocks.field_150338_P);
        Item.func_179216_c(Blocks.field_150337_Q);
        Item.func_179216_c(Blocks.field_150340_R);
        Item.func_179216_c(Blocks.field_150339_S);
        Item.func_179214_a(Blocks.field_150333_U, new ItemSlab(Blocks.field_150333_U, Blocks.field_150333_U, Blocks.field_150334_T).func_77655_b("stoneSlab"));
        Item.func_179216_c(Blocks.field_150336_V);
        Item.func_179216_c(Blocks.field_150335_W);
        Item.func_179216_c(Blocks.field_150342_X);
        Item.func_179216_c(Blocks.field_150341_Y);
        Item.func_179216_c(Blocks.field_150343_Z);
        Item.func_179216_c(Blocks.field_150478_aa);
        Item.func_179216_c(Blocks.field_185764_cQ);
        Item.func_179216_c(Blocks.field_185765_cR);
        Item.func_179216_c(Blocks.field_185766_cS);
        Item.func_179216_c(Blocks.field_185767_cT);
        Item.func_179216_c(Blocks.field_185768_cU);
        Item.func_179216_c(Blocks.field_185769_cV);
        Item.func_179214_a(Blocks.field_185771_cX, new ItemSlab(Blocks.field_185771_cX, Blocks.field_185771_cX, Blocks.field_185770_cW).func_77655_b("purpurSlab"));
        Item.func_179216_c(Blocks.field_150474_ac);
        Item.func_179216_c(Blocks.field_150476_ad);
        Item.func_179216_c(Blocks.field_150486_ae);
        Item.func_179216_c(Blocks.field_150482_ag);
        Item.func_179216_c(Blocks.field_150484_ah);
        Item.func_179216_c(Blocks.field_150462_ai);
        Item.func_179216_c(Blocks.field_150458_ak);
        Item.func_179216_c(Blocks.field_150460_al);
        Item.func_179216_c(Blocks.field_150468_ap);
        Item.func_179216_c(Blocks.field_150448_aq);
        Item.func_179216_c(Blocks.field_150446_ar);
        Item.func_179216_c(Blocks.field_150442_at);
        Item.func_179216_c(Blocks.field_150456_au);
        Item.func_179216_c(Blocks.field_150452_aw);
        Item.func_179216_c(Blocks.field_150450_ax);
        Item.func_179216_c(Blocks.field_150429_aA);
        Item.func_179216_c(Blocks.field_150430_aB);
        Item.func_179214_a(Blocks.field_150431_aC, new ItemSnow(Blocks.field_150431_aC));
        Item.func_179216_c(Blocks.field_150432_aD);
        Item.func_179216_c(Blocks.field_150433_aE);
        Item.func_179216_c(Blocks.field_150434_aF);
        Item.func_179216_c(Blocks.field_150435_aG);
        Item.func_179216_c(Blocks.field_150421_aI);
        Item.func_179216_c(Blocks.field_180407_aO);
        Item.func_179216_c(Blocks.field_180408_aP);
        Item.func_179216_c(Blocks.field_180404_aQ);
        Item.func_179216_c(Blocks.field_180403_aR);
        Item.func_179216_c(Blocks.field_180406_aS);
        Item.func_179216_c(Blocks.field_180405_aT);
        Item.func_179216_c(Blocks.field_150423_aK);
        Item.func_179216_c(Blocks.field_150424_aL);
        Item.func_179216_c(Blocks.field_150425_aM);
        Item.func_179216_c(Blocks.field_150426_aN);
        Item.func_179216_c(Blocks.field_150428_aP);
        Item.func_179216_c(Blocks.field_150415_aT);
        Item.func_179214_a(Blocks.field_150418_aU, new ItemMultiTexture(Blocks.field_150418_aU, Blocks.field_150418_aU, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockSilverfish.EnumType.func_176879_a(itemStack.func_77960_j()).func_176882_c();
            }
        }).func_77655_b("monsterStoneEgg"));
        Item.func_179214_a(Blocks.field_150417_aV, new ItemMultiTexture(Blocks.field_150417_aV, Blocks.field_150417_aV, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockStoneBrick.EnumType.func_176613_a(itemStack.func_77960_j()).func_176614_c();
            }
        }).func_77655_b("stonebricksmooth"));
        Item.func_179216_c(Blocks.field_150420_aW);
        Item.func_179216_c(Blocks.field_150419_aX);
        Item.func_179216_c(Blocks.field_150411_aY);
        Item.func_179216_c(Blocks.field_150410_aZ);
        Item.func_179216_c(Blocks.field_150440_ba);
        Item.func_179214_a(Blocks.field_150395_bd, new ItemColored(Blocks.field_150395_bd, false));
        Item.func_179216_c(Blocks.field_180390_bo);
        Item.func_179216_c(Blocks.field_180391_bp);
        Item.func_179216_c(Blocks.field_180392_bq);
        Item.func_179216_c(Blocks.field_180386_br);
        Item.func_179216_c(Blocks.field_180385_bs);
        Item.func_179216_c(Blocks.field_180387_bt);
        Item.func_179216_c(Blocks.field_150389_bf);
        Item.func_179216_c(Blocks.field_150390_bg);
        Item.func_179216_c(Blocks.field_150391_bh);
        Item.func_179214_a(Blocks.field_150392_bi, new ItemLilyPad(Blocks.field_150392_bi));
        Item.func_179216_c(Blocks.field_150385_bj);
        Item.func_179216_c(Blocks.field_150386_bk);
        Item.func_179216_c(Blocks.field_150387_bl);
        Item.func_179216_c(Blocks.field_150381_bn);
        Item.func_179216_c(Blocks.field_150378_br);
        Item.func_179216_c(Blocks.field_150377_bs);
        Item.func_179216_c(Blocks.field_185772_cY);
        Item.func_179216_c(Blocks.field_150380_bt);
        Item.func_179216_c(Blocks.field_150379_bu);
        Item.func_179214_a(Blocks.field_150376_bx, new ItemSlab(Blocks.field_150376_bx, Blocks.field_150376_bx, Blocks.field_150373_bw).func_77655_b("woodSlab"));
        Item.func_179216_c(Blocks.field_150372_bz);
        Item.func_179216_c(Blocks.field_150412_bA);
        Item.func_179216_c(Blocks.field_150477_bB);
        Item.func_179216_c(Blocks.field_150479_bC);
        Item.func_179216_c(Blocks.field_150475_bE);
        Item.func_179216_c(Blocks.field_150485_bF);
        Item.func_179216_c(Blocks.field_150487_bG);
        Item.func_179216_c(Blocks.field_150481_bH);
        Item.func_179216_c(Blocks.field_150483_bI);
        Item.func_179216_c(Blocks.field_150461_bJ);
        Item.func_179214_a(Blocks.field_150463_bK, new ItemMultiTexture(Blocks.field_150463_bK, Blocks.field_150463_bK, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockWall.EnumType.func_176660_a(itemStack.func_77960_j()).func_176659_c();
            }
        }).func_77655_b("cobbleWall"));
        Item.func_179216_c(Blocks.field_150471_bO);
        Item.func_179214_a(Blocks.field_150467_bQ, new ItemAnvilBlock(Blocks.field_150467_bQ).func_77655_b("anvil"));
        Item.func_179216_c(Blocks.field_150447_bR);
        Item.func_179216_c(Blocks.field_150445_bS);
        Item.func_179216_c(Blocks.field_150443_bT);
        Item.func_179216_c(Blocks.field_150453_bW);
        Item.func_179216_c(Blocks.field_150451_bX);
        Item.func_179216_c(Blocks.field_150449_bY);
        Item.func_179216_c(Blocks.field_150438_bZ);
        Item.func_179214_a(Blocks.field_150371_ca, new ItemMultiTexture(Blocks.field_150371_ca, Blocks.field_150371_ca, new String[]{"default", "chiseled", "lines"}).func_77655_b("quartzBlock"));
        Item.func_179216_c(Blocks.field_150370_cb);
        Item.func_179216_c(Blocks.field_150408_cc);
        Item.func_179216_c(Blocks.field_150409_cd);
        Item.func_179214_a(Blocks.field_150406_ce, new ItemCloth(Blocks.field_150406_ce).func_77655_b("clayHardenedStained"));
        Item.func_179216_c(Blocks.field_180401_cv);
        Item.func_179216_c(Blocks.field_180400_cw);
        Item.func_179216_c(Blocks.field_150407_cf);
        Item.func_179214_a(Blocks.field_150404_cg, new ItemCloth(Blocks.field_150404_cg).func_77655_b("woolCarpet"));
        Item.func_179216_c(Blocks.field_150405_ch);
        Item.func_179216_c(Blocks.field_150402_ci);
        Item.func_179216_c(Blocks.field_150403_cj);
        Item.func_179216_c(Blocks.field_150400_ck);
        Item.func_179216_c(Blocks.field_150401_cl);
        Item.func_179216_c(Blocks.field_180399_cE);
        Item.func_179216_c(Blocks.field_185774_da);
        Item.func_179214_a(Blocks.field_150398_cm, new ItemMultiTexture((Block)Blocks.field_150398_cm, (Block)Blocks.field_150398_cm, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockDoublePlant.EnumPlantType.func_176938_a(itemStack.func_77960_j()).func_176939_c();
            }
        }).func_77655_b("doublePlant"));
        Item.func_179214_a(Blocks.field_150399_cn, new ItemCloth(Blocks.field_150399_cn).func_77655_b("stainedGlass"));
        Item.func_179214_a(Blocks.field_150397_co, new ItemCloth(Blocks.field_150397_co).func_77655_b("stainedGlassPane"));
        Item.func_179214_a(Blocks.field_180397_cI, new ItemMultiTexture(Blocks.field_180397_cI, Blocks.field_180397_cI, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockPrismarine.EnumType.func_176810_a(itemStack.func_77960_j()).func_176809_c();
            }
        }).func_77655_b("prismarine"));
        Item.func_179216_c(Blocks.field_180398_cJ);
        Item.func_179214_a(Blocks.field_180395_cM, new ItemMultiTexture(Blocks.field_180395_cM, Blocks.field_180395_cM, new ItemMultiTexture.Mapper(){

            @Override
            public String apply(ItemStack itemStack) {
                return BlockRedSandstone.EnumType.func_176825_a(itemStack.func_77960_j()).func_176828_c();
            }
        }).func_77655_b("redSandStone"));
        Item.func_179216_c(Blocks.field_180396_cN);
        Item.func_179214_a(Blocks.field_180389_cP, new ItemSlab(Blocks.field_180389_cP, Blocks.field_180389_cP, Blocks.field_180388_cO).func_77655_b("stoneSlab2"));
        Item.func_179216_c(Blocks.field_185776_dc);
        Item.func_179216_c(Blocks.field_185777_dd);
        Item.func_179216_c(Blocks.field_189877_df);
        Item.func_179216_c(Blocks.field_189878_dg);
        Item.func_179216_c(Blocks.field_189879_dh);
        Item.func_179216_c(Blocks.field_189880_di);
        Item.func_179216_c(Blocks.field_189881_dj);
        Item.func_179216_c(Blocks.field_190976_dk);
        Item.func_179214_a(Blocks.field_190977_dl, new ItemShulkerBox(Blocks.field_190977_dl));
        Item.func_179214_a(Blocks.field_190978_dm, new ItemShulkerBox(Blocks.field_190978_dm));
        Item.func_179214_a(Blocks.field_190979_dn, new ItemShulkerBox(Blocks.field_190979_dn));
        Item.func_179214_a(Blocks.field_190980_do, new ItemShulkerBox(Blocks.field_190980_do));
        Item.func_179214_a(Blocks.field_190981_dp, new ItemShulkerBox(Blocks.field_190981_dp));
        Item.func_179214_a(Blocks.field_190982_dq, new ItemShulkerBox(Blocks.field_190982_dq));
        Item.func_179214_a(Blocks.field_190983_dr, new ItemShulkerBox(Blocks.field_190983_dr));
        Item.func_179214_a(Blocks.field_190984_ds, new ItemShulkerBox(Blocks.field_190984_ds));
        Item.func_179214_a(Blocks.field_190985_dt, new ItemShulkerBox(Blocks.field_190985_dt));
        Item.func_179214_a(Blocks.field_190986_du, new ItemShulkerBox(Blocks.field_190986_du));
        Item.func_179214_a(Blocks.field_190987_dv, new ItemShulkerBox(Blocks.field_190987_dv));
        Item.func_179214_a(Blocks.field_190988_dw, new ItemShulkerBox(Blocks.field_190988_dw));
        Item.func_179214_a(Blocks.field_190989_dx, new ItemShulkerBox(Blocks.field_190989_dx));
        Item.func_179214_a(Blocks.field_190990_dy, new ItemShulkerBox(Blocks.field_190990_dy));
        Item.func_179214_a(Blocks.field_190991_dz, new ItemShulkerBox(Blocks.field_190991_dz));
        Item.func_179214_a(Blocks.field_190975_dA, new ItemShulkerBox(Blocks.field_190975_dA));
        Item.func_179216_c(Blocks.field_192427_dB);
        Item.func_179216_c(Blocks.field_192428_dC);
        Item.func_179216_c(Blocks.field_192429_dD);
        Item.func_179216_c(Blocks.field_192430_dE);
        Item.func_179216_c(Blocks.field_192431_dF);
        Item.func_179216_c(Blocks.field_192432_dG);
        Item.func_179216_c(Blocks.field_192433_dH);
        Item.func_179216_c(Blocks.field_192434_dI);
        Item.func_179216_c(Blocks.field_192435_dJ);
        Item.func_179216_c(Blocks.field_192436_dK);
        Item.func_179216_c(Blocks.field_192437_dL);
        Item.func_179216_c(Blocks.field_192438_dM);
        Item.func_179216_c(Blocks.field_192439_dN);
        Item.func_179216_c(Blocks.field_192440_dO);
        Item.func_179216_c(Blocks.field_192441_dP);
        Item.func_179216_c(Blocks.field_192442_dQ);
        Item.func_179214_a(Blocks.field_192443_dR, new ItemCloth(Blocks.field_192443_dR).func_77655_b("concrete"));
        Item.func_179214_a(Blocks.field_192444_dS, new ItemCloth(Blocks.field_192444_dS).func_77655_b("concrete_powder"));
        Item.func_179216_c(Blocks.field_185779_df);
        Item.func_179217_a(256, "iron_shovel", new ItemSpade(ToolMaterial.IRON).func_77655_b("shovelIron"));
        Item.func_179217_a(257, "iron_pickaxe", new ItemPickaxe(ToolMaterial.IRON).func_77655_b("pickaxeIron"));
        Item.func_179217_a(258, "iron_axe", new ItemAxe(ToolMaterial.IRON).func_77655_b("hatchetIron"));
        Item.func_179217_a(259, "flint_and_steel", new ItemFlintAndSteel().func_77655_b("flintAndSteel"));
        Item.func_179217_a(260, "apple", new ItemFood(4, 0.3f, false).func_77655_b("apple"));
        Item.func_179217_a(261, "bow", new ItemBow().func_77655_b("bow"));
        Item.func_179217_a(262, "arrow", new ItemArrow().func_77655_b("arrow"));
        Item.func_179217_a(263, "coal", new ItemCoal().func_77655_b("coal"));
        Item.func_179217_a(264, "diamond", new Item().func_77655_b("diamond").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(265, "iron_ingot", new Item().func_77655_b("ingotIron").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(266, "gold_ingot", new Item().func_77655_b("ingotGold").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(267, "iron_sword", new ItemSword(ToolMaterial.IRON).func_77655_b("swordIron"));
        Item.func_179217_a(268, "wooden_sword", new ItemSword(ToolMaterial.WOOD).func_77655_b("swordWood"));
        Item.func_179217_a(269, "wooden_shovel", new ItemSpade(ToolMaterial.WOOD).func_77655_b("shovelWood"));
        Item.func_179217_a(270, "wooden_pickaxe", new ItemPickaxe(ToolMaterial.WOOD).func_77655_b("pickaxeWood"));
        Item.func_179217_a(271, "wooden_axe", new ItemAxe(ToolMaterial.WOOD).func_77655_b("hatchetWood"));
        Item.func_179217_a(272, "stone_sword", new ItemSword(ToolMaterial.STONE).func_77655_b("swordStone"));
        Item.func_179217_a(273, "stone_shovel", new ItemSpade(ToolMaterial.STONE).func_77655_b("shovelStone"));
        Item.func_179217_a(274, "stone_pickaxe", new ItemPickaxe(ToolMaterial.STONE).func_77655_b("pickaxeStone"));
        Item.func_179217_a(275, "stone_axe", new ItemAxe(ToolMaterial.STONE).func_77655_b("hatchetStone"));
        Item.func_179217_a(276, "diamond_sword", new ItemSword(ToolMaterial.DIAMOND).func_77655_b("swordDiamond"));
        Item.func_179217_a(277, "diamond_shovel", new ItemSpade(ToolMaterial.DIAMOND).func_77655_b("shovelDiamond"));
        Item.func_179217_a(278, "diamond_pickaxe", new ItemPickaxe(ToolMaterial.DIAMOND).func_77655_b("pickaxeDiamond"));
        Item.func_179217_a(279, "diamond_axe", new ItemAxe(ToolMaterial.DIAMOND).func_77655_b("hatchetDiamond"));
        Item.func_179217_a(280, "stick", new Item().func_77664_n().func_77655_b("stick").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(281, "bowl", new Item().func_77655_b("bowl").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(282, "mushroom_stew", new ItemSoup(6).func_77655_b("mushroomStew"));
        Item.func_179217_a(283, "golden_sword", new ItemSword(ToolMaterial.GOLD).func_77655_b("swordGold"));
        Item.func_179217_a(284, "golden_shovel", new ItemSpade(ToolMaterial.GOLD).func_77655_b("shovelGold"));
        Item.func_179217_a(285, "golden_pickaxe", new ItemPickaxe(ToolMaterial.GOLD).func_77655_b("pickaxeGold"));
        Item.func_179217_a(286, "golden_axe", new ItemAxe(ToolMaterial.GOLD).func_77655_b("hatchetGold"));
        Item.func_179217_a(287, "string", new ItemBlockSpecial(Blocks.field_150473_bD).func_77655_b("string").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(288, "feather", new Item().func_77655_b("feather").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(289, "gunpowder", new Item().func_77655_b("sulphur").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(290, "wooden_hoe", new ItemHoe(ToolMaterial.WOOD).func_77655_b("hoeWood"));
        Item.func_179217_a(291, "stone_hoe", new ItemHoe(ToolMaterial.STONE).func_77655_b("hoeStone"));
        Item.func_179217_a(292, "iron_hoe", new ItemHoe(ToolMaterial.IRON).func_77655_b("hoeIron"));
        Item.func_179217_a(293, "diamond_hoe", new ItemHoe(ToolMaterial.DIAMOND).func_77655_b("hoeDiamond"));
        Item.func_179217_a(294, "golden_hoe", new ItemHoe(ToolMaterial.GOLD).func_77655_b("hoeGold"));
        Item.func_179217_a(295, "wheat_seeds", new ItemSeeds(Blocks.field_150464_aj, Blocks.field_150458_ak).func_77655_b("seeds"));
        Item.func_179217_a(296, "wheat", new Item().func_77655_b("wheat").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(297, "bread", new ItemFood(5, 0.6f, false).func_77655_b("bread"));
        Item.func_179217_a(298, "leather_helmet", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD).func_77655_b("helmetCloth"));
        Item.func_179217_a(299, "leather_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST).func_77655_b("chestplateCloth"));
        Item.func_179217_a(300, "leather_leggings", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.LEGS).func_77655_b("leggingsCloth"));
        Item.func_179217_a(301, "leather_boots", new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET).func_77655_b("bootsCloth"));
        Item.func_179217_a(302, "chainmail_helmet", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.HEAD).func_77655_b("helmetChain"));
        Item.func_179217_a(303, "chainmail_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.CHEST).func_77655_b("chestplateChain"));
        Item.func_179217_a(304, "chainmail_leggings", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.LEGS).func_77655_b("leggingsChain"));
        Item.func_179217_a(305, "chainmail_boots", new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.FEET).func_77655_b("bootsChain"));
        Item.func_179217_a(306, "iron_helmet", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.HEAD).func_77655_b("helmetIron"));
        Item.func_179217_a(307, "iron_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.CHEST).func_77655_b("chestplateIron"));
        Item.func_179217_a(308, "iron_leggings", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.LEGS).func_77655_b("leggingsIron"));
        Item.func_179217_a(309, "iron_boots", new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.FEET).func_77655_b("bootsIron"));
        Item.func_179217_a(310, "diamond_helmet", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.HEAD).func_77655_b("helmetDiamond"));
        Item.func_179217_a(311, "diamond_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.CHEST).func_77655_b("chestplateDiamond"));
        Item.func_179217_a(312, "diamond_leggings", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.LEGS).func_77655_b("leggingsDiamond"));
        Item.func_179217_a(313, "diamond_boots", new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.FEET).func_77655_b("bootsDiamond"));
        Item.func_179217_a(314, "golden_helmet", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.HEAD).func_77655_b("helmetGold"));
        Item.func_179217_a(315, "golden_chestplate", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.CHEST).func_77655_b("chestplateGold"));
        Item.func_179217_a(316, "golden_leggings", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.LEGS).func_77655_b("leggingsGold"));
        Item.func_179217_a(317, "golden_boots", new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.FEET).func_77655_b("bootsGold"));
        Item.func_179217_a(318, "flint", new Item().func_77655_b("flint").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(319, "porkchop", new ItemFood(3, 0.3f, true).func_77655_b("porkchopRaw"));
        Item.func_179217_a(320, "cooked_porkchop", new ItemFood(8, 0.8f, true).func_77655_b("porkchopCooked"));
        Item.func_179217_a(321, "painting", new ItemHangingEntity(EntityPainting.class).func_77655_b("painting"));
        Item.func_179217_a(322, "golden_apple", new ItemAppleGold(4, 1.2f, false).func_77848_i().func_77655_b("appleGold"));
        Item.func_179217_a(323, "sign", new ItemSign().func_77655_b("sign"));
        Item.func_179217_a(324, "wooden_door", new ItemDoor(Blocks.field_180413_ao).func_77655_b("doorOak"));
        Item item = new ItemBucket(Blocks.field_150350_a).func_77655_b("bucket").func_77625_d(16);
        Item.func_179217_a(325, "bucket", item);
        Item.func_179217_a(326, "water_bucket", new ItemBucket(Blocks.field_150358_i).func_77655_b("bucketWater").func_77642_a(item));
        Item.func_179217_a(327, "lava_bucket", new ItemBucket(Blocks.field_150356_k).func_77655_b("bucketLava").func_77642_a(item));
        Item.func_179217_a(328, "minecart", new ItemMinecart(EntityMinecart.Type.RIDEABLE).func_77655_b("minecart"));
        Item.func_179217_a(329, "saddle", new ItemSaddle().func_77655_b("saddle"));
        Item.func_179217_a(330, "iron_door", new ItemDoor(Blocks.field_150454_av).func_77655_b("doorIron"));
        Item.func_179217_a(331, "redstone", new ItemRedstone().func_77655_b("redstone"));
        Item.func_179217_a(332, "snowball", new ItemSnowball().func_77655_b("snowball"));
        Item.func_179217_a(333, "boat", new ItemBoat(EntityBoat.Type.OAK));
        Item.func_179217_a(334, "leather", new Item().func_77655_b("leather").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(335, "milk_bucket", new ItemBucketMilk().func_77655_b("milk").func_77642_a(item));
        Item.func_179217_a(336, "brick", new Item().func_77655_b("brick").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(337, "clay_ball", new Item().func_77655_b("clay").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(338, "reeds", new ItemBlockSpecial(Blocks.field_150436_aH).func_77655_b("reeds").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(339, "paper", new Item().func_77655_b("paper").func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(340, "book", new ItemBook().func_77655_b("book").func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(341, "slime_ball", new Item().func_77655_b("slimeball").func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(342, "chest_minecart", new ItemMinecart(EntityMinecart.Type.CHEST).func_77655_b("minecartChest"));
        Item.func_179217_a(343, "furnace_minecart", new ItemMinecart(EntityMinecart.Type.FURNACE).func_77655_b("minecartFurnace"));
        Item.func_179217_a(344, "egg", new ItemEgg().func_77655_b("egg"));
        Item.func_179217_a(345, "compass", new ItemCompass().func_77655_b("compass").func_77637_a(CreativeTabs.field_78040_i));
        Item.func_179217_a(346, "fishing_rod", new ItemFishingRod().func_77655_b("fishingRod"));
        Item.func_179217_a(347, "clock", new ItemClock().func_77655_b("clock").func_77637_a(CreativeTabs.field_78040_i));
        Item.func_179217_a(348, "glowstone_dust", new Item().func_77655_b("yellowDust").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(349, "fish", new ItemFishFood(false).func_77655_b("fish").func_77627_a(true));
        Item.func_179217_a(350, "cooked_fish", new ItemFishFood(true).func_77655_b("fish").func_77627_a(true));
        Item.func_179217_a(351, "dye", new ItemDye().func_77655_b("dyePowder"));
        Item.func_179217_a(352, "bone", new Item().func_77655_b("bone").func_77664_n().func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(353, "sugar", new Item().func_77655_b("sugar").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(354, "cake", new ItemBlockSpecial(Blocks.field_150414_aQ).func_77625_d(1).func_77655_b("cake").func_77637_a(CreativeTabs.field_78039_h));
        Item.func_179217_a(355, "bed", new ItemBed().func_77625_d(1).func_77655_b("bed"));
        Item.func_179217_a(356, "repeater", new ItemBlockSpecial(Blocks.field_150413_aR).func_77655_b("diode").func_77637_a(CreativeTabs.field_78028_d));
        Item.func_179217_a(357, "cookie", new ItemFood(2, 0.1f, false).func_77655_b("cookie"));
        Item.func_179217_a(358, "filled_map", new ItemMap().func_77655_b("map"));
        Item.func_179217_a(359, "shears", new ItemShears().func_77655_b("shears"));
        Item.func_179217_a(360, "melon", new ItemFood(2, 0.3f, false).func_77655_b("melon"));
        Item.func_179217_a(361, "pumpkin_seeds", new ItemSeeds(Blocks.field_150393_bb, Blocks.field_150458_ak).func_77655_b("seeds_pumpkin"));
        Item.func_179217_a(362, "melon_seeds", new ItemSeeds(Blocks.field_150394_bc, Blocks.field_150458_ak).func_77655_b("seeds_melon"));
        Item.func_179217_a(363, "beef", new ItemFood(3, 0.3f, true).func_77655_b("beefRaw"));
        Item.func_179217_a(364, "cooked_beef", new ItemFood(8, 0.8f, true).func_77655_b("beefCooked"));
        Item.func_179217_a(365, "chicken", new ItemFood(2, 0.3f, true).func_185070_a(new PotionEffect(MobEffects.field_76438_s, 600, 0), 0.3f).func_77655_b("chickenRaw"));
        Item.func_179217_a(366, "cooked_chicken", new ItemFood(6, 0.6f, true).func_77655_b("chickenCooked"));
        Item.func_179217_a(367, "rotten_flesh", new ItemFood(4, 0.1f, true).func_185070_a(new PotionEffect(MobEffects.field_76438_s, 600, 0), 0.8f).func_77655_b("rottenFlesh"));
        Item.func_179217_a(368, "ender_pearl", new ItemEnderPearl().func_77655_b("enderPearl"));
        Item.func_179217_a(369, "blaze_rod", new Item().func_77655_b("blazeRod").func_77637_a(CreativeTabs.field_78035_l).func_77664_n());
        Item.func_179217_a(370, "ghast_tear", new Item().func_77655_b("ghastTear").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(371, "gold_nugget", new Item().func_77655_b("goldNugget").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(372, "nether_wart", new ItemSeeds(Blocks.field_150388_bm, Blocks.field_150425_aM).func_77655_b("netherStalkSeeds"));
        Item.func_179217_a(373, "potion", new ItemPotion().func_77655_b("potion"));
        \u2603 = new ItemGlassBottle().func_77655_b("glassBottle");
        Item.func_179217_a(374, "glass_bottle", \u2603);
        Item.func_179217_a(375, "spider_eye", new ItemFood(2, 0.8f, false).func_185070_a(new PotionEffect(MobEffects.field_76436_u, 100, 0), 1.0f).func_77655_b("spiderEye"));
        Item.func_179217_a(376, "fermented_spider_eye", new Item().func_77655_b("fermentedSpiderEye").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(377, "blaze_powder", new Item().func_77655_b("blazePowder").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(378, "magma_cream", new Item().func_77655_b("magmaCream").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(379, "brewing_stand", new ItemBlockSpecial(Blocks.field_150382_bo).func_77655_b("brewingStand").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(380, "cauldron", new ItemBlockSpecial(Blocks.field_150383_bp).func_77655_b("cauldron").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(381, "ender_eye", new ItemEnderEye().func_77655_b("eyeOfEnder"));
        Item.func_179217_a(382, "speckled_melon", new Item().func_77655_b("speckledMelon").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(383, "spawn_egg", new ItemMonsterPlacer().func_77655_b("monsterPlacer"));
        Item.func_179217_a(384, "experience_bottle", new ItemExpBottle().func_77655_b("expBottle"));
        Item.func_179217_a(385, "fire_charge", new ItemFireball().func_77655_b("fireball"));
        Item.func_179217_a(386, "writable_book", new ItemWritableBook().func_77655_b("writingBook").func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(387, "written_book", new ItemWrittenBook().func_77655_b("writtenBook").func_77625_d(16));
        Item.func_179217_a(388, "emerald", new Item().func_77655_b("emerald").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(389, "item_frame", new ItemHangingEntity(EntityItemFrame.class).func_77655_b("frame"));
        Item.func_179217_a(390, "flower_pot", new ItemBlockSpecial(Blocks.field_150457_bL).func_77655_b("flowerPot").func_77637_a(CreativeTabs.field_78031_c));
        Item.func_179217_a(391, "carrot", new ItemSeedFood(3, 0.6f, Blocks.field_150459_bM, Blocks.field_150458_ak).func_77655_b("carrots"));
        Item.func_179217_a(392, "potato", new ItemSeedFood(1, 0.3f, Blocks.field_150469_bN, Blocks.field_150458_ak).func_77655_b("potato"));
        Item.func_179217_a(393, "baked_potato", new ItemFood(5, 0.6f, false).func_77655_b("potatoBaked"));
        Item.func_179217_a(394, "poisonous_potato", new ItemFood(2, 0.3f, false).func_185070_a(new PotionEffect(MobEffects.field_76436_u, 100, 0), 0.6f).func_77655_b("potatoPoisonous"));
        Item.func_179217_a(395, "map", new ItemEmptyMap().func_77655_b("emptyMap"));
        Item.func_179217_a(396, "golden_carrot", new ItemFood(6, 1.2f, false).func_77655_b("carrotGolden").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(397, "skull", new ItemSkull().func_77655_b("skull"));
        Item.func_179217_a(398, "carrot_on_a_stick", new ItemCarrotOnAStick().func_77655_b("carrotOnAStick"));
        Item.func_179217_a(399, "nether_star", new ItemSimpleFoiled().func_77655_b("netherStar").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(400, "pumpkin_pie", new ItemFood(8, 0.3f, false).func_77655_b("pumpkinPie").func_77637_a(CreativeTabs.field_78039_h));
        Item.func_179217_a(401, "fireworks", new ItemFirework().func_77655_b("fireworks"));
        Item.func_179217_a(402, "firework_charge", new ItemFireworkCharge().func_77655_b("fireworksCharge").func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(403, "enchanted_book", new ItemEnchantedBook().func_77625_d(1).func_77655_b("enchantedBook"));
        Item.func_179217_a(404, "comparator", new ItemBlockSpecial(Blocks.field_150441_bU).func_77655_b("comparator").func_77637_a(CreativeTabs.field_78028_d));
        Item.func_179217_a(405, "netherbrick", new Item().func_77655_b("netherbrick").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(406, "quartz", new Item().func_77655_b("netherquartz").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(407, "tnt_minecart", new ItemMinecart(EntityMinecart.Type.TNT).func_77655_b("minecartTnt"));
        Item.func_179217_a(408, "hopper_minecart", new ItemMinecart(EntityMinecart.Type.HOPPER).func_77655_b("minecartHopper"));
        Item.func_179217_a(409, "prismarine_shard", new Item().func_77655_b("prismarineShard").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(410, "prismarine_crystals", new Item().func_77655_b("prismarineCrystals").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(411, "rabbit", new ItemFood(3, 0.3f, true).func_77655_b("rabbitRaw"));
        Item.func_179217_a(412, "cooked_rabbit", new ItemFood(5, 0.6f, true).func_77655_b("rabbitCooked"));
        Item.func_179217_a(413, "rabbit_stew", new ItemSoup(10).func_77655_b("rabbitStew"));
        Item.func_179217_a(414, "rabbit_foot", new Item().func_77655_b("rabbitFoot").func_77637_a(CreativeTabs.field_78038_k));
        Item.func_179217_a(415, "rabbit_hide", new Item().func_77655_b("rabbitHide").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(416, "armor_stand", new ItemArmorStand().func_77655_b("armorStand").func_77625_d(16));
        Item.func_179217_a(417, "iron_horse_armor", new Item().func_77655_b("horsearmormetal").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(418, "golden_horse_armor", new Item().func_77655_b("horsearmorgold").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(419, "diamond_horse_armor", new Item().func_77655_b("horsearmordiamond").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f));
        Item.func_179217_a(420, "lead", new ItemLead().func_77655_b("leash"));
        Item.func_179217_a(421, "name_tag", new ItemNameTag().func_77655_b("nameTag"));
        Item.func_179217_a(422, "command_block_minecart", new ItemMinecart(EntityMinecart.Type.COMMAND_BLOCK).func_77655_b("minecartCommandBlock").func_77637_a(null));
        Item.func_179217_a(423, "mutton", new ItemFood(2, 0.3f, true).func_77655_b("muttonRaw"));
        Item.func_179217_a(424, "cooked_mutton", new ItemFood(6, 0.8f, true).func_77655_b("muttonCooked"));
        Item.func_179217_a(425, "banner", new ItemBanner().func_77655_b("banner"));
        Item.func_179217_a(426, "end_crystal", new ItemEndCrystal());
        Item.func_179217_a(427, "spruce_door", new ItemDoor(Blocks.field_180414_ap).func_77655_b("doorSpruce"));
        Item.func_179217_a(428, "birch_door", new ItemDoor(Blocks.field_180412_aq).func_77655_b("doorBirch"));
        Item.func_179217_a(429, "jungle_door", new ItemDoor(Blocks.field_180411_ar).func_77655_b("doorJungle"));
        Item.func_179217_a(430, "acacia_door", new ItemDoor(Blocks.field_180410_as).func_77655_b("doorAcacia"));
        Item.func_179217_a(431, "dark_oak_door", new ItemDoor(Blocks.field_180409_at).func_77655_b("doorDarkOak"));
        Item.func_179217_a(432, "chorus_fruit", new ItemChorusFruit(4, 0.3f).func_77848_i().func_77655_b("chorusFruit").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(433, "chorus_fruit_popped", new Item().func_77655_b("chorusFruitPopped").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(434, "beetroot", new ItemFood(1, 0.6f, false).func_77655_b("beetroot"));
        Item.func_179217_a(435, "beetroot_seeds", new ItemSeeds(Blocks.field_185773_cZ, Blocks.field_150458_ak).func_77655_b("beetroot_seeds"));
        Item.func_179217_a(436, "beetroot_soup", new ItemSoup(6).func_77655_b("beetroot_soup"));
        Item.func_179217_a(437, "dragon_breath", new Item().func_77637_a(CreativeTabs.field_78038_k).func_77655_b("dragon_breath").func_77642_a(\u2603));
        Item.func_179217_a(438, "splash_potion", new ItemSplashPotion().func_77655_b("splash_potion"));
        Item.func_179217_a(439, "spectral_arrow", new ItemSpectralArrow().func_77655_b("spectral_arrow"));
        Item.func_179217_a(440, "tipped_arrow", new ItemTippedArrow().func_77655_b("tipped_arrow"));
        Item.func_179217_a(441, "lingering_potion", new ItemLingeringPotion().func_77655_b("lingering_potion"));
        Item.func_179217_a(442, "shield", new ItemShield().func_77655_b("shield"));
        Item.func_179217_a(443, "elytra", new ItemElytra().func_77655_b("elytra"));
        Item.func_179217_a(444, "spruce_boat", new ItemBoat(EntityBoat.Type.SPRUCE));
        Item.func_179217_a(445, "birch_boat", new ItemBoat(EntityBoat.Type.BIRCH));
        Item.func_179217_a(446, "jungle_boat", new ItemBoat(EntityBoat.Type.JUNGLE));
        Item.func_179217_a(447, "acacia_boat", new ItemBoat(EntityBoat.Type.ACACIA));
        Item.func_179217_a(448, "dark_oak_boat", new ItemBoat(EntityBoat.Type.DARK_OAK));
        Item.func_179217_a(449, "totem_of_undying", new Item().func_77655_b("totem").func_77625_d(1).func_77637_a(CreativeTabs.field_78037_j));
        Item.func_179217_a(450, "shulker_shell", new Item().func_77655_b("shulkerShell").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(452, "iron_nugget", new Item().func_77655_b("ironNugget").func_77637_a(CreativeTabs.field_78035_l));
        Item.func_179217_a(453, "knowledge_book", new ItemKnowledgeBook().func_77655_b("knowledgeBook"));
        Item.func_179217_a(2256, "record_13", new ItemRecord("13", SoundEvents.field_187828_ep).func_77655_b("record"));
        Item.func_179217_a(2257, "record_cat", new ItemRecord("cat", SoundEvents.field_187832_er).func_77655_b("record"));
        Item.func_179217_a(2258, "record_blocks", new ItemRecord("blocks", SoundEvents.field_187830_eq).func_77655_b("record"));
        Item.func_179217_a(2259, "record_chirp", new ItemRecord("chirp", SoundEvents.field_187834_es).func_77655_b("record"));
        Item.func_179217_a(2260, "record_far", new ItemRecord("far", SoundEvents.field_187836_et).func_77655_b("record"));
        Item.func_179217_a(2261, "record_mall", new ItemRecord("mall", SoundEvents.field_187838_eu).func_77655_b("record"));
        Item.func_179217_a(2262, "record_mellohi", new ItemRecord("mellohi", SoundEvents.field_187840_ev).func_77655_b("record"));
        Item.func_179217_a(2263, "record_stal", new ItemRecord("stal", SoundEvents.field_187842_ew).func_77655_b("record"));
        Item.func_179217_a(2264, "record_strad", new ItemRecord("strad", SoundEvents.field_187844_ex).func_77655_b("record"));
        Item.func_179217_a(2265, "record_ward", new ItemRecord("ward", SoundEvents.field_187848_ez).func_77655_b("record"));
        Item.func_179217_a(2266, "record_11", new ItemRecord("11", SoundEvents.field_187826_eo).func_77655_b("record"));
        Item.func_179217_a(2267, "record_wait", new ItemRecord("wait", SoundEvents.field_187846_ey).func_77655_b("record"));
    }

    private static void func_179216_c(Block block) {
        Item.func_179214_a(block, new ItemBlock(block));
    }

    protected static void func_179214_a(Block block, Item item) {
        Item.func_179219_a(Block.func_149682_b(block), Block.field_149771_c.func_177774_c(block), item);
        field_179220_a.put(block, item);
    }

    private static void func_179217_a(int n, String string, Item item) {
        Item.func_179219_a(n, new ResourceLocation(string), item);
    }

    private static void func_179219_a(int n, ResourceLocation resourceLocation, Item item) {
        field_150901_e.func_177775_a(n, resourceLocation, item);
    }

    public ItemStack func_190903_i() {
        return new ItemStack(this);
    }

    public static enum ToolMaterial {
        WOOD(0, 59, 2.0f, 0.0f, 15),
        STONE(1, 131, 4.0f, 1.0f, 5),
        IRON(2, 250, 6.0f, 2.0f, 14),
        DIAMOND(3, 1561, 8.0f, 3.0f, 10),
        GOLD(0, 32, 12.0f, 0.0f, 22);

        private final int field_78001_f;
        private final int field_78002_g;
        private final float field_78010_h;
        private final float field_78011_i;
        private final int field_78008_j;

        private ToolMaterial(int n2, int n3, float f, float f2, int n4) {
            this.field_78001_f = n2;
            this.field_78002_g = n3;
            this.field_78010_h = f;
            this.field_78011_i = f2;
            this.field_78008_j = n4;
        }

        public int func_77997_a() {
            return this.field_78002_g;
        }

        public float func_77998_b() {
            return this.field_78010_h;
        }

        public float func_78000_c() {
            return this.field_78011_i;
        }

        public int func_77996_d() {
            return this.field_78001_f;
        }

        public int func_77995_e() {
            return this.field_78008_j;
        }

        public Item func_150995_f() {
            if (this == WOOD) {
                return Item.func_150898_a(Blocks.field_150344_f);
            }
            if (this == STONE) {
                return Item.func_150898_a(Blocks.field_150347_e);
            }
            if (this == GOLD) {
                return Items.field_151043_k;
            }
            if (this == IRON) {
                return Items.field_151042_j;
            }
            if (this == DIAMOND) {
                return Items.field_151045_i;
            }
            return null;
        }
    }
}

