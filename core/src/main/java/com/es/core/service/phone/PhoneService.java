package com.es.core.service.phone;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.dto.PhoneDto;
import com.es.core.model.phone.Phone;

import java.util.Map;
import java.util.Set;

public interface PhoneService {
    Page<PhoneDto> findAllPhones(Pageable page, String search);

    Phone findPhoneById(Long phoneId);

    void save(Phone phone);

    Map<String, Phone> findPhonesByModels(Set<String> codes);
}
