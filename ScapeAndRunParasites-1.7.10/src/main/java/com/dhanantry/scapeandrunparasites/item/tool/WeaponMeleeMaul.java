/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.IEntityOwnable
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.player.CriticalHitEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;

import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeaponMeleeMaul
extends WeaponToolMeleeBase {
    private static final int MAUL_COOLDOWN_TICKS = 200;
    private static final int DEBUFF_TICKS = 200;
    private static final double SLAM_RADIUS = 4.0;
    private static final double KNOCKUP = 0.85;
    private static final double PUSH = 1.25;
    private static final String NBT_DASH = "srp_maul_dash";
    private static final String NBT_DASH_TICKS = "srp_maul_dash_t";
    private static final String NBT_DASH_DX = "srp_maul_dash_dx";
    private static final String NBT_DASH_DZ = "srp_maul_dash_dz";
    private static final String NBT_DASH_ITEM = "srp_maul_dash_item";
    private static final String NBT_DASH_DY = "srp_maul_dash_dy";
    private static final String NBT_DASH_TOTAL = "srp_maul_dash_total";
    private static final String NBT_DASH_ORIG = "srp_maul_dash_orig";
    private static final String NBT_SLAM_PENDING = "srp_maul_slam_pending";
    private static final String NBT_SLAM_PENDING_T = "srp_maul_slam_pending_t";
    private static final String NBT_SLAM_DELAY = "srp_maul_slam_delay";
    private static final String NBT_CHARGED_AOE = "srp_maul_charged_aoe";
    private static final double DASH_MAX_UP = 0.95;
    private static final double DASH_MAX_DOWN = -0.25;
    private static final int SLAM_WAIT_MAX_TICKS = 200;
    private static final int SENTIENT_DASH_TICKS = 4;
    private static final double SENTIENT_DASH_DIST = 16.0;
    private static final int SENTIENT_COOLDOWN_TICKS = 500;
    private static final int SENTIENT_MIN_CHARGE_TICKS = 6;

    public WeaponMeleeMaul(Item.ToolMaterial material, String name, double attackspeed, float range, float attackD, boolean fear, byte id) {
        super(material, name, attackspeed, range, attackD, fear, id);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
    }

    @Override
    public Item getNext() {
        if (this == SRPItems.weapon_maul) {
            return SRPItems.weapon_maulSentient;
        }
        return null;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean flag = super.hitEntity(stack, target, attacker);
        if (flag && this.calling && attacker instanceof EntityPlayer && target.getHealth() <= 0.0f) {
            EntityPlayer player = (EntityPlayer)attacker;
            if (!player.getEntityData().getBoolean(NBT_CHARGED_AOE)) {
                return flag;
            }
            int times = 2;
            if (this.calling) {
                times = 5;
            }
            for (int i = 0; i <= times; ++i) {
                this.hitTarget(stack, target, player);
            }
            if (target.getHealth() <= 0.0f) {
                NBTTagCompound compound = stack.getTagCompound();
                if (compound == null) {
                    compound = new NBTTagCompound();
                }
                if (compound.hasKey("srpkills")) {
                    int key = (int)((float)compound.getInteger("srpkills") + target.getMaxHealth());
                    compound.setInteger("srpkills", key);
                } else {
                    compound.setInteger("srpkills", (int)target.getMaxHealth());
                }
            }
        }
        return flag;
    }

    private void hitTarget(ItemStack stack, EntityLivingBase targetEntity, EntityPlayer attacker) {
        if (!ForgeHooks.onPlayerAttackTarget((EntityPlayer)attacker, (Entity)targetEntity)) {
            return;
        }
        if (targetEntity.canAttackWithItem() && !targetEntity.hitByEntity((Entity)attacker)) {
            float f = (float)attacker.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            float f1 = EnchantmentHelper.getModifierForCreature((ItemStack)attacker.getHeldItemMainhand(), (EnumCreatureAttribute)targetEntity.getCreatureAttribute());
            float f2 = attacker.getCooledAttackStrength(0.5f);
            f1 *= f2;
            attacker.resetCooldown();
            if ((f *= 0.2f + f2 * f2 * 0.8f) > 0.0f || f1 > 0.0f) {
                ItemStack itemstack;
                boolean flag = f2 > 0.9f;
                boolean flag1 = false;
                int i = 0;
                i += EnchantmentHelper.getRespiration((EntityLivingBase)attacker);
                if (attacker.isSprinting() && flag) {
                    com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(attacker.worldObj, (EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(attacker), 1.0f, 1.0f);
                    ++i;
                    flag1 = true;
                }
                boolean flag2 = flag && attacker.fallDistance > 0.0f && !attacker.onGround && !attacker.isOnLadder() && !attacker.isInWater() && !attacker.isPotionActive(net.minecraft.potion.Potion.blindness) && !attacker.isRiding() && targetEntity instanceof EntityLivingBase;
                flag2 = flag2 && !attacker.isSprinting();
                CriticalHitEvent hitResult = ForgeHooks.getCriticalHit((EntityPlayer)attacker, (Entity)targetEntity, (boolean)flag2, (float)(flag2 ? 1.5f : 1.0f));
                boolean bl = flag2 = hitResult != null;
                if (flag2) {
                    f *= hitResult.getDamageModifier();
                }
                f += f1;
                boolean flag3 = false;
                double d0 = attacker.distanceWalkedModified - attacker.prevDistanceWalkedModified;
                if (flag && !flag2 && !flag1 && attacker.onGround && d0 < (double)attacker.getAIMoveSpeed() && (itemstack = attacker.getHeldItem(EnumHand.MAIN_HAND)).getItem() instanceof ItemSword) {
                    flag3 = true;
                }
                float f4 = 0.0f;
                boolean flag4 = false;
                int j = EnchantmentHelper.getFireAspectModifier((EntityLivingBase)attacker);
                f4 = targetEntity.getHealth();
                if (j > 0 && !targetEntity.isBurning()) {
                    flag4 = true;
                    targetEntity.setFire(1);
                }
                targetEntity.setHealth(targetEntity.getHealth() - f);
                targetEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 0.0f);
                boolean flag5 = true;
                if (flag5) {
                    if (flag3) {
                        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(attacker.worldObj, (EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(attacker), 1.0f, 1.0f);
                        attacker.spawnSweepParticles();
                    }
                    if (flag2) {
                        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(attacker.worldObj, (EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(attacker), 1.0f, 1.0f);
                        attacker.onCriticalHit((Entity)targetEntity);
                    }
                    if (!flag2 && !flag3) {
                        if (flag) {
                            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(attacker.worldObj, (EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(attacker), 1.0f, 1.0f);
                        } else {
                            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(attacker.worldObj, (EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(attacker), 1.0f, 1.0f);
                        }
                    }
                    if (f1 > 0.0f) {
                        attacker.onEnchantmentCritical((Entity)targetEntity);
                    }
                    attacker.setLastAttacker((Entity)targetEntity);
                    EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, (Entity)attacker);
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)attacker, (Entity)targetEntity);
                    ItemStack itemstack1 = attacker.getHeldItemMainhand();
                    if (!itemstack1.isEmpty()) {
                        ItemStack beforeHitCopy = itemstack1.copy();
                        stack.damageItem(1, (EntityLivingBase)attacker);
                        if (itemstack1.isEmpty()) {
                            ForgeEventFactory.onPlayerDestroyItem((EntityPlayer)attacker, (ItemStack)beforeHitCopy, (EnumHand)EnumHand.MAIN_HAND);
                            attacker.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                    float f5 = f4 - targetEntity.getHealth();
                    attacker.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0f));
                    if (j > 0) {
                        targetEntity.setFire(j * 4);
                    }
                    if (attacker.worldObj instanceof WorldServer && f5 > 2.0f) {
                        int k = (int)((double)f5 * 0.5);
                        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)attacker.worldObj), EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5f), targetEntity.posZ, k, 0.1, 0.0, 0.1, 0.2, new int[0]);
                    }
                    attacker.addExhaustion(0.1f);
                } else {
                    com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(attacker.worldObj, (EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(attacker), 1.0f, 1.0f);
                    if (flag4) {
                        targetEntity.extinguish();
                    }
                }
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(EnumChatFormatting.YELLOW + I18n.format((String)("tootip.srparasites.weaponm." + this.idTool), (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.RED + I18n.format((String)("tootip.srparasites.weaponm." + this.idTool * 10), (Object[])new Object[0]));
        if (this.calling && SRPConfigSystems.useScent) {
            tooltip.add(EnumChatFormatting.BLACK + I18n.format((String)("tootip.srparasites.weaponm." + this.idTool * 100), (Object[])new Object[0]));
        }
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return this.calling ? EnumAction.BOW : EnumAction.NONE;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return this.calling ? 72000 : 0;
    }

    private void doMaulSlam(World world, EntityPlayer player, ItemStack stack) {
        AxisAlignedBB bb = Boxes.grow(Boxes.entityBox(player), 4.0, 1.5, 4.0);
        List list = EntityQueries.entities(world, EntityLivingBase.class, bb);
        ItemStack held = player.getHeldItemMainhand();
        float base = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        for (EntityLivingBase e : list) {
            if (e == player || !e.isEntityAlive() || e.isOnSameTeam((Entity)player) || player.isOnSameTeam((Entity)e) || e instanceof IEntityOwnable && ((IEntityOwnable)e).getOwnerId() != null) continue;
            float bonus = 0.0f;
            if (!held.isEmpty()) {
                bonus = EnchantmentHelper.getModifierForCreature((ItemStack)held, (EnumCreatureAttribute)e.getCreatureAttribute());
            }
            float slamDamage = (base + bonus) * 2.0f;
            e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), slamDamage);
            double dx = e.posX - player.posX;
            double dz = e.posZ - player.posZ;
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 1.0E-4) {
                dx = 0.0;
                dz = 0.0;
                len = 1.0;
            }
            double nx = dx / len;
            double nz = dz / len;
            e.motionY = Math.max(e.motionY, 0.85);
            e.motionX += nx * 1.25;
            e.motionZ += nz * 1.25;
            e.velocityChanged = true;
            e.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 200, 0));
            e.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.weakness, 200, 0));
        }
        if (world instanceof WorldServer) {
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)world), EnumParticleTypes.SWEEP_ATTACK, player.posX, player.posY + 1.0, player.posZ, 16, 1.4, 0.25, 1.4, 0.0, new int[0]);
        }
        if (!world.isRemote) {
            player.getEntityData().setBoolean(NBT_CHARGED_AOE, false);
        }
        this.spawnGroundShake(world, player);
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(world, null, player.posX, player.posY, player.posZ, SRPSounds.VENGEANCE_ROCK, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.PLAYERS, 0.9f, 0.9f);
        stack.damageItem(1, (EntityLivingBase)player);
    }

    private void armPendingSlam(NBTTagCompound ptag, int maxTicks) {
        ptag.setBoolean(NBT_SLAM_PENDING, true);
        ptag.setInteger(NBT_SLAM_PENDING_T, maxTicks);
        ptag.setInteger(NBT_SLAM_DELAY, 1);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (this.calling) {
            if (!world.isRemote) {
                NBTTagCompound ptag = player.getEntityData();
                if (player.isSneaking() && ptag.getBoolean(NBT_SLAM_PENDING) && !player.onGround) {
                    this.forceDrop(player);
                    return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
                }
            }
            if (player.getCooldownTracker().hasCooldown((Item)this)) {
                return new ActionResult(EnumActionResult.PASS, (Object)stack);
            }
            player.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        if (player.getCooldownTracker().hasCooldown((Item)this)) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        player.swingArm(hand);
        if (!world.isRemote) {
            this.doMaulSlam(world, player, stack);
            player.getCooldownTracker().setCooldown((Item)this, 200);
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    private void forceDrop(EntityPlayer player) {
        player.motionY = -3.5;
        player.velocityChanged = true;
        player.fallDistance = 0.0f;
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (this.calling) {
            return false;
        }
        if (player.getCooldownTracker().hasCooldown((Item)this)) {
            return false;
        }
        player.swingArm(hand);
        if (!player.worldObj.isRemote) {
            this.doMaulSlam(player.worldObj, player, stack);
            player.getCooldownTracker().setCooldown((Item)this, 200);
        }
        return true;
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (this.calling) {
            return false;
        }
        return false;
    }

    private void spawnGroundShake(World world, EntityPlayer player) {
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        int r = (int)Math.ceil(4.0);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; ++dx) {
            for (int dz = -r; dz <= r; ++dz) {
                double dist2 = dx * dx + dz * dz;
                if (dist2 > 16.0) continue;
                int x = (int)Math.floor(player.posX) + dx;
                int z = (int)Math.floor(player.posZ) + dz;
                int baseY = (int)Math.floor(Boxes.entityBox(player).minY);
                int groundY = -1;
                for (int dy = 2; dy >= -6; --dy) {
                    int y = baseY + dy;
                    pos.setPos(x, y, z);
                    IBlockState state = BlockStates.get(world, (BlockPos)pos);
                    if (state.getMaterial() == Material.air || state.getMaterial() == Material.water || state.getMaterial() == Material.lava) continue;
                    groundY = y;
                    break;
                }
                if (groundY < 0) continue;
                pos.setPos(x, groundY, z);
                IBlockState ground = BlockStates.get(world, (BlockPos)pos);
                if (ground.getMaterial() == Material.air) continue;
                int stateId = BlockStates.stateId((IBlockState)ground);
                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.BLOCK_CRACK, (double)x + 0.5, (double)groundY + 1.02, (double)z + 0.5, 6, 0.35, 0.06, 0.35, 0.15, new int[]{stateId});
            }
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        Vec3d dir;
        if (!this.calling) {
            return;
        }
        if (!(entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)entityLiving;
        if (player.getCooldownTracker().hasCooldown((Item)this)) {
            return;
        }
        int used = this.getMaxItemUseDuration(stack) - timeLeft;
        if (used < 6) {
            return;
        }
        float charge = this.getBowCharge(used);
        if (charge <= 0.0f) {
            return;
        }
        Vec3d look = player.getLookVec();
        if (look.lengthVector() < 0.001) {
            return;
        }
        double y = look.yCoord;
        if (y > 0.95) {
            y = 0.95;
        }
        if (y < -0.25) {
            y = -0.25;
        }
        if ((dir = new Vec3d(look.xCoord, y, look.zCoord)).lengthVector() < 0.001) {
            return;
        }
        dir = dir.normalize();
        double totalDist = 16.0 * (double)charge;
        int dashTicks = Math.max(3, (int)Math.ceil(4.0f * charge));
        if (!world.isRemote) {
            NBTTagCompound ptag = player.getEntityData();
            ptag.setBoolean(NBT_DASH, true);
            ptag.setInteger(NBT_DASH_TICKS, dashTicks);
            ptag.setInteger(NBT_DASH_ORIG, dashTicks);
            ptag.setBoolean(NBT_CHARGED_AOE, true);
            ptag.setDouble(NBT_DASH_DX, dir.xCoord);
            ptag.setDouble(NBT_DASH_DY, dir.yCoord);
            ptag.setDouble(NBT_DASH_DZ, dir.zCoord);
            ptag.setDouble(NBT_DASH_TOTAL, totalDist);
            ptag.setString(NBT_DASH_ITEM, RegistryNames.get(this).toString());
            ptag.setBoolean(NBT_SLAM_PENDING, false);
            ptag.setInteger(NBT_SLAM_PENDING_T, 0);
            ptag.setInteger(NBT_SLAM_DELAY, 0);
            player.getCooldownTracker().setCooldown((Item)this, 500);
        }
    }

    private float getBowCharge(int chargeTicks) {
        float f = (float)chargeTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    private RayTraceResult rayTraceEntities(World world, EntityPlayer player, Vec3d start, Vec3d end) {
        Entity closest = null;
        Vec3d closestHit = null;
        double closestDist = start.squareDistanceTo(end);
        AxisAlignedBB box = Boxes.grow(Boxes.expand(Boxes.entityBox(player), end.xCoord - start.xCoord, end.yCoord - start.yCoord, end.zCoord - start.zCoord), 1.0);
        List entities = world.getEntitiesWithinAABBExcludingEntity((Entity)player, box);
        for (Entity e : entities) {
            double d;
            AxisAlignedBB eb;
            RayTraceResult r;
            if (!e.canBeCollidedWith() || (r = Boxes.calculateIntercept((eb = Boxes.grow(Boxes.entityBox(e), 0.3)), start, end)) == null || !((d = start.squareDistanceTo(r.hitVec)) < closestDist)) continue;
            closestDist = d;
            closest = e;
            closestHit = r.hitVec;
        }
        if (closest == null) {
            return null;
        }
        RayTraceResult out = new RayTraceResult(closest, closestHit);
        out.typeOfHit = RayTraceResult.Type.ENTITY;
        return out;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        ItemStack slamStack;
        super.onUpdate(stack, world, entity, slot, selected);
        if (!this.calling) {
            return;
        }
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)entity;
        NBTTagCompound ptag = player.getEntityData();
        ItemStack held = player.getHeldItemMainhand();
        ItemStack itemStack = slamStack = !held.isEmpty() && held.getItem() == this ? held : stack;
        if (!world.isRemote && ptag.getBoolean(NBT_SLAM_PENDING)) {
            boolean landed;
            int delay = ptag.getInteger(NBT_SLAM_DELAY);
            if (delay > 0) {
                ptag.setInteger(NBT_SLAM_DELAY, delay - 1);
                player.fallDistance = 0.0f;
                return;
            }
            int t = ptag.getInteger(NBT_SLAM_PENDING_T);
            boolean bl = landed = player.onGround || player.isCollidedVertically && player.motionY <= 0.0 || player.isInLava() || player.isInWater();
            if (landed) {
                this.doMaulSlam(world, player, slamStack);
                ptag.setBoolean(NBT_SLAM_PENDING, false);
                ptag.setInteger(NBT_SLAM_PENDING_T, 0);
                ptag.setInteger(NBT_SLAM_DELAY, 0);
            } else if (t <= 0) {
                ptag.setBoolean(NBT_SLAM_PENDING, false);
                ptag.setInteger(NBT_SLAM_PENDING_T, 0);
                ptag.setInteger(NBT_SLAM_DELAY, 0);
            } else {
                ptag.setInteger(NBT_SLAM_PENDING_T, t - 1);
                player.fallDistance = 0.0f;
            }
        }
        if (!ptag.getBoolean(NBT_DASH)) {
            return;
        }
        ItemStack heldMain = player.getHeldItemMainhand();
        if (!selected || heldMain.isEmpty() || heldMain.getItem() != this) {
            ptag.setBoolean(NBT_DASH, false);
            if (!world.isRemote) {
                this.armPendingSlam(ptag, 200);
            }
            return;
        }
        if (!ptag.getString(NBT_DASH_ITEM).equals(RegistryNames.get(this).toString())) {
            ptag.setBoolean(NBT_DASH, false);
            if (!world.isRemote) {
                this.armPendingSlam(ptag, 200);
            }
            return;
        }
        int ticksLeft = ptag.getInteger(NBT_DASH_TICKS);
        int origTicks = ptag.getInteger(NBT_DASH_ORIG);
        if (ticksLeft <= 0 || origTicks <= 0) {
            if (!world.isRemote) {
                this.armPendingSlam(ptag, 200);
            }
            ptag.setBoolean(NBT_DASH, false);
            return;
        }
        double dx = ptag.getDouble(NBT_DASH_DX);
        double dy = ptag.getDouble(NBT_DASH_DY);
        double dz = ptag.getDouble(NBT_DASH_DZ);
        double totalDist = ptag.getDouble(NBT_DASH_TOTAL);
        double step = totalDist / (double)origTicks;
        player.fallDistance = 0.0f;
        if (!world.isRemote) {
            Vec3d end;
            Vec3d start = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
            RayTraceResult hitE = this.rayTraceEntities(world, player, start, end = start.addVector(dx * step, dy * step, dz * step));
            if (hitE != null && hitE.typeOfHit == RayTraceResult.Type.ENTITY && hitE.entityHit instanceof EntityLivingBase) {
                this.doMaulSlam(world, player, slamStack);
                EntityLivingBase t = (EntityLivingBase)hitE.entityHit;
                float base = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                t.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), base * 2.0f);
                ptag.setBoolean(NBT_DASH, false);
                return;
            }
            RayTraceResult hitB = world.rayTraceBlocks(start, end, false, true, false);
            if (hitB != null && hitB.typeOfHit == RayTraceResult.Type.BLOCK) {
                this.armPendingSlam(ptag, 200);
                ptag.setBoolean(NBT_DASH, false);
                return;
            }
            player.motionX = dx * step;
            player.motionZ = dz * step;
            player.motionY = dy * step;
            if (player.motionY < -1.25) {
                player.motionY = -1.25;
            }
            if (player.motionY > 1.25) {
                player.motionY = 1.25;
            }
            player.velocityChanged = true;
            ptag.setInteger(NBT_DASH_TICKS, ticksLeft - 1);
        }
    }
}

