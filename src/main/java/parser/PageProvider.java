package parser;

import java.io.Closeable;

public interface PageProvider extends Closeable {
    public String getPage(String url);
}
