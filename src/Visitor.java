import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Visitor extends XVisitor {
    private XLog log;
    private Map<String, XTrace> uniqueTraces;
    private String resultFile;

    public Visitor(String resultFile) {
        uniqueTraces = new HashMap<>();
        this.resultFile = resultFile;
    }

    @Override
    public void init(XLog log) {
        this.log = log;
    }

    @Override
    public void visitExtensionPre(XExtension ext, XLog log) {
        super.visitExtensionPre(ext, log);
    }

    @Override
    public void visitClassifierPre(XEventClassifier classifier, XLog log) {
        super.visitClassifierPre(classifier, log);
    }

    @Override
    public void visitTracePre(XTrace trace, XLog log) {
        super.visitTracePre(trace, log);

        //System.out.println("--- trace start ---");
        //trace.getAttributes().forEach((key, attr) -> System.out.println(attr));
    }

    @Override
    public void visitTracePost(XTrace trace, XLog log) {
        super.visitTracePost(trace, log);

        XTraceImplHashed t = new XTraceImplHashed(trace);
        t.getHash();

        uniqueTraces.putIfAbsent(t.getHash(), t);
        //System.out.println("--- trace end ---");
    }

    @Override
    public void visitEventPre(XEvent event, XTrace trace) {
        super.visitEventPre(event, trace);

        //event.getAttributes().entrySet().stream()
        //    .filter(e -> e.getKey().equals("concept:name"))
        //    .forEach(e2 -> System.out.println(e2.getValue()));
    }

    @Override
    public void visitLogPost(XLog log) {
        super.visitLogPost(log);

        uniqueTraces.forEach((k, t) -> {
            if (t instanceof XTraceImplHashed) {
                System.out.println(((XTraceImplHashed) t).getHash());
            }
        });

        System.out.println("Log size: " + log.size());
        System.out.println("Unique log size: " + uniqueTraces.size());
        System.out.println("Information reduced by: " + ((double)log.size() - uniqueTraces.size())/log.size() * 100 + "%");

        // clear old traces
        log.clear();
        // clone metadata
        XLog uniqueLog = (XLog) log.clone();
        //uniqueLog.clear();
        // add unique traces
        uniqueLog.addAll(uniqueTraces.values());

        XesXmlSerializer out = new XesXmlSerializer();
        try {
            out.serialize(uniqueLog, new BufferedOutputStream(new FileOutputStream(resultFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}