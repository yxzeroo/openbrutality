package org.brutality.core;

public interface SetScaleApi {
    void setScale(float scale);

    float getScale();

    boolean checkState();

    void setY(int yPosition);

    void setX(int xPosition);

    int getY();

    int getX();
}
