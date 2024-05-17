package fp.drawing;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import fp.StdDraw;
import fp.Vec2;
import fp.entities.Collider;

public class ShapeBuilder {
    private class Instruction {
        int kind;
        Object data;
        Instruction(int kind, Object data) {this.kind=kind;this.data=data;}
        public String toString() {
            return String.format("((%d) {%s})", kind, data);
        }
        Instruction copy() {return new Instruction(kind, data);}
    }
    private class CodeData {
        int code;
        int ending;
        CodeData(int code, int ending) {this.code=(code<<8)|1;this.ending=ending;}
    }
    private class StyleData {
        Color f, s;
        double sw, rot;
        Vec2 pos;
        StyleData() {f=StdDraw.BLACK;s=null;sw=0;rot=0;pos=new Vec2();}
        StyleData copy() {StyleData sd = new StyleData();sd.f=f;sd.s=s;sd.sw=sw;sd.rot=rot;sd.pos=pos;return sd;}
        // StyleData(Color fi, Color st, double lw, Vec2 p) {f=fi;s=st;sw=lw;pos=p;rot=0;}
    }
    private class ArgData {
        String[][] args;
        ArgData(String argss) {
            LinkedList<String[]> fgrp = new LinkedList<>();
            LinkedList<String> cgrp = new LinkedList<>();
            for (String carg : argss.split(",")) {
                if (carg.charAt(0) == '?') {
                    fgrp.addLast(cgrp.toArray(String[]::new));
                    cgrp.clear();
                } else {
                    cgrp.addLast(carg);
                }
            }
            args = fgrp.toArray(String[][]::new);
        }
        boolean hasArg(String arg) {
            for (String[] al : args) {
                for (String a : al) {
                    if (a.equals(arg)) return true;
                }
            }
            return false;
        }
    }
    private class ParamData {
        HashMap<String, String> params = new HashMap<>();
        HashSet<Integer> provided = new HashSet<>();
        public String toString() {
            return String.format("{%s} %s", provided, params.entrySet());
        }
    }
    // prevent processing the same file multiple times
    private HashSet<String> reads = new HashSet<>();
    // meta data
    private HashMap<String, Object> metas = new HashMap<>();
    private HashMap<String, Color> colors = new HashMap<>();
    private HashMap<String, Instruction[]> defs = new HashMap<>();
    private LinkedList<ParamData> argstack = new LinkedList<>();
    private LinkedList<StyleData> stylestack = new LinkedList<>();
    private StyleData cstyle = new StyleData();
    private Path basePath;
    private final int APX = 1, QE = 2, QP = 3, QNP = 4;
    private boolean colliderNext = false, notShape = false;
    private Collider workc = null;
    private Shape working = null;
    private String fmtScope(String fname, LinkedList<String> scopestack) {
        String s = fname + "::<" + String.join(".", scopestack) + ">";
        return s + "::";
    }
    private Color getColor(String[] parts, int i) {
        return new Color(Integer.parseInt(parts[i]), Integer.parseInt(parts[i+1]), Integer.parseInt(parts[i+2]));
    }
    private CodeData resolveCmplxCode(String line) {
        char supercode = line.charAt(0);
        char nxt1 = line.charAt(1);
        if (supercode == 'A') {
            switch (nxt1) {
                case 'P':
                    switch (line.charAt(2)) {
                        case 'X':return new CodeData(APX, 3);
                        default:break;
                    }
                default:break;
            }
        } else if (supercode == 'Q') {
            switch (nxt1) {
                case 'E':return new CodeData(QE, 2);
                case 'P':return new CodeData(QP, 2);
                case 'N':
                    switch (line.charAt(2)) {
                        case 'P':return new CodeData(QNP, 3);
                        default:break;
                    }
                default:break;
            }
        }
        throw new IllegalArgumentException("BAD COMPLEX CODE");
    }
    private Object parseMV(String mv) {
        if (mv.matches("^[\\-+]?([0-9]|0[bdoxBDOX]).*$")) {
            mv = mv.toLowerCase();
            if (mv.indexOf('b') >= 0) {
                return Integer.parseInt(mv, 2);
            }
            if (mv.indexOf('o') >= 0) {
                return Integer.parseInt(mv, 8);
            }
            if (mv.indexOf('x') >= 0) {
                return Integer.parseInt(mv, 16);
            }
            return Integer.parseInt(mv);
        }
        if (mv.matches("^[\\-+]?[0-9]\\.[0-9]*$")) {
            return Double.parseDouble(mv);
        }
        if (mv.toLowerCase().matches("^(true|false)$")) {
            return Boolean.parseBoolean(mv);
        }
        if (mv.startsWith("\"")) {
            return mv.substring(1, mv.length()-1);
        }
        return mv;
    }
    public Set<Entry<String, Object>> getMetas(String pat) {
        String pat2 = ".*?"+pat.replaceAll("(?<!\\\\)\\.", "\\.")+".*";
        return metas.entrySet().stream().filter(p->p.getKey().matches(pat2)).collect(Collectors.toSet());
    }
    public Object getKeyMeta(String key) {
        return metas.get(key);
    }
    public Object getMeta(String pat) {
        return getMetas(pat).iterator().next().getValue();
    }
    public Object selectMeta(String key) {
        Object[] lst = (Object[])getMeta(key);
        return lst[(int)(Math.random()*lst.length)];
    }
    public Object selectMeta(String key, int index) {
        return ((Object[])getMeta(key))[index];
    }
    public Color color(String name) {
        return colors.get(name);
    }
    /*
     * parses the content of asset files
     */
    private void parse(String[] lines) {
        LinkedList<Integer> statestack = new LinkedList<>();
        LinkedList<String> scopestack = new LinkedList<>();
        int state = 0;
        String cname = "";
        ArrayList<Instruction> is = new ArrayList<>();
        LinkedList<LinkedList<String>> nkeys = new LinkedList<>();
        LinkedList<Integer> countstack = new LinkedList<>();
        String fname = null;
        for (String line : lines) {
            if (line.length() == 0 || line.charAt(0) == '/') {continue;}
            if (line.charAt(0) == '\u0003') {fname = line.substring(1);continue;}
            if (line.startsWith("#-")) {
                boolean cont = true;
                line = line.substring(2);
                if (line.equalsIgnoreCase("colors")) {
                    statestack.addLast(state);
                    state = 1;
                } else if (line.equalsIgnoreCase("objs")) {
                    statestack.addLast(state);
                    state = 2;
                } else if (line.equalsIgnoreCase("end")) {
                    if (state == 0) {
                        metas.put(fmtScope(fname, scopestack)+"count", countstack.pop());
                        metas.put(fmtScope(fname, scopestack)+"names", nkeys.removeLast().toArray(String[]::new));
                        scopestack.removeLast();
                    } else {
                        state = statestack.removeLast();
                    }
                } else if (line.equalsIgnoreCase("meta")) {
                    statestack.addLast(state);
                    state = 5;
                } else {cont = false;line = "#-"+line;}
                if (cont) continue;
            }
            if (state == 3 || state == 4) {
                if (line.charAt(0) == 'E') {
                    String k = fmtScope(fname, scopestack) + cname;
                    defs.put(k, is.toArray(Instruction[]::new));
                    nkeys.getLast().add(k);
                    is.clear();
                    cname = "";
                    state = statestack.removeLast();
                    continue;
                }
            }
            switch (state) {
                case 0:
                    if (line.startsWith("#-scope,")) {
                        countstack.addLast(0);
                        nkeys.addLast(new LinkedList<>());
                        scopestack.addLast(line.substring(8));
                    }
                    break;
                case 1:
                    String[] parts = line.split(",");
                    String csv = fmtScope(fname, scopestack);
                    // System.out.println(csv);
                    csv = csv.substring(csv.indexOf('<')+1, csv.indexOf('>'));
                    // System.out.println(csv);
                    colors.put(csv+(csv.length()>0?".":"")+parts[0], getColor(parts, 1));
                    break;
                case 2:
                    char cmd = line.charAt(0);
                    if (cmd == 'I') {
                        cname = line.substring(1);
                        statestack.addLast(state);
                        state = 3;
                        countstack.addLast(countstack.removeLast()+1);
                    } else if (cmd == 'F') {
                        cname = line.substring(1, line.indexOf("{"));
                        statestack.addLast(state);
                        state = 4;
                        countstack.addLast(countstack.removeLast()+1);
                        is.add(new Instruction((((int)('F'))<<8)|2, new ArgData(line.substring(line.indexOf('{')+1,line.length()-1))));
                    }
                    break;
                case 3:
                    is.add(new Instruction((int)(line.charAt(0)), line.substring(1)));
                    break;
                case 4:
                    char fcmd = line.charAt(0);
                    switch (fcmd) {
                        case 'A':
                        case 'Q':
                            CodeData cd = resolveCmplxCode(line);
                            is.add(new Instruction(cd.code, line.substring(cd.ending)));
                            break;
                        default:
                            is.add(new Instruction((int)(line.charAt(0)), line.substring(1)));
                            break;
                    }
                    break;
                case 5:
                    metas.put(fmtScope(fname, scopestack)+line.substring(0,line.indexOf(':')), parseMV(line.substring(line.indexOf(':')+1)));
                    break;
                default:
                    throw new IllegalArgumentException("BAD OBJECT DATA");
            }
        }
        for (Instruction[] ent : defs.values()) {
            for (int i = 0; i < ent.length; i ++) {
                if (ent[i].kind == 'J' || ent[i].kind == 'U') {
                    ent[i].data = resolveUJName((char)ent[i].kind, ent[i].data.toString());
                }
            }
        }
    }
    /*
     * the way that names are stored in the HashMap's are too cumbersome for the asset files, so they must
     * be converted
     */
    private String resolveUJName(char k, String inst) {
        // System.out.println(inst);
        String[] parts = new String[]{inst.substring(0, (k=='J')?inst.indexOf('{'):inst.length()), (k=='J')?inst.substring(inst.indexOf('{')):""};
        String pat = ".*?<"+parts[0].substring(0, parts[0].lastIndexOf('.')).replace(".", "\\.")+">::"+parts[0].substring(parts[0].lastIndexOf('.')+1);
        // System.out.println(pat);
        for (String pkey : defs.keySet()) {
            if (pkey.matches(pat)) {
                // System.out.println(pkey);
                return pkey+parts[1];
            }
        }
        throw new NoSuchElementException();
    }
    private List<String> process(String fname, List<String> lines) {
        ArrayList<String> finals = new ArrayList<>(lines.size());
        boolean fdes = true;
        for (String l : lines) {
            if (l.startsWith("#-use")) {
                LinkedList<String> subs = new LinkedList<>();
                l = l.substring(6);
                if (l.contains("$$")) {
                    String[] parts = l.split("\\$\\$");
                    LinkedList<String> l2 = new LinkedList<>();
                    subs.add(parts[0]);
                    for (int i = 1; i < parts.length; i ++) {
                        if (i % 2 == 1) {
                            for (String s : subs) {
                                for (String v : parts[i].substring(1, parts[i].length()-1).split(",")) {
                                    l2.add(s + v);
                                }
                            }
                            subs.clear();
                        } else {
                            for (String s : l2) {
                                subs.add(s + parts[i]);
                            }
                            l2.clear();
                        }
                    }
                } else {
                    subs.add(l);
                }
                for (String s : subs) {
                    if (!reads.contains(basePath.resolve(s).normalize().toString())) {
                        List<String> lns = readLines(s);
                        Path bp = basePath;
                        basePath = bp.resolve(s).normalize().getParent();
                        finals.addAll(process(s, lns));
                        basePath = bp;
                    }
                }
            } else {
                if (fdes) {
                    fdes = false;
                    finals.add("\u0003" + fname);
                    // finals.add("#-file");
                }
                finals.add(l);
            }
        }
        return finals;
    }
    private List<String> readLines(String file) {
        try {
            Path p = basePath.resolve(Path.of(file)).normalize();
            reads.add(p.toString());
            return Files.readAllLines(p);
        } catch (IOException IE) {IE.printStackTrace();System.exit(1);return null;}
    }
    public ShapeBuilder(String file) {
        basePath = Path.of(file).normalize().toAbsolutePath().getParent();
        try {
            String fn = Path.of(file).getFileName().toString();
            parse(process(fn, readLines(fn)).toArray(String[]::new));
            System.out.println(defs.entrySet());
        } catch (NoSuchElementException NSE) {
            NSE.printStackTrace();
            System.out.println(defs.entrySet());
            System.out.println(colors.entrySet());
            throw new IllegalStateException();
        }
        // System.out.println(colors.entrySet());
        // for (Entry<String, Instruction[]> ent : defs.entrySet()) {
        //     System.out.print(ent.getKey());
        //     System.out.print(" : ");
        //     System.out.println(Arrays.toString(ent.getValue()));
        // }
    }
    private String prepFuncI(Instruction i) {
        String[] p = i.data.toString().split("\\[");
        String r = p[0];
        ParamData pd = argstack.getLast();
        for (int j = 1; j < p.length; j ++) {
            String k = p[j].substring(0, p[j].indexOf(']'));
            r += pd.params.get(k);
            r += p[j].substring(p[j].indexOf(']')+1);
        }
        // System.out.println(i);
        // System.out.println(pd);
        // System.out.println(r);
        return r;
    }
    private String evalBFunc(String str) {
        String[] parts = str.split(":");
        switch (parts[0].intern()) {
            case "irng":
                return Integer.toString((int)(Math.random()*(
                    ((parts.length>2)?(
                        (Integer.parseInt(parts[2])-Integer.parseInt(parts[1]))/((parts.length>3)?(
                            Integer.parseInt(parts[3])
                        ):1)
                        ):(Integer.parseInt(parts[1]))
                    )
                )));
            case "drng":
                return Double.toString(Math.random()*(
                    ((parts.length>2)?(
                        (Double.parseDouble(parts[2])-Double.parseDouble(parts[1]))/((parts.length>3)?(
                            Double.parseDouble(parts[3])
                        ):1)
                        ):(Double.parseDouble(parts[1]))
                    )
                ));
            case "geom":
                colliderNext = true;
                return "";
            case "noshape":
                notShape = true;
                return "";
            default:return"";
        }
    }
    private String prepAllI(String id) {
        String[] p = id.split("(?<!::)<");
        String f = p[0];
        for (int i = 1; i < p.length; i ++) {
            String[] c = p[i].split(">(?!::)");
            f += evalBFunc(c[0]);
            f += c.length>1?c[1]:"";
        }
        return f;
    }
    private void executeR(String name, boolean function) {
        // System.out.println("EXECR: " + name);
        Instruction[] codes = defs.get(name);
        int contd = 0;
        boolean poly = false, abs = false;
        LinkedList<Vec2> polc = new LinkedList<>();
        for (Instruction i : codes) {
            i = i.copy();
            if ((i.kind & 0xff) == 2) {continue;}
            i.data = prepAllI(i.data.toString());
            if (i.kind == 'H') continue;
            if (function) {
                ParamData pd = argstack.getLast();
                if (contd > 0) {
                    if ((i.kind & 0xff) == 1) {
                        switch (i.kind >> 8) {
                            case QE:
                                contd --;
                                break;
                            case QP:case QNP:
                                contd ++;
                                break;
                            default:break;
                        }
                    }
                    continue;
                }
                if ((i.kind & 0xff) == 1) {
                    switch (i.kind >> 8) {
                        case APX:
                            boolean xorc = false;
                            for (String c : i.data.toString().split(",")) {
                                if (pd.provided.contains(Integer.parseInt(c.substring(1)))) {
                                    if (xorc) {
                                        throw new IllegalArgumentException("APX FAILURE");
                                    }
                                    xorc = true;
                                }
                            }
                            if (!xorc) {
                                throw new IllegalArgumentException("APX FAILURE");
                            }
                            break;
                        case QP:
                            // System.out.println("TESTQP");
                            if (!pd.provided.contains(Integer.parseInt(i.data.toString().substring(1)))) {
                                // System.out.println("QPCONT");
                                contd ++;
                            }
                            break;
                        case QNP:
                            if (pd.provided.contains(Integer.parseInt(i.data.toString().substring(1)))) {
                                contd ++;
                            }
                            break;
                        default:break;
                    }
                    continue;
                } else {
                    i.data = prepFuncI(i);
                }
            }
            if (poly) {
                if (i.kind == 'v' || i.kind == 'V') {
                    poly = false;
                    Shape pols = Shape.Poly(polc.toArray(Vec2[]::new), new Transform(working.transform, cstyle.pos, cstyle.rot));
                    if (colliderNext) {
                        workc.addCollider(Collider.ofPoly(pols.transform, pols.points));
                    }
                    colliderNext = false;
                    polc.clear();
                    if (notShape) {
                        notShape = false;
                        continue;
                    }
                    pols.fill(cstyle.f);
                    pols.stroke(cstyle.s);
                    pols.strokewidth(cstyle.sw);
                    working.addShape(pols);
                } else {
                    if (!(i.kind == 'g' || i.kind == 'G')) continue;
                    abs = i.kind == 'G';
                    for (String cor : i.data.toString().split(";")) {
                        Vec2 pv = new Vec2(Double.parseDouble(cor.substring(0, cor.indexOf(','))), Double.parseDouble(cor.substring(cor.indexOf(',')+1)));
                        polc.addLast((abs||polc.size()==0)?pv:(polc.getLast().add(pv)));
                    }
                }
                continue;
            }
            if (i.kind == 'D') {System.out.println("DEBUGSTMT: "+i.data.toString());continue;}
            switch (i.kind) {
                case'l':case'L':
                    stylestack.addLast(cstyle);
                    cstyle = cstyle.copy();
                    break;
                case'k':case'K':
                    cstyle = stylestack.removeLast();
                    break;
                case'c':case'C':
                    if (i.data instanceof String) {
                        String d = (String)i.data;
                        if (d.indexOf(',') >= 0) {
                            cstyle.f = getColor(d.split(","), 0);
                        } else if (d.equals("null")) {
                            cstyle.f = null;
                        } else {
                            cstyle.f = colors.get(d);
                        }
                    }
                    break;
                case's':case'S':
                    if (i.data instanceof String) {
                        String d = (String)i.data;
                        if (d.indexOf(',') >= 0) {
                            cstyle.s = getColor(d.split(","), 0);
                        } else if (d.equals("null")) {
                            cstyle.s = null;
                        } else {
                            cstyle.s = colors.get(d);
                        }
                    }
                    break;
                case'w':case'W':
                    cstyle.sw = Double.parseDouble(i.data.toString());
                    break;
                case'b':case'B':
                    String dat = i.data.toString();
                    double bhw = Double.parseDouble(dat.substring(0, dat.indexOf(','))), bhh = Double.parseDouble(dat.substring(dat.indexOf(',')+1));
                    Shape boxs = Shape.Rect(bhw, bhh, new Transform(working.transform, cstyle.pos, cstyle.rot));
                    boxs.fill(cstyle.f);
                    boxs.stroke(cstyle.s);
                    boxs.strokewidth(cstyle.sw);
                    if (!notShape)
                    working.addShape(boxs);
                    notShape = false;
                    if (colliderNext) {
                        workc.addCollider(Collider.boxCollider(boxs.transform, bhw, bhh));
                    }
                    break;
                case'r':case'R':
                    double r = Double.parseDouble(i.data.toString());
                    Shape s = Shape.Circle(r, new Transform(working.transform, cstyle.pos, cstyle.rot));
                    s.fill(cstyle.f);
                    s.stroke(cstyle.s);
                    s.strokewidth(cstyle.sw);
                    if (!notShape)
                    working.addShape(s);
                    notShape = false;
                    if (colliderNext) {
                        workc.addCollider(Collider.circleCollider(s.transform, r));
                    }
                    break;
                case'm':
                    String[] relparts = i.data.toString().split(",");
                    cstyle.pos = cstyle.pos.add(new Vec2(Double.parseDouble(relparts[0]),Double.parseDouble(relparts[1])));
                    break;
                case'M':
                    String[] parts = i.data.toString().split(",");
                    cstyle.pos = new Vec2(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]));
                    break;
                case'o':
                    cstyle.rot += Double.parseDouble(i.data.toString());
                    break;
                case'O':
                    cstyle.rot = Double.parseDouble(i.data.toString());
                    break;
                case'j':case'J':
                    executeC(i.data.toString());
                    break;
                case'u':case'U':
                    executeR(i.data.toString(), false);
                    break;
                case'p':case'P':
                    poly = true;
                    break;
                default:
                    System.out.println("UNHANDLED");
                    System.out.println(i);
                    break;
            }
            colliderNext = false;
        }
    }
    private ParamData parseArgs(ArgData ad, String[] args) {
        ParamData pd = new ParamData();
        for (String c : args) {
            if (c.charAt(0) == '?') {
                pd.provided.add(Integer.parseInt(c.substring(1)));
                continue;
            }
            String[] p = c.split("=");
            String a = p[0];
            String v = p[1];
            if (ad.hasArg(a)) {
                pd.params.put(a, v);
            }
        }
        return pd;
    }
    private void executeC(String d) {
        // System.out.println("CALLEXEC");
        String n = d.substring(0, d.indexOf('{'));
        Instruction fargs = defs.get(n)[0];
        ArgData ad = (ArgData)fargs.data;
        ParamData pd = parseArgs(ad, d.substring(d.indexOf('{')+1, d.length()-1).split(","));
        // System.out.println(pd);
        argstack.addLast(pd);
        executeR(n, true);
        argstack.removeLast();
    }
    public BuildResult execute(String name, Transform t) {
        // System.out.println(name);
        working = Shape.Group(t);
        workc = Collider.compoundCollider(working.transform);
        cstyle = new StyleData();
        stylestack.clear();
        argstack.clear();
        executeR(name, false);
        BuildResult r = new BuildResult(working, workc);
        working = null;
        workc = null;
        return r;
    }
    public BuildResult execute(String name) {
        return execute(name, new Transform());
    }
    public void debug() {
        System.out.println(defs.keySet());
        System.out.println(colors.keySet());
    }
}
