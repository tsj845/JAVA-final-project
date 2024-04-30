package fp.drawing;

import java.awt.Color;
import java.util.LinkedList;

import fp.FPoint;
import fp.StdDraw;

public class Shape {
    private enum ShapeType {
        Rect,Circ,Poly,Group;
    }
    private final LinkedList<Shape> shapes;
    private final FPoint[] points;
    private final ShapeType type;
    public final Transform transform;
    public Color fill;
    public Color stroke;
    public double strokewidth;
    private Shape() {
        this.type = ShapeType.Group;
        this.shapes = new LinkedList<>();
        this.points = null;
        this.transform = new Transform();
        this.fill = null;
        this.stroke = null;
        this.strokewidth = 0.0d;
    }
    private Shape(Transform t) {
        this.type = ShapeType.Group;
        this.shapes = new LinkedList<>();
        this.points = null;
        this.transform = t;
        this.fill = null;
        this.stroke = null;
        this.strokewidth = 0.0d;
    }
    private Shape(ShapeType type, FPoint[] points) {
        this.type = type;
        this.points = points;
        this.shapes = null;
        this.transform = new Transform();
        this.fill = StdDraw.BLACK;
        this.stroke = StdDraw.BLACK;
        this.strokewidth = 0.0d;
    }
    private Shape(ShapeType type, FPoint[] points, Transform t) {
        this.type = type;
        this.points = points;
        this.shapes = null;
        this.transform = t;
        this.fill = StdDraw.BLACK;
        this.stroke = StdDraw.BLACK;
        this.strokewidth = 0.0d;
    }
    public static Shape Group() {
        return new Shape();
    }
    public static Shape Group(Transform t) {
        return new Shape(t);
    }
    public static Shape Rect(double width, double height) {
        double hw = width/2.0d, hh = height/2.0d;
        return new Shape(ShapeType.Rect, new FPoint[]{new FPoint(-hw,-hh),new FPoint(hw,-hh),new FPoint(hw,hh),new FPoint(-hw,hh)});
    }
    public static Shape Rect(double width, double height, Transform t) {
        double hw = width/2.0d, hh = height/2.0d;
        return new Shape(ShapeType.Rect, new FPoint[]{new FPoint(-hw,-hh),new FPoint(hw,-hh),new FPoint(hw,hh),new FPoint(-hw,hh)}, t);
    }
    public static Shape Circle(double radius) {
        return new Shape(ShapeType.Circ, new FPoint[]{new FPoint(0.0, radius)});
    }
    public static Shape Circle(double radius, Transform t) {
        return new Shape(ShapeType.Circ, new FPoint[]{new FPoint(0.0, radius)}, t);
    }
    public static Shape Poly(FPoint[] points) {
        return new Shape(ShapeType.Poly, points);
    }
    public static Shape Poly(FPoint[] points, Transform t) {
        return new Shape(ShapeType.Poly, points, t);
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
    public void draw() {
        if (points == null) {
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
                for (FPoint p : transform.apply(points)) {
                    x[i] = p.x;
                    y[i++] = p.y;
                }
                StdDraw.setPenRadius(strokewidth);
                if (fill != null) {
                    StdDraw.setPenColor(fill);
                    StdDraw.filledPolygon(x, y);
                }
                if (strokewidth > 0.0d) {
                    StdDraw.setPenColor(stroke);
                    StdDraw.polygon(x, y);
                }
                break;
            case Circ:
                StdDraw.setPenRadius(strokewidth);
                if (fill != null) {
                    StdDraw.setPenColor(fill);
                    StdDraw.filledCircle(transform.getTranslation().x, transform.getTranslation().y, points[0].y);
                }
                if (strokewidth > 0.0d) {
                    StdDraw.setPenColor(stroke);
                    StdDraw.circle(transform.getTranslation().x, transform.getTranslation().y, points[0].y);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
