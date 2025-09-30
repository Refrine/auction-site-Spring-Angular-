package com.example.auction.repo;

import com.example.auction.models.Bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid,Long> {
}
