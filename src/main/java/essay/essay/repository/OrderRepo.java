package essay.essay.repository;

import essay.essay.Models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order,Long> {
    Optional<Order> findByOrderId(String orderId);

    Page<Order> findByOrderStatus(String orderStatus, Pageable pageable);

    Page<Order> findByOrderStatusAndPaymentStatus(String orderStatus, String paymentStatus, Pageable pageable);

    Page<Order> findByPaymentStatus(String paymentStatus, Pageable pageable);
}
