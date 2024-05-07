package fp.entities;

import java.util.ArrayList;

import fp.Vec2;
import fp.drawing.Transform;

public class Collider {
    private enum ColliderType {
        Box,Circle,Triangle,Compound;
    }
    private final Transform transform;
    private ColliderType type;
    private double[] data;
    private CLine[] cache;
    private ArrayList<Collider> subs = null;
    private Collider(ColliderType type, Transform t) {this.type = type;cache = null;transform = t;data = null;}
    public static Collider compoundCollider(Transform t) {
        Collider c = new Collider(ColliderType.Compound, t);
        c.subs = new ArrayList<>();
        return c;
    }
    public static Collider boxCollider(Transform t, double w, double h) {
        Collider c = new Collider(ColliderType.Box, t);
        double hw = w/2.0d, hh = h/2.0d;
        c.data = new double[]{w, h, hw, hh};
        c.cache = new CLine[]{
            new CLine(new Vec2(-hw, -hh), new Vec2(hw, -hh)),
            new CLine(new Vec2(hw, -hh), new Vec2(hw, hh)),
            new CLine(new Vec2(hw, hh), new Vec2(-hw, hh)),
            new CLine(new Vec2(-hw, hh), new Vec2(-hw, -hh))
        };
        return c;
    }
    public static Collider circleCollider(Transform t, double r) {
        Collider c = new Collider(ColliderType.Circle, t);
        c.data = new double[]{r, r*r};
        return c;
    }
    public static Collider triangleCollider(Transform t, Vec2 p1, Vec2 p2, Vec2 p3) {
        Collider c = new Collider(ColliderType.Triangle, t);
        c.cache = new CLine[]{new CLine(p1, p2),new CLine(p2, p3),new CLine(p3, p1)};
        Vec2 center = Vec2.average(p1, p2, p3);
        c.data = new double[]{p1.x,p1.y,p2.x,p2.y,p3.x,p3.y,center.x,center.y};
        return c;
    }
    private Collider[] getSubs() {
        if (type == ColliderType.Compound) {
            return subs.toArray(Collider[]::new);
        }
        return new Collider[]{this};
    }
    public boolean collides(Collider other) {
        for (Collider tc : getSubs()) {
            for (Collider oc: other.getSubs()) {
                if (tc._collides(oc)) return true;
            }
        }
        return false;
    }
    private boolean _collides(Collider other) {
        switch (type) {
            case Box:
                switch (other.type) {
                    case Box:
                        return Collider.cBoxBox(this, other);
                    case Circle:
                        return Collider.cBoxCirc(this, other);
                    case Triangle:
                        return Collider.cBoxTri(this, other);
                    default:
                        throw new IllegalArgumentException();
                }
            case Circle:
                switch (other.type) {
                    case Box:
                        return Collider.cBoxCirc(other, this);
                    case Circle:
                        return Collider.cCircCirc(this, other);
                    case Triangle:
                        return Collider.cCircTri(this, other);
                    default:
                        throw new IllegalArgumentException();
                }
            case Triangle:
                switch (other.type) {
                    case Box:
                        return Collider.cBoxTri(other, this);
                    case Circle:
                        return Collider.cCircTri(other, this);
                    case Triangle:
                        return Collider.cTriTri(this, other);
                    default:
                        throw new IllegalArgumentException();
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
    private static CLine[] cTriGetTransformed(Collider ctri) {
        Transform t = ctri.transform;
        CLine[] c = ctri.cache;
        return new CLine[]{c[0].getTransformed(t),c[1].getTransformed(t),c[2].getTransformed(t)};

    }
    private static boolean cBoxContains(CLine[] tcache, Vec2 center, Vec2 p) {
        CLine chk = new CLine(center, p);
        for (CLine tst : tcache) {
            if (chk.intersects(tst)) return false;
        }
        return true;
    }
    private static boolean cBoxContains(Collider cbox, Vec2 p) {
        CLine[] tcache = cBoxGetTransformed(cbox);
        CLine chk = new CLine(cbox.transform.getTranslation(), p);
        for (CLine tst : tcache) {
            if (chk.intersects(tst)) return false;
        }
        return true;
    }
    /*
     * float sign (fPoint p1, fPoint p2, fPoint p3)
{
    return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
}

bool PointInTriangle (fPoint pt, fPoint v1, fPoint v2, fPoint v3)
{
    float d1, d2, d3;
    bool has_neg, has_pos;

    d1 = sign(pt, v1, v2);
    d2 = sign(pt, v2, v3);
    d3 = sign(pt, v3, v1);

    has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
    has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

    return !(has_neg && has_pos);
}
     */
    private static double _cTCPartial(Vec2 p1, Vec2 p2, Vec2 p3) {
        return (p1.x-p3.x)*(p2.y-p3.y)-(p2.x-p3.x)*(p1.y-p3.y);
    }
    private static boolean cTriContains(CLine[] tcache, Vec2 p) {
        double d1,d2,d3;
        boolean ne,po;
        d1=_cTCPartial(p, tcache[0].p1, tcache[1].p1);
        d2=_cTCPartial(p, tcache[1].p1, tcache[2].p1);
        d3=_cTCPartial(p, tcache[2].p1, tcache[0].p1);
        ne = (d1<0.0d)||(d2<0.0d)||(d3<0.0d);
        po = (d1>0.0d)||(d2>0.0d)||(d3>0.0d);
        return !(ne&&po);
    }
    private static boolean cCircContains(Vec2 c, double r2, Vec2 p) {
        return c.sqDist(p) <= r2;
    }
    private static boolean cCircContains(Collider c, Vec2 p) {
        return cCircContains(c.transform.getTranslation(), c.data[1], p);
    }
    private static boolean cBoxBox(Collider c1, Collider c2) {
        CLine[] tc1 = cBoxGetTransformed(c1);
        CLine[] tc2 = cBoxGetTransformed(c2);
        Vec2 c = c1.transform.getTranslation();
        return cBoxContains(tc1, c, tc2[0].p1) || cBoxContains(tc1, c, tc2[1].p1) || cBoxContains(tc1, c, tc2[2].p1) || cBoxContains(tc1, c, tc2[3].p1);
    }
    private static boolean cBoxCirc(Collider c1, Collider c2) {
        System.out.println("BC");
        Vec2 ccenter = c2.transform.getTranslation();
        CLine[] tcache = cBoxGetTransformed(c1);
        CLine hchk = new CLine(ccenter, tcache[0].angle-90, c2.data[0]).mirror(true);
        CLine vchk = new CLine(ccenter, tcache[1].angle-90, c2.data[0]).mirror(true);
        boolean corners = Math.min(Math.min(tcache[0].p1.sqDist(ccenter),tcache[1].p1.sqDist(ccenter)),Math.min(tcache[2].p1.sqDist(ccenter),tcache[3].p1.sqDist(ccenter))) <= c2.data[1];
        return corners || hchk.intersects(tcache[0]) || hchk.intersects(tcache[2]) || vchk.intersects(tcache[1]) || vchk.intersects(tcache[3]) || cBoxContains(c1, ccenter);
    }
    private static boolean cBoxTri(Collider c1, Collider c2) {
        CLine[] bc = cBoxGetTransformed(c1);
        Vec2 c = c1.transform.getTranslation();
        CLine[] tc = cTriGetTransformed(c2);
        return (
            cBoxContains(bc, c, tc[0].p1) || cBoxContains(bc, c, tc[1].p1) || cBoxContains(bc, c, tc[2].p1) ||
            bc[0].intersects(tc[0]) || bc[0].intersects(tc[1]) || bc[0].intersects(tc[2]) ||
            bc[1].intersects(tc[0]) || bc[1].intersects(tc[1]) || bc[1].intersects(tc[2])
        );
    }
    private static boolean cCircCirc(Collider c1, Collider c2) {
        return c1.transform.getTranslation().sqDist(c2.transform.getTranslation()) <= (c1.data[1]+c2.data[1]);
    }
    private static boolean cCircTri(Collider c1, Collider c2) {
        Vec2 c = c1.transform.getTranslation();
        double r2 = c1.data[1];
        CLine[] tc = cTriGetTransformed(c2);
        CLine chk1 = new CLine(c, tc[0].angle-90, c2.data[0]).mirror(true);
        CLine chk2 = new CLine(c, tc[1].angle-90, c2.data[0]).mirror(true);
        CLine chk3 = new CLine(c, tc[2].angle-90, c2.data[0]).mirror(true);
        return (
            cCircContains(c, r2, tc[0].p1) || cCircContains(c, r2, tc[1].p1) || cCircContains(c, r2, tc[2].p1) ||
            chk1.intersects(tc[0]) || chk2.intersects(tc[1]) || chk3.intersects(tc[2])
        );
    }
    private static boolean cTriTri(Collider c1, Collider c2) {
        CLine[] tc1 = cTriGetTransformed(c1);
        CLine[] tc2 = cTriGetTransformed(c2);
        return (
            cTriContains(tc1, tc2[0].p1) || cTriContains(tc1, tc2[1].p1) || cTriContains(tc1, tc2[2].p1) ||
            cTriContains(tc2, tc1[0].p1) || cTriContains(tc2, tc1[1].p1) || cTriContains(tc2, tc1[2].p1) ||
            tc1[0].intersects(tc2[0]) || tc1[0].intersects(tc2[1]) ||
            tc1[1].intersects(tc2[0]) || tc1[1].intersects(tc2[1])
        );
    }
}
