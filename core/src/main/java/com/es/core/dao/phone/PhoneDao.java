package com.es.core.dao.phone;

import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.model.phone.Phone;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PhoneDao {
    Optional<Phone> get(Long key);

    void save(Phone phone);

    Page<Phone> findAll(Pageable pageable, String search);

    List<Phone> findPhonesByModels(Set<String> brands);
}
