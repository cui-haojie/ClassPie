package org.example.classpiserver.mapper.account;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.Accounts;

@Mapper
public interface AccountMapper {

    @Select("select * from accounts where account = #{account} and password = #{password}")
    Accounts selectAccount(@Param("account") String account, @Param("password") String password);

    @Insert("insert into accounts (account, name, status, password, mechanism, status_number,email_or_phone) " +
            "values (#{account}, #{name}, #{status}, #{password}, #{mechanism}, #{status_number} ,#{email_or_phone})")
    boolean addUser(Accounts account);

    @Select("select * from accounts where account = #{account}")
    Integer selectAccountByAccount(@Param("account") String account);

    @Update("update accounts set password = #{newPassword} where account = #{account}")
    boolean changePassword(@Param("newPassword") String newPassword, @Param("account") String account);

    @Select("SELECT * FROM accounts where account = #{account} limit 1")
    Accounts getAccount(@Param("account") String account);

    @Select("select name from accounts where account = #{account}")
    String getAccountName(@Param("account") String account);

    @Select("select status from accounts where account = #{account}")
    String getAccountStatus(@Param("account") String account);

    @Update("update accounts set name = #{name}, mechanism = #{mechanism}, email_or_phone = #{email_or_phone}, status_number = #{status_number}, status = #{status} where account = #{account}")
    boolean updateAccount(Accounts account);

    @Update("update accounts set avatar_url = #{avatar_url} where account = #{account}")
    boolean updateAvatarUrl(@Param("account") String account, @Param("avatar_url") String avatar_url);
}
