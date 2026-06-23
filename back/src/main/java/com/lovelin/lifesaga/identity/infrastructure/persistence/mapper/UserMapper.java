package com.lovelin.lifesaga.identity.infrastructure.persistence.mapper;

import com.lovelin.lifesaga.identity.infrastructure.persistence.record.UserRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<UserRecord> findById(@Param("id") long id);

    Optional<UserRecord> findByOpenid(@Param("openid") String openid);

    int insert(UserRecord userRecord);

    int update(UserRecord userRecord);
}
