package org.philwilson.huffduffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class AtomFeedParser {

    // We don't use namespaces
    private static final String ns = null;

    // TODO after population cache these collections so items can be displayed on app load when it 
    // was left on a detail page
    public static List<Entry> ITEMS = new ArrayList<Entry>();
    public static Map<String, Entry> ITEM_MAP = new HashMap<String, Entry>();

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        // empty the ITEMS list
        ITEMS.clear();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                Entry entry = readEntry(parser);
                ITEMS.add(entry);
                // TODO replace this by the ID huffduffer uses
                ITEM_MAP.put(entry.getId(), entry);
            } else {
                skip(parser);
            }
        }
        return ITEMS;
    }

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
    public static class Entry {
        public final String id;
        public final String title;
        public final String link;
        public final String summary;
        public final String authorName;

        public Entry(String id, String title, String summary, String link, String authorName) {
            this.id = id;
            this.title = title;
            this.summary = summary;
            this.link = link;
            this.authorName = authorName;
        }
        
        public String getId() {
            return this.id;
        }
        
        @Override        
        public String toString() {
            return this.title;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or
    // link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise,
    // skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String id = null;
        String title = null;
        String summary = null;
        String tempLink = null;
        String link = null;
        String name = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String elementName = parser.getName();
            if (elementName.equals("id")) {
                id = readId(parser);
            } else if (elementName.equals("title")) {
                title = readTitle(parser);
            } else if (elementName.equals("name")) {
                name = readName(parser);
            } else if (elementName.equals("content")) {
                summary = readContent(parser);
            } else if (elementName.equals("link")) {
                tempLink = readLink(parser);
                if (!tempLink.isEmpty()) {
                    link = tempLink;
                }
            } else {
                skip(parser);
            }
        }
        return new Entry(id, title, summary, link, name);
    }

    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return id;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String relType = parser.getAttributeValue(null, "rel");
        if (relType.equals("alternate")) {
            link = parser.getAttributeValue(null, "href");
        }
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "content");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "content");
        return summary;
    }

    // Processes name tags in the feed. These are part of the <author> section.
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested
    // tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps
    // going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being
    // 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
    }
}
