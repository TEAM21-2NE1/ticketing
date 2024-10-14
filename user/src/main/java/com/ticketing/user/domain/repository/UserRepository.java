package com.ticketing.user.domain.repository;

import com.ticketing.user.application.dto.response.GetNicknameResponseDto;
import com.ticketing.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByNicknameAndIsDeletedFalse(String nickname);

    @Query("SELECT new com.ticketing.user.application.dto.response.GetNicknameResponseDto(u.id, u.nickname) FROM User u WHERE u.id IN :userIds AND u.isDeleted = false")
    List<GetNicknameResponseDto> findNicknamesByUserIdsAndIsDeletedFalse(List<Long> userIds);
}
