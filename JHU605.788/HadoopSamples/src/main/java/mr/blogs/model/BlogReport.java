package mr.blogs.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BlogReport {

    private int numBlogs;
    private String keyword;
    private String author;
    private String fullReport;

    public BlogReport() {

    }

    public BlogReport(int numBlogs, String keyword, String author, String fullReport) {
        super();
        this.numBlogs = numBlogs;
        this.keyword = keyword;
        this.author = author;
        this.fullReport = fullReport;
    }

    public int getNumBlogs() {
        return numBlogs;
    }

    public void setNumBlogs(int numBlogs) {
        this.numBlogs = numBlogs;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFullReport() {
        return fullReport;
    }

    public void setFullReport(String fullReport) {
        this.fullReport = fullReport;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
