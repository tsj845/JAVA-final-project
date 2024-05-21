package fp.drawing;

import fp.Vec2;

public class Transform {
    private final Transform parent;
    private Vec2 translation;
    private double rotation;
    private double scale;
    public Transform() {
        parent = null;
        this.translation = new Vec2(0.0d, 0.0d);
        this.rotation = 0.0d;
        scale = 1.0d;
    }
    public Transform(Vec2 translation, double rotation) {
        parent = null;
        this.translation = translation;
        this.rotation = rotation;
        scale = 1.0d;
    }
    public Transform(Transform parent) {
        this.parent = parent;
        translation = new Vec2();
        rotation = 0.0d;
        scale = 1.0d;
    }
    public Transform(Transform parent, Vec2 translation, double rotation) {
        this.parent = parent;
        this.translation = translation;
        this.rotation = rotation;
        scale = 1.0d;
    }
    private Transform(Vec2 t, double r, double s) {
        parent = null;
        translation = t;
        rotation = r;
        scale = s;
    }
    public Transform shallowCopy() {
        return new Transform(translation, rotation, scale);
    }
    private Transform applied(Transform t) {
        return new Transform(translation.mul(scale).add(t.translation.rotDeg(rotation)), t.rotation+rotation, t.scale*scale);
    }
    public Transform getEquivalent() {
        if (parent == null) return shallowCopy();
        return parent.getEquivalent().applied(this);
    }
    public static Vec2[] scaledBy(Vec2[] input, double factor) {
        Vec2[] output = new Vec2[input.length];
        Vec2 center = Vec2.average(input);
        for (int i = 0; i < input.length; i ++) {
            Vec2 o = input[i];
            output[i] = center.add(o.rel(center).mul(factor));
        }
        return Vec2.normalize(output);
    }
    public static Vec2[] scaledAbs(Vec2[] input, double value) {
        Vec2[] output = new Vec2[input.length];
        for (int i = 0; i < input.length; i ++) {
            Vec2 o = input[i];
            output[i] = o.add(o.norm().mul(value));
        }
        return output;
    }
    public Vec2[] apply(Vec2[] input) {
        Vec2[] output = new Vec2[input.length];
        double scomp = Math.sin(Math.toRadians(rotation));
        double ccomp = Math.cos(Math.toRadians(rotation));
        for (int i = 0; i < input.length; i ++) {
            Vec2 o = input[i].mul(scale);
            output[i] = new Vec2(((ccomp*o.x)+(scomp*o.y))+translation.x, ((-scomp*o.x)+(ccomp*o.y))+translation.y);
        }
        if (parent != null) {
            return parent.apply(output);
        }
        return output;
    }
    public Vec2 getTranslation() {
        return translation;
    }
    public void setTranslation(Vec2 translation) {
        this.translation = translation;
    }
    public void translate(Vec2 translation) {
        this.translation = new Vec2(this.translation.x + translation.x, this.translation.y + translation.y);
    }
    public void localTranslate(Vec2 translation) {
        translate(translation.rotDeg(getTotalRotation()));
    }
    public double getTotalRotation() {
        if (parent != null) {
            return rotation + parent.getTotalRotation();
        }
        return rotation;
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
    public void scale(double mult) {
        scale *= mult;
    }
    public String toString() {
        if (parent != null) {
            return getEquivalent().toString();
        }
        return String.format("x=%f,y=%f,a=%f,s=%f", translation.x, translation.y, rotation, scale);
    }
}
