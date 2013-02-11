package mr.reviews.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ReviewReport {

    private int numReviews;
    private String keyword;
    private String author;
    private String fullReport;

    public ReviewReport() {

    }

    public ReviewReport(int numReviews, String keyword, String author, String fullReport) {
        super();
        this.numReviews = numReviews;
        this.keyword = keyword;
        this.author = author;
        this.fullReport = fullReport;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
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
