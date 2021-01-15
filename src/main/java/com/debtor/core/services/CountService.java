package com.debtor.core.services;

import com.debtor.core.dtos.PaymentDto;
import com.debtor.core.models.Repayment;
import java.util.List;
import java.util.Set;

public interface CountService {

  List<Repayment> check(Set<PaymentDto> payments);

}
