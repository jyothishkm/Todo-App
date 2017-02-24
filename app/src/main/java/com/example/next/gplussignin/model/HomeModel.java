package com.example.next.gplussignin.model;

/**
 * Created by next on 13/2/17.
 */
public class HomeModel
{
    private int sNo;
    private String data;
    private String userId;
    private String title;
    private String time;

    public int getsNo()
    {
        return sNo;
    }

    public void setsNo(int sNo)
    {
        this.sNo = sNo;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }
}
