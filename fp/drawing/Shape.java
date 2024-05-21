package fp.drawing;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;

import fp.Vec2;
import fp.StdDraw;

public class Shape implements Drawable {
    private enum ShapeType {
        Rect,Circ,Poly,Group;
    }
    private final LinkedList<Shape> shapes;
    private final ShapeType type;
    public final Vec2[] points;
    public final Transform transform;
    private Color fill;
    private Color stroke;
    private double strokewidth;
    private double roundness;
    private Shape() {
        this.type = ShapeType.Group;
        this.shapes = new LinkedList<>();
        this.points = null;
        this.transform = new Transform();
        this.fill = null;
        this.stroke = null;
        this.strokewidth = 0.0d;
        this.roundness = 0.0d;
    }
    private Shape(Transform t) {
        this.type = ShapeType.Group;
        this.shapes = new LinkedList<>();
        this.points = null;
        this.transform = t;
        this.fill = null;
        this.stroke = null;
        this.strokewidth = 0.0d;
        this.roundness = 0.0d;
    }
    private Shape(ShapeType type, Vec2[] points) {
        this.type = type;
        this.points = points;
        this.shapes = null;
        this.transform = new Transform();
        this.fill = StdDraw.BLACK;
        this.stroke = StdDraw.BLACK;
        this.strokewidth = 0.0d;
        this.roundness = 0.0d;
    }
    private Shape(ShapeType type, Vec2[] points, Transform t) {
        this.type = type;
        this.points = points;
        this.shapes = null;
        this.transform = t;
        this.fill = StdDraw.BLACK;
        this.stroke = StdDraw.BLACK;
        this.strokewidth = 0.0d;
        this.roundness = 0.0d;
    }
    public static Shape Group() {
        return new Shape();
    }
    public static Shape Group(Transform t) {
        return new Shape(t);
    }
    public static Shape Rect(double width, double height) {
        double hw = width/2.0d, hh = height/2.0d;
        return new Shape(ShapeType.Rect, new Vec2[]{new Vec2(-hw,-hh),new Vec2(hw,-hh),new Vec2(hw,hh),new Vec2(-hw,hh)});
    }
    public static Shape Rect(double width, double height, Transform t) {
        double hw = width/2.0d, hh = height/2.0d;
        return new Shape(ShapeType.Rect, new Vec2[]{new Vec2(-hw,-hh),new Vec2(hw,-hh),new Vec2(hw,hh),new Vec2(-hw,hh)}, t);
    }
    public static Shape Circle(double radius) {
        return new Shape(ShapeType.Circ, new Vec2[]{new Vec2(0.0, radius)});
    }
    public static Shape Circle(double radius, Transform t) {
        return new Shape(ShapeType.Circ, new Vec2[]{new Vec2(0.0, radius)}, t);
    }
    public static Shape Poly(Vec2[] points) {
        return new Shape(ShapeType.Poly, Vec2.normalize(points));
    }
    public static Shape RawPoly(Vec2[] points, Transform t) {
        return new Shape(ShapeType.Poly, points, t);
    }
    public static Shape Poly(Vec2[] points, Transform t) {
        return new Shape(ShapeType.Poly, Vec2.normalize(points), t);
    }
    public void addShape(Shape s) {
        if (shapes == null) throw new IllegalArgumentException();
        shapes.add(s);
    }
    public boolean removeShape(Shape s) {
        if (shapes == null) throw new IllegalArgumentException();
        return shapes.remove(s);
    }
    public Shape popShape() {
        if (shapes == null) throw new IllegalArgumentException();
        return shapes.removeLast();
    }
    public Shape get(int i) {
        if (shapes == null) throw new IllegalArgumentException();
        return shapes.get(i);
    }
    public Shape getLast() {
        if (shapes == null) throw new IllegalArgumentException();
        return shapes.getLast();
    }
    public Shape getFirst() {
        if (shapes == null) throw new IllegalArgumentException();
        return shapes.getFirst();
    }
    public double minX() {
        if (type == ShapeType.Group) {
            double min = Double.POSITIVE_INFINITY;
            for (Shape s : shapes) {
                min = Math.min(min, s.minX());
            }
            return min;
        }
        if (type == ShapeType.Circ) {
            return transform.getEquivalent().getTranslation().x - points[0].y;
        }
        return Vec2.minX(transform.apply(points));
    }
    public double maxX() {
        if (type == ShapeType.Group) {
            double max = Double.NEGATIVE_INFINITY;
            for (Shape s : shapes) {
                max = Math.max(max, s.maxX());
            }
            return max;
        }
        if (type == ShapeType.Circ) {
            return transform.getEquivalent().getTranslation().x + points[0].y;
        }
        return Vec2.maxX(transform.apply(points));
    }
    public double minY() {
        if (type == ShapeType.Group) {
            double min = Double.POSITIVE_INFINITY;
            for (Shape s : shapes) {
                min = Math.min(min, s.minY());
            }
            return min;
        }
        if (type == ShapeType.Circ) {
            return transform.getEquivalent().getTranslation().y - points[0].y;
        }
        return Vec2.minY(transform.apply(points));
    }
    public double maxY() {
        if (type == ShapeType.Group) {
            double max = Double.NEGATIVE_INFINITY;
            for (Shape s : shapes) {
                max = Math.max(max, s.maxY());
            }
            return max;
        }
        if (type == ShapeType.Circ) {
            return transform.getEquivalent().getTranslation().y + points[0].y;
        }
        return Vec2.maxY(transform.apply(points));
    }
    public void draw() {
        if (type == ShapeType.Group) {
            for (Shape s : shapes) {
                s.draw();
            }
            return;
        }
        switch (type) {
            case Rect:
            case Poly:
                double[] x = new double[points.length], y = new double[points.length];
                int i = 0;
                for (Vec2 p : transform.apply(points)) {
                // for (Vec2 p : points) {
                    x[i] = p.x;
                    y[i++] = p.y;
                }
                // if (stroke != null) {
                //     double rad = roundness*strokewidth;
                //     double ewidth = strokewidth - rad;
                //     double[] x2 = new double[points.length], y2 = new double[points.length];
                //     int j = 0;
                //     for (Vec2 p : Transform.scaledBy(points, 1.5)) {
                //     // for (Vec2 p : transform.apply(Transform.scaledAbs(points, ewidth))) {
                //         x2[j] = p.x+0.5;
                //         y2[j++] = p.y+0.5;
                //     }
                //     StdDraw.setPenRadius(rad);
                //     StdDraw.setPenColor(stroke);
                //     StdDraw.filledPolygon(x2, y2);
                // }
                if (stroke != null) {
                    StdDraw.setPenRadius(strokewidth);
                    StdDraw.setPenColor(stroke);
                    StdDraw.polygon(x, y);
                }
                StdDraw.setPenRadius(0.0d);
                if (fill != null) {
                    StdDraw.setPenColor(fill);
                    StdDraw.filledPolygon(x, y);
                }
                break;
            case Circ:
                Vec2 c = transform.apply(new Vec2[]{new Vec2()})[0];
                if (stroke != null) {
                    StdDraw.setPenRadius(strokewidth);
                    StdDraw.setPenColor(stroke);
                    StdDraw.circle(c.x, c.y, points[0].y);
                }
                StdDraw.setPenRadius(0.0d);
                if (fill != null) {
                    StdDraw.setPenColor(fill);
                    StdDraw.filledCircle(c.x, c.y, points[0].y);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    private String getA(Color c) {
        if (c == null) return "\u001b[0m";
        return String.format("\u001b[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());
    }
    public String toString() {
        if (type == ShapeType.Group) {
            return String.format("%sGRP%s{%s}%s::%s", getA(fill), getA(stroke), transform.toString(), getA(null), shapes.toString());
        }
        if (type == ShapeType.Circ) {
            return String.format("%sCIRC%s{%s,r=%f}%s", getA(fill), getA(stroke), transform.toString(), points[0].y, getA(null));
        }
        if (type == ShapeType.Rect) {
            return String.format("%sRECT%s{%s,w=%f,h=%f}%s", getA(fill), getA(stroke), transform.toString(), Vec2.maxX(points)-Vec2.minX(points), Vec2.maxY(points)-Vec2.minY(points), getA(null));
        }
        if (type == ShapeType.Poly) {
            return String.format("%sPOLY%s{%s}%s::%s", getA(fill), getA(stroke), transform.toString(), getA(null), Arrays.toString(points));
        }
        throw new IllegalArgumentException();
    }
    // accessors
    public Color fill() {return fill;}
    public void fill(Color f) {this.fill = f;}
    public Color stroke() {return stroke;}
    public void stroke(Color s) {this.stroke = s;}
    public double strokewidth() {return strokewidth;}
    public void strokewidth(double w) {this.strokewidth = Math.max(0.0d, w);}
    public double roundness() {return roundness;}
    public void roundness(double r) {roundness=Math.max(0.0d, Math.min(r, 100.0d))/100.0d;}
}
