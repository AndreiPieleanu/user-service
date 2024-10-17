package s6.userservice.datalayer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import s6.userservice.datalayer.entities.User;

import java.util.Optional;

@Repository
public interface IUserDal extends JpaRepository<User, Integer> {
    @Transactional
    @Modifying
    @Query("update User us set us.email=:#{#userEntity.email}, " +
            "us.firstName=:#{#userEntity.firstName}, " +
            "us.lastName=:#{#userEntity.lastName}, " +
            "us.password=:#{#userEntity.password} where us.id=:#{#userEntity.id}")
    void updateUser(User userEntity);
    @Query("select us from User us where us.email=:#{#username}")
    Optional<User> findByEmail(String username);
    @Transactional
    @Modifying
    @Query("update User us set us.role=:#{#user.role} " +
            "where us.id=:#{#user.id}")
    void updateUserRole(User user);
}
