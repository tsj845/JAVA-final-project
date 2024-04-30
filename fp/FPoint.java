package fp;

public class FPoint {
    public static final double EPSILON = Math.ulp(1.0d);
    public final double x, y;
    public FPoint() {
        x = 0.0d;
        y = 0.0d;
    }
    public FPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public boolean equals(Object other) {
        if (!(other instanceof FPoint)) return false;
        FPoint o = (FPoint)other;
        return Math.abs(x-o.x)<=FPoint.EPSILON && Math.abs(y-o.y)<=FPoint.EPSILON;
    }
    public static FPoint average(FPoint...fps) {
        return new FPoint();
    }
    public FPoint add(FPoint other) {
        return new FPoint(x+other.x, y+other.y);
    }
    public FPoint sub(FPoint other) {
        return new FPoint(x-other.x, y-other.y);
    }
    public FPoint mul(double scalar) {
        return new FPoint(x*scalar, y*scalar);
    }
    public FPoint div(double scalar) {
        return new FPoint(x/scalar, y/scalar);
    }
    public double dot(FPoint other) {
        return x*other.x + y*other.y;
    }
    public double mag() {
        return Math.sqrt(x*x+y*y);
    }
    public FPoint norm() {
        return div(mag());
    }
    public double sqDist(FPoint other) {
        FPoint i = sub(other);
        return i.x*i.x + i.y*i.y;
    }
    public double radAngle(FPoint other) {
        return Math.acos(dot(other)/(mag()*other.mag()));
    }
    public double degAngle(FPoint other) {
        return Math.toDegrees(radAngle(other));
    }
}
