package org.superbiz.fetch.model;

public class YahooTradingPeriod {
    private String timezone;
    private long end;
    private long start;
    private int gmtoffset;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public int getGmtoffset() {
        return gmtoffset;
    }

    public void setGmtoffset(int gmtoffset) {
        this.gmtoffset = gmtoffset;
    }
}
