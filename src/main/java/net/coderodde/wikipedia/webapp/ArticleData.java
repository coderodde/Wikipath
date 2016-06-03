package net.coderodde.wikipedia.webapp;

import java.util.Objects;

public class ArticleData {

    private final String url;
    private final String title;
    
    public ArticleData(String url, String title) {
        this.url   = Objects.requireNonNull(url, "The URL text is null.");
        this.title = Objects.requireNonNull(title, 
                                            "The article title is null.");
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getTitle() {
        return this.title;
    }
}
