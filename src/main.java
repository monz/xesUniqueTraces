import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class main {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please specify in/out file: xesUniqueTraces.jar <inXES> <outXES>");
            System.exit(-1);
        }

        String xesPath = args[0];
        String resultPath = args[1];

        XesXmlParser parser = new XesXmlParser();
        //XesLiteXmlParser parser = new XesLiteXmlParser(false);

        File xesFile = new File(xesPath);
        if (! parser.canParse(xesFile)) {
            System.exit(-1);
        }

        Long start, stop;
        start = System.currentTimeMillis();
        List<XLog> res = null;
        try {
            res = parser.parse(new BufferedInputStream(new FileInputStream(xesFile)));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        stop = System.currentTimeMillis();
        System.out.println("Document parsed in " + (stop - start) + " millis.");

        start = System.currentTimeMillis();
        if (res != null) {
            Visitor v = new Visitor(resultPath);
            res.forEach(l -> l.accept(v));
        }
        stop = System.currentTimeMillis();
        System.out.println("Unique traces calculated in " + (stop - start) + " millis.");
    }
}
