package com.example.auction.controllers;

import com.example.auction.dto.AuctionDto;
import com.example.auction.repo.AuctionRepository;
import com.example.auction.services.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionService auctionService;


    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ResponseEntity<List<AuctionDto>> getAllAuction(){
        List<AuctionDto> auctions = auctionService.getAllAuctions();
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionDto> getAuctionById(@PathVariable Long id) {
        AuctionDto auction = auctionService.getAuctionById(id);
        return ResponseEntity.ok(auction);
    }


    @PostMapping
    public ResponseEntity<AuctionDto> createAuction(@RequestBody AuctionDto auctionDto) {
        AuctionDto createdAuction = auctionService.createAuction(auctionDto);
        return new ResponseEntity<>(createdAuction, HttpStatus.CREATED);
    }
}
