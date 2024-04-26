package fp;

import java.util.LinkedList;
import java.util.TreeSet;

import fp.events.Event;
import fp.events.EventListener;
import fp.events.EventType;
import fp.events.KEvent;

import java.awt.event.*;

/*
 * observes and relays events
 */

public class Observer implements MouseListener, MouseMotionListener, KeyListener {
    private static final LinkedList<EventListener> listeners = new LinkedList<>();
    private static final Object MOUSE_LOCK = new Object();
    private static final Object KEY_LOCK = new Object();
    private static final Object SIG_LOCK = new Object();
    private static final Observer ob = new Observer();
    // private static class KeyData implements Comparable<KeyData> {
    //     final int keycode;
    //     int timedown = 0;
    //     KeyData(int kc) {keycode = kc;}
    //     @Override
    //     public int compareTo(KeyData o) {
    //         if (o == null) throw new NullPointerException();
    //         if (o.keycode == keycode) return 0;
    //         if (o.keycode > keycode) return -1;
    //         return 1;
    //     }
    // }
    // private static final TreeSet<KeyData> keys_down = new TreeSet<>();
    private static final TreeSet<Integer> keys_down = new TreeSet<>();
    static {init();}
    private Observer() {}
    private static void init() {
        StdDraw.frame.addKeyListener(ob);
        StdDraw.frame.addMouseListener(ob);
        StdDraw.frame.addMouseMotionListener(ob);
    }
    public static void register(EventListener el) {
        listeners.add(el);
    }
    public static void unregister(EventListener el) {
        listeners.remove(el);
    }
    public static void signal(Event sig) {
        synchronized(SIG_LOCK) {
            for (EventListener el : listeners) {
                el.trigger(sig);
            }
        }
    }
    private static char _vkcToChar(int keycode) {
        switch(keycode) {
            case(KeyEvent.VK_0):return'0';
            case(KeyEvent.VK_1):return'1';
            case(KeyEvent.VK_2):return'2';
            case(KeyEvent.VK_3):return'3';
            case(KeyEvent.VK_4):return'4';
            case(KeyEvent.VK_5):return'5';
            case(KeyEvent.VK_6):return'6';
            case(KeyEvent.VK_7):return'7';
            case(KeyEvent.VK_8):return'8';
            case(KeyEvent.VK_9):return'9';
            case(KeyEvent.VK_A):return'a';
            case(KeyEvent.VK_B):return'b';
            case(KeyEvent.VK_C):return'c';
            case(KeyEvent.VK_D):return'd';
            case(KeyEvent.VK_E):return'e';
            case(KeyEvent.VK_F):return'f';
            case(KeyEvent.VK_G):return'g';
            case(KeyEvent.VK_H):return'h';
            case(KeyEvent.VK_I):return'i';
            case(KeyEvent.VK_J):return'j';
            case(KeyEvent.VK_K):return'k';
            case(KeyEvent.VK_L):return'l';
            case(KeyEvent.VK_M):return'm';
            case(KeyEvent.VK_N):return'n';
            case(KeyEvent.VK_O):return'o';
            case(KeyEvent.VK_P):return'p';
            case(KeyEvent.VK_Q):return'q';
            case(KeyEvent.VK_R):return'r';
            case(KeyEvent.VK_S):return's';
            case(KeyEvent.VK_T):return't';
            case(KeyEvent.VK_U):return'u';
            case(KeyEvent.VK_V):return'v';
            case(KeyEvent.VK_W):return'w';
            case(KeyEvent.VK_X):return'x';
            case(KeyEvent.VK_Y):return'y';
            case(KeyEvent.VK_Z):return'z';
            case(KeyEvent.VK_PERIOD):return'.';
            case(KeyEvent.VK_COMMA):return',';
            case(KeyEvent.VK_SEMICOLON):return';';
            case(KeyEvent.VK_EQUALS):return'=';
            case(KeyEvent.VK_SLASH):return'/';
            case(KeyEvent.VK_QUOTE):return'\'';
            case(KeyEvent.VK_BACK_QUOTE):return'`';
            case(KeyEvent.VK_BACK_SLASH):return'\\';
            case(KeyEvent.VK_MINUS):return'-';
            case(KeyEvent.VK_OPEN_BRACKET):return'[';
            case(KeyEvent.VK_CLOSE_BRACKET):return']';
            default:return (char)0;
        }
    }
    private static char _vkcManUpper(char c) {
        switch(c) {
            case('`'):return'~';
            case('1'):return'!';
            case('2'):return'@';
            case('3'):return'#';
            case('4'):return'$';
            case('5'):return'%';
            case('6'):return'^';
            case('7'):return'&';
            case('8'):return'*';
            case('9'):return'(';
            case('0'):return')';
            case('-'):return'_';
            case('='):return'+';
            case('['):return'{';
            case(']'):return'}';
            case('\\'):return'|';
            case(';'):return':';
            case('\''):return'"';
            case(','):return'<';
            case('.'):return'>';
            case('/'):return'?';
            default:return 0;
        }
    }
    private static char vkcToChar(int keycode) {
        char c = _vkcToChar(keycode);
        return ((KeyEvent.SHIFT_DOWN_MASK&keycode) != 0) ? (Character.isAlphabetic(c) ? Character.toUpperCase(c) : _vkcManUpper(c)) : c;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        synchronized(KEY_LOCK) {
            Observer.signal(new KEvent(EventType.KeyPress, e.getKeyChar()));
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        synchronized(KEY_LOCK) {
            // Observer.keys_down.add(new KeyData(e.getKeyCode()));
            Observer.keys_down.add(e.getKeyCode());
            Observer.signal(new KEvent(EventType.KeyDown, e.getKeyCode()));
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        synchronized(KEY_LOCK) {
            // KeyData kd = null;
            // for (KeyData d : keys_down) {
            //     if (d.keycode == e.getKeyCode()) {kd = d;break;}
            // }
            // if (kd.timedown < 250) {
            //     char c = Observer.vkcToChar(kd.keycode);
            //     if (c != 0) Observer.signal(new KEvent(EventType.KeyPress, c));
            // }
            // Observer.keys_down.remove(kd);
            Observer.keys_down.remove(e.getKeyCode());
            Observer.signal(new KEvent(EventType.KeyUp, e.getKeyCode()));
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
