package com.amazonaws.lambda.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class EoDOHLC {

    private Date date;
    private String symbol;
    private String exchange;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double adjusted;
    private Long volume;

    public EoDOHLC(){}

    @JsonCreator
    public EoDOHLC(
            @JsonProperty("date")
                    Date date,
            @JsonProperty("code") String symbol,
            @JsonProperty("exchange_short_name") String exchange,
            @JsonProperty("open") Double open,
            @JsonProperty("high") Double high,
            @JsonProperty("low") Double low,
            @JsonProperty("close") Double close,
            @JsonProperty("adjusted_close") Double adjusted,
            @JsonProperty("volume") Long volume) {
        this.date = date;
        this.symbol = symbol;
        this.exchange = exchange;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.adjusted = adjusted;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "EoDOHLC{" +
                "date=" + date +
                ", symbol='" + symbol + '\'' +
                ", exchange='" + exchange + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", adjusted=" + adjusted +
                ", volume=" + volume +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getAdjusted() {
        return adjusted;
    }

    public void setAdjusted(Double adjusted) {
        this.adjusted = adjusted;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
