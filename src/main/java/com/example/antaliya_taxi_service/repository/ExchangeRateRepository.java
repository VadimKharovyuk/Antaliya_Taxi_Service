package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByFromCurrencyAndToCurrency(Currency fromCurrency, Currency toCurrency);
}