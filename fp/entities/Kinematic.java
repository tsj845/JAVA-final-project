package fp.entities;

import fp.Vec2;
import fp.drawing.Shape;
import fp.drawing.Transform;

public abstract class Kinematic {
    private Vec2 velocity;
    private double angularVelocity;
    private KinParams params;
    protected Shape shape;
    protected Collider collider;
    protected Kinematic() {
        velocity = new Vec2();
        angularVelocity = 0.0d;
        params = new KinParams();
    }
    protected Kinematic(KinParams params) {
        velocity = new Vec2();
        angularVelocity = 0.0d;
        this.params = params;
    }
    public final Vec2 getVelocity() {return velocity;}
    public final void setVelocity(Vec2 velocity) {this.velocity = velocity;}
    public final double getAngular() {return angularVelocity;}
    public final void setAngular(double angularVelocity) {this.angularVelocity = angularVelocity;}
    public final void impulse(Vec2 impulse) {
        this.velocity = new Vec2(velocity.x + impulse.x, velocity.y + impulse.y);
    }
    public final void angularImpulse(double impulse) {
        angularVelocity += impulse;
    }
    public final void realImpulse(Vec2 application, Vec2 force) {
        Transform t = shape.transform;
        CLine centl = new CLine(t.getTranslation(), application);
    }
    public void update() {
        shape.transform.translate(velocity);
    }
}
