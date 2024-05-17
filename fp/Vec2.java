package fp;

public class Vec2 {
    public static final double EPSILON = Math.ulp(1.0d);
    public final double x, y;
    public static int dbgNormal = 2;
    public Vec2() {
        x = 0.0d;
        y = 0.0d;
    }
    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public boolean equals(Object other) {
        if (!(other instanceof Vec2)) return false;
        Vec2 o = (Vec2)other;
        return Math.abs(x-o.x)<=Vec2.EPSILON && Math.abs(y-o.y)<=Vec2.EPSILON;
    }
    public String toString() {
        return String.format("<%f, %f>", x, y);
    }
    public static Vec2 average(Vec2...fps) {
        double x = 0.0, y = 0.0;
        for (Vec2 fp : fps) {
            x += fp.x;
            y += fp.y;
        }
        return new Vec2(x/(double)fps.length, y/(double)fps.length);
    }
    public static Vec2[] normalize(Vec2[] in) {
        Vec2 c = average(in);
        Vec2[] out = new Vec2[in.length];
        for (int i = 0; i < in.length; i ++) {
            out[i] = in[i].sub(c);
        }
        return out;
    }
    public static double minX(Vec2[] points) {
        double min = Double.POSITIVE_INFINITY;
        for (Vec2 p : points) min = Math.min(p.x,min);
        return min;
    }
    public static double minY(Vec2[] points) {
        double min = Double.POSITIVE_INFINITY;
        for (Vec2 p : points) min = Math.min(p.y,min);
        return min;
    }
    public static double maxX(Vec2[] points) {
        double min = Double.NEGATIVE_INFINITY;
        for (Vec2 p : points) min = Math.max(p.x,min);
        return min;
    }
    public static double maxY(Vec2[] points) {
        double min = Double.NEGATIVE_INFINITY;
        for (Vec2 p : points) min = Math.max(p.y,min);
        return min;
    }
    public Vec2 rotRad(double angle) {
        double scomp = Math.sin(angle);
        double ccomp = Math.cos(angle);
        return new Vec2(((ccomp*x)+(scomp*y)), ((-scomp*x)+(ccomp*y)));
    }
    public Vec2 rotDeg(double angle) {
        return rotRad(Math.toRadians(angle));
    }
    public Vec2 add(Vec2 other) {
        return new Vec2(this.x+other.x, this.y+other.y);
    }
    public Vec2 sub(Vec2 other) {
        return new Vec2(this.x-other.x, this.y-other.y);
    }
    public Vec2 mul(double scalar) {
        return new Vec2(x*scalar, y*scalar);
    }
    public Vec2 div(double scalar) {
        return new Vec2(x/scalar, y/scalar);
    }
    public double dot(Vec2 other) {
        return x*other.x + y*other.y;
    }
    public double mag() {
        return Math.sqrt(x*x+y*y);
    }
    public Vec2 norm() {
        double ma = mag();
        return ma > 0.0d ? div(ma) : new Vec2();
    }
    public double sqDist(Vec2 other) {
        Vec2 i = sub(other);
        return i.x*i.x + i.y*i.y;
    }
    public double radAngle(Vec2 other) {
        Vec2 d = other.sub(this);
        double r = Math.atan2(d.y, d.x);
        return (Double.isNaN(r) ? 0.0d : r);
    }
    public double degAngle(Vec2 other) {
        return Math.toDegrees(radAngle(other));
    }
    public Vec2 rel(Vec2 other) {
        return sub(other);
    }
}
