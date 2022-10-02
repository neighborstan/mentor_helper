package com.ua.javarush.mentor.persist.repository;

import com.ua.javarush.mentor.persist.model.GroupToWeekend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupToWeekendRepository extends JpaRepository<GroupToWeekend, Integer> {
}
