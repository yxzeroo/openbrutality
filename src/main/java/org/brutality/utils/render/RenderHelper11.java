package org.brutality.utils.render;

import lombok.Generated;

public final class RenderHelper11 {
    public double x2;
    public double y2;

    public RenderHelper11() {
    }

    @Generated
    public void setX(double x2) {
        this.x2 = x2;
    }

    @Generated
    public void setY(double y2) {
        this.y2 = y2;
    }

    public RenderHelper11 computeRenderHelper11(double d, double d2) {
        return new RenderHelper11(this.x2 + d, this.y2 + d2);
    }

    @Generated
    public double getY() {
        return this.y2;
    }

    public RenderHelper11(double x2, double y2) {
        this.x2 = x2;
        this.y2 = y2;
    }

    public RenderHelper11 computeRenderHelper11(RenderHelper11 renderHelper11) {
        return this.computeRenderHelper11(renderHelper11.x2, renderHelper11.y2);
    }

    @Generated
    public double getX() {
        return this.x2;
    }
}
