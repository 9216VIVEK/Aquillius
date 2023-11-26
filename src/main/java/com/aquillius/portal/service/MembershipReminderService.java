package com.aquillius.portal.service;



public interface MembershipReminderService {

    void sendMembershipRenewalReminder()throws Exception;

    void sendLastDayReminder()throws Exception;

}
