package com.example.auction.services;

import com.example.auction.dto.BidDto;
import com.example.auction.models.Auction;
import com.example.auction.models.Bid;
import com.example.auction.models.User;
import com.example.auction.repo.AuctionRepository;
import com.example.auction.repo.BidRepository;
import com.example.auction.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BidService {
    private final BidRepository bidRepo;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final AuctionService auctionService;

    public BidService(BidRepository bidRepo,
                      AuctionRepository auctionRepository,
                      UserRepository userRepository,
                      AuctionService auctionService) {
        this.bidRepo = bidRepo;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.auctionService = auctionService;
    }

    public List<BidDto> getAllBids() {
        return bidRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BidDto getBidById(Long id) {
        Bid bid = bidRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bid not found with id: " + id));
        return convertToDto(bid);
    }

    public BidDto createBid(BidDto bidDto) {

        User bidder = userRepository.findById(bidDto.getBidderId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + bidDto.getBidderId()));

        Auction auction = auctionRepository.findById(bidDto.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + bidDto.getAuctionId()));


        if (!auction.getActive()) {
            throw new RuntimeException("Cannot place bid on inactive auction");
        }


        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            throw new RuntimeException("Auction has ended");
        }


        BigDecimal minBidAmount = auction.getCurrentPrice().add(auction.getMinBidIncrement());
        if (bidDto.getAmount().compareTo(minBidAmount) < 0) {
            throw new RuntimeException("Bid amount must be at least: " + minBidAmount);
        }


        if (auction.getProduct().getSeller().getId().equals(bidDto.getBidderId())) {
            throw new RuntimeException("Seller cannot bid on their own auction");
        }

        // Создаем новую ставку
        Bid bid = new Bid();
        bid.setBidder(bidder);
        bid.setAuction(auction);
        bid.setAmount(bidDto.getAmount());
        bid.setBidTime(LocalDateTime.now());

        Bid savedBid = bidRepo.save(bid);


        auction.setCurrentPrice(bidDto.getAmount());
        auctionRepository.save(auction);


        auctionService.extendAuctionOnNewBid(auction.getId());

        return convertToDto(savedBid);
    }

    public List<BidDto> getBidsByAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + auctionId));

        return bidRepo.findByAuctionOrderByBidTimeDesc(auction).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BidDto> getBidsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return bidRepo.findByBidderOrderByBidTimeDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BidDto> getUserBidsOnAuction(Long userId, Long auctionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + auctionId));

        return bidRepo.findByBidderAndAuctionOrderByBidTimeDesc(user, auction).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BidDto getHighestBidForAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + auctionId));

        Optional<Bid> highestBid = bidRepo.findHighestBidForAuction(auction);

        return highestBid.map(this::convertToDto).orElse(null);
    }

    public Long getBidCountForAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + auctionId));

        return bidRepo.countBidsByAuction(auction);
    }

    public boolean isUserLeadingAuction(Long auctionId, Long userId) {
        BidDto highestBid = getHighestBidForAuction(auctionId);
        return highestBid != null && highestBid.getBidderId().equals(userId);
    }

    public BigDecimal getMinNextBidAmount(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + auctionId));

        return auction.getCurrentPrice().add(auction.getMinBidIncrement());
    }

    public List<BidDto> getRecentBids(int limit) {
        return bidRepo.findTopBids(limit).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BidDto> getBidsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return bidRepo.findBidsBetweenDates(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteBid(Long bidId) {
        Bid bid = bidRepo.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found with id: " + bidId));


        Auction auction = bid.getAuction();
        if (!auction.getActive()) {
            throw new RuntimeException("Cannot delete bid from completed auction");
        }

        bidRepo.delete(bid);


        if (auction.getCurrentPrice().equals(bid.getAmount())) {
            Optional<Bid> newHighestBid = bidRepo.findHighestBidForAuction(auction);
            if (newHighestBid.isPresent()) {
                auction.setCurrentPrice(newHighestBid.get().getAmount());
            } else {
                auction.setCurrentPrice(auction.getStartPrice());
            }
            auctionRepository.save(auction);
        }
    }

    public BidDto getLastBidForAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + auctionId));

        List<Bid> lastBids = bidRepo.findLastBidForAuction(auction);
        return lastBids.isEmpty() ? null : convertToDto(lastBids.get(0));
    }


    private BidDto convertToDto(Bid bid) {
        BidDto dto = new BidDto();
        dto.setId(bid.getId());
        dto.setBidderId(bid.getBidder().getId());
        dto.setBidderUsername(bid.getBidder().getUsername());
        dto.setAuctionId(bid.getAuction().getId());
        dto.setAmount(bid.getAmount());
        dto.setBidTime(bid.getBidTime());


        dto.setProductName(bid.getAuction().getProduct().getName());
        dto.setCurrentPrice(bid.getAuction().getCurrentPrice());
        dto.setAuctionEndTime(bid.getAuction().getEndTime());
        dto.setAuctionActive(bid.getAuction().getActive());


        Optional<Bid> highestBid = bidRepo.findHighestBidForAuction(bid.getAuction());
        dto.setIsWinning(highestBid.isPresent() && highestBid.get().getId().equals(bid.getId()));

        return dto;
    }


    public BidStatistics getBidStatistics(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + auctionId));

        Long totalBids = bidRepo.countBidsByAuction(auction);
        Optional<Bid> highestBid = bidRepo.findHighestBidForAuction(auction);
        List<Bid> allBids = bidRepo.findByAuctionOrderByBidTimeDesc(auction);

        BidStatistics stats = new BidStatistics();
        stats.setTotalBids(totalBids);
        stats.setHighestBid(highestBid.map(Bid::getAmount).orElse(BigDecimal.ZERO));
        stats.setUniqueBidders(allBids.stream()
                .map(bid -> bid.getBidder().getId())
                .distinct()
                .count());

        return stats;
    }


    public static class BidStatistics {
        private Long totalBids;
        private BigDecimal highestBid;
        private Long uniqueBidders;


        public Long getTotalBids() { return totalBids; }
        public void setTotalBids(Long totalBids) { this.totalBids = totalBids; }

        public BigDecimal getHighestBid() { return highestBid; }
        public void setHighestBid(BigDecimal highestBid) { this.highestBid = highestBid; }

        public Long getUniqueBidders() { return uniqueBidders; }
        public void setUniqueBidders(Long uniqueBidders) { this.uniqueBidders = uniqueBidders; }
    }
}