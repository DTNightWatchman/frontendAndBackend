/**
 * created by YT
 * description: 表示一个搜索结果
 * User:lenovo
 * Data:2022-07-27
 * Time:15:49
 */
public class Result {
    private String title;
    private String url;

    // 正文的一段摘要
    private String desc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Result{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
