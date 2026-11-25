package org.brutality.event.impl.render;

import lombok.Generated;
import org.brutality.event.Event;

public class GetShaderTypeEvent extends Event {
    private GetShaderTypeEvent.BlurMode shaderType;

    public GetShaderTypeEvent(GetShaderTypeEvent.BlurMode shaderType) {
        this.shaderType = shaderType;
    }

    @Generated
    public GetShaderTypeEvent.BlurMode getShaderType() {
        return this.shaderType;
    }

    @Generated
    public void setShaderType(GetShaderTypeEvent.BlurMode shaderType) {
        this.shaderType = shaderType;
    }

    public static enum BlurMode {
        BLUR,
        SHADOW,
        GLOW;

        private static GetShaderTypeEvent.BlurMode[] computeBlurModeArray() {
            return new GetShaderTypeEvent.BlurMode[]{BLUR, SHADOW, GLOW};
        }
    }
}
