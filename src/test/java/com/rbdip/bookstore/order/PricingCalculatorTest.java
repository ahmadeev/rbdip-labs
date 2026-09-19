package com.rbdip.bookstore.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Эталонный тест покрывает только базовый путь расчёта цены (обычный
 * клиент, без купона, без оптовой скидки). Остальные ветки (VIP,
 * wholesale, купоны, отрицательный итог, скидка за объём, потолок
 * 1000) намеренно НЕ покрыты - это задание ЛР2: написать
 * характеризационные тесты на эти случаи перед рефакторингом класса.
 */
class PricingCalculatorTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void calculatesVipStatusOrder() {
        List<PricingCalculator.LineItem> items = List.of(
                new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)
        );
        String customerType = "vip";
        String couponCode = null;

        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);

        assertThat(total).isEqualByComparingTo("18.00");
    }

    @Test
    void calculatesWholesaleStatusOrder() {
        List<PricingCalculator.LineItem> items = List.of(
                new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)
        );
        String customerType = "wholesale";
        String couponCode = null;

        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);

        assertThat(total).isEqualByComparingTo("17.00");
    }

    @Test
    void calculatesSaveTenCouponOrder() {
        List<PricingCalculator.LineItem> items = List.of(
                new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)
        );
        String customerType = null;
        String couponCode = "SAVE10";

        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);

        assertThat(total).isEqualByComparingTo("10.00");
    }

    @Test
    void calculatesSaveTwentyPercentsCouponOrder() {
        List<PricingCalculator.LineItem> items = List.of(
                new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)
        );
        String customerType = null;
        String couponCode = "SAVE20PERCENT";

        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);

        assertThat(total).isEqualByComparingTo("16.00");
    }

    @Test
    void calculatesNegativeTotalOrder() {
        List<PricingCalculator.LineItem> items = List.of(
                new PricingCalculator.LineItem(new BigDecimal("-10.00"), 2)
        );
        String customerType = null;
        String couponCode = null;

        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void calculatesLargeTotalOrder() {
        List<PricingCalculator.LineItem> items = List.of(
                new PricingCalculator.LineItem(new BigDecimal("1000.00"), 2)
        );
        String customerType = null;
        String couponCode = null;

        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);

        assertThat(total).isEqualByComparingTo("1960.00");
    }
}
