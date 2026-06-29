package org.example.classpiserver.mapper.notification;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.Notification;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Insert("insert into notification (account, class_id, homework_id, type, message, is_read) values (#{account}, #{class_id}, #{homework_id}, #{type}, #{message}, 0)")
    boolean addNotification(Notification notification);

    @Select("select * from notification where account = #{account} order by id desc limit 50")
    List<Notification> getNotifications(@Param("account") String account);

    @Update("update notification set is_read = 1 where id = #{id} and account = #{account}")
    boolean markNotificationRead(@Param("id") Integer id, @Param("account") String account);

    @Update("update notification set is_read = 1 where account = #{account} and is_read = 0")
    boolean markAllNotificationsRead(@Param("account") String account);

    @Select("select count(*) from notification where account = #{account} and is_read = 0")
    Integer getUnreadNotificationCount(@Param("account") String account);
}
