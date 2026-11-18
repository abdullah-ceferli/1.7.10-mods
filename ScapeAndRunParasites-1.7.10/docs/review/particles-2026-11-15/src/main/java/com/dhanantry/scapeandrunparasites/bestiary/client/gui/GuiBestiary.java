/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.IAttribute
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  org.lwjgl.input.Mouse
 */
package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.bestiary.BestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.BlocksPage;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.CelestialEventsPage;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiCurrentProgress;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.ParasitesPage;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.StatsPage;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.StatusEffectsPage;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.SystemsPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

public class GuiBestiary
extends GuiScreen {
    private final EntityPlayer player;
    private BestiaryPage page = BestiaryPage.HOME;
    private ParasiteTier selectedTier = null;
    private final ParasitesPage parasitesPage;
    private boolean isJumbled;
    private BestiaryEntry selectedMob = null;
    private final List<ParasiteTier> visibleTiers = new ArrayList<ParasiteTier>();
    private final List<BestiaryEntry> visibleMobs = new ArrayList<BestiaryEntry>();
    private final Map<String, EntityLivingBase> entityCache = new HashMap<String, EntityLivingBase>();
    private float spinDeg = 0.0f;
    private int scrollTiers = 0;
    private int scrollMobs = 0;
    private static final int THUMB = 28;
    private static final int BTN_H = 20;
    private static final int V_PAD = 8;
    private static final int ROW_H = 36;
    private static final int LIST_X = 30;
    private static final int LIST_W = 140;
    private static final int LIST_TOP = 50;
    private static final int UI_THUMB_MARGIN_PX = 6;
    private static final int UI_ICON_MARGIN_PX = 3;
    private static final int UI_DETAIL_MARGIN_PX = 12;
    private static final float UI_THUMB_SHRINK = 0.92f;
    private static final float UI_ICON_SHRINK = 0.92f;
    private static final float UI_DETAIL_SHRINK = 0.88f;
    private static final float UI_SCALE_MIN = 6.0f;
    private static final float UI_SCALE_MAX = 110.0f;

    ParasiteTier getSelectedTier() {
        return this.selectedTier;
    }

    private int LIST_BOTTOM() {
        return this.height - 40;
    }

    public GuiBestiary(EntityPlayer player) {
        this.player = player;
        this.parasitesPage = new ParasitesPage(player, this);
    }

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    public static List<ParasiteTier> getDisplayOrderLocalized() {
        ArrayList<ParasiteTier> list = new ArrayList<ParasiteTier>(Arrays.asList(ParasiteTier.values()));
        list.sort(Comparator.comparing(t -> I18n.format((String)GuiBestiary.tierLangKey(t), (Object[])new Object[0]).toLowerCase(Locale.ROOT)));
        return list;
    }

    private void applyTierScrollLayout() {
        int contentH = this.visibleTiers.size() * 36;
        int maxScroll = Math.max(0, contentH - (this.LIST_BOTTOM() - 50));
        this.scrollTiers = Math.max(0, Math.min(this.scrollTiers, maxScroll));
        for (GuiButton b : this.buttonList) {
            boolean inView;
            if (!(b instanceof ListButton) || b.id < 100 || b.id >= 200) continue;
            ListButton lb = (ListButton)b;
            int rowY = lb.rowTopY - this.scrollTiers;
            b.yPosition = rowY + 4;
            b.visible = inView = rowY + 28 > 50 && rowY < this.LIST_BOTTOM();
        }
    }

    private void applyMobScrollLayout() {
        int contentH = this.visibleMobs.size() * 36;
        int maxScroll = Math.max(0, contentH - (this.LIST_BOTTOM() - 50));
        this.scrollMobs = Math.max(0, Math.min(this.scrollMobs, maxScroll));
        for (GuiButton b : this.buttonList) {
            boolean inView;
            if (!(b instanceof ListButton) || b.id < 200 || b.id >= 300) continue;
            ListButton lb = (ListButton)b;
            int rowY = lb.rowTopY - this.scrollMobs;
            b.yPosition = rowY + 4;
            b.visible = inView = rowY + 28 > 50 && rowY < this.LIST_BOTTOM();
        }
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel == 0) {
            return;
        }
        int delta = (int)Math.signum(dWheel) * -18;
        switch (this.page) {
            case PARASITES: {
                this.scrollTiers += delta * 2;
                this.applyTierScrollLayout();
                break;
            }
            case MOB_LIST: {
                this.scrollMobs += delta * 2;
                this.applyMobScrollLayout();
                break;
            }
        }
    }

    private static String loreKeyFromMobId(String mobId) {
        try {
            ResourceLocation rl = new ResourceLocation(mobId);
            return "lore." + rl.getResourceDomain() + "." + rl.getResourcePath();
        }
        catch (Exception ignored) {
            int i = mobId.indexOf(58);
            if (i > 0 && i < mobId.length() - 1) {
                return "lore." + mobId.substring(0, i) + "." + mobId.substring(i + 1);
            }
            return "lore." + mobId;
        }
    }

    public static String tierLangKey(ParasiteTier t) {
        String key = t.name().toLowerCase(Locale.ROOT);
        if ("infected".equals(key)) {
            key = "assimilated";
        }
        return "bestiary.tier." + key;
    }

    public void initGui() {
        super.initGui();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.buttonList.clear();
        switch (this.page) {
            case HOME: {
                int cx = this.width / 2;
                int startY = 78;
                int gap = 24;
                int w = 150;
                int x = cx - w / 2;
                this.buttonList.add(new GuiButton(10, x, startY + gap * 0, w, 20, this.distort(I18n.format((String)"bestiary.tab.parasites", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(11, x, startY + gap * 1, w, 20, this.distort(I18n.format((String)"bestiary.tab.blocks", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(12, x, startY + gap * 2, w, 20, this.distort(I18n.format((String)"bestiary.tab.celestial", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(13, x, startY + gap * 3, w, 20, this.distort(I18n.format((String)"bestiary.tab.effects", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(14, x, startY + gap * 4, w, 20, this.distort(I18n.format((String)"bestiary.tab.systems", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(15, x, startY + gap * 5, w, 20, this.distort(I18n.format((String)"bestiary.tab.stats", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(16, x, startY + gap * 6, w, 20, this.distort(I18n.format((String)"bestiary.tab.current_progress", (Object[])new Object[0]))));
                break;
            }
            case MOB_LIST: {
                this.visibleMobs.clear();
                IBestiaryProgress prog = (IBestiaryProgress)this.player.getCapability(BestiaryCapability.CAP, null);
                if (prog != null && this.selectedTier != null) {
                    for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
                        if (e.tier != this.selectedTier || !GuiBestiary.isKnown(prog, e)) continue;
                        this.visibleMobs.add(e);
                    }
                }
                int rowTop = 50;
                for (int i = 0; i < this.visibleMobs.size(); ++i) {
                    BestiaryEntry e = this.visibleMobs.get(i);
                    String name = this.distort(I18n.format((String)e.nameKey, (Object[])new Object[0]));
                    this.buttonList.add(new ListButton(200 + i, 30, rowTop, 140, 20, name));
                    rowTop += 36;
                }
                this.applyMobScrollLayout();
                this.buttonList.add(new GuiButton(2, 10, 10, 80, 20, this.distort(I18n.format((String)"bestiary.nav.tiers", (Object[])new Object[0]))));
                break;
            }
            case MOB_DETAIL: {
                this.buttonList.add(new GuiButton(3, 10, 10, 100, 20, I18n.format((String)"bestiary.nav.mob_list", (Object[])new Object[0])));
            }
        }
    }

    private void drawPanel(int x, int y, int w, int h) {
        GuiBestiary.drawRect((int)(x - 1), (int)(y - 1), (int)(x + w + 1), (int)(y + h + 1), (int)-1442840576);
        GuiBestiary.drawRect((int)x, (int)y, (int)(x + w), (int)(y + h), (int)-2013265920);
    }

    private void drawHeaderBar(int x, int y, int w, int h) {
        GuiBestiary.drawRect((int)x, (int)y, (int)(x + w), (int)(y + h), (int)-1441722095);
        GuiBestiary.drawRect((int)x, (int)(y + h - 1), (int)(x + w), (int)(y + h), (int)-1442840576);
    }

    private static boolean isKnown(IBestiaryProgress prog, BestiaryEntry e) {
        if (prog == null || e == null) {
            return false;
        }
        if (prog.getKills(e.mobId) > 0) {
            return true;
        }
        return prog.isMobSeen(e.mobId);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            this.page = BestiaryPage.HOME;
            this.selectedTier = null;
            this.selectedMob = null;
            this.initGui();
            return;
        }
        switch (this.page) {
            case HOME: {
                if (button.id == 10) {
                    this.parasitesPage.openParasitesRoot();
                    this.mc.displayGuiScreen((GuiScreen)this.parasitesPage);
                    return;
                }
                if (button.id == 11) {
                    this.mc.displayGuiScreen((GuiScreen)new BlocksPage(this.player, this));
                    return;
                }
                if (button.id == 12) {
                    this.mc.displayGuiScreen((GuiScreen)new CelestialEventsPage(this.player, this));
                    return;
                }
                if (button.id == 13) {
                    this.mc.displayGuiScreen((GuiScreen)new StatusEffectsPage(this.player, this));
                    return;
                }
                if (button.id == 14) {
                    this.mc.displayGuiScreen((GuiScreen)new SystemsPage(this.player, this));
                    return;
                }
                if (button.id == 15) {
                    this.mc.displayGuiScreen((GuiScreen)new StatsPage(this.player, this));
                    return;
                }
                if (button.id != 16) break;
                this.mc.displayGuiScreen((GuiScreen)new GuiCurrentProgress(this.player, this));
                return;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        String title = this.distort(I18n.format((String)"lore.srparasites.compendium", (Object[])new Object[0]));
        int titleW = this.fontRendererObj.getStringWidth(title);
        this.drawString(this.fontRendererObj, title, this.width - titleW - 10, 12, 0xFFFFFF);
        this.spinDeg += partialTicks * 1.5f;
        switch (this.page) {
            case HOME: {
                int cardW = 170;
                int cardH = 190;
                int cardX = (this.width - cardW) / 2;
                int cardY = 55;
                this.drawPanel(cardX, cardY, cardW, cardH);
                this.drawHeaderBar(cardX, cardY, cardW, 16);
                this.drawCenteredString(this.fontRendererObj, this.distort(I18n.format((String)"bestiary.select_category", (Object[])new Object[0])), this.width / 2, cardY + 4, 0xAAAAAA);
                break;
            }
            case PARASITES: {
                this.drawString(this.fontRendererObj, this.distort(I18n.format((String)"bestiary.parasite_tiers", (Object[])new Object[0])), 20, 50, 0xFFFFFF);
                if (this.selectedTier == null) break;
                this.drawTierPreview(this.selectedTier, 180, 70);
                break;
            }
            case MOB_LIST: {
                int leftX = 22;
                int leftY = 36;
                int leftW = 156;
                int leftH = this.LIST_BOTTOM() - 50 + 22;
                this.drawPanel(leftX, leftY, leftW, leftH);
                this.drawHeaderBar(leftX, leftY, leftW, 16);
                this.drawString(this.fontRendererObj, this.distort(I18n.format((String)"bestiary.mob_list", (Object[])new Object[0])), leftX + 6, leftY + 4, 0xCCCCCC);
                int rightX = 182;
                int rightY = 36;
                int rightW = this.width - rightX - 12;
                int rightH = leftH;
                this.drawPanel(rightX, rightY, rightW, rightH);
                this.drawHeaderBar(rightX, rightY, rightW, 16);
                break;
            }
            case MOB_DETAIL: {
                if (this.selectedMob == null) break;
                this.drawMobDetail(this.selectedMob, 20, 40);
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawTierPreview(ParasiteTier tier, int x, int y) {
        IBestiaryProgress prog = (IBestiaryProgress)this.player.getCapability(BestiaryCapability.CAP, null);
        if (prog == null) {
            return;
        }
        int shown = 0;
        for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
            if (e.tier != tier || !GuiBestiary.isKnown(prog, e)) continue;
            String name = this.distort(I18n.format((String)e.nameKey, (Object[])new Object[0]));
            this.fontRendererObj.drawString(name, x + 30, y + 4, 0xDDDDDD);
            float scale = SRPBestiaryRegistry.getRenderScale(e.mobId);
            this.renderEntityPreview(e.mobId, x + 14, y + 14, 28, 28, this.spinDeg, 3, 0.92f);
            y += 32;
            if (++shown < 5) continue;
            break;
        }
        if (shown == 0) {
            this.fontRendererObj.drawString(this.distort(I18n.format((String)"bestiary.unlock_by_kill", (Object[])new Object[0])), x, y, 0x777777);
        }
    }

    private void drawMobListWithRenders(List<BestiaryEntry> mobs, int xRight) {
        IBestiaryProgress prog = (IBestiaryProgress)this.player.getCapability(BestiaryCapability.CAP, null);
        if (prog == null) {
            return;
        }
        int rowY = 50 - this.scrollMobs;
        for (BestiaryEntry e : mobs) {
            int rowTop = rowY;
            int rowBot = rowTop + 28;
            if (rowBot > 50 && rowTop < this.LIST_BOTTOM()) {
                int centerY = rowTop + 14;
                this.renderEntityPreview(e.mobId, xRight + 14, centerY, 28, 28, this.spinDeg, 6, 0.92f);
                int textY = centerY - this.fontRendererObj.FONT_HEIGHT / 2;
                int kills = prog.getKills(e.mobId);
                String rowPlain = I18n.format((String)e.nameKey, (Object[])new Object[0]) + " (" + I18n.format((String)"bestiary.kills_label", (Object[])new Object[0]) + ": " + kills + ")";
                String rowDraw = this.isJumbled ? GuiDistortionHelper.jamText(rowPlain) : rowPlain;
                this.fontRendererObj.drawString(rowDraw, xRight + 28 + 8, textY, 0xCCCCCC);
            }
            rowY += 36;
        }
        if (mobs.isEmpty()) {
            this.fontRendererObj.drawString(this.distort(I18n.format((String)"bestiary.no_mobs_unlocked_in_tier", (Object[])new Object[0])), xRight, 50, 0x777777);
        }
    }

    private void drawMobDetail(BestiaryEntry e, int x, int y) {
        IBestiaryProgress prog = (IBestiaryProgress)this.player.getCapability(BestiaryCapability.CAP, null);
        if (prog == null) {
            this.fontRendererObj.drawString(this.distort(I18n.format((String)"bestiary.cap_missing_client", (Object[])new Object[0])), x, y, 0xFF5555);
            return;
        }
        int kills = prog.getKills(e.mobId);
        String nameLinePlain = I18n.format((String)e.nameKey, (Object[])new Object[0]) + " (" + I18n.format((String)"bestiary.kills", (Object[])new Object[0]) + ": " + kills + ")";
        String nameLine = this.isJumbled ? GuiDistortionHelper.jamText(nameLinePlain) : nameLinePlain;
        this.fontRendererObj.drawString(nameLine, x, y, 0xFFFFFF);
        int boxW = 160;
        int boxH = 120;
        int cx = x + 80;
        int cy = (y += 6) + 10 + 60;
        this.renderEntityPreview(e.mobId, cx, cy, 160, 120, this.spinDeg, 12, 0.88f);
        int infoX = x + 160 + 14;
        int infoY = y + 4;
        if (kills >= 3) {
            double hp = e.baseHp > 0 ? (double)e.baseHp : this.readEntityStat(e.mobId, SharedMonsterAttributes.maxHealth);
            double dmg = e.baseDamage > 0.0f ? (double)e.baseDamage : this.readEntityStat(e.mobId, SharedMonsterAttributes.attackDamage);
            String statsLine = this.distort(I18n.format((String)"bestiary.stats", (Object[])new Object[]{String.valueOf((int)Math.round(hp)), String.valueOf((float)dmg)}));
            this.fontRendererObj.drawString(statsLine, infoX, infoY, 0xAAAAAA);
            infoY += 12;
        } else {
            String gate3 = this.distort(I18n.format((String)"bestiary.more_info_at_n", (Object[])new Object[]{3}));
            this.fontRendererObj.drawString(gate3, infoX, infoY, 0x666666);
            infoY += 12;
        }
        y = y + 10 + 120 + 10;
        if (kills >= 10) {
            String lore = I18n.format((String)GuiBestiary.loreKeyFromMobId(e.mobId), (Object[])new Object[0]);
            this.fontRendererObj.drawSplitString(lore, x, y + 6, this.width - x - 20, 0xCCCCCC);
        } else {
            String gate10 = this.distort(I18n.format((String)"bestiary.lore_unlocks_at_n", (Object[])new Object[]{10}));
            this.fontRendererObj.drawString(gate10, x, y + 6, 0x666666);
        }
    }

    private double readEntityStat(String mobId, IAttribute attr) {
        Entity ent = EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(mobId), (World)this.player.worldObj);
        if (!(ent instanceof EntityLivingBase)) {
            return -1.0;
        }
        EntityLivingBase living = (EntityLivingBase)ent;
        IAttributeInstance inst = living.getEntityAttribute(attr);
        return inst != null ? inst.getBaseValue() : -1.0;
    }

    private float computeAutoScale(EntityLivingBase ent, int boxW, int boxH) {
        float padW = Math.max(0, boxW - 4);
        float padH = Math.max(0, boxH - 4);
        float w = Math.max(0.6f, ent.width);
        float h = Math.max(0.6f, ent.height);
        float scaleW = padW / w;
        float scaleH = padH / h;
        float scale = Math.min(scaleW, scaleH);
        scale = Math.max(8.0f, Math.min(scale, 120.0f));
        return scale;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderEntityPreview(String mobId, int cx, int cy, int boxW, int boxH, float yawDeg, int marginPx, float extraShrink) {
        EntityLivingBase ent = this.entityCache.computeIfAbsent(mobId, id -> {
            Entity created = EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(id), (World)this.player.worldObj);
            return created instanceof EntityLivingBase ? (EntityLivingBase)created : null;
        });
        if (ent == null) {
            return;
        }
        float oYawOff = ent.renderYawOffset;
        float oYawHead = ent.rotationYawHead;
        float oYaw = ent.rotationYaw;
        float oPitch = ent.rotationPitch;
        float ppYawOff = ent.prevRenderYawOffset;
        float ppYawHead = ent.prevRotationYawHead;
        float ppYaw = ent.prevRotationYaw;
        float ppPitch = ent.prevRotationPitch;
        float oLimb = ent.limbSwing;
        float oLimbAmt = ent.limbSwingAmount;
        float ppLimbAmt = ent.prevLimbSwingAmount;
        int oTicks = ent.ticksExisted;
        ent.ticksExisted = 0;
        ent.prevLimbSwingAmount = 0.0f;
        ent.limbSwingAmount = 0.0f;
        ent.limbSwing = 0.0f;
        ent.prevRotationPitch = 0.0f;
        ent.rotationPitch = 0.0f;
        ent.renderYawOffset = ent.prevRenderYawOffset = yawDeg;
        ent.rotationYawHead = ent.prevRotationYawHead = yawDeg;
        ent.rotationYaw = ent.prevRotationYaw = yawDeg;
        int innerW = Math.max(0, boxW - (marginPx << 1));
        int innerH = Math.max(0, boxH - (marginPx << 1));
        float scale = this.computeAutoScale(ent, innerW, innerH);
        scale *= SRPBestiaryRegistry.getRenderScale(mobId);
        if (extraShrink > 0.0f) {
            scale *= extraShrink;
        }
        scale = Math.max(6.0f, Math.min(scale, 110.0f));
        GlStateManager.pushMatrix();
        try {
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.translate((float)cx, (float)cy, (float)50.0f);
            GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
            GlStateManager.rotate((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            RenderManager rm = this.mc.getRenderManager();
            boolean prevShadow = rm.isRenderShadow();
            float prevViewY = rm.playerViewY;
            rm.setPlayerViewY(180.0f);
            rm.setRenderShadow(false);
            rm.renderEntity((Entity)ent, 0.0, 0.0, 0.0, 0.0f, this.mc.getRenderPartialTicks(), true);
            rm.setRenderShadow(prevShadow);
            rm.setPlayerViewY(prevViewY);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableTexture2D();
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
        }
        finally {
            GlStateManager.popMatrix();
            ent.renderYawOffset = oYawOff;
            ent.prevRenderYawOffset = ppYawOff;
            ent.rotationYawHead = oYawHead;
            ent.prevRotationYawHead = ppYawHead;
            ent.rotationYaw = oYaw;
            ent.prevRotationYaw = ppYaw;
            ent.rotationPitch = oPitch;
            ent.prevRotationPitch = ppPitch;
            ent.limbSwing = oLimb;
            ent.limbSwingAmount = oLimbAmt;
            ent.prevLimbSwingAmount = ppLimbAmt;
            ent.ticksExisted = oTicks;
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public static class TierButton
    extends GuiButton {
        final ParasiteTier tier;
        final ItemStack icon;
        public int rowTopY;

        TierButton(int id, int x, int y, int w, int h, String text, ParasiteTier tier, ItemStack icon) {
            super(id, x, y, w, h, text);
            this.rowTopY = y;
            this.tier = tier;
            this.icon = icon == null ? ItemStack.EMPTY : icon;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            super.drawButton(mc, mouseX, mouseY, partialTicks);
            if (!this.visible) {
                return;
            }
            int iconX = this.xPosition + 4;
            int iconY = this.yPosition + (this.height - 16) / 2;
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(this.icon, iconX, iconY);
            RenderHelper.disableStandardItemLighting();
        }
    }

    private static class ListButton
    extends GuiButton {
        final int rowTopY;

        ListButton(int id, int x, int rowTopY, int w, int h, String txt) {
            super(id, x, rowTopY, w, h, txt);
            this.rowTopY = rowTopY;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            boolean hover = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int bg = hover ? 0x66222222 : 0x44111111;
            ListButton.drawRect((int)(this.xPosition - 2), (int)(this.yPosition - 2), (int)(this.xPosition + this.width + 2), (int)(this.yPosition + this.height + 2), (int)-1442840576);
            ListButton.drawRect((int)(this.xPosition - 1), (int)(this.yPosition - 1), (int)(this.xPosition + this.width + 1), (int)(this.yPosition + this.height + 1), (int)bg);
            int color = hover ? 0xFFFFFF : 0xDDDDDD;
            this.drawString(mc.fontRenderer, this.displayString, this.xPosition + 6, this.yPosition + 6, color);
        }
    }

    private static enum BestiaryPage {
        HOME,
        PARASITES,
        MOB_LIST,
        MOB_DETAIL,
        STATS;

    }
}

