package essay.essay.services;

import essay.essay.Models.UserModel;
import essay.essay.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    public UserModel saveUser(UserModel user){
        return userRepo.save(user);
    }
    public Optional<UserModel> findUser(String email){
        return userRepo.findByEmail(email);
    }
}
