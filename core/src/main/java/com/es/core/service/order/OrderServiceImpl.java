package com.es.core.service.order;

import com.es.core.dao.color.ColorDao;
import com.es.core.dao.order.OrderDao;
import com.es.core.dao.orderItem.OrderItemDao;
import com.es.core.dto.order.UserPersonalInfoDto;
import com.es.core.exception.CartChangedException;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.NotValidDataException;
import com.es.core.exception.OrderItemException;
import com.es.core.model.cart.Cart;
import com.es.core.model.color.Color;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.service.cart.CartService;
import com.es.core.service.stock.StockService;
import com.es.core.util.ExceptionMessage;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private StockService stockService;

    @Resource
    private CartService cartService;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private ColorDao colorDao;

    @Value("${delivery.price}")
    BigDecimal deliveryPrice;

    @Override
    public Order createOrder() {
        Cart cart = cartService.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            return new Order();
        }

        List<OrderItem> orderItems = getOrderItemsByCart(cart);

        Order order = new Order();
        order.setOrderItems(orderItems);
        order.setSubtotal(cart.getTotalPrice());
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(cart.getTotalPrice().add(deliveryPrice));
        order.setStatus(OrderStatus.NEW);

        return order;
    }

    @Override
    @Transactional
    public void placeOrder(Order order, UserPersonalInfoDto userPersonalInfoDto) {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new NotValidDataException(ExceptionMessage.ORDER_EMPTY_MESSAGE);
        }

        validateCartConsistency(order);
        setOrderPersonalInfo(order, userPersonalInfoDto);

        order.setSecureId(UUID.randomUUID().toString());

        orderDao.saveOrder(order);
        saveAndValidateOrderItems(order);

        Map<Long, Integer> phoneIdToQuantity = order.getOrderItems().stream()
                .collect(Collectors.toMap(orderItem ->
                                orderItem.getPhone().getId(),
                        OrderItem::getQuantity)
                );

        stockService.decreaseStock(phoneIdToQuantity);

        cartService.cleanupSessionAndReservedItems();
    }

    @Override
    public Order getOrderBySecureId(String secureId) {
        Order order = orderDao.findOrderBySecureId(secureId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessage.ORDER_BY_SECURE_ID_NOT_FOUND, secureId)
                ));

        List<Long> phoneId = order.getOrderItems().stream()
                .map(orderItem ->
                        orderItem.getPhone().getId()
                ).toList();

        if (!phoneId.isEmpty()) {
            Map<Long, Set<Color>> colorsMap = colorDao.findColorsForPhoneIds(phoneId);

            order.getOrderItems().forEach(orderItem -> {
                orderItem.getPhone().setColors(colorsMap.getOrDefault(orderItem.getPhone().getId(), Set.of()));
            });
        }

        return order;
    }

    @Override
    public boolean isOrderConsistency(Order draft) {
        if (draft == null || draft.getOrderItems() == null) {
            return false;
        }

        Order newDraft = createOrder();
        return newDraft != null &&
                newDraft.getOrderItems() != null &&
                Objects.equals(newDraft.getOrderItems(), draft.getOrderItems());
    }

    private void validateCartConsistency(Order order) {
        Cart cart = cartService.getCart();

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CartChangedException(ExceptionMessage.CART_CHANGED_MESSAGE);
        }

        List<OrderItem> cartItems = getOrderItemsByCart(cart);

        if (!order.getOrderItems().equals(cartItems)) {
            throw new CartChangedException(ExceptionMessage.CART_CHANGED_MESSAGE);
        }
    }

    private void setOrderPersonalInfo(Order order, UserPersonalInfoDto userPersonalInfoDto) {
        order.setFirstName(userPersonalInfoDto.getFirstName());
        order.setLastName(userPersonalInfoDto.getLastName());
        order.setDeliveryAddress(userPersonalInfoDto.getDeliveryAddress());
        order.setContactPhoneNo(userPersonalInfoDto.getContactPhoneNo());
        order.setAdditionalInformation(userPersonalInfoDto.getAdditionalInformation());
    }

    private void saveAndValidateOrderItems(Order order) {
        int[] updatedRow = orderItemDao.saveOrderItemsByOrderId(order.getId(), order.getOrderItems());

        for (int row : updatedRow) {
            if (row == 0) {
                throw new OrderItemException(ExceptionMessage.ORDER_ITEM_INSERT_EXCEPTION);
            }
        }
    }

    private List<OrderItem> getOrderItemsByCart(Cart cart) {
        return cart.getItems().stream()
                .map(ci -> new OrderItem(ci.getPhone(), ci.getQuantity()))
                .toList();
    }
    
}
