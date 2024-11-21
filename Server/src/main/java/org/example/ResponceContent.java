package org.example;

//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
public class ResponceContent {
    private String mimeType;
    private byte[] content;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

}

