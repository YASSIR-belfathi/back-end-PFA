package rag_system.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rag_system.demo.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT s FROM User s WHERE userName = :name")
    Optional<User> findByUserName(@Param("name") String name);
}
