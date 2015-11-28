package com.lakecloud.foundation.dao;
import org.springframework.stereotype.Repository;
import com.lakecloud.core.base.GenericDAO;
import com.lakecloud.foundation.domain.Payment;
@Repository("paymentDAO")
public class PaymentDAO extends GenericDAO<Payment> {

}