import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XTraceImpl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class XTraceImplHashed extends XTraceImpl {
    private String hash;

    public XTraceImplHashed(XTrace trace) {
        this((XAttributeMap) trace.getAttributes().clone());

        trace.forEach(xEvent -> this.add((XEvent) xEvent.clone()));
    }

    public XTraceImplHashed(XAttributeMap attributeMap) {
        super(attributeMap);
    }

    public String getHash() {
        if (hash == null || hash.isEmpty()) {
            hash = calculateHash();
        }
        //System.out.println("Calculated hash:" + hash);
        return hash;
    }

    private String calculateHash() {
        //System.out.println("in calculate hash");
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        this.forEach(xEvent -> xEvent.getAttributes()
            .entrySet().stream()
            .filter(e -> e.getKey().equals("concept:name"))
            .forEach(e2 -> md.update(e2.getValue().toString().getBytes())));

        return digestToString(md.digest());
    }

    private static String digestToString(byte[] digest) {
        // convert byte[] to string; hex value
        StringBuffer checksum = new StringBuffer(32);
        String s;
        for (byte b : digest) {
            s = Integer.toHexString(0xFF & b);
            if (s.length() == 1) {
                checksum.append('0');
            }
            checksum.append(s);
        }

        return checksum.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof XTraceImplHashed) {
            XTraceImplHashed ob = (XTraceImplHashed) o;
            return ob.getHash().equals(this.getHash());
        } else {
            return false;
        }
    }
}
