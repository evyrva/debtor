package com.debtor.core.dtos;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDto {

  UserDto creditor;
  List<UserDto> debtors;
  BigDecimal bill;

}
