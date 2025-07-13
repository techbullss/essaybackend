package essay.essay.repository;

import essay.essay.Models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order,Long> {
    Optional<Order> findByOrderId(String orderId);

    Page<Order> findByOrderStatus(String orderStatus, Pageable pageable);

    Page<Order> findByOrderStatusAndPaymentStatus(String orderStatus, String paymentStatus, Pageable pageable);

    Page<Order> findByPaymentStatus(String paymentStatus, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "(:orderId IS NULL OR o.orderId = :orderId) AND " +
            "(:email IS NULL OR o.email = :email) AND " +
            "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<Order> findByFilters(
            @Param("orderId") String orderId,
            @Param("email") String email,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);
}
