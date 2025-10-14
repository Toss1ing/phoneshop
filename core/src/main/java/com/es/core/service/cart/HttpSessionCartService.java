package com.es.core.service.cart;

import com.es.core.dao.phone.PhoneDao;
import com.es.core.dao.stock.StockDao;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import com.es.core.util.ExceptionMessage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Map;
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

        ReadWriteLock lock = getLock();
        lock.writeLock().lock();
        try {
            Phone phone = phoneDao.get(phoneId)
                    .orElseThrow(() -> new NotFoundException(String.format(
                            ExceptionMessage.PHONE_NOT_FOUND_BY_ID_MESSAGE,
                            phoneId
                    )));

            Cart cart = getSessionCart();

            CartItem existCartItem = getCartItemByPhoneId(phoneId, cart);

            long newQuantity = getNewQuantity(existCartItem, quantity);

            validateQuantity(newQuantity, phoneId);

            if (existCartItem != null) {
                existCartItem.setQuantity(newQuantity);
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

    private long getNewQuantity(CartItem existCartItem, long quantity) {
        if (existCartItem == null) {
            return quantity;
        }
        return existCartItem.getQuantity() + quantity;
    }

    private void validateQuantity(long newQuantity, Long phoneId) {
        Stock stock = stockDao.getStockByPhoneId(phoneId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ExceptionMessage.STOCK_NOT_FOUND_BY_PHONE_MESSAGE,
                        phoneId
                )));

        if (newQuantity > stock.getStock()) {
            throw new OutOfStockException(ExceptionMessage.OUT_OF_STOCK_MESSAGE);
        }
    }

    private CartItem getCartItemByPhoneId(Long phoneId, Cart cart) {
        return cart.getItems().stream()
                .filter(cartItem -> cartItem.getPhone().getId().equals(phoneId))
                .findFirst()
                .orElse(null);
    }

    public void removeLockForSession(String sessionId) {
        sessionLocks.remove(sessionId);
    }

}
