package com.aquillius.portal.service.serviceImpl;

import com.aquillius.portal.entity.Membership;
import com.aquillius.portal.entity.Notification;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.repository.NotificationRepository;
import com.aquillius.portal.repository.UserRepository;
import com.aquillius.portal.service.EmailService;
import com.aquillius.portal.service.MembershipReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipReminderServiceImpl implements MembershipReminderService {

    private final UserRepository userRepository;

    private final NotificationRepository notificationRepository;

    private final EmailService emailService;

    @Scheduled(cron = "0 0 0 25 * ?") // At midnight on the 25th of every month
    public void sendMembershipRenewalReminder() {
        String subject = "Membership Renewal Reminder";
        String content = "Your due date for renewal of membership of next month is coming, do renew it on time";

        userRepository
                .findAll()
                .stream()
                .filter(user -> user.getMemberships() != null && !user.getMemberships().isEmpty())
                .forEach(user -> {
                    List<Membership> memberships = user.getMemberships();
                    boolean isDueDateOn5thOfNextMonth=false;
                    for(Membership m:memberships){
                        if(isDueDateOn5thOfNextMonth(m)){
                            isDueDateOn5thOfNextMonth=true;
                            break;
                        }
                    }
                    if(isDueDateOn5thOfNextMonth) {
                        Notification notification = new Notification(content, LocalDateTime.now());
                        notificationRepository.save(notification);
                        List<Notification> list = user.getNotifications();
                        if (list == null) list = new ArrayList<>();
                        list.add(notification);
                        user.setNotifications(list);
                        userRepository.save(user);
                        emailService.sendEmail(user.getEmail(), subject, content);
                    }
                });
    }

    private boolean isDueDateOn5thOfNextMonth(Membership membership) {
        LocalDate dueDate = membership.getDueDate();
        LocalDate currentDate = LocalDate.now();
        LocalDate nextMonth5th = currentDate.plusMonths(1).withDayOfMonth(5);
        return dueDate.isEqual(nextMonth5th);
    }

    @Scheduled(cron = "0 0 0 4 * ?") // At midnight on the 4th of every month
    public void sendLastDayReminder() {
        String subject = "Membership Last Day Reminder";
        String content = "Tomorrow is last date for membership renewal after which 5% fine will be charged, " +
                "please renew the membership on time";

        userRepository
                .findAll()
                .stream()
                .filter(user -> user.getMemberships() != null && !user.getMemberships().isEmpty())
                .forEach(user -> {
                    List<Membership> memberships = user.getMemberships();
                    boolean isDueDateOn5thOfCurrentMonth=false;
                    for(Membership m:memberships){
                        if(isDueDateOn5thOfCurrentMonth(m)){
                            isDueDateOn5thOfCurrentMonth=true;
                            break;
                        }
                    }
                    if(isDueDateOn5thOfCurrentMonth) {
                        Notification notification = new Notification(content, LocalDateTime.now());
                        notificationRepository.save(notification);
                        List<Notification> list = user.getNotifications();
                        if (list == null) list = new ArrayList<>();
                        list.add(notification);
                        user.setNotifications(list);
                        userRepository.save(user);
                        emailService.sendEmail(user.getEmail(), subject, content);
                    }
                });
    }

    private boolean isDueDateOn5thOfCurrentMonth(Membership m) {
        LocalDate dueDate = m.getDueDate();
        LocalDate currentDate = LocalDate.now();
        LocalDate currentMonth5th = currentDate.withDayOfMonth(5);
        return dueDate.isEqual(currentMonth5th);
    }
}
