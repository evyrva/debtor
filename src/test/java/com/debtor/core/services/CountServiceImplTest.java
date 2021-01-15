package com.debtor.core.services;

import com.debtor.core.dtos.PaymentDto;
import com.debtor.core.dtos.UserDto;
import com.debtor.core.models.Repayment;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CountServiceImplTest {

  CountServiceImpl countService;

  @BeforeEach
  void setUp() {
    countService = new CountServiceImpl();
  }

  @Test
  void test() {
    final CountService countService = new CountServiceImpl();
    final UserDto egor = UserDto.builder().name("Egor").build();
    final UserDto taras = UserDto.builder().name("Taras").build();
    final UserDto yura = UserDto.builder().name("Yura").build();
    final UserDto babay = UserDto.builder().name("Babay").build();
    final UserDto kolt = UserDto.builder().name("Kolt").build();
    final UserDto bagriy = UserDto.builder().name("Bagriy").build();
    final List<Repayment> repayments = countService.check(Set.of(
        PaymentDto.builder()
            .creditor(egor)
            .bill(BigDecimal.valueOf(10138))
            .debtors(List.of(egor, taras, yura, babay, kolt, bagriy))
            .build()
        ,
        PaymentDto.builder()
            .creditor(taras)
            .bill(BigDecimal.valueOf(10200))
            .debtors(List.of(egor, taras, yura, babay, kolt, bagriy))
            .build()
        ,
        PaymentDto.builder()
            .creditor(kolt)
            .bill(BigDecimal.valueOf(200))
            .debtors(List.of(egor, taras, yura, babay, kolt, bagriy))
            .build()
        ,
        PaymentDto.builder()
            .creditor(babay)
            .bill(BigDecimal.valueOf(1200))
            .debtors(List.of(egor, taras, yura, babay, kolt, bagriy))
            .build()
    ));

    repayments.stream()
        .map(
            repayment -> repayment.getDebtor().getName() + " -> " + repayment.getCreditor().getName() + ": " + repayment
                .getDebt())
        .forEach(System.out::println);

  }

}