package org.superbiz.fetch.model;

public class YahooCurrentTradingPeriod {
    /*": {
        "pre": {
            "timezone": "EST",
                    "end": 1518791400,
                    "start": 1518771600,
                    "gmtoffset": -18000
        },
        "regular": {
            "timezone": "EST",
                    "end": 1518814800,
                    "start": 1518791400,
                    "gmtoffset": -18000
        },
        "post": {
            "timezone": "EST",
                    "end": 1518829200,
                    "start": 1518814800,
                    "gmtoffset": -18000
        }
    },*/

    private YahooTradingPeriod pre;
    private YahooTradingPeriod regular;
    private YahooTradingPeriod post;

    public YahooTradingPeriod getPre() {
        return pre;
    }

    public void setPre(YahooTradingPeriod pre) {
        this.pre = pre;
    }

    public YahooTradingPeriod getRegular() {
        return regular;
    }

    public void setRegular(YahooTradingPeriod regular) {
        this.regular = regular;
    }

    public YahooTradingPeriod getPost() {
        return post;
    }

    public void setPost(YahooTradingPeriod post) {
        this.post = post;
    }
}
