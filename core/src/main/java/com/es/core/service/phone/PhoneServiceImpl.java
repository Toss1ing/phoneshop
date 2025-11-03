package com.es.core.service.phone;

import com.es.core.dao.color.ColorDao;
import com.es.core.dao.pagination.Page;
import com.es.core.dao.pagination.Pageable;
import com.es.core.dao.phone.PhoneDao;
import com.es.core.dto.PhoneDto;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.NotValidDataException;
import com.es.core.model.color.Color;
import com.es.core.model.phone.Phone;
import com.es.core.util.ExceptionMessage;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhoneServiceImpl implements PhoneService {

    @Resource
    private PhoneDao phoneDao;

    @Resource
    private ColorDao colorDao;

    @Override
    public Page<PhoneDto> findAllPhones(Pageable pageable, String search) {
        Page<Phone> phonesPage = phoneDao.findAll(pageable, search);

        if (phonesPage.content().isEmpty()) {
            return new Page<>(
                    List.of(),
                    pageable.getPage(),
                    pageable.getSize(),
                    0L
            );
        }

        List<Long> phoneIds = phonesPage.content().stream()
                .map(Phone::getId)
                .toList();

        Map<Long, Set<Color>> phoneColors = colorDao.findColorsForPhoneIds(phoneIds);

        List<PhoneDto> phonesDto = phonesPage.content().stream()
                .map(phone -> {
                    phone.setColors(phoneColors.getOrDefault(phone.getId(), Set.of()));
                    return mapPhoneToDto(phone);
                })
                .toList();

        return new Page<>(
                phonesDto,
                phonesPage.pageNumber(),
                phonesPage.pageSize(),
                phonesPage.totalElements()
        );
    }

    @Override
    public Phone findPhoneById(Long phoneId) {
        if (phoneId == null) {
            throw new NotValidDataException(ExceptionMessage.PHONE_ID_IS_NULL);
        }

        return phoneDao.get(phoneId).orElseThrow(() -> new NotFoundException(String.format(
                ExceptionMessage.PHONE_NOT_FOUND_BY_ID_MESSAGE,
                phoneId
        )));
    }

    @Override
    @Transactional
    public void save(Phone phone) {
        phoneDao.save(phone);
        colorDao.saveColorsByPhoneId(phone.getColors(), phone.getId());
    }

    private PhoneDto mapPhoneToDto(Phone phone) {
        return new PhoneDto(
                phone.getId(),
                phone.getImageUrl(),
                phone.getBrand(),
                phone.getModel(),
                phone.getColors(),
                phone.getDisplaySizeInches(),
                phone.getPrice()
        );
    }

}
