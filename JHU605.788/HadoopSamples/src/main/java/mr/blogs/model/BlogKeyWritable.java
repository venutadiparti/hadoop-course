package mr.blogs.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.hadoop.io.WritableComparable;

public class BlogKeyWritable implements WritableComparable<BlogKeyWritable> {

    private String author;
    private String keyword;

    public BlogKeyWritable() {
    }

    public BlogKeyWritable(String author, String keyword) {
        this.setAuthor(author);
        this.setKeyword(keyword);
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        author = input.readUTF();
        keyword = input.readUTF();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(author);
        output.writeUTF(keyword);
    }

    @Override
    public int compareTo(BlogKeyWritable other) {
        return new CompareToBuilder()
               .append(this.author, other.author)
               .append(this.keyword, other.keyword)
               .toComparison();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
