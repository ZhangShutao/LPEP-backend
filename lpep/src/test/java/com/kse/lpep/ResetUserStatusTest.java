package com.kse.lpep;

import com.kse.lpep.mapper.IProgSubmitMapper;
import com.kse.lpep.mapper.ISubmitMapper;
import com.kse.lpep.mapper.IUserFootprintMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ResetUserStatusTest {
    @Autowired
    private IUserFootprintMapper userFootprintMapper;
    @Autowired
    private IProgSubmitMapper progSubmitMapper;
    @Autowired
    private ISubmitMapper submitMapper;

    @Test
    void resetUserExper(String userId, String experId){

    }
}
