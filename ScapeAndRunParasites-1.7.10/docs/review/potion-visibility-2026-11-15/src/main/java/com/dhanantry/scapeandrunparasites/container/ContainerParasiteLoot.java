/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IContainerListener
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 */
package com.dhanantry.scapeandrunparasites.container;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class ContainerParasiteLoot
extends Container {
    private final TileEntityParasiteLoot te;
    private int interactionPulse = 0;
    public int clientPulse = 0;
    private static final float MIN_DAMAGE = 0.5f;
    private static final float MAX_DAMAGE = 8.0f;
    private static final DamageSource PARASITIC = DamageSource.magic;

    public ContainerParasiteLoot(InventoryPlayer playerInv, final TileEntityParasiteLoot te, EntityPlayer player) {
        int c;
        int r;
        this.te = te;
        int idx = 0;
        for (r = 0; r < 3; ++r) {
            for (c = 0; c < 9; ++c) {
                this.addSlotToContainer(new Slot(te, idx++, 8 + c * 18, 30 + r * 18){

                    public boolean isItemValid(ItemStack stack) {
                        return TileEntityParasiteLoot.isValidParasiteLootItem(stack);
                    }

                    public ItemStack onTake(EntityPlayer p, ItemStack stack) {
                        float f = te.getFullness();
                        float dmg = 0.5f + 7.5f * (1.0f - f);
                        if (!p.worldObj.isRemote) {
                            ContainerParasiteLoot.this.interactionPulse = ContainerParasiteLoot.this.interactionPulse + 1 & Short.MAX_VALUE;
                            if (SRPConfigWorld.parasiteLootDamageOnTake && dmg > 0.0f) {
                                p.attackEntityFrom(PARASITIC, dmg);
                            }
                            ContainerParasiteLoot.this.applyParasiteEffects(p, f);
                        }
                        return super.onTake(p, stack);
                    }
                });
            }
        }
        for (r = 0; r < 3; ++r) {
            for (c = 0; c < 9; ++c) {
                this.addSlotToContainer(new Slot((IInventory)playerInv, c + r * 9 + 9, 8 + c * 18, 88 + r * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot((IInventory)playerInv, i, 8 + i * 18, 146));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.te.isUseableByPlayer(playerIn);
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack in = slot.getStack();
            ret = in.copy();
            boolean teStart = false;
            int teEnd = 27;
            int invStart = 27;
            int invEnd = 54;
            int hotStart = 54;
            int hotEnd = 63;
            if (index < 27) {
                float fBefore = this.te.getFullness();
                if (!this.mergeItemStack(in, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
                if (!playerIn.worldObj.isRemote) {
                    float dmg;
                    if (SRPConfigWorld.parasiteLootDamageOnTake && (dmg = 0.5f + 7.5f * (1.0f - fBefore)) > 0.0f) {
                        playerIn.attackEntityFrom(PARASITIC, dmg);
                    }
                    if (fBefore < 1.0f) {
                        int viralDur;
                        int viralAmp;
                        int corrosDur;
                        int corrosAmp;
                        if (fBefore < 0.25f) {
                            corrosAmp = 1;
                            corrosDur = 400;
                            viralAmp = 3;
                            viralDur = 600;
                        } else if (fBefore < 0.5f) {
                            corrosAmp = 1;
                            corrosDur = 200;
                            viralAmp = 2;
                            viralDur = 400;
                        } else if (fBefore < 0.75f) {
                            corrosAmp = 0;
                            corrosDur = 200;
                            viralAmp = 1;
                            viralDur = 200;
                        } else {
                            corrosAmp = 0;
                            corrosDur = 100;
                            viralAmp = 1;
                            viralDur = 100;
                        }
                        playerIn.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.CORRO_E, corrosDur, corrosAmp, false, false));
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)playerIn, viralDur, viralAmp);
                    }
                    this.interactionPulse = this.interactionPulse + 1 & Short.MAX_VALUE;
                }
            } else if (index < 54) {
                if (!this.mergeItemStack(in, 0, 27, false)) {
                    if (!this.mergeItemStack(in, 54, 63, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!playerIn.worldObj.isRemote) {
                    this.interactionPulse = this.interactionPulse + 1 & Short.MAX_VALUE;
                }
            } else if (index < 63) {
                if (!this.mergeItemStack(in, 0, 27, false)) {
                    if (!this.mergeItemStack(in, 27, 54, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!playerIn.worldObj.isRemote) {
                    this.interactionPulse = this.interactionPulse + 1 & Short.MAX_VALUE;
                }
            }
            if (in.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (in.getCount() == ret.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, in);
        }
        return ret;
    }

    private void applyParasiteEffects(EntityPlayer p, float fullness) {
        int viralDur;
        int viralAmp;
        int corrosDur;
        int corrosAmp;
        if (p.worldObj.isRemote) {
            return;
        }
        if (fullness < 0.25f) {
            corrosAmp = 1;
            corrosDur = 400;
            viralAmp = 3;
            viralDur = 600;
        } else if (fullness < 0.5f) {
            corrosAmp = 1;
            corrosDur = 200;
            viralAmp = 2;
            viralDur = 400;
        } else if (fullness < 0.75f) {
            corrosAmp = 0;
            corrosDur = 200;
            viralAmp = 1;
            viralDur = 200;
        } else if (fullness < 1.0f) {
            corrosAmp = 0;
            corrosDur = 100;
            viralAmp = 1;
            viralDur = 100;
        } else {
            return;
        }
        p.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.CORRO_E, corrosDur, corrosAmp, false, false));
        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)p, viralDur, viralAmp);
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener l : this.crafters) {
            l.sendProgressBarUpdate((Container)this, 0, this.te.getFullnessField());
            l.sendProgressBarUpdate((Container)this, 1, this.interactionPulse);
        }
    }

    public void updateProgressBar(int id, int data) {
        if (id == 1) {
            this.clientPulse = data;
        }
    }

    public void addCraftingToCrafters(IContainerListener l) {
        super.addCraftingToCrafters(l);
        l.sendProgressBarUpdate((Container)this, 0, this.te.getFullnessField());
        l.sendProgressBarUpdate((Container)this, 1, this.interactionPulse);
    }
}

