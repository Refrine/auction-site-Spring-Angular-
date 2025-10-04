package com.example.auction.repo;

import com.example.auction.models.Auction;
import com.example.auction.models.Bid;

import com.example.auction.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid,Long> {

    List<Bid> findByAuctionOrderByBidTimeDesc(Auction auction);


    List<Bid> findByBidderOrderByBidTimeDesc(User bidder);


    List<Bid> findByBidderAndAuctionOrderByBidTimeDesc(User bidder, Auction auction);


    @Query("SELECT b FROM Bid b WHERE b.auction = :auction ORDER BY b.bidTime DESC")
    List<Bid> findLastBidForAuction(@Param("auction") Auction auction);


    @Query("SELECT b FROM Bid b WHERE b.auction = :auction AND b.amount = (SELECT MAX(b2.amount) FROM Bid b2 WHERE b2.auction = :auction)")
    Optional<Bid> findHighestBidForAuction(@Param("auction") Auction auction);


    @Query("SELECT COUNT(b) FROM Bid b WHERE b.auction = :auction")
    Long countBidsByAuction(@Param("auction") Auction auction);


    List<Bid> findByAmountGreaterThanEqual(Double amount);


    @Query("SELECT b FROM Bid b WHERE b.bidTime BETWEEN :startDate AND :endDate ORDER BY b.bidTime DESC")
    List<Bid> findBidsBetweenDates(@Param("startDate") java.time.LocalDateTime startDate,
                                   @Param("endDate") java.time.LocalDateTime endDate);


    @Query("SELECT b FROM Bid b ORDER BY b.amount DESC LIMIT :limit")
    List<Bid> findTopBids(@Param("limit") int limit);
}
