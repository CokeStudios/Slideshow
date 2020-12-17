package org.teacon.slides.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Matrix4f;
import org.teacon.slides.projector.ProjectorTileEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProjectorTileEntityRenderer extends TileEntityRenderer<ProjectorTileEntity> {

    public ProjectorTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ProjectorTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        matrixStack.push();

        final Matrix4f transformation = matrixStack.getLast().getMatrix();
        final float width = tile.currentSlide.getSize().x, height = tile.currentSlide.getSize().y;
        final SlideRenderEntry entry = SlideRenderData.getEntry(tile.currentSlide.getImageLocation());

        transformation.mul(tile.getTransformation());
        entry.render(buffer, transformation, width, height, tile.currentSlide.getColor(), combinedLight);

        matrixStack.pop();
    }

    @Override
    public boolean isGlobalRenderer(ProjectorTileEntity tile) {
        return true;
    }
}