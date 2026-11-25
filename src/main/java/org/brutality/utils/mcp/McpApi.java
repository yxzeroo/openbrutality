package org.brutality.utils.mcp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public interface McpApi {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fontRenderer = mc.fontRendererObj;
}
