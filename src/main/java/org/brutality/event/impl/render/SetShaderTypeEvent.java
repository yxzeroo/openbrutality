package org.brutality.event.impl.render;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.utils.render.RenderHelper;

public class SetShaderTypeEvent extends Event {
    private SetShaderTypeEvent.BlurMode shaderType;

    @Generated
    public void setShaderType(SetShaderTypeEvent.BlurMode shaderType) {
        this.shaderType = shaderType;
    }

    @Generated
    public SetShaderTypeEvent.BlurMode getShaderType() {
        return this.shaderType;
    }

    @Override
    public void process() {
        try (RenderHelper renderHelper = new RenderHelper()) {
            super.process();
        }
    }

    public SetShaderTypeEvent(SetShaderTypeEvent.BlurMode shaderType) {
        this.shaderType = shaderType;
    }

    public static enum BlurMode {
        BLUR,
        SHADOW,
        GLOW;

        private static SetShaderTypeEvent.BlurMode[] computeBlurModeArray() {
            return new SetShaderTypeEvent.BlurMode[]{BLUR, SHADOW, GLOW};
        }
    }
}
