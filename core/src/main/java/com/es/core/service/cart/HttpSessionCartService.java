package com.es.core.service.cart;

import com.es.core.dao.phone.PhoneDao;
import com.es.core.dao.stock.StockDao;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HttpSessionCartService implements CartService {

    private static final String SESSION_CART_KEY = "cart";

    @Resource
    private HttpSession httpSession;

    @Resource
    private StockDao stockDao;

    @Resource
    private PhoneDao phoneDao;

    private final Map<String, ReadWriteLock> sessionLocks = new ConcurrentHashMap<>();

    private ReadWriteLock getLock() {
        return sessionLocks.computeIfAbsent(
                httpSession.getId(),
                id -> new ReentrantReadWriteLock()
        );
    }

    private Cart getSessionCart() {
        Cart cart = (Cart) httpSession.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = new Cart();
            httpSession.setAttribute(SESSION_CART_KEY, cart);
        }
        return cart;
    }

    @Override
    public Cart getCart() {
        ReadWriteLock lock = getLock();
        lock.readLock().lock();
        try {
            return getSessionCart();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Cart addPhone(Long phoneId, Long quantity) {
        //TODO validate phoneId and quantity
        //TODO write all messages on new class in package util

        ReadWriteLock lock = getLock();
        lock.writeLock().lock();
        try {
            Phone phone = phoneDao.get(phoneId)
                    .orElseThrow(() -> new NotFoundException("Phone not found"));

            Stock stock = stockDao.getStockByPhoneId(phoneId)
                    .orElseThrow(() -> new NotFoundException("Stock for phone with id not found"));

            Cart cart = getSessionCart();

            Optional<CartItem> existCartItem = cart.getItems().stream()
                    .filter(cartItem -> cartItem.getPhone().getId().equals(phone.getId()))
                    .findFirst();

            long newQuantity = existCartItem
                    .map(item -> item.getQuantity() + quantity)
                    .orElse(quantity);

            if (newQuantity > stock.getStock()) {
                throw new OutOfStockException("Out of stock");
            }

            if (existCartItem.isPresent()) {
                existCartItem.get().setQuantity(newQuantity);
            } else {
                cart.getItems().add(new CartItem(phone, newQuantity));
            }

            recalculateCart(cart);
            return cart;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(Map<Long, Long> items) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void remove(Long phoneId) {
        throw new UnsupportedOperationException("TODO");
    }

    private void recalculateCart(Cart cart) {
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

}
