package baritone.selection;

import baritone.Baritone;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.listener.AbstractGameEventListener;
import baritone.api.selection.ISelection;
import baritone.utils.IRenderer;
import net.minecraft.util.math.AxisAlignedBB;

public class SelectionRenderer implements IRenderer, AbstractGameEventListener {

    public static final double SELECTION_BOX_EXPANSION = .005D;

    private final SelectionManager manager;

    SelectionRenderer(Baritone baritone, SelectionManager manager) {
        this.manager = manager;
        baritone.getGameEventHandler().registerEventListener(this);
    }

    public static void renderSelections(ISelection[] selections) {
        float opacity = settings.selectionOpacity.getValue();
        boolean ignoreDepth = settings.renderSelectionIgnoreDepth.getValue();
        float lineWidth = settings.selectionLineWidth.getValue();

        if (!settings.renderSelection.getValue()) {
            return;
        }

        IRenderer.startLines(settings.colorSelection.getValue(), opacity, lineWidth, ignoreDepth);

        for (ISelection selection : selections) {
            IRenderer.drawAABB(selection.aabb(), SELECTION_BOX_EXPANSION);
        }

        if (settings.renderSelectionCorners.getValue()) {
            IRenderer.glColor(settings.colorSelectionPos1.getValue(), opacity);

            for (ISelection selection : selections) {
                IRenderer.drawAABB(new AxisAlignedBB(selection.pos1(), selection.pos1().add(1, 1, 1)));
            }

            IRenderer.glColor(settings.colorSelectionPos2.getValue(), opacity);

            for (ISelection selection : selections) {
                IRenderer.drawAABB(new AxisAlignedBB(selection.pos2(), selection.pos2().add(1, 1, 1)));
            }
        }

        IRenderer.endLines(ignoreDepth);
    }

    @Override
    public void onRenderPass(RenderEvent event) {
        renderSelections(manager.getSelections());
    }
}
