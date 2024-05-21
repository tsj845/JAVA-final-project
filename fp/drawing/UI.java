package fp.drawing;

import java.awt.Color;
import java.awt.Font;
// import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import fp.*;

public class UI implements Drawable {
    public LinkedHashMap<String, String> vals = new LinkedHashMap<>();
    public UI() {}
    public void draw() {
        StdDraw.setPenColor(new Color(230, 230, 230));
        final Font of = StdDraw.getFont();
        StdDraw.setFont(new Font(Font.MONOSPACED, Font.PLAIN, of.getSize()/2));
        double tx = 0, ty = 0.95;
        for (Entry<String, String> e : vals.entrySet()) {
            if (tx < 1) {
                StdDraw.textLeft(tx, ty, e.getKey() + " : " + e.getValue());
            } else {
                StdDraw.textRight(tx, ty, e.getKey() + " : " + e.getValue());
            }
            ty -= 0.05;
            if (ty <= 0) {
                ty = 1;
                tx = 1;
            }
        }
        StdDraw.setFont(new Font(Font.MONOSPACED, Font.PLAIN, of.getSize()*2));
        if (Game.gameover) {
            StdDraw.text(0.5, 0.5, "GAME OVER");
            StdDraw.setFont(new Font(Font.MONOSPACED, Font.PLAIN, of.getSize()));
            StdDraw.text(0.5, 0.2, "Quit     play Again");
            StdDraw.setFont(new Font(Font.MONOSPACED, Font.PLAIN, of.getSize()*2));
        } else if (Game.paused) {
            StdDraw.text(0.5, 0.5, "PAUSED");
        } else {
            StdDraw.setFont(new Font(Font.MONOSPACED, Font.PLAIN, of.getSize()));
        }
        if (Game.gameover || Game.paused) {
            StdDraw.text(0.5, 0.4, Integer.toString(Game.score));
        } else {
            StdDraw.text(0.5, 0.9, Integer.toString(Game.score));
        }
        StdDraw.setFont(of);
    }
}
