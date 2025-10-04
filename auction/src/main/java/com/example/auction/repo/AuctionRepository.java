package com.example.auction.repo;

import com.example.auction.models.Auction;
import com.example.auction.models.Product;
import com.example.auction.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {

    Optional<Auction> findByProduct(Product product);


    List<Auction> findByActiveTrue();


    List<Auction> findByActiveFalse();


    List<Auction> findByActiveTrueAndEndTimeBefore(LocalDateTime endTime);


    @Query("SELECT a FROM Auction a WHERE a.product.seller = :seller")
    List<Auction> findBySeller(@Param("seller") User seller);


    @Query("SELECT DISTINCT a FROM Auction a JOIN a.bids b WHERE b.bidder = :user")
    List<Auction> findAuctionsByBidder(@Param("user") User user);


    @Query("SELECT a FROM Auction a WHERE a.active = true AND a.endTime < :currentTime")
    List<Auction> findExpiredActiveAuctions(@Param("currentTime") LocalDateTime currentTime);


    @Query("SELECT a FROM Auction a WHERE a.currentPrice >= :minPrice AND a.active = true")
    List<Auction> findActiveAuctionsWithMinPrice(@Param("minPrice") Double minPrice);


    @Query("SELECT a FROM Auction a WHERE a.currentPrice <= :maxPrice AND a.active = true")
    List<Auction> findActiveAuctionsWithMaxPrice(@Param("maxPrice") Double maxPrice);
}
