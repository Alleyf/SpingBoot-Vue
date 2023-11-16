package com.example.service;

import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService extends UserDetailsService {

    List<Account> getAccounts();

    Account getAccountById(@Param("id") Integer id);

    Account getAccountByName(@Param("username") String username);

    Account getAccountByNameOrEmail(@Param("uoe") String uoe);

    String registerEmailVerifyCode(String type, String email, String ip);

    String registerEmailAccount(EmailRegisterVO emailRegisterVO);

    String resetConfirm(ConfirmResetVO confirmResetVO);

    String resetEmailAccountPassword(EmailResetVO emailResetVO);
}
