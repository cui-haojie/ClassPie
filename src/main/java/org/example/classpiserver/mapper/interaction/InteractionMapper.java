package org.example.classpiserver.mapper.interaction;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.InteractionPick;
import org.example.classpiserver.entity.InteractionResponse;

import java.util.List;

@Mapper
public interface InteractionMapper {

    @Insert("insert into course_interaction_response (activity_id, account, option_index, content, round_num) values (#{activity_id}, #{account}, #{option_index}, #{content}, #{round_num})")
    boolean addInteractionResponse(InteractionResponse response);

    @Update("update course_interaction_response set content = #{content} where activity_id = #{activity_id} and account = #{account} and round_num = #{round_num}")
    boolean updateInteractionResponse(@Param("activity_id") Long activity_id, @Param("account") String account,
                                      @Param("round_num") Integer round_num, @Param("content") String content);

    @Select("select * from course_interaction_response where activity_id = #{activity_id} and account = #{account} and round_num = #{round_num} limit 1")
    InteractionResponse getInteractionResponseByAccountAndRound(@Param("activity_id") Long activity_id,
                                                                @Param("account") String account,
                                                                @Param("round_num") Integer round_num);

    @Select("select r.*, a.name as account_name from course_interaction_response r " +
            "left join accounts a on r.account = a.account where r.activity_id = #{activity_id} and r.round_num = #{round_num} order by r.create_time asc")
    List<InteractionResponse> getInteractionResponsesByRound(@Param("activity_id") Long activity_id,
                                                             @Param("round_num") Integer round_num);

    @Select("select count(distinct account) from course_interaction_response where activity_id = #{activity_id}")
    Integer countDistinctInteractionParticipants(@Param("activity_id") Long activity_id);

    @Insert("insert into course_interaction_pick (activity_id, account) values (#{activity_id}, #{account})")
    boolean addInteractionPick(@Param("activity_id") Long activity_id, @Param("account") String account);

    @Select("select p.*, a.name as account_name from course_interaction_pick p " +
            "left join accounts a on p.account = a.account where p.activity_id = #{activity_id} order by p.create_time desc")
    List<InteractionPick> getInteractionPicksByActivityId(@Param("activity_id") Long activity_id);

    @Select("select account from course_interaction_pick where activity_id = #{activity_id}")
    List<String> getPickedAccountsByActivityId(@Param("activity_id") Long activity_id);
}
