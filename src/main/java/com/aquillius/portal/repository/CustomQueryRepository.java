package com.aquillius.portal.repository;

import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.PurchasableItem;
import com.aquillius.portal.model.PurchaseMembershipHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomQueryRepository {

//    private final JdbcTemplate jdbcTemplate;
//
//    public CustomQueryRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public List<PurchaseMembershipHistory> getPurchaseMembershipHistory(User user) {
//        long id = user.getId();
//        String sql = "SELECT m.type AS membership_type, p.purchase_date_time, p.price " +
//                "FROM Purchase p " +
//                "INNER JOIN Membership m ON p.membership_id = m.id " +
//                "WHERE p.user_id = ? " +
//                "AND p.purchasable_item = ? " +
//                "AND p.payment_status = 'SUCCESS' " +
//                "ORDER BY p.purchase_date_time DESC";
//
//        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
//            PurchaseMembershipHistory history = new PurchaseMembershipHistory();
//            history.setMembershipType(resultSet.getString("membership_type"));
//            LocalDateTime purchaseDateTime = resultSet.getTimestamp("purchase_date_time").toLocalDateTime();
//            history.setDate(purchaseDateTime.toLocalDate());
//            history.setPrice(resultSet.getLong("price"));
//            return history;
//        }, id, PurchasableItem.MEMBERSHIP.toString());
//    }
}
