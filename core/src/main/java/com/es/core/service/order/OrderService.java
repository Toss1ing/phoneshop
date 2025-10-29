package com.es.core.service.order;

import com.es.core.dto.order.UserPersonalInfoDto;
import com.es.core.model.order.Order;

public interface OrderService {

    Order createOrder();

    void placeOrder(Order order, UserPersonalInfoDto userPersonalInfoDto);

    Order getOrderBySecureId(String secureId);

    boolean isOrderConsistency(Order draft);

}
