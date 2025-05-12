package com.statistics.statisticsbackend.repositories;

import com.statistics.statisticsbackend.models.Role;
import com.statistics.statisticsbackend.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Searches for a user by email
     *
     * @param email The email address of a user
     * @return The user to which the given email belongs, if it exists
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Searches for a user by username
     *
     * @param fullName The username of a user
     * @return The user to which the given username belongs, if it exists
     */
    Optional<User> findUserByFullName(String fullName);


    /**
     * Searches for a user by upload code
     *
     * @param uploadCode The email address of a user
     * @return The user to which the given email belongs, if it exists
     */
    Optional<User> findUserByUploadCode(String uploadCode);

    List<User> findAllByRoleAndSchoolClass_Id(Role role, String schoolClassId);

    /**
     * checks if a user with the given email exists
     *
     * @param email The email address of a user
     * @return true if a user with the given email address already exists, false if a user with the given email address does not exist
     */
    boolean existsByEmail(String email);
}
