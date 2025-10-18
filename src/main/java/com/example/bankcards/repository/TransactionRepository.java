package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findBySourceCardUserOrDestinationCardUser(User sourceUser, User destinationUser, Pageable pageable);

    boolean existsBySourceCard(Card card);

    boolean existsByDestinationCard(Card card);

}