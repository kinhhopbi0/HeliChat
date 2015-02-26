package com.pdv.heli.component.chat;

import java.util.Calendar;

/**
 * Created by via on 2/3/15.
 */
public class BubbleInfo {
    private String textContent;
    private Calendar time;
    private boolean isYou;

    public BubbleInfo() {

    }

    public BubbleInfo(String textContent, boolean isYou) {
        this.textContent = textContent;
        this.isYou = isYou;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public boolean isYou() {
        return isYou;
    }

    public void setYou(boolean isYou) {
        this.isYou = isYou;
    }
}
