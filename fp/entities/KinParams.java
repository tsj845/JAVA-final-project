package fp.entities;

public class KinParams {
    private double friction;
    private double mass;
    private double moment;
    public KinParams() {
        mass = 1.0d;
        moment = 1.0d;
        friction = 0.0d;
    }
    public KinParams(double mass, double moment, double friction) {
        this.mass = mass;
        this.moment = moment;
        this.friction = friction;
    }
    public double friction() {return friction;}
    public double mass() {return mass;}
    public double moment() {return moment;}
    public void friction(double f) {
        if (f < 0.0d) throw new IllegalArgumentException("friction must be non-negative");
        friction = f;
    }
    public void mass(double m) {
        if (m <= 0.0d) throw new IllegalArgumentException("mass must be positive");
        mass = m;
    }
    public void moment(double m) {
        if (m <= 0.0d) throw new IllegalArgumentException("moment must be positive");
        moment = m;
    }
}
