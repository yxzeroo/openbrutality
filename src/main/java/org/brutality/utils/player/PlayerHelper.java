package org.brutality.utils.player;

import net.minecraft.util.Vec3;

public class PlayerHelper {
    public Vec3 vec3 = new Vec3(0.0, 0.0, 0.0);
    public Vec3 vec32 = new Vec3(0.0, 0.0, 0.0);
    public Vec3 vec33 = new Vec3(0.0, 0.0, 0.0);
    public Vec3 vec34 = new Vec3(0.0, -10.0, 0.0);
    public double b = 2.0;
    public double amount = 1.0;
    public double amount2 = 0.03333333333333333;

    public Vec3 computeVec3() {
        double d = 1.0 / this.amount;
        this.vec32 = this.computeVec3(this.vec32, d);
        this.vec32 = this.vec32.add(this.vec34);
        this.vec32 = this.computeVec3(this.vec32, this.b);
        this.vec3 = this.computeVec3(this.vec3, d);
        this.vec32 = this.computeVec3(this.vec32, this.amount2);
        this.vec3 = this.vec3.add(this.vec32);
        this.vec33 = this.vec33.add(this.vec3);
        this.vec3 = new Vec3(0.0, 0.0, 0.0);
        this.vec32 = new Vec3(0.0, 0.0, 0.0);
        return this.vec33;
    }

    public Vec3 computeVec3(Vec3 vec, double d) {
        return new Vec3(vec.xCoord * d, vec.yCoord * d, vec.zCoord * d);
    }
}
