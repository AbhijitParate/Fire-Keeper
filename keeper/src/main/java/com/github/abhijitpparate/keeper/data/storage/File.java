package com.github.abhijitpparate.keeper.data.storage;


public class File {

    public enum Type {
        IMAGE, VIDEO, AUDIO
    }

    private String url;
    private String name;
    private String type;

    public File(String name, Type type) {
        this.name = name;
        this.type = String.valueOf(type);
    }

    public File() {
        // Required by firebase
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public static Type getType(String type){
        if (type.contains("image")){
            return Type.IMAGE;
        } else return null;
    }

    public void setType(String type) {
        this.type = type;
    }
}
