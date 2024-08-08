package com.example.job_desc_backend.service;



import com.example.job_desc_backend.model.BillEntity;
import com.example.job_desc_backend.repository.BillMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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


}
