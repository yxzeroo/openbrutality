package org.brutality.anticheat.data;

public class DataHandler {
    public double posX;
    public double posY;
    public double posZ;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;
    public double motionX;
    public double motionY;
    public double motionZ;
    public float yaw;
    public float pitch;
    public float factor;
    public float factor2;
    public boolean onGround;
    public boolean flag;
    public int hurtTime;
    public long tick;
    public long timeStamp;
    private Double doubleValue = null;

    public void reset() {
        this.doubleValue = null;
    }

    public double computeDouble3() {
        return this.posZ - this.prevPosZ;
    }

    public double computeDouble2() {
        return this.posX - this.prevPosX;
    }

    public double computeDouble4() {
        return this.posY - this.prevPosY;
    }

    public double computeDouble() {
        if (this.doubleValue == null) {
            this.doubleValue = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        }

        return this.doubleValue;
    }
}
