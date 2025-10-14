package com.es.core.dao.color;

import com.es.core.model.color.Color;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ColorDao {

    Map<Long, Set<Color>> findColorsForPhoneIds(List<Long> phoneIds);

    void saveColorsByPhoneId(Set<Color> colors, Long phoneId);

}
