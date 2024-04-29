package fp.entities;

import fp.FPoint;
import fp.drawing.Shape;

public abstract class Kinematic {
    private FPoint velocity;
    private double angularVelocity;
    protected Shape shape;
    protected Kinematic() {
        velocity = new FPoint(0.01,0.01);
        angularVelocity = 0.0d;
    }
    public final FPoint getVelocity() {return velocity;}
    public final void setVelocity(FPoint velocity) {this.velocity = velocity;}
    public final void impulse(FPoint impulse) {this.velocity = new FPoint(velocity.x + impulse.x, velocity.y + impulse.y);}
    public final double getAngular() {return angularVelocity;}
    public final void setAngular(double angularVelocity) {this.angularVelocity = angularVelocity;}
    public final void angularImpulse(double impulse) {angularVelocity += impulse;}
    public void update() {
        shape.transform.translate(velocity);
    }
}
