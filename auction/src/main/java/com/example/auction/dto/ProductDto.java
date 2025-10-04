package com.example.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private BigDecimal startPrice;
    private String imageUrl;
    private LocalDateTime createdAt;


    private Long sellerId;
    private String sellerUsername;


    private Long auctionId;
    private Boolean auctionActive;
    private BigDecimal currentPrice;
    private LocalDateTime auctionEndTime;
    private Long timeRemainingSeconds;


    private Integer auctionDurationMinutes;


    public ProductDto() {}

    public ProductDto(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getStartPrice() { return startPrice; }
    public void setStartPrice(BigDecimal startPrice) { this.startPrice = startPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

    public Boolean getAuctionActive() { return auctionActive; }
    public void setAuctionActive(Boolean auctionActive) { this.auctionActive = auctionActive; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public LocalDateTime getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) { this.auctionEndTime = auctionEndTime; }

    public Long getTimeRemainingSeconds() { return timeRemainingSeconds; }
    public void setTimeRemainingSeconds(Long timeRemainingSeconds) { this.timeRemainingSeconds = timeRemainingSeconds; }

    public Integer getAuctionDurationMinutes() { return auctionDurationMinutes; }
    public void setAuctionDurationMinutes(Integer auctionDurationMinutes) { this.auctionDurationMinutes = auctionDurationMinutes; }

}
