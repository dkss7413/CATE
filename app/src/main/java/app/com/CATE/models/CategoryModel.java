package app.com.CATE.models;

public class CategoryModel{
    private String id ;
    private String name ;
    private String detail ;
    private String key ;
    private Boolean state;

    public void setId(String id) {
        this.id = id ;
    }
    public void setName(String name) {
        this.name = name ;
    }
    public void setDetail(String detail) {
        this.detail = detail ;
    }
    public void setKey(String key){ this.key = key; }
    public void setState(Boolean state){
        this.state = state;
    }

    public String getId() {
        return this.id ;
    }
    public String getName() {
        return this.name ;
    }
    public String getDetail() {
        return this.detail ;
    }
    public String getKey(){
        return this.key;
    }
    public Boolean getState(){
        return this.state;
    }

    public CategoryModel(String id, String name, String detail, String key, Boolean state) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.key = key;
        this.state = state;
    }

}
