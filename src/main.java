import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class main {
    //private static String xesPath = "/home/markus/Desktop/example.xes";
    //private static String xesPath = "/home/markus/Desktop/example2.xes";
    //private static String xesPath = "/home/markus/Desktop/testo.xes";

    //private static String resultPath = "/home/markus/Desktop/unique.xes";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please specify in/out file: uniqueTraces.jar <inXES> <outXES>");
            System.exit(-1);
        }

        String xesPath = args[0];
        String resultPath = args[1];

        XesXmlParser parser = new XesXmlParser();

        File xesFile = new File(xesPath);
        if (! parser.canParse(xesFile)) {
            System.exit(-1);
        }

        List<XLog> res = null;
        try {
            res = parser.parse(new BufferedInputStream(new FileInputStream(xesFile)));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        if (res != null) {
            Visitor v = new Visitor(resultPath);
            res.forEach(l -> l.accept(v));

//            System.out.println("GlobalTraceAttributes:");
//            res.forEach(e -> e.getGlobalTraceAttributes().forEach(i -> i.getKey()));
//            System.out.println("GlobalEventAttributes:");
//            res.forEach(e -> e.getGlobalEventAttributes().forEach(i -> i.getKey()));
//            System.out.println("Classifiers:");
//            res.forEach(e -> e.getClassifiers().forEach(i -> System.out.println(i.name())));
//            System.out.println("getInfo:");
//            res.forEach(e -> e.forEach(t -> t.getAttributes().forEach((s, xAttribute) -> System.out.println(xAttribute.))));
        }
    }
}
