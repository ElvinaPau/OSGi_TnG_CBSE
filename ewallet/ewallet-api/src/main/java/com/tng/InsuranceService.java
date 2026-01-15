package com.tng;

import java.util.List;

public interface InsuranceService {
    // 购买车险
    void purchaseMotorPolicy(String username, String plateNo);

    // 提交理赔
    void submitClaim(String username, String policyId);

    // 查看我的保单 (简单返回字符串列表，为了方便在 OSGi 命令行显示)
    List<String> viewPolicies(String username);
}