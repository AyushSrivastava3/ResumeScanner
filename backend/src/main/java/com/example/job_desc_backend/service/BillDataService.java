
package com.example.job_desc_backend.service;
import com.example.job_desc_backend.model.BillEntity;
import com.example.job_desc_backend.repository.BillMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class BillDataService {

    @Autowired
    private BillMongoRepository billRepository;

    public BillEntity saveBill(BillEntity bill) {

        return billRepository.save(bill);
    }


    public List<BillEntity> getAllBills() {
        return billRepository.findAll();
    }

    public void deleteBill(String id) {
        billRepository.deleteById(id);
    }

    public BillEntity getBillById(String id) {
        return billRepository.findById(id).orElse(null);
    }


    public List<BillEntity> getBillsForCurrentWeek() {
//        LocalDate now = LocalDate.now();
//        WeekFields weekFields = WeekFields.of(Locale.getDefault());
//        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
//        int currentYear = now.getYear();
//
//        // Retrieve all bills and filter for the current week
//        return billRepository.findAll().stream()
//                .filter(bill -> {
//                    LocalDate billDate = LocalDate.parse(bill.getDate()); // Adjust the date parsing based on your date format
//                    return billDate.get(weekFields.weekOfWeekBasedYear()) == currentWeek &&
//                            billDate.getYear() == currentYear;
//                })
//                .collect(Collectors.toList());
        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
        LocalDateTime now = LocalDateTime.now();
        return billRepository.findBillWithinDateRange(weekAgo, now);
    }


    public List<BillEntity> getBillsForToday() {
//        LocalDate today = LocalDate.now();
//
//        // Retrieve all bills and filter for today's date
//        return billRepository.findAll().stream()
//                .filter(bill -> {
//                    LocalDate billDate = LocalDate.parse(bill.getDate()); // Adjust the date parsing based on your date format
//                    return billDate.isEqual(today);
//                })
//                .collect(Collectors.toList());
//    }
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        return billRepository.findBillsAddedToday(todayStart);

    }
}
