package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import com.rbdip.bookstore.customer.CustomerRepository;
import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * God-класс: валидация, расчёт цены, персистентность и "уведомление
 * клиента" смешаны в одном методе. Цель для рефакторинга по SRP в ЛР1.
 */
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PricingCalculator pricingCalculator;
    private final OrderEmailNotifier orderEmailNotifier;
    private final OrderRequestValidator orderRequestValidator;
    private final CustomerRepository customerRepository;

    public OrderService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PricingCalculator pricingCalculator,
            OrderEmailNotifier orderEmailNotifier,
            OrderRequestValidator orderRequestValidator,
            CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.pricingCalculator = pricingCalculator;
        this.orderEmailNotifier = orderEmailNotifier;
        this.orderRequestValidator = orderRequestValidator;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        orderRequestValidator.validateOrderRequest(request);

        List<Product> products = new ArrayList<>();
        List<PricingCalculator.LineItem> lineItems = new ArrayList<>();
        for (CreateOrderRequest.Item raw : request.items()) {
            Product product = productRepository.findById(raw.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product " + raw.productId() + " not found"));
            int quantity = raw.quantity() == null ? 1 : raw.quantity();
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive");
            }
            products.add(product);
            lineItems.add(new PricingCalculator.LineItem(product.getPrice(), quantity));
        }

        BigDecimal total = pricingCalculator.calculateOrderTotal(
                lineItems, request.customerType() == null ? "regular" : request.customerType(), request.couponCode());

        Customer customer = customerRepository.save(new Customer(
                request.customerFullName(),
                request.customerAddress(),
                request.customerPhone()));

        Order order = new Order(customer, "new");
        order = orderRepository.save(order);

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = lineItems.get(i).quantity();
            orderItemRepository.save(new OrderItem(order.getId(), product.getName(), product.getPrice(), quantity));
        }

        orderEmailNotifier.sendConfirmationEmail(request.customerFullName(), order.getId(), total);

        return order;
    }
}
