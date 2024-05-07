package fp.drawing;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import fp.Vec2;

public class ShapeBuilder {
    private class Instruction {
        int kind;
        Object data;
    }
    private LinkedList<Vec2> points = new LinkedList<>();
    private HashMap<String, Color> colors = new HashMap<>();
    private HashMap<String, Instruction[]> defs = new HashMap<>();
    private String fmtScope(LinkedList<String> scopestack) {
        return String.join(".", scopestack);
    }
    private Color getColor(String[] parts, int i) {
        return new Color(Integer.parseInt(parts[i]), Integer.parseInt(parts[i+1]), Integer.parseInt(parts[i+2]));
    }
    private void parse(String[] lines) {
        LinkedList<Integer> statestack = new LinkedList<>();
        LinkedList<String> scopestack = new LinkedList<>();
        int state = 0;
        for (String line : lines) {
            if (line.startsWith("#-")) {
                line = line.substring(2);
                if (line.equalsIgnoreCase("colors")) {
                    statestack.addLast(state);
                    state = 1;
                } else if (line.equalsIgnoreCase("objs")) {
                    statestack.addLast(state);
                    state = 2;
                } else if (line.equalsIgnoreCase("end")) {
                    state = statestack.removeLast();
                }
                continue;
            }
            switch (state) {
                case 0:
                    break;
                case 1:
                    if (line.length() > 0) {
                        String[] parts = line.split("\\.");
                        colors.put(fmtScope(scopestack)+"."+parts[0], getColor(parts, 1));
                    }
                default:
                    throw new IllegalArgumentException("BAD OBJECT DATA");
            }
        }
    }
    private List<String> process(List<String> lines) {
        ArrayList<String> finals = new ArrayList<>(lines.size());
        for (String l : lines) {
            if (l.startsWith("#-use")) {
                finals.addAll(process(ShapeBuilder.readLines(l.substring(6))));
            } else {
                finals.add(l);
            }
        }
        return finals;
    }
    private static List<String> readLines(String file) {
        try {
            return Files.readAllLines(Path.of(file));
        } catch (IOException IE) {IE.printStackTrace();System.exit(1);return null;}
    }
    public ShapeBuilder(String file) {
        parse(process(readLines(file)).toArray(String[]::new));
    }
    public Shape toShape() {
        return Shape.Poly(points.toArray(Vec2[]::new));
    }
    public Shape withTransform(Transform t) {
        return Shape.Poly(points.toArray(Vec2[]::new), t);
    }
}
