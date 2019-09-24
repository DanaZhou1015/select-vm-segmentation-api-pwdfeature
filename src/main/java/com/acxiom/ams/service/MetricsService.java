package com.acxiom.ams.service;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 4:13 PM 10/9/2018
 */
public interface MetricsService {
   void listMetricsEveryFifteenDaysForTV(String startDate, String endDate);

   void listMetricsEveryFifteenDaysFor2P(String startDate, String endDate);
}
