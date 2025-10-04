package com.example.auction.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidDto {

    private Long id;
    private Long bidderId;
    private String bidderUsername;
    private Long auctionId;
    private BigDecimal amount;
    private LocalDateTime bidTime;
    private Boolean isWinning;


    private String productName;
    private BigDecimal currentPrice;
    private LocalDateTime auctionEndTime;
    private Boolean auctionActive;


    public BidDto() {}

    public BidDto(Long id, Long bidderId, Long auctionId, BigDecimal amount, LocalDateTime bidTime) {
        this.id = id;
        this.bidderId = bidderId;
        this.auctionId = auctionId;
        this.amount = amount;
        this.bidTime = bidTime;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBidderId() { return bidderId; }
    public void setBidderId(Long bidderId) { this.bidderId = bidderId; }

    public String getBidderUsername() { return bidderUsername; }
    public void setBidderUsername(String bidderUsername) { this.bidderUsername = bidderUsername; }

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }

    public Boolean getIsWinning() { return isWinning; }
    public void setIsWinning(Boolean winning) { isWinning = winning; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public LocalDateTime getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) { this.auctionEndTime = auctionEndTime; }

    public Boolean getAuctionActive() { return auctionActive; }
    public void setAuctionActive(Boolean auctionActive) { this.auctionActive = auctionActive; }
}
