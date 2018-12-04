package webserver.models;

public class SearchModel {
    private String type;
    private String keyword;


    public SearchModel() {
        setType(null);
        setKeyword(null);
    }

    public SearchModel(String type, String keyword) {
        setType(type);
        setKeyword(keyword);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
