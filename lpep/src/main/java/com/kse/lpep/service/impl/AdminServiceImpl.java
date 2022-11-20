package com.kse.lpep.service.impl;

import com.kse.lpep.service.IAdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements IAdminService {
    /*
     * 1.判断userId是否存在
     * 2.判断experId和groupId是否对应
     */
    @Override
    public int addTesterToExper(String userId, String experId, String groupId) {
        return 0;
    }
}
