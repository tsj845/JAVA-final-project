package fp.entities;

import fp.Vec2;
import fp.drawing.Shape;
import fp.drawing.Transform;

public abstract class Kinematic {
    public static final double EDGE_TOLERANCE = 0.01;
    public static final double MIN_SPEED = 0.002;
    private Vec2 velocity;
    private double angularVelocity;
    private KinParams params;
    protected Shape shape;
    protected Collider collider;
    protected boolean wraps = true;
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
    public final boolean collides(Kinematic other) {
        return collider.collides(other.collider);
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
        velocity = velocity.add(impulse.div(params.mass()).rotDeg(shape.transform.getRotation()));
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
    public void update(double dt) {
        Transform t = shape.transform;
        t.translate(velocity.mul(dt));
        if (params.friction() > 0) {
            double sx = Math.signum(velocity.x), sy = Math.signum(velocity.y);
            Vec2 dv = velocity.norm().mul(-Math.min(velocity.mag(), params.friction())).div(params.mass());
            // System.out.println(dv);
            accelLinear(dv, dt);
            if (Math.signum(velocity.x) * sx < 0) {
                velocity = new Vec2(0, velocity.y);
            }
            if (Math.signum(velocity.y) * sy < 0) {
                velocity = new Vec2(velocity.x, 0);
            }
        }
        if (velocity.mag() < Kinematic.MIN_SPEED) {
            velocity = new Vec2();
        }
        // Vec2 vd = velocity.sub(velocity.norm().mul(-params.friction()).mul(dt));
        // velocity = (vd.norm() == velocity.norm() || (vd.x==0.0d&&vd.y==0.0d)) ? vd : new Vec2();
        if (shape.minX() > 1+Kinematic.EDGE_TOLERANCE) {
            if (!wraps) {
                escaped();
                return;
            }
            t.translate(new Vec2(-shape.maxX()-(Kinematic.EDGE_TOLERANCE/2),0));
        }
        if (shape.maxX() < -Kinematic.EDGE_TOLERANCE) {
            if (!wraps) {
                escaped();
                return;
            }
            t.translate(new Vec2(-shape.minX()+1+(Kinematic.EDGE_TOLERANCE/2), 0));
        }
        if (shape.minY() > 1+Kinematic.EDGE_TOLERANCE) {
            if (!wraps) {
                escaped();
                return;
            }
            t.translate(new Vec2(0, -shape.maxY()-(Kinematic.EDGE_TOLERANCE/2)));
        }
        if (shape.maxY() < -Kinematic.EDGE_TOLERANCE) {
            if (!wraps) {
                escaped();
                return;
            }
            t.translate(new Vec2(0, -shape.minY()+1+(Kinematic.EDGE_TOLERANCE/2)));
        }
    }
    void escaped() {}
}
