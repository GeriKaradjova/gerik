package com.ram.repository;

import com.ram.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Long>
{
	OrderEntity findByOrderId(String orderId);
	OrderEntity findById(long id);

	@Query(value = "SELECT * FROM orders",
			countQuery = "SELECT COUNT(*) FROM orders",
			nativeQuery = true)
	Page<OrderEntity> findAllOrders(Pageable pageable);
	
	@Query(value = "SELECT * FROM orders WHERE user_id=?1", nativeQuery = true)
	List<OrderEntity> findOrderByUserId(long userId);

	@Transactional
	@Modifying
	@Query(value = "Update orders set price=:price WHERE user_id=:userId", nativeQuery = true)
	void updateOrderPrice(@Param("price") float price,@Param("userId") long userId);
	
	@Query("SELECT order from OrderEntity order where order.orderId=:orderId")
	OrderEntity findOrderEntityByOrderId(@Param("orderId") String orderId);
	
}
