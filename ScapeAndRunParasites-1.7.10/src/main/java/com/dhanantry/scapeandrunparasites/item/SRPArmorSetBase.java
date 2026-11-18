/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.util.EnumHelper
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.event.entity.living.LivingFallEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SRPArmorSetBase
extends ItemArmor {
    public static final int BOOTS_SAVE_COOLDOWN_TICKS = 2400;
    public static final int DEBUFF_TICKS = 200;
    public static final int BOOTS_MIN_FALL_DIST = 6;
    private static final String NBT_ROOT = "SRPArmorCD";
    private static final String NBT_BOOT_CD = "bootsCD";
    private static final String NBT_SET_CD = "setCD";
    private static final String NBT_BOOT_READY = "bootsReadyPlayed";
    private static final String NBT_SET_READY = "setReadyPlayed";
    private final String setKey;
    public static final ItemArmor.ArmorMaterial HIJACKED_MAT = EnumHelper.addArmorMaterial((String)"HIJACKED", (String)"srparasites:hijacked_iron", (int)40, (int[])new int[]{4, 7, 9, 4}, (int)15, (String)SRPSounds.FLESH_GROW, (float)3.0f);

    public SRPArmorSetBase(String registryName, ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot, String setKey) {
        super(material, 1, slot);
        this.setKey = setKey;
        RegistryNames.set(this, new ResourceLocation("srparasites", registryName));
        this.setUnlocalizedName("srparasites." + registryName);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    public SRPArmorSetBase(String registryName) {
        this(registryName, SRPArmorSetBase.inferMaterial(registryName), SRPArmorSetBase.inferSlot(registryName), SRPArmorSetBase.inferSetKey(registryName));
    }

    public String getSetKey() {
        return this.setKey;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (world.isRemote) {
            return;
        }
        if (SRPArmorSetBase.isWearingFullSet(player, this.setKey) && player.isPotionActive(SRPPotions.BLEED_E)) {
            player.removeActivePotionEffect(SRPPotions.BLEED_E);
        }
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        String layer = slot == EntityEquipmentSlot.LEGS ? "2" : "1";
        return "srparasites:textures/models/armor/" + this.setKey + "_layer_" + layer + ".png";
    }

    public static boolean isWearingFullSet(EntityPlayer player, String setKey) {
        if (player == null || setKey == null || setKey.isEmpty()) {
            return false;
        }
        NonNullList armor = player.inventory.armorInventory;
        if (armor.size() < 4) {
            return false;
        }
        for (ItemStack st : armor) {
            if (st.isEmpty() || !(st.getItem() instanceof SRPArmorSetBase)) {
                return false;
            }
            if (((SRPArmorSetBase)st.getItem()).setKey.equals(setKey)) continue;
            return false;
        }
        return true;
    }

    public static String getAnySetKey(EntityPlayer p) {
        if (p == null) {
            return "";
        }
        ItemStack head = p.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!head.isEmpty() && head.getItem() instanceof SRPArmorSetBase) {
            return ((SRPArmorSetBase)head.getItem()).getSetKey();
        }
        ItemStack chest = p.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!chest.isEmpty() && chest.getItem() instanceof SRPArmorSetBase) {
            return ((SRPArmorSetBase)chest.getItem()).getSetKey();
        }
        ItemStack legs = p.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        if (!legs.isEmpty() && legs.getItem() instanceof SRPArmorSetBase) {
            return ((SRPArmorSetBase)legs.getItem()).getSetKey();
        }
        ItemStack feet = p.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!feet.isEmpty() && feet.getItem() instanceof SRPArmorSetBase) {
            return ((SRPArmorSetBase)feet.getItem()).getSetKey();
        }
        return "";
    }

    private static ItemArmor.ArmorMaterial inferMaterial(String regName) {
        String s = regName.toLowerCase(Locale.ROOT);
        if (s.startsWith("hijacked_")) {
            return HIJACKED_MAT;
        }
        if (s.contains("leather")) {
            return ItemArmor.ArmorMaterial.LEATHER;
        }
        if (s.contains("chain")) {
            return ItemArmor.ArmorMaterial.CHAIN;
        }
        if (s.contains("gold")) {
            return ItemArmor.ArmorMaterial.GOLD;
        }
        if (s.contains("diamond")) {
            return ItemArmor.ArmorMaterial.DIAMOND;
        }
        return ItemArmor.ArmorMaterial.IRON;
    }

    private static EntityEquipmentSlot inferSlot(String regName) {
        String s = regName.toLowerCase(Locale.ROOT);
        if (s.endsWith("_helmet") || s.endsWith("_helm")) {
            return EntityEquipmentSlot.HEAD;
        }
        if (s.endsWith("_chestpiece") || s.endsWith("_chestplate") || s.endsWith("_chest")) {
            return EntityEquipmentSlot.CHEST;
        }
        if (s.endsWith("_leggings") || s.endsWith("_legs")) {
            return EntityEquipmentSlot.LEGS;
        }
        if (s.endsWith("_boots")) {
            return EntityEquipmentSlot.FEET;
        }
        throw new IllegalArgumentException("Cannot infer armor slot from registry name: " + regName);
    }

    private static String inferSetKey(String regName) {
        int i = regName.lastIndexOf(95);
        return i > 0 ? regName.substring(0, i) : regName;
    }

    private static NBTTagCompound getCDTag(EntityPlayer p) {
        NBTTagCompound root = p.getEntityData();
        if (!root.hasKey(NBT_ROOT)) {
            root.setTag(NBT_ROOT, (NBTBase)new NBTTagCompound());
        }
        return root.getCompoundTag(NBT_ROOT);
    }

    private static int getBootsCD(EntityPlayer p) {
        return SRPArmorSetBase.getCDTag(p).getInteger(NBT_BOOT_CD);
    }

    private static int getSetCD(EntityPlayer p) {
        return SRPArmorSetBase.getCDTag(p).getInteger(NBT_SET_CD);
    }

    private static void setBootsCD(EntityPlayer p, int v) {
        NBTTagCompound t = SRPArmorSetBase.getCDTag(p);
        t.setInteger(NBT_BOOT_CD, v);
        t.setBoolean(NBT_BOOT_READY, false);
    }

    private static void setSetCD(EntityPlayer p, int v) {
        NBTTagCompound t = SRPArmorSetBase.getCDTag(p);
        t.setInteger(NBT_SET_CD, v);
        t.setBoolean(NBT_SET_READY, false);
    }

    private static void bloodImpact(World w, Entity e) {
        if (!(w instanceof WorldServer)) {
            return;
        }
        WorldServer ws = (WorldServer)w;
        double x = e.posX;
        double y = e.posY + (double)e.height * 0.5;
        double z = e.posZ;
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.REDSTONE, x, y, z, 0, 0.9, 0.05, 0.05, 1.3, new int[0]);
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.DAMAGE_INDICATOR, x, y, z, 24, 0.45, 0.35, 0.45, 0.2, new int[0]);
    }

    private static void applyCrashDebuffs(EntityLivingBase ent) {
        ent.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 200, 1, false, true));
        ent.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.weakness, 200, 1, false, true));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent e) {
        float mult;
        if (!(e.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer p = (EntityPlayer)e.getEntityLiving();
        String key = SRPArmorSetBase.getAnySetKey(p);
        if (!SRPArmorSetBase.isHijackedSetKey(key) || !SRPArmorSetBase.isWearingFullSet(p, key)) {
            return;
        }
        if (SRPArmorSetBase.isFireDamage(e.getSource()) && (mult = (float)Math.max(0.0, SRPConfigSystems.hijackedArmorFireMult)) != 1.0f) {
            e.setAmount(e.getAmount() * mult);
        }
    }

    private static boolean isFireDamage(DamageSource src) {
        if (src == null) {
            return false;
        }
        return src.isFireDamage() || src == DamageSource.lava || src == DamageSource.HOT_FLOOR || "inFire".equals(src.getDamageType()) || "onFire".equals(src.getDamageType()) || "lava".equals(src.getDamageType());
    }

    private static boolean isHijackedSetKey(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        String s = key.toLowerCase(Locale.ROOT);
        return s.startsWith("hijacked_");
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        SRPArmorSetBase bootsItem;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP client = mc.thePlayer;
        if (SRPArmorSetBase.isHijackedSetKey(this.getSetKey())) {
            tooltip.add(EnumChatFormatting.AQUA + I18n.format((String)"tooltip.srparasites.hijacked.fullset_bleed", (Object[])new Object[0]));
            tooltip.add(EnumChatFormatting.RED + I18n.format((String)"tooltip.srparasites.hijacked.fire_penalty", (Object[])new Object[]{String.format(Locale.ROOT, "%.2f", SRPConfigSystems.hijackedArmorFireMult)}));
            tooltip.add(EnumChatFormatting.BLUE + I18n.format((String)"tooltip.srparasites.hijacked.boots_save", (Object[])new Object[0]));
        }
        if (client == null) {
            return;
        }
        ItemStack feet = client.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!feet.isEmpty() && feet.getItem() instanceof SRPArmorSetBase && SRPArmorSetBase.isHijackedSetKey((bootsItem = (SRPArmorSetBase)feet.getItem()).getSetKey()) && bootsItem.getSetKey().equals(this.getSetKey())) {
            int bootCd = SRPArmorSetBase.getBootsCD((EntityPlayer)client);
            if (bootCd > 0) {
                tooltip.add(EnumChatFormatting.BLUE + I18n.format((String)"tooltip.srparasites.hijacked.boots_cd", (Object[])new Object[]{SRPArmorSetBase.formatTicks(bootCd)}));
            } else {
                tooltip.add(EnumChatFormatting.BLUE + I18n.format((String)"tooltip.srparasites.ready", (Object[])new Object[0]));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    private static String formatTicks(int ticks) {
        if (ticks <= 0) {
            return "0:00";
        }
        int s = (ticks + 19) / 20;
        int m = s / 60;
        int ss = s % 60;
        return String.format(Locale.ROOT, "%d:%02d", m, ss);
    }

    @Mod.EventBusSubscriber(modid="srparasites")
    public static class Events {
        @SubscribeEvent
        public static void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
            String key;
            if (!(e.getEntityLiving() instanceof EntityPlayer)) {
                return;
            }
            EntityPlayer p = (EntityPlayer)e.getEntityLiving();
            NBTTagCompound cd = SRPArmorSetBase.getCDTag(p);
            int boot = cd.getInteger(SRPArmorSetBase.NBT_BOOT_CD);
            int set = cd.getInteger(SRPArmorSetBase.NBT_SET_CD);
            if (boot > 0) {
                cd.setInteger(SRPArmorSetBase.NBT_BOOT_CD, boot - 1);
            }
            if (set > 0) {
                cd.setInteger(SRPArmorSetBase.NBT_SET_CD, set - 1);
            }
            boolean hasHijackedBoots = false;
            boolean hasFullHijackedSet = false;
            ItemStack feet = p.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if (!feet.isEmpty() && feet.getItem() instanceof SRPArmorSetBase) {
                SRPArmorSetBase bootsItem = (SRPArmorSetBase)feet.getItem();
                hasHijackedBoots = SRPArmorSetBase.isHijackedSetKey(bootsItem.getSetKey());
            }
            if (SRPArmorSetBase.isHijackedSetKey(key = SRPArmorSetBase.getAnySetKey(p)) && SRPArmorSetBase.isWearingFullSet(p, key)) {
                hasFullHijackedSet = true;
            }
            if (cd.hasKey(SRPArmorSetBase.NBT_BOOT_CD) && cd.getInteger(SRPArmorSetBase.NBT_BOOT_CD) == 0 && !cd.getBoolean(SRPArmorSetBase.NBT_BOOT_READY) && hasHijackedBoots) {
                cd.setBoolean(SRPArmorSetBase.NBT_BOOT_READY, true);
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(p.worldObj, null, EntityQueries.position(p), SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.PLAYERS, 0.5f, 1.5f);
            }
            if (cd.hasKey(SRPArmorSetBase.NBT_SET_CD) && cd.getInteger(SRPArmorSetBase.NBT_SET_CD) == 0 && !cd.getBoolean(SRPArmorSetBase.NBT_SET_READY) && hasFullHijackedSet) {
                cd.setBoolean(SRPArmorSetBase.NBT_SET_READY, true);
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(p.worldObj, null, EntityQueries.position(p), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.PLAYERS, 0.7f, 1.0f);
            }
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent e) {
            if (!(e.getEntityLiving() instanceof EntityPlayer)) {
                return;
            }
            EntityPlayer p = (EntityPlayer)e.getEntityLiving();
            ItemStack boots = p.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if (boots.isEmpty() || !(boots.getItem() instanceof SRPArmorSetBase)) {
                return;
            }
            SRPArmorSetBase bootsItem = (SRPArmorSetBase)boots.getItem();
            if (!SRPArmorSetBase.isHijackedSetKey(bootsItem.getSetKey())) {
                return;
            }
            if (e.getDistance() < 6.0f) {
                return;
            }
            if (SRPArmorSetBase.getBootsCD(p) > 0) {
                return;
            }
            e.setCanceled(true);
            p.fallDistance = 0.0f;
            SRPArmorSetBase.bloodImpact(p.worldObj, (Entity)p);
            SRPArmorSetBase.applyCrashDebuffs((EntityLivingBase)p);
            SRPArmorSetBase.setBootsCD(p, 2400);
        }
    }
}

