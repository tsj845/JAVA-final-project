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
    public final void accelLinearRaw(Vec2 accel) {
        velocity = velocity.add(accel);
    }
    public final void accelAngularRaw(double accel) {
        angularVelocity += accel;
    }
    public final void linearImpulseRaw(Vec2 impulse) {
        velocity = velocity.add(impulse.div(params.mass()));
    }
    public final void angularImpulseRaw(double impulse) {
        angularVelocity += impulse/params.moment();
    }
    public final void realImpulseRaw(Vec2 application, Vec2 force) {
        Transform t = shape.transform;
        CLine centl = new CLine(t.getTranslation(), application);
        CLine forcl = new CLine(application.sub(force), application);
        double torque = centl.length*forcl.length*Math.sin(forcl.radBetween(centl));
        angularImpulseRaw(torque);
    }
    public final void accelLinear(Vec2 accel, double dt) {
        accelLinearRaw(accel.mul(dt));
    }
    public final void accelAngular(double accel, double dt) {
        accelAngularRaw(accel*dt);
    }
    public final void linearImpulse(Vec2 impulse, double dt) {
        linearImpulseRaw(impulse.mul(dt));
    }
    public final void angularImpulse(double impulse, double dt) {
        angularImpulseRaw(impulse*dt);
    }
    public final void realImpulse(Vec2 application, Vec2 force, double dt) {
        realImpulseRaw(application, force.mul(dt));
    }
    public void update() {
        shape.transform.translate(velocity);
    }
}
