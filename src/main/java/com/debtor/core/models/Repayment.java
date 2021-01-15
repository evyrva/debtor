package com.debtor.core.models;

import com.debtor.core.dtos.UserDto;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Repayment {

  UserDto creditor;
  UserDto debtor;
  BigDecimal debt;

}
