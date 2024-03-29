package com.groom.cookiehouse.repository;

import com.groom.cookiehouse.domain.furniture.Furniture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FurnitureRepository extends JpaRepository<Furniture, Long> {
}
