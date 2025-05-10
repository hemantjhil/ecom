package com.example.ecommerce.repository;

import com.example.ecommerce.model.DiscountCode;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
// repo to save, check by code , find all discount codes
public class DiscountCodeRepository {
    private final Map<String, DiscountCode> discountCodes = new HashMap<>();

    public DiscountCode save(DiscountCode discountCode) {
        discountCodes.put(discountCode.getCode(), discountCode);
        return discountCode;
    }

    public Optional<DiscountCode> findByCode(String code) {
        return Optional.ofNullable(discountCodes.get(code));
    }

    public List<DiscountCode> findAll() {
        return new ArrayList<>(discountCodes.values());
    }

}
