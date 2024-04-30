package fp.entities;

public class KinParams {
    public double friction;
    public double mass;
    public double moment;
    public KinParams() {
        mass = 0.0d;
        moment = 0.0d;
        friction = 0.0d;
    }
    public KinParams(double mass, double moment, double friction) {
        this.mass = mass;
        this.moment = moment;
        this.friction = friction;
    }
}
