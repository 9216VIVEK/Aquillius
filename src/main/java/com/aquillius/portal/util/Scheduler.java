package com.aquillius.portal.util;

import com.aquillius.portal.enums.MembershipPlan;
import com.aquillius.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

public class Scheduler {

    @Autowired
    UserRepository userRepository;

//    @Scheduled(cron = "0 0 0 1 * ?") // At midnight on the 1st of every month
//    public void cancelMembership() {
//        userRepository
//                .findAll()
//                .stream()
//                .filter(user -> user.getMembership() != null
//                        && user.getMembership().getMembershipPlan() == MembershipPlan.MONTHLY
//                        && user.getMembership().getMembershipEndDate().isBefore(LocalDate.now()))
//                .forEach(user -> {
//                    user.setMembership(null);
//                    userRepository.save(user);
//                });
//    }
}
