package com.es.core.service.cart;

import com.es.core.dto.cart.MassAddToCart;
import com.es.core.exception.NotFoundException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.phone.Phone;
import com.es.core.service.phone.PhoneService;
import com.es.core.service.stock.StockService;
import com.es.core.util.ExceptionMessage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        return new Cart(cart);
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

            if (cartItem == null) {
                throw new NotFoundException(ExceptionMessage.CART_ITEM_NOT_FOUND);
            }

            cart.getItems().remove(cartItem);

            Long phoneIdToDelete = cartItem.getPhone().getId();
            Integer quantity = cartItem.getQuantity();

            stockService.decreaseReservedQuantity(phoneIdToDelete, quantity);

            recalculateCart();

        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    @Override
    public void cleanupSessionAndReservedItems() {
        if (cart.getItems().isEmpty()) {
            return;
        }

        Map<Long, Integer> reservedItems = cart.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getPhone().getId(),
                        CartItem::getQuantity
                ));

        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalQuantity(0);

        stockService.cleanUpReserved(reservedItems);
    }

    @Override
    public void addPhonesByModels(MassAddToCart massAddToCart) {

        sessionLock.writeLock().lock();
        try {
            Map<String, Integer> items = extractValidItems(massAddToCart);

            if (items.isEmpty()) {
                return;
            }

            Map<String, Phone> phones = phoneService.findPhonesByModels(items.keySet());

            Map<Long, Integer> phoneIdToQuantity = new HashMap<>();
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                Phone phone = phones.get(entry.getKey());
                phoneIdToQuantity.put(phone.getId(), entry.getValue());
            }

            stockService.reserveAndValidateItems(phoneIdToQuantity);

            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                Phone phone = phones.get(entry.getKey());
                int qty = entry.getValue();

                CartItem existing = getCartItemByPhoneId(phone.getId());
                if (existing == null) {
                    cart.getItems().add(new CartItem(phone, qty));
                } else {
                    existing.setQuantity(existing.getQuantity() + qty);
                }
            }

            recalculateCart();

        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    private Map<String, Integer> extractValidItems(MassAddToCart massAddToCart) {
        Map<String, Integer> result = new LinkedHashMap<>();

        for (int i = 0; i < 8; i++) {
            String code = massAddToCart.getProductModels().get(i);
            Integer qty = massAddToCart.getQuantities().get(i);

            if (code != null && !code.isBlank()
                    && qty != null && qty >= 1) {

                result.put(code.trim(), qty);
            }
        }

        return result;
    }



    private void recalculateCart() {
        long totalQuantity = cart.getItems().stream()
                .mapToLong(CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = cart.getItems().stream()
                .map(cartItem -> {
                    if (cartItem.getPhone().getPrice() == null) {
                        return BigDecimal.ZERO;
                    }
                    return cartItem.getPhone()
                            .getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);


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
