package com.innocito.standupai.repository;

import com.innocito.standupai.dto.StandUpResponseDTO;
import com.innocito.standupai.entity.StandupUpdate;
import com.innocito.standupai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StandupUpdateRepository extends JpaRepository<StandupUpdate, Long> {
    List<StandupUpdate> findByDate(LocalDate date);
    List<StandupUpdate> findByUserIdAndDate(Long userId, LocalDate date);

    @Query("SELECT s FROM StandupUpdate s WHERE s.date = :date AND s.blockers IS NOT NULL AND s.blockers != ''")
    List<StandupUpdate> findBlockersByDate(LocalDate date);

    @Query("SELECT DISTINCT s.blockers FROM StandupUpdate s WHERE s.blockers IS NOT NULL AND s.blockers != ''")
    List<String> findAllUniqueBlockers();
    List<StandupUpdate> findByUserId(Long userId);
    //List<StandupUpdate> findByDate(LocalDate date);

    int countByUserId(Long userId);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);
    int countByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT s.user.id FROM StandupUpdate s")
    List<Long> findDistinctUserIds();
    List<StandupUpdate> findByUser(User user);
    List<StandupUpdate> findByUserIn(List<User> users);
    //List<StandupUpdate> findByUserId(Long userId);
}