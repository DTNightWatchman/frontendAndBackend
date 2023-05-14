/**
 * created by YT
 * description: 这个类把 文档id 和 文档相关性的权重 进行包裹
 * User:lenovo
 * Data:2022-07-26
 * Time:21:15
 */
public class Weight {
    private int docId;

    // 这个表示相关性，这个值越大，相关性就越强
    private int weight;

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
