package fp.entities;

import fp.Vec2;
import fp.drawing.Transform;

public class CLine {
    public final Vec2 p1, p2;
    public final double length;
    public final double angle;
    public CLine(Vec2 p1, Vec2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.length = p2.sub(p1).mag();
        Vec2 inter = p2.sub(p1);
        this.angle = Math.toDegrees(Math.atan2(inter.y, inter.x));
    }
    public CLine(Vec2 start, double angle, double length) {
        double xm = Math.cos(Math.toRadians(angle)), ym = Math.sin(Math.toRadians(angle));
        this.p1 = start;
        this.p2 = start.add(new Vec2(length*xm, length*ym));
        this.length = length;
        Vec2 inter = p2.sub(p1);
        this.angle = Math.toDegrees(Math.atan2(inter.y, inter.x));
    }
    public CLine mirror(boolean usep1) {
        if (usep1) {
            return new CLine(p2, p1.add(p1.sub(p2)));
        } else {
            return new CLine(p1, p2.add(p2.sub(p1)));
        }
    }
    public CLine getTransformed(Transform t) {
        Vec2[] app = t.apply(new Vec2[]{p1,p2});
        return new CLine(app[0], app[1]);
    }
    public Vec2 midpoint() {
        return p2.sub(p1).div(2.0d).add(p1);
    }
    public boolean onLine(Vec2 p3) {
        return p2.sub(p3).norm() == p3.sub(p1).norm();
    }
    private static boolean partialIntersectCalc(CLine cl1, CLine cl2) {
        double c1xm = cl1.p2.x - cl1.p1.x;
        double c1ym = cl1.p2.y - cl1.p1.y;
        return Math.signum(c1xm*(cl2.p1.y-cl1.p2.y) - c1ym*(cl2.p1.x-cl1.p2.x)) != Math.signum(c1xm*(cl2.p2.y-cl1.p2.y) - c1ym*(cl2.p2.x-cl1.p2.x));
    }
    public boolean intersects(CLine other) {
        return partialIntersectCalc(this, other) && partialIntersectCalc(other, this);
    }
}
