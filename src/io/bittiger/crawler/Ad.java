package io.bittiger.crawler;

import java.io.Serializable;
import java.util.List;

public class Ad implements Serializable {

    public static final long serialVersionUID = 1L;

    private int adId;
    private int campaignId;          // √
    private int query_group_id;      // √
    private double bidPrice;         // √
    private String query;            // √

    private String category;
    private double price;
    private String title;
    private String detail_url;
    private String thumbnail;

    private List<String> keyWords;   //tokenize title, lowercase

    private double relevanceScore;
    private double pClick;
    private double rankScore;
    private double costPerClick;
    private int position;//1:top, 2:bottom
    private String description;
    private String brand;

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getQuery_group_id() {
        return query_group_id;
    }

    public void setQuery_group_id(int query_group_id) {
        this.query_group_id = query_group_id;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(List<String> keyWords) {
        this.keyWords = keyWords;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public double getpClick() {
        return pClick;
    }

    public void setpClick(double pClick) {
        this.pClick = pClick;
    }

    public double getRankScore() {
        return rankScore;
    }

    public void setRankScore(double rankScore) {
        this.rankScore = rankScore;
    }

    public double getCostPerClick() {
        return costPerClick;
    }

    public void setCostPerClick(double costPerClick) {
        this.costPerClick = costPerClick;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
