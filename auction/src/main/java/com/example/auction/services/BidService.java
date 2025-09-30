package com.example.auction.services;

import com.example.auction.dto.BidDto;
import com.example.auction.repo.BidRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {
    private final BidRepository bidRepo;

    public BidService(BidRepository bidRepo) {
        this.bidRepo = bidRepo;
    }


    public List<BidDto> getAllBids(){
        return bidRepo.findAll().stream().map(bid -> new BidDto(bid.getId(), bid.getBidder().getId(), bid.getAuction().getId(), bid.getAmount(), bid.getBidTime()))
                .collect(Collectors.toList());
    }
}
