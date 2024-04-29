package fp.drawing;

import java.awt.Color;
import java.util.LinkedList;

import fp.FPoint;
import fp.StdDraw;

public class Shape {
    private final LinkedList<Shape> shapes;
    private final FPoint[] points;
    public Transform transform;
    public Color fill;
    public Color stroke;
    public double strokewidth;
    public Shape(FPoint[] points) {
        this.shapes = null;
        this.points = points;
        this.transform = new Transform();
        this.fill = new Color(0);
        this.stroke = new Color(0);
        this.strokewidth = 0.0d;
    }
    public Shape(FPoint[] points, Transform transform) {
        this.shapes = null;
        this.points = points;
        this.transform = transform;
        this.fill = new Color(0);
        this.stroke = new Color(0);
        this.strokewidth = 0.0d;
    }
    public Shape(FPoint[] points, Color fill) {
        this.shapes = null;
        this.points = points;
        this.transform = new Transform();
        this.fill = fill;
        this.stroke = new Color(0);
        this.strokewidth = 0.0d;
    }
    public Shape(FPoint[] points, Transform transform, Color fill) {
        this.shapes = null;
        this.points = points;
        this.transform = transform;
        this.fill = fill;
        this.stroke = new Color(0);
        this.strokewidth = 0.0d;
    }
    public Shape(FPoint[] points, Color fill, Color stroke) {
        this.shapes = null;
        this.points = points;
        this.transform = new Transform();
        this.fill = fill;
        this.stroke = stroke;
        this.strokewidth = 0.0d;
    }
    public Shape(FPoint[] points, Transform transform, Color fill, Color stroke) {
        this.shapes = null;
        this.points = points;
        this.transform = transform;
        this.fill = fill;
        this.stroke = stroke;
        this.strokewidth = 0.0d;
    }
    public Shape(FPoint[] points, Color fill, Color stroke, double strokewidth) {
        this.shapes = null;
        this.points = points;
        this.transform = new Transform();
        this.fill = fill;
        this.stroke = stroke;
        this.strokewidth = strokewidth;
    }
    public Shape(FPoint[] points, Transform transform, Color fill, Color stroke, double strokewidth) {
        this.shapes = null;
        this.points = points;
        this.transform = transform;
        this.fill = fill;
        this.stroke = stroke;
        this.strokewidth = strokewidth;
    }
    private Shape() {
        this.shapes = new LinkedList<>();
        this.points = null;
        this.transform = new Transform();
        this.fill = null;
        this.stroke = null;
        this.strokewidth = 0.0d;
    }
    public Shape shapeGroup() {
        return new Shape();
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
        double[] x = new double[points.length], y = new double[points.length];
        int i = 0;
        for (FPoint p : transform.apply(points)) {
            x[i] = p.x;
            y[i++] = p.y;
        }
        StdDraw.setPenRadius(strokewidth);
        StdDraw.setPenColor(fill);
        StdDraw.filledPolygon(x, y);
        if (strokewidth > 0) {
            StdDraw.setPenColor(stroke);
            StdDraw.polygon(x, y);
        }
    }
}
