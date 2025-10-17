package com.es.core.service.phone;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.dto.PhoneDto;
import com.es.core.model.phone.Phone;

public interface PhoneService {
    Page<PhoneDto> findAllPhones(Pageable page, String search);

    Phone findPhoneById(Long phoneId);

    void save(Phone phone);
}
