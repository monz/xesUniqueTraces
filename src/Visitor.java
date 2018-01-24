import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Visitor extends XVisitor {
    private XLog log;
    private Map<String, XTrace> uniqueTraces;
    private String resultFile;
    private ExecutorService executor;
    private List<Future<AbstractMap.SimpleEntry<String, XTrace>>> futures;

    public Visitor(String resultFile) {
        uniqueTraces = new HashMap<>();
        executor = Executors.newFixedThreadPool(2);
        futures = new ArrayList<>();
        this.resultFile = resultFile;
        System.out.println("constructor");
    }

    @Override
    public void init(XLog log) {
        this.log = log;
    }

    @Override
    public void visitTracePost(XTrace trace, XLog log) {
        super.visitTracePost(trace, log);

        HashFunction hf = Hashing.md5();
        Hasher h = hf.newHasher();
        trace.forEach(xEvent -> xEvent.getAttributes()
            .entrySet().stream()
            .filter(e -> e.getKey().equals("concept:name"))
            .forEach(e2 -> h.putString(e2.getValue().toString(), Charsets.UTF_8)));

        HashCode hc = h.hash();
        uniqueTraces.putIfAbsent(hc.toString(), trace);
    }

    @Override
    public void visitLogPost(XLog log) {
        super.visitLogPost(log);

        //uniqueTraces.forEach((k, t) -> System.out.println(k));

        System.out.println("Log size: " + log.size());
        System.out.println("Unique log size: " + uniqueTraces.size());
        System.out.println("Information reduced by: " + ((double)log.size() - uniqueTraces.size())/log.size() * 100 + "%");

        // clear old traces
        log.clear();
        // clone metadata
        XLog uniqueLog = (XLog) log.clone();
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