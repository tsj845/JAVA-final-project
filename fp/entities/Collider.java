package fp.entities;

import fp.FPoint;
import fp.StdDraw;
import fp.drawing.Transform;

public class Collider {
    private enum ColliderType {
        Box,Circle,Triangle;
    }
    private static class CLine {
        final FPoint p1, p2;
        final double length;
        final double angle;
        CLine(FPoint p1, FPoint p2) {
            this.p1 = p1;
            this.p2 = p2;
            this.length = p2.sub(p1).mag();
            FPoint inter = p2.sub(p1);
            this.angle = Math.toDegrees(Math.atan2(inter.y, inter.x));
        }
        CLine(FPoint start, double angle, double length) {
            double xm = Math.cos(Math.toRadians(angle)), ym = Math.sin(Math.toRadians(angle));
            this.p1 = start;
            this.p2 = start.add(new FPoint(length*xm, length*ym));
            this.length = length;
            FPoint inter = p2.sub(p1);
            this.angle = Math.toDegrees(Math.atan2(inter.y, inter.x));
        }
        CLine mirror(boolean usep1) {
            if (usep1) {
                return new CLine(p2, p1.add(p1.sub(p2)));
            } else {
                return new CLine(p1, p2.add(p2.sub(p1)));
            }
        }
        CLine getTransformed(Transform t) {
            FPoint[] app = t.apply(new FPoint[]{p1,p2});
            return new CLine(app[0], app[1]);
        }
        FPoint midpoint() {
            return p2.sub(p1).div(2.0d).add(p1);
        }
        boolean onLine(FPoint p3) {
            return p2.sub(p3).norm() == p3.sub(p1).norm();
        }
        private static boolean partialIntersectCalc(CLine cl1, CLine cl2) {
            double c1xm = cl1.p2.x - cl1.p1.x;
            double c1ym = cl1.p2.y - cl1.p1.y;
            return Math.signum(c1xm*(cl2.p1.y-cl1.p2.y) - c1ym*(cl2.p1.x-cl1.p2.x)) != Math.signum(c1xm*(cl2.p2.y-cl1.p2.y) - c1ym*(cl2.p2.x-cl1.p2.x));
        }
        boolean intersects(CLine other) {
            return partialIntersectCalc(this, other) && partialIntersectCalc(other, this);
        }
    }
    private ColliderType type;
    private double[] data;
    private CLine[] cache;
    private final Transform transform;
    private Collider(ColliderType type, Transform t) {this.type = type;cache = null;transform = t;data = null;}
    public static Collider boxCollider(Transform t, double w, double h) {
        Collider c = new Collider(ColliderType.Box, t);
        double hw = w/2.0d, hh = h/2.0d;
        c.data = new double[]{w, h, hw, hh};
        c.cache = new CLine[]{
            new CLine(new FPoint(-hw, -hh), new FPoint(hw, -hh)),
            new CLine(new FPoint(hw, -hh), new FPoint(hw, hh)),
            new CLine(new FPoint(hw, hh), new FPoint(-hw, hh)),
            new CLine(new FPoint(-hw, hh), new FPoint(-hw, -hh))
        };
        return c;
    }
    public static Collider circleCollider(Transform t, double r) {
        Collider c = new Collider(ColliderType.Circle, t);
        c.data = new double[]{r, r*r};
        return c;
    }
    // TODO
    public static Collider triangleCollider(Transform t, FPoint p1, FPoint p2, FPoint p3) {
        Collider c = new Collider(ColliderType.Triangle, t);
        // FPoint center = FPoint.average(p1, p2, p3);
        c.cache = new CLine[]{new CLine(p1, p2),new CLine(p2, p3),new CLine(p3, p1),null,null,null};
        c.cache[3] = new CLine(p1, p2);
        FPoint center = new FPoint();
        c.data = new double[]{p1.x,p1.y,p2.x,p2.y,p3.x,p3.y,center.x,center.y};
        return c;
    }
    public boolean collides(Collider other) {
        switch (type) {
            case Box:
                switch (other.type) {
                    case Box:
                        return Collider.cBoxBox(this, other);
                    case Circle:
                        return Collider.cBoxCirc(this, other);
                    case Triangle:
                        return Collider.cBoxTri(this, other);
                }
            case Circle:
                switch (other.type) {
                    case Box:
                        return Collider.cBoxCirc(other, this);
                    case Circle:
                        return Collider.cCircCirc(this, other);
                    case Triangle:
                        return Collider.cCircTri(this, other);
                }
            case Triangle:
                switch (other.type) {
                    case Box:
                        return Collider.cBoxTri(other, this);
                    case Circle:
                        return Collider.cCircTri(other, this);
                    case Triangle:
                        return Collider.cTriTri(this, other);
                }
            default:
                throw new IllegalArgumentException();
        }
    }
    private static CLine[] cBoxGetTransformed(Collider cbox) {
        Transform t = cbox.transform;
        CLine[] c = cbox.cache;
        return new CLine[]{c[0].getTransformed(t),c[1].getTransformed(t),c[2].getTransformed(t),c[3].getTransformed(t)};
    }
    private static boolean cBoxContains(Collider cbox, FPoint p) {
        CLine[] tcache = cBoxGetTransformed(cbox);
        CLine chk = new CLine(cbox.transform.getTranslation(), p);
        for (CLine tst : tcache) {
            if (chk.intersects(tst)) return false;
        }
        return true;
    }
    private static boolean cBoxBox(Collider c1, Collider c2) {
        return true;
    }
    private static boolean cBoxCirc(Collider c1, Collider c2) {
        System.out.println("BC");
        FPoint ccenter = c2.transform.getTranslation();
        CLine[] tcache = cBoxGetTransformed(c1);
        CLine hchk = new CLine(ccenter, tcache[0].angle-90, c2.data[0]).mirror(true);
        CLine vchk = new CLine(ccenter, tcache[1].angle-90, c2.data[0]).mirror(true);
        StdDraw.setPenColor();
        StdDraw.setPenRadius(0.005);
        StdDraw.line(hchk.p1.x, hchk.p1.y, hchk.p2.x, hchk.p2.y);
        StdDraw.line(vchk.p1.x, vchk.p1.y, vchk.p2.x, vchk.p2.y);
        StdDraw.show();
        boolean corners = Math.min(Math.min(tcache[0].p1.sqDist(ccenter),tcache[1].p1.sqDist(ccenter)),Math.min(tcache[2].p1.sqDist(ccenter),tcache[3].p1.sqDist(ccenter))) <= c2.data[1];
        return corners || hchk.intersects(tcache[0]) || hchk.intersects(tcache[2]) || vchk.intersects(tcache[1]) || vchk.intersects(tcache[3]) || cBoxContains(c1, ccenter);
    }
    private static boolean cBoxTri(Collider c1, Collider c2) {
        return true;
    }
    private static boolean cCircCirc(Collider c1, Collider c2) {
        return c1.transform.getTranslation().sqDist(c2.transform.getTranslation()) <= (c1.data[1]+c2.data[1]);
    }
    private static boolean cCircTri(Collider c1, Collider c2) {
        return true;
    }
    private static boolean cTriTri(Collider c1, Collider c2) {
        return true;
    }
}
