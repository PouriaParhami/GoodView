package com.redfirelab.android.wpmobileapp.ultilities;

import java.io.Serializable;

/**
 * Created by Pouria on 11/13/2017.
 * Android project.
 * <p>
 * i change this class automatically, i implement the serializeable, but know
 * i implement parcelable becuse i read Parcelable is faster
 * but i dont know usage of this is change or not or i need getter and seeters or not
 */

public class WordpressPostData implements Serializable {

    private int pId;
    private String pTitle;
    private String pDescription;
    private String pLinkOfPicture;
    private String pContent;
    private String pPostLink;
    private String pBaseUrl;


    public WordpressPostData(int id, String title, String description, String linkOfPicture, String content, String postLink, String baseUrl) {

        this.pId = id;
        this.pTitle = title;
        this.pDescription = description;
        this.pLinkOfPicture = linkOfPicture;
        this.pContent = content;
        this.pPostLink = postLink;
        this.pBaseUrl = baseUrl;
    }

    //------------------- get ----------------------------------

    public int getpId() {
        return pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public String getpDescription() {
        return pDescription;
    }

    public String getpLinkOfPicture() {
        return pLinkOfPicture;
    }

    public String getpContent() {
        return pContent;
    }

    public String getpPostLink() {
        return pPostLink;
    }

    public String getpBaseUrl(){return  pBaseUrl;}
    //--------------- set -------------------------------------

    public void setpId(int pId) {
        this.pId = pId;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public void setpLinkOfPicture(String pLinkOfPicture) {
        this.pLinkOfPicture = pLinkOfPicture;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent;
    }

    public void setpPostLink(String pPostLink) {
        this.pPostLink = pPostLink;
    }

    public void setpBaseUrl(String pBaseUrl) { this.pBaseUrl = pBaseUrl;}
}
