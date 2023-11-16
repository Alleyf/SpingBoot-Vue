package com.example.mapper;


import com.example.entity.dto.Account;
import com.example.entity.vo.request.EmailRegisterVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AccountMapper {
    @Select("select distinct id,username,email,role,register_time from db_account")
    List<Account> getAccounts();

    @Select("select  * from db_account where id = #{id}")
    Account getAccountById(Integer id);

    @Select("select  * from db_account where username = #{username}")
    Account getAccountByName(String username);

    @Select("select  * from db_account where email = #{email}")
    Account getAccountByEmail(String email);

    @Select("select  * from db_account where username = #{uoe} or email = #{uoe}")
    Account getAccountByNameOrEmail(String uoe);


    @Insert("insert into db_account(username,password,email,role,register_time) values(#{username}," +
            "#{password}," +
            "#{email}," +
            "#{role}," +
            "#{register_time})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Integer addAccount(Account account);

    @Update("update db_account set  password=#{newEncryptPassword} where email=#{email}")
    Integer updatePasswordByEmail(@Param("email") String email, @Param("newEncryptPassword") String newEncryptPassword);
}
