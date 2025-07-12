package essay.essay.repository;

import essay.essay.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel,Long> {
    Optional<UserModel> findByEmail(String email);
}
