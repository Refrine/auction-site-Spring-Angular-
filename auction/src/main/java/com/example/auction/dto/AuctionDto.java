package com.example.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private String category;

    private Long sellerId;
    private String sellerUsername;

    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal minBidIncrement;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime originalEndTime;
    private Integer timeExtensionMinutes;

    private Boolean active;
    private Long currentWinningBidId;
    private String currentWinnerUsername;

    private Integer totalBids;
    private Long timeRemainingSeconds;


    public AuctionDto() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }

    public BigDecimal getStartPrice() { return startPrice; }
    public void setStartPrice(BigDecimal startPrice) { this.startPrice = startPrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getMinBidIncrement() { return minBidIncrement; }
    public void setMinBidIncrement(BigDecimal minBidIncrement) { this.minBidIncrement = minBidIncrement; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDateTime getOriginalEndTime() { return originalEndTime; }
    public void setOriginalEndTime(LocalDateTime originalEndTime) { this.originalEndTime = originalEndTime; }

    public Integer getTimeExtensionMinutes() { return timeExtensionMinutes; }
    public void setTimeExtensionMinutes(Integer timeExtensionMinutes) { this.timeExtensionMinutes = timeExtensionMinutes; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Long getCurrentWinningBidId() { return currentWinningBidId; }
    public void setCurrentWinningBidId(Long currentWinningBidId) { this.currentWinningBidId = currentWinningBidId; }

    public String getCurrentWinnerUsername() { return currentWinnerUsername; }
    public void setCurrentWinnerUsername(String currentWinnerUsername) { this.currentWinnerUsername = currentWinnerUsername; }

    public Integer getTotalBids() { return totalBids; }
    public void setTotalBids(Integer totalBids) { this.totalBids = totalBids; }

    public Long getTimeRemainingSeconds() { return timeRemainingSeconds; }
    public void setTimeRemainingSeconds(Long timeRemainingSeconds) { this.timeRemainingSeconds = timeRemainingSeconds; }
}