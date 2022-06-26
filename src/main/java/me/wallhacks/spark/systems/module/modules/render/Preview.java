package me.wallhacks.spark.systems.module.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;

@Module.Registration(name = "PreViewer", description = "Shulker and Map Preview")
public class Preview extends Module {
    public static Preview INSTANCE;

    public Preview() {
        INSTANCE = this;
    }

    BooleanSetting showNametags = new BooleanSetting("ShowNametags", this, true);
    DoubleSetting scale = new DoubleSetting("Scale", this, 2, 1, 3);


    BooleanSetting hotbarPreview = new BooleanSetting("HotbarShulker", this, false);
    BooleanSetting hoverPreview = new BooleanSetting("HoverShulker", this, true);
    BooleanSetting hovermapPreview = new BooleanSetting("HoverMap", this, true);

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(showNametags.isOn())
            for(Entity entity : mc.world.loadedEntityList){
                if(entity instanceof EntityItem){
                    int id = Item.getIdFromItem(((EntityItem)entity).getItem().getItem());

                    if(id > 218 && id < 235){
                        float dis = Math.max(3,Math.min(RenderUtil.getRenderDistance(entity.getPositionVector()),80))-2;
                        float ma = (float) (scale.getValue() * 0.04f);
                        float mi = (float) (scale.getValue() * 0.005f);
                        float m = ma-mi;
                        float size = mi + (m / 40f)*dis;
                        renderNameTagShulker((EntityItem)entity,size);
                    }
                }

            }
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Chat event) {
        if(mc.player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox && hotbarPreview.isOn())
        {
            ScaledResolution scaledresolution = new ScaledResolution(mc);

            int y = (int) (scaledresolution.getScaledHeight() - 61 - 60);
            int x = (int) ((scaledresolution.getScaledWidth() - 144) / 2f);

            RenderShulkerPreviewTooltips(mc.player.getHeldItemMainhand(),x,y);


        }
    }

    @SubscribeEvent
    public void onToolTip(RenderTooltipEvent.Pre event) {

        int x = event.getX();
        int y = event.getY();
        ItemStack item = event.getStack();


        if (item.getItem() instanceof ItemShulkerBox)
        {
            if(hoverPreview.isOn())
            {
                x += 12;
                y -= 12;
                RenderShulkerPreviewTooltips(item,x,y);
                event.setCanceled(true);
            }
        }
        else if (item.getItem() instanceof ItemMap)
        {
            if(hovermapPreview.isOn())
            {
                x += 12;
                y -= 12;


                RenderMapPreviewToolTips(item,x,y);
                event.setCanceled(true);
            }

        }


    }


    void RenderMapPreviewToolTips(ItemStack item, int x, int y){

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        float z = mc.getRenderItem().zLevel ;
        mc.getRenderItem().zLevel = 300.0F;

        int l1 = x;
        int i2 = y;
        int k = 60+10;
        int i0 = 60;

        drawGradientRect(l1 - 3, i2 - 4, l1 + i0 + 3, i2 - 3, -267386864, -267386864);
        drawGradientRect(l1 - 3, i2 + k + 3, l1 + i0 + 3, i2 + k + 4, -267386864, -267386864);
        drawGradientRect(l1 - 3, i2 - 3, l1 + i0 + 3, i2 + k + 3, -267386864, -267386864);
        drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
        drawGradientRect(l1 + i0 + 3, i2 - 3, l1 + i0 + 4, i2 + k + 3, -267386864, -267386864);
        int i1 = 1347420415;
        int j1 = 1344798847;
        drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
        drawGradientRect(l1 + i0 + 2, i2 - 3 + 1, l1 + i0 + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
        drawGradientRect(l1 - 3, i2 - 3, l1 + i0 + 3, i2 - 3 + 1, 1347420415, 1347420415);
        drawGradientRect(l1 - 3, i2 + k + 2, l1 + i0 + 3, i2 + k + 3, 1344798847, 1344798847);


        MapData mapdata = Items.FILLED_MAP.getMapData(item, this.mc.world);

        String toolTip = item.getTooltip(mc.player,mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).get(0);
        // text
        mc.fontRenderer.drawStringWithShadow(toolTip, x, y , -1);

        if(mapdata == null)
        {
            mc.fontRenderer.drawStringWithShadow(ChatFormatting.RED +"No MapData!", x, y+15 , -1);

            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            mc.fontRenderer.drawStringWithShadow(ChatFormatting.RED +"Move map to inventory", x*2, y*2+50 , -1);
            mc.fontRenderer.drawStringWithShadow(ChatFormatting.RED +"to get data from server!", x*2, y*2+60 , -1);
        }


        GlStateManager.color(1,1,1,1);

        GlStateManager.translate(x-1, y+9, 0);
        GlStateManager.scale(0.48f, 0.48f, 0.48f);




        if (mapdata != null)
        {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableLighting();

        mc.getRenderItem().zLevel = z;
    }

    void RenderShulkerPreviewTooltips(ItemStack shulker, int x, int y)
    {

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        RenderHelper.enableStandardItemLighting();


        float z = mc.getRenderItem().zLevel;
        mc.getRenderItem().zLevel = 300.0F;

        // background

        int l1 = x;
        int i2 = y;
        int k = 61;
        int i0 = 144;

        drawGradientRect(l1 - 3, i2 - 4, l1 + i0 + 3, i2 - 3, -267386864, -267386864);
        drawGradientRect(l1 - 3, i2 + k + 3, l1 + i0 + 3, i2 + k + 4, -267386864, -267386864);
        drawGradientRect(l1 - 3, i2 - 3, l1 + i0 + 3, i2 + k + 3, -267386864, -267386864);
        drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
        drawGradientRect(l1 + i0 + 3, i2 - 3, l1 + i0 + 4, i2 + k + 3, -267386864, -267386864);
        int i1 = 1347420415;
        int j1 = 1344798847;
        drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
        drawGradientRect(l1 + i0 + 2, i2 - 3 + 1, l1 + i0 + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
        drawGradientRect(l1 - 3, i2 - 3, l1 + i0 + 3, i2 - 3 + 1, 1347420415, 1347420415);
        drawGradientRect(l1 - 3, i2 + k + 2, l1 + i0 + 3, i2 + k + 3, 1344798847, 1344798847);


        String toolTip = shulker.getTooltip(mc.player,mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).get(0);
        // text
        mc.fontRenderer.drawStringWithShadow(toolTip, x, y , -1);

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();


        NBTTagCompound tagCompound = shulker.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10))
        {
            NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
            if (blockEntityTag.hasKey("Items", 9))
            {

                NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist); // load the itemstacks from the tag to


                y += mc.fontRenderer.FONT_HEIGHT+2;

                // loop through items in shulker inventory
                for (int i = 0; i < nonnulllist.size(); i++)
                {
                    ItemStack itemStack = nonnulllist.get(i);
                    int offsetX = (i % 9) * 16;
                    int offsetY = (i / 9) * 16;
                    if(!nonnulllist.isEmpty()){
                        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX + x, offsetY + y);
                        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX + x, offsetY + y, null);
                    }
                }

            }
        }

        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = z;

        GlStateManager.enableDepth();

    }

    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public void renderNameTagShulker(EntityItem e, float size)
    {

        ItemStack shulker = e.getItem();
        NBTTagCompound tagCompound = shulker.getTagCompound();



        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);

        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10))
        {
            NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
            if (blockEntityTag.hasKey("Items", 9))
                ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist); // load the itemstacks from the tag to
        }



        double p_188388_2_ = mc.getRenderPartialTicks();

        double d0 = e.lastTickPosX + (e.posX - e.lastTickPosX) * p_188388_2_;
        double d1 = e.lastTickPosY + (e.posY - e.lastTickPosY) * p_188388_2_;
        double d2 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * p_188388_2_;

        float x = (float) d0;


        float y = (float) d1;
        float z = (float) d2;

        y = y + size*5;


        if(mc.getRenderManager().options == null)
            return;


        float p_189692_6_ = mc.getRenderManager().playerViewY;
        float p_189692_7_ = mc.getRenderManager().playerViewX;
        float p_189692_2_ =	(float) (x	- mc.getRenderManager().viewerPosX);
        float p_189692_3_ =	(float) (y	- mc.getRenderManager().viewerPosY);
        float p_189692_4_ = (float) (z	- mc.getRenderManager().viewerPosZ);


        GL11.glPushMatrix();
        GlStateManager.translate(p_189692_2_, p_189692_3_, p_189692_4_);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-p_189692_6_, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(mc.getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * p_189692_7_, 1.0F, 0.0F, 0.0F);


        GlStateManager.scale(-size, -size, size);


        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();

        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)(-1 - 1), (double)(-mc.fontRenderer.FONT_HEIGHT-4), 0.0D).color(0.1F, 0.1F, 0.1F, 0.5F).endVertex();
        bufferbuilder.pos((double)(-1 - 1), (double)(50), 0.0D).color(0.1F, 0.1F, 0.1F, 0.5F).endVertex();
        bufferbuilder.pos((double)(144 + 1), (double)(50), 0.0D).color(0.1F, 0.1F, 0.1F, 0.5F).endVertex();
        bufferbuilder.pos((double)(144 + 1), (double)(-mc.fontRenderer.FONT_HEIGHT-4), 0.0D).color(0.1F, 0.1F, 0.1F, 0.5F).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);


        //items
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();


        for (int i = 0; i < nonnulllist.size(); i++)
        {
            ItemStack itemStack = nonnulllist.get(i);
            int offsetX = (i % 9) * 16;
            int offsetY = (i / 9) * 16;
            if(!nonnulllist.isEmpty())
            {
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);

            }
        }

        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();

        GlStateManager.popMatrix();


        mc.fontRenderer.drawStringWithShadow(shulker.getDisplayName(), 0,-mc.fontRenderer.FONT_HEIGHT-2 , -1);





        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.scale(-1, -1, size);

        GL11.glPopMatrix();



    }


}

