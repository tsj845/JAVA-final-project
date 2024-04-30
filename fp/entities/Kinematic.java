package fp.entities;

import fp.FPoint;
import fp.drawing.Shape;

public abstract class Kinematic {
    private FPoint velocity;
    private double angularVelocity;
    private KinParams params;
    protected Shape shape;
    protected Collider collider;
    protected Kinematic() {
        velocity = new FPoint();
        angularVelocity = 0.0d;
        params = new KinParams();
    }
    protected Kinematic(KinParams params) {
        velocity = new FPoint();
        angularVelocity = 0.0d;
        this.params = params;
    }
    public final FPoint getVelocity() {return velocity;}
    public final void setVelocity(FPoint velocity) {this.velocity = velocity;}
    public final double getAngular() {return angularVelocity;}
    public final void setAngular(double angularVelocity) {this.angularVelocity = angularVelocity;}
    public final void impulse(FPoint impulse) {
        this.velocity = new FPoint(velocity.x + impulse.x, velocity.y + impulse.y);
    }
    public final void angularImpulse(double impulse) {
        angularVelocity += impulse;
    }
    public void update() {
        shape.transform.translate(velocity);
    }
}
