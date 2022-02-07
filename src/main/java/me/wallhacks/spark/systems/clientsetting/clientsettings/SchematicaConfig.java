package me.wallhacks.spark.systems.clientsetting.clientsettings;

import com.github.lunatrius.schematica.client.gui.control.GuiSchematicControl;
import com.github.lunatrius.schematica.client.gui.load.GuiSchematicLoad;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.InputEvent;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.systems.setting.settings.BlockListSelectSetting;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.KeySetting;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ClientSetting.Registration(name = "SchematicaConfig", description = "Configs for schematica")
public class SchematicaConfig extends ClientSetting {
    public static SchematicaConfig INSTANCE;
    public IntSetting renderDistance = new IntSetting("RenderDistance", this, 8, 1, 64);
    public BlockListSelectSetting extraAirBlocks = new BlockListSelectSetting("ExtraAirBlocks", this, new Block[]{});
    public BooleanSetting highlight = new BooleanSetting("Highlight", this, true);
    public BooleanSetting highlightAir = new BooleanSetting("HighlightAir", this, true);
    public BooleanSetting showDebugInfo = new BooleanSetting("ShowDebugInfo", this, false);


    public KeySetting LOAD = new KeySetting("LoadSchematic", this, -1, "Binds");
    public KeySetting CONTROL = new KeySetting("MoveSchematic", this, -1, "Binds");
    public KeySetting LAYER_INC = new KeySetting("NextLayer", this, -1, "Binds");
    public KeySetting LAYER_DEC = new KeySetting("PreviousLayer", this, -1, "Binds");
    public KeySetting LAYER_TOGGLE = new KeySetting("ToggleLayerMode", this, -1, "Binds");
    public KeySetting RENDER_TOGGLE = new KeySetting("ToggleRender", this, -1, "Binds");
    public KeySetting MOVE_HERE = new KeySetting("MoveHere", this, -1, "Binds");
    public KeySetting PICK_BLOC = new KeySetting("PickBlock", this, -1, "Binds");

    public SchematicaConfig() {
        super();
        Spark.eventBus.register(this);
        INSTANCE = this;
    }

    public static SchematicaConfig getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        Setting setting = event.getSetting();
        if (!this.getSettings().contains(setting)) return;
        if (setting.getCategory().equals("General") && setting != showDebugInfo) RenderSchematic.INSTANCE.refresh();
        if (setting.getCategory().equals("Binds")) {
            for (Setting bind : this.getSettings()) {
                if (!bind.getCategory().equals("Binds") || bind == setting) continue;
                if (((KeySetting) bind).getKey() == -1) continue;
                if (((KeySetting) bind).getKey() == ((KeySetting) setting).getKey()) bind.setValue(-1);
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(final InputEvent event) {
        if (mc.currentScreen == null) {
            final SchematicWorld schematic = ClientProxy.schematic;
            int key = event.getKey();
            if (key == LOAD.getKey())
                mc.displayGuiScreen(new GuiSchematicLoad(mc.currentScreen));

            if (key == CONTROL.getKey())
                mc.displayGuiScreen(new GuiSchematicControl(mc.currentScreen));

            if (key == LAYER_INC.getKey())
                if (schematic != null && schematic.layerMode != SchematicWorld.LayerMode.ALL) {
                    schematic.renderingLayer = MathHelper.clamp(schematic.renderingLayer + 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }

            if (key == LAYER_DEC.getKey())
                if (schematic != null && schematic.layerMode != SchematicWorld.LayerMode.ALL) {
                    schematic.renderingLayer = MathHelper.clamp(schematic.renderingLayer - 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }


            if (key == LAYER_TOGGLE.getKey())
                if (schematic != null) {
                    schematic.layerMode = SchematicWorld.LayerMode.next(schematic.layerMode);
                    RenderSchematic.INSTANCE.refresh();
                }


            if (key == RENDER_TOGGLE.getKey())
                if (schematic != null) {
                    schematic.isRendering = !schematic.isRendering;
                    RenderSchematic.INSTANCE.refresh();
                }


            if (key == MOVE_HERE.getKey())
                if (schematic != null) {
                    ClientProxy.moveSchematicToPlayer(schematic);
                    RenderSchematic.INSTANCE.refresh();
                }


            if (key == PICK_BLOC.getKey())
                if (schematic != null && schematic.isRendering) {
                    pickBlock(schematic, ClientProxy.objectMouseOver);
                }
        }
    }

    private boolean pickBlock(final SchematicWorld schematic, final RayTraceResult objectMouseOver) {
        if (objectMouseOver == null) {
            return false;
        }

        if (objectMouseOver.typeOfHit == RayTraceResult.Type.MISS) {
            return false;
        }

        final EntityPlayerSP player = mc.player;
        if (!ForgeHooks.onPickBlock(objectMouseOver, player, schematic)) {
            return true;
        }

        if (player.capabilities.isCreativeMode) {
            final int slot = player.inventoryContainer.inventorySlots.size() - 10 + player.inventory.currentItem;
            mc.playerController.sendSlotPacket(player.inventory.getStackInSlot(player.inventory.currentItem), slot);
            return true;
        }

        return false;
    }
}
