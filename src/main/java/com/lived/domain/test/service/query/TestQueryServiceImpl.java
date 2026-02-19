package com.lived.domain.test.service.query;

import com.lived.domain.test.exception.TestException;
import com.lived.domain.test.exception.code.TestErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestQueryServiceImpl implements TestQueryService {

    @Override
    public void checkFlag(Long flag) {
        if (flag == 1) {
            throw new TestException(TestErrorCode.TEST_EXCEPTION);
        }
    }
}
