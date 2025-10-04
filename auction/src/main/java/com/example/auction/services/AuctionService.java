package com.example.auction.services;

import com.example.auction.dto.AuctionDto;
import com.example.auction.models.Auction;
import com.example.auction.models.Product;
import com.example.auction.models.User;
import com.example.auction.repo.AuctionRepository;
import com.example.auction.repo.ProductRepository;
import com.example.auction.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    private static final int DEFAULT_AUCTION_DURATION = 10;
    private static final int BID_TIME_EXTENSION = 5;

    public AuctionService(AuctionRepository auctionRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<AuctionDto> getAllAuctions() {
        return auctionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AuctionDto> getActiveAuctions() {
        return auctionRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AuctionDto getAuctionById(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        return convertToDto(auction);
    }

    public AuctionDto createAuction(AuctionDto auctionDto) {

        Product product = productRepository.findById(auctionDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + auctionDto.getProductId()));


        if (auctionRepository.findByProduct(product).isPresent()) {
            throw new RuntimeException("Auction already exists for this product");
        }


        Auction auction = new Auction();
        auction.setProduct(product);
        auction.setStartPrice(auctionDto.getStartPrice());
        auction.setCurrentPrice(auctionDto.getStartPrice());
        auction.setMinBidIncrement(auctionDto.getMinBidIncrement() != null ?
                auctionDto.getMinBidIncrement() : new BigDecimal("1.00"));
        auction.setStartTime(LocalDateTime.now());


        int duration = auctionDto.getAuctionDurationMinutes() != null ?
                auctionDto.getAuctionDurationMinutes() : DEFAULT_AUCTION_DURATION;
        auction.setEndTime(LocalDateTime.now().plusMinutes(duration));
        auction.setOriginalEndTime(auction.getEndTime());
        auction.setTimeExtensionMinutes(BID_TIME_EXTENSION);
        auction.setActive(true);

        Auction savedAuction = auctionRepository.save(auction);
        return convertToDto(savedAuction);
    }

    public AuctionDto updateAuction(Long id, AuctionDto auctionDto) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));


        if (auctionDto.getMinBidIncrement() != null) {
            auction.setMinBidIncrement(auctionDto.getMinBidIncrement());
        }

        Auction updatedAuction = auctionRepository.save(auction);
        return convertToDto(updatedAuction);
    }

    public void deleteAuction(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        auctionRepository.delete(auction);
    }

    public AuctionDto extendAuctionTime(Long auctionId, int additionalMinutes) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (!auction.getActive()) {
            throw new RuntimeException("Cannot extend inactive auction");
        }

        auction.setEndTime(auction.getEndTime().plusMinutes(additionalMinutes));
        Auction updatedAuction = auctionRepository.save(auction);
        return convertToDto(updatedAuction);
    }


    public void extendAuctionOnNewBid(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (auction.getActive()) {
            LocalDateTime newEndTime = LocalDateTime.now().plusMinutes(BID_TIME_EXTENSION);

            if (newEndTime.isAfter(auction.getEndTime())) {
                auction.setEndTime(newEndTime);
                auctionRepository.save(auction);
            }
        }
    }

    public List<AuctionDto> getExpiringAuctions() {
        LocalDateTime threshold = LocalDateTime.now().plusMinutes(5);
        return auctionRepository.findByActiveTrueAndEndTimeBefore(threshold).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AuctionDto> getAuctionsBySeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return auctionRepository.findBySeller(seller).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AuctionDto completeAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (!auction.getActive()) {
            throw new RuntimeException("Auction is already completed");
        }

        auction.setActive(false);
        auction.setEndTime(LocalDateTime.now());



        Auction completedAuction = auctionRepository.save(auction);
        return convertToDto(completedAuction);
    }

    public AuctionDto getAuctionByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Auction auction = auctionRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Auction not found for this product"));

        return convertToDto(auction);
    }


    private AuctionDto convertToDto(Auction auction) {
        AuctionDto dto = new AuctionDto();
        dto.setId(auction.getId());
        dto.setProductId(auction.getProduct().getId());
        dto.setProductName(auction.getProduct().getName());
        dto.setProductDescription(auction.getProduct().getDescription());
        dto.setProductImageUrl(auction.getProduct().getImageUrl());
        dto.setCategory(auction.getProduct().getCategory());

        dto.setSellerId(auction.getProduct().getSeller().getId());
        dto.setSellerUsername(auction.getProduct().getSeller().getUsername());

        dto.setStartPrice(auction.getStartPrice());
        dto.setCurrentPrice(auction.getCurrentPrice());
        dto.setMinBidIncrement(auction.getMinBidIncrement());

        dto.setStartTime(auction.getStartTime());
        dto.setEndTime(auction.getEndTime());
        dto.setOriginalEndTime(auction.getOriginalEndTime());
        dto.setTimeExtensionMinutes(auction.getTimeExtensionMinutes());

        dto.setActive(auction.getActive());


        if (auction.getActive() && auction.getEndTime() != null) {
            long secondsRemaining = java.time.Duration.between(LocalDateTime.now(), auction.getEndTime()).getSeconds();
            dto.setTimeRemainingSeconds(Math.max(0, secondsRemaining));
        } else {
            dto.setTimeRemainingSeconds(0L);
        }

        return dto;
    }


    public BigDecimal getCurrentPrice(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        return auction.getCurrentPrice();
    }


    public void updateCurrentPrice(Long auctionId, BigDecimal newPrice) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        auction.setCurrentPrice(newPrice);
        auctionRepository.save(auction);
    }
}