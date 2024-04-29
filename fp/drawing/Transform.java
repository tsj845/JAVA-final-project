package fp.drawing;

import fp.FPoint;

public class Transform {
    private FPoint translation;
    private double rotation;
    public Transform() {
        this.translation = new FPoint(0.0d, 0.0d);
        this.rotation = 0.0d;
    }
    public Transform(FPoint translation, double rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }
    public FPoint[] apply(FPoint[] input) {
        FPoint[] output = new FPoint[input.length];
        double scomp = Math.sin(Math.toRadians(rotation));
        double ccomp = Math.cos(Math.toRadians(rotation));
        for (int i = 0; i < input.length; i ++) {
            FPoint o = input[i];
            output[i] = new FPoint((ccomp*o.x)+(scomp*o.y)+translation.x, (-scomp*o.x)+(ccomp*o.y)+translation.y);
        }
        return output;
    }
    public FPoint getTranslation() {
        return translation;
    }
    public void setTranslation(FPoint translation) {
        this.translation = translation;
    }
    public void translate(FPoint translation) {
        this.translation = new FPoint(this.translation.x + translation.x, this.translation.y + translation.y);
    }
    public double getRotation() {
        return rotation;
    }
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
    public void rotate(double rotation) {
        this.rotation += rotation;
    }
}
