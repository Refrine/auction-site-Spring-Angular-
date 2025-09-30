package com.example.auction.services;

import com.example.auction.dto.AuctionDto;
import com.example.auction.models.Auction;
import com.example.auction.repo.AuctionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }


    public List<AuctionDto> getAllAuctions() {
        return auctionRepository.findAll().stream()
                .map(a -> new AuctionDto(a.getId(), a.getTitle()))
                .collect(Collectors.toList());
    }


    public AuctionDto getAuctionById(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        return new AuctionDto(auction.getId(), auction.getTitle());
    }
}