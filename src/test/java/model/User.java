package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private String user;
    @JsonProperty("ID")
    private Integer id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Items items;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

//    {
//        "user": "TionBail",
//            "ID": "1",
//            "name": "Ivan Petrov",
//            "items": {
//        "email": "ivan.petrov@test.com",
//                "language": "RU",
//                "active": true
//     }
//    }
}
