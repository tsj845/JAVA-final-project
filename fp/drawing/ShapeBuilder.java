package fp.drawing;

import java.util.LinkedList;
import fp.FPoint;

public class ShapeBuilder {
    private LinkedList<FPoint> points = new LinkedList<>();
    public ShapeBuilder() {}
    public ShapeBuilder(FPoint[] points) {
        for (FPoint p : points) {
            this.points.add(p);
        }
    }
    public FPoint pop() {
        return points.removeLast();
    }
    public void push(FPoint p) {
        points.add(p);
    }
    public Shape toShape() {
        return new Shape(points.toArray(FPoint[]::new));
    }
}
