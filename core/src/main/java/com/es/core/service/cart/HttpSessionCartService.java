package com.es.core.service.cart;

import com.es.core.exception.NotFoundException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.phone.Phone;
import com.es.core.service.phone.PhoneService;
import com.es.core.service.stock.StockService;
import com.es.core.util.ExceptionMessage;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class HttpSessionCartService implements CartService {

    private final StockService stockService;
    private final PhoneService phoneService;
    private final Cart cart;

    private final ReadWriteLock sessionLock = new ReentrantReadWriteLock();

    public HttpSessionCartService(
            PhoneService phoneService,
            StockService stockService
    ) {
        this.stockService = stockService;
        this.phoneService = phoneService;
        this.cart = new Cart();
    }

    @Override
    public Cart getCart() {

        sessionLock.readLock().lock();
        try {
            return new Cart(cart);
        } finally {
            sessionLock.readLock().unlock();
        }
    }

    @Override
    public Cart addPhone(Long phoneId, Integer quantity) {
        sessionLock.writeLock().lock();
        try {
            Phone phone = phoneService.findPhoneById(phoneId);
            stockService.reservePhone(phoneId, quantity);

            CartItem existingItem = getCartItemByPhoneId(phoneId);
            if (existingItem == null) {
                cart.getItems().add(new CartItem(phone, quantity));
            } else {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            }

            recalculateCart();
            return new Cart(cart);

        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    @Transactional
    @Override
    public void update(Map<Long, Integer> items) {
        sessionLock.writeLock().lock();
        try {
            Map<Long, CartItem> cartItemsById = mapCartItemsById();

            Map<Long, Integer> phoneToQuantity = calculateDeltas(items, cartItemsById);

            validateAndReserveIfNeeded(phoneToQuantity);

            updateCartQuantities(items, cartItemsById);

            recalculateCart();

        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    @Override
    public void remove(Long phoneId) {

        sessionLock.writeLock().lock();

        try {
            CartItem cartItem = getCartItemByPhoneId(phoneId);
            cart.getItems().remove(cartItem);

            Long phoneIdToDelete = cartItem.getPhone().getId();
            Integer quantity = cartItem.getQuantity();

            stockService.decreaseReservedQuantity(phoneIdToDelete, quantity);

            recalculateCart();

        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    private void recalculateCart() {
        long totalQuantity = cart.getItems().stream()
                .mapToLong(CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = cart.getItems().stream()
                .map(cartItem -> cartItem
                        .getPhone()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                ).reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(totalPrice);
        cart.setTotalQuantity(totalQuantity);
    }

    private CartItem getCartItemByPhoneId(Long phoneId) {
        return cart.getItems().stream()
                .filter(cartItem -> cartItem.getPhone().getId().equals(phoneId))
                .findFirst()
                .orElse(null);
    }

    private Map<Long, CartItem> mapCartItemsById() {
        return cart.getItems().stream()
                .collect(Collectors.toMap(
                        cartItem -> cartItem.getPhone().getId(),
                        cartItem -> cartItem
                ));
    }

    private Map<Long, Integer> calculateDeltas(
            Map<Long, Integer> items,
            Map<Long, CartItem> cartItemsById
    ) {
        Map<Long, Integer> phoneToQuantity = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long phoneId = entry.getKey();
            Integer newQuantity = entry.getValue();

            CartItem existingItem = requireCartItem(phoneId, cartItemsById);

            int delta = newQuantity - existingItem.getQuantity();
            if (delta != 0) {
                phoneToQuantity.put(phoneId, delta);
            }
        }
        return phoneToQuantity;
    }

    private void validateAndReserveIfNeeded(Map<Long, Integer> phoneToQuantity) {
        if (!phoneToQuantity.isEmpty()) {
            stockService.reserveAndValidateItems(phoneToQuantity);
        }
    }

    private void updateCartQuantities(
            Map<Long, Integer> items,
            Map<Long, CartItem> cartItemsById
    ) {
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long phoneId = entry.getKey();
            Integer newQuantity = entry.getValue();

            CartItem existingItem = requireCartItem(phoneId, cartItemsById);

            existingItem.setQuantity(newQuantity);
        }
    }

    private CartItem requireCartItem(Long phoneId, Map<Long, CartItem> cartItemsById) {
        CartItem cartItem = cartItemsById.get(phoneId);
        if (cartItem == null) {
            throw new NotFoundException(String.format(
                    ExceptionMessage.PHONE_NOT_FOUND_BY_ID_MESSAGE, phoneId));
        }
        return cartItem;
    }


}
