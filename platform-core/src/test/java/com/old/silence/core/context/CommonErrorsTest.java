package com.old.silence.core.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.old.silence.core.test.context.AbstractEnumMessageSourceResolvableTests;
import static com.old.silence.core.test.data.RandomData.randomId;
import static com.old.silence.core.test.data.RandomData.randomName;
import static com.old.silence.core.test.data.RandomData.randomPositiveInt;

/**
 * @author moryzang
 */
class CommonErrorsTest extends AbstractEnumMessageSourceResolvableTests<CommonErrors> {

    public CommonErrorsTest() {
        super("messages/platform_common_messages");
    }

    @Override
    protected Map<CommonErrors, Collection<Object>> createArgumentsMap() {
        Map<CommonErrors, Collection<Object>> map = new HashMap<>();
        map.put(CommonErrors.NOT_NULL, Collections.singletonList(randomName("Property")));
        map.put(CommonErrors.NOT_EMPTY, Collections.singletonList(randomName("Property")));
        map.put(CommonErrors.NOT_BLANK, Collections.singletonList(randomName("Property")));
        map.put(CommonErrors.INVALID_PARAMETER, Collections.singletonList(randomName("Property")));
        map.put(CommonErrors.DATE_RANGE_EXCEEDED, Collections.singletonList(randomPositiveInt()));
        map.put(CommonErrors.DATA_NOT_EXIST, Arrays.asList(randomName("Entity"), randomId()));
        map.put(CommonErrors.PARTICULAR_DATA_EXISTED, Collections.singletonList(randomName("Entity")));
        map.put(CommonErrors.SERVICE_UNAVAILABLE, Collections.singletonList(randomName("Service")));
        map.put(CommonErrors.REMOTE_CALL_FAILED, Arrays.asList(randomName("Property"), randomName("Error")));
        map.put(CommonErrors.REMOTE_CALL_EMPTY_BODY, Collections.singletonList(randomName("Service")));

        return map;
    }
}
