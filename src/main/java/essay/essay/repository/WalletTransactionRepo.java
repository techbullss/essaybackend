package essay.essay.repository;


import essay.essay.Models.UserModel;
import essay.essay.Models.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletTransactionRepo extends JpaRepository<WalletTransaction, Long> {


}