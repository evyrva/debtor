package com.debtor.core.services;

import com.debtor.core.dtos.PaymentDto;
import com.debtor.core.dtos.UserDto;
import com.debtor.core.models.Repayment;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class CountServiceImpl implements CountService {

  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  private static final int scale = 2;

  @Override
  public List<Repayment> check(Set<PaymentDto> payments) {
    final Map<UserDto, BigDecimal> absoluteUsersCurrency = calculateAbsoluteUsersCurrency(payments);
    final Set<Set<Entry<UserDto, BigDecimal>>> powerSet = Sets.powerSet(absoluteUsersCurrency.entrySet());
    absoluteUsersCurrency.entrySet()
        .forEach(entry -> System.out.println(entry.getKey().getName() + ": " + entry.getValue().toEngineeringString()));

    System.out.println("sum: "
        + absoluteUsersCurrency.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).toEngineeringString());

    return powerSet.stream()
        .filter(set -> set.size() > 1)
        .sorted(Comparator.comparingInt(Set::size))
        .flatMap(set -> calculateRepaymentsFromSet(set).stream())
        .collect(Collectors.toList());
  }

  private List<Repayment> calculateRepaymentsFromSet(Set<Entry<UserDto, BigDecimal>> set) {
    List<Repayment> results = new ArrayList<>();
    if (set.stream().map(Entry::getValue).reduce(BigDecimal.ZERO, BigDecimal::add)
        .equals(BigDecimal.ZERO.setScale(scale, ROUNDING_MODE))) {

      final List<Entry<UserDto, BigDecimal>> creditors = set.stream()
          .filter(entry -> entry.getValue().signum() > 0)
          .collect(Collectors.toList());
      final List<Entry<UserDto, BigDecimal>> debtors = set.stream()
          .filter(entry -> entry.getValue().signum() < 0)
          .collect(Collectors.toList());

      for (Entry<UserDto, BigDecimal> creditor : creditors) {
        while (creditor.getValue().signum() > 0) {
          debtors.stream().filter(debtor -> debtor.getValue().signum() < 0).findAny()
              .ifPresent(debtor -> {
                final BigDecimal debt = creditor.getValue().min(debtor.getValue().abs());

                results.add(Repayment.builder()
                    .creditor(creditor.getKey())
                    .debtor(debtor.getKey())
                    .debt(debt)
                    .build());
                creditor.setValue(creditor.getValue().subtract(debt));
                debtor.setValue(debtor.getValue().add(debt));
              });
        }
      }
    }
    return results;
  }

  private Map<UserDto, BigDecimal> calculateAbsoluteUsersCurrency(Set<PaymentDto> payments) {
    Map<UserDto, BigDecimal> absoluteUsersCurrencyMap = new HashMap<>();

    payments.forEach(p -> {
      absoluteUsersCurrencyMap.merge(p.getCreditor(), p.getBill(),
          (o, n) -> o.add(n));
      p.getDebtors().forEach(d -> {
        final BigDecimal billPerOne = p.getBill()
            .divide(BigDecimal.valueOf(p.getDebtors().size()), scale, RoundingMode.HALF_UP);
        absoluteUsersCurrencyMap.merge(d, billPerOne.negate(), (oldValue, newValue) -> oldValue.subtract(billPerOne));
      });
    });

    round(absoluteUsersCurrencyMap);

    return absoluteUsersCurrencyMap;
  }

  private void round(Map<UserDto, BigDecimal> currencyMap) {
    final BigDecimal remains = currencyMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    currencyMap.entrySet().stream()
        .filter(entry -> entry.getValue().signum() > 0)
        .findAny()
        .ifPresent(entry -> entry.setValue(entry.getValue().subtract(remains)));
  }

}
