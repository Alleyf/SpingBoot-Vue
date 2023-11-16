package com.example.service.impl;

import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FlowUtils flowUtils;
    @Resource
    PasswordEncoder encoder;
    @Resource
    private AccountMapper userMapper;

    @Override
    public List<Account> getAccounts() {
        return userMapper.getAccounts();
    }

    @Override
    public Account getAccountById(Integer id) {
        return userMapper.getAccountById(id);
    }

    @Override
    public Account getAccountByName(String username) {
        return userMapper.getAccountByName(username);
    }

    @Override
    public Account getAccountByNameOrEmail(String uoe) {
        return userMapper.getAccountByNameOrEmail(uoe);
    }


    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        // 同步ip，防止多线程访问(防止压测导致同时请求，根据ip进行排队)
        synchronized (ip.intern()) {
            // 判断ip是否超出限制
            if (!this.verifyLimit(ip))
                return "请求频繁，请稍后重试！";
            else {
                // 生成随机验证码
                int code = new Random().nextInt(100000, 999999);
                // 构建消息队列数据
                Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
                // 发送消息队列
                amqpTemplate.convertAndSend("mail", data);
                // 将验证码存储到redis并设置验证码有效期为3分钟
                stringRedisTemplate.opsForValue().set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
                return null;
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名或邮箱获取账号
        Account account = getAccountByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码不正确");
        }
        //返回用户信息
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }


    @Override
    public String registerEmailAccount(EmailRegisterVO emailRegisterVO) {
        String email = emailRegisterVO.getEmail();
        String username = emailRegisterVO.getUsername();
        String password = emailRegisterVO.getPassword();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) return "如未获取验证码，请先获取验证码，如已经获取验证码，则验证码已过期，请重新获取";
        if (!code.equals(emailRegisterVO.getCode())) return "验证码错误，请重新输入";
        if (this.existAccountByEmail(email)) return "该电子邮箱已被注册";
        if (this.existAccountByName(username)) return "该用户名已被注册，请更换一个新的用户名";
        String encrypt_password = encoder.encode(password);
        Account account = new Account(null, username, encrypt_password, email, "user", new Date());
        boolean addFlag = userMapper.addAccount(account) == 1;
        if (addFlag) {
            stringRedisTemplate.delete(key);
            return null;
        } else
            return "内部错误，请联系管理员";
    }

    /**
     * 判断前端是否正确输入验证码
     *
     * @param confirmResetVO 验证重置视图对象
     * @return 验证成功与否信息
     */
    @Override
    public String resetConfirm(ConfirmResetVO confirmResetVO) {
        String email = confirmResetVO.getEmail();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) return "如未获取验证码，请先获取验证码，如已经获取验证码，则验证码已过期，请重新获取";
        if (!code.equals(confirmResetVO.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    /**
     * 根据邮箱重置密码
     *
     * @param emailResetVO 重置密码视图对象
     * @return 重置成功与否信息
     */
    @Override
    public String resetEmailAccountPassword(EmailResetVO emailResetVO) {
        String email = emailResetVO.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVO(email, emailResetVO.getCode()));
        if (verify != null) return verify;
        String new_encryptPassword = encoder.encode(emailResetVO.getPassword());
        boolean update_flag = userMapper.updatePasswordByEmail(email, new_encryptPassword) != -1;
        if (update_flag)
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
        return null;
    }

    private boolean existAccountByEmail(String email) {
        return userMapper.getAccountByEmail(email) != null;
    }

    private boolean existAccountByName(String name) {
        return userMapper.getAccountByName(name) != null;
    }

    private boolean verifyLimit(String ip) {
        // 拼接key
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        // 调用flowUtils的limitOnceCheck方法，传入key和60.
        return flowUtils.limitOnceCheck(key, 60);
    }


}
