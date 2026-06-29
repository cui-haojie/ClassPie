package org.example.classpiserver.entity;

public class InteractionResponse {
    private Long id;
    private Long activity_id;
    private String account;
    private Integer option_index;
    private String content;
    private Integer round_num;
    private String create_time;
    private String account_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getOption_index() {
        return option_index;
    }

    public void setOption_index(Integer option_index) {
        this.option_index = option_index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRound_num() {
        return round_num;
    }

    public void setRound_num(Integer round_num) {
        this.round_num = round_num;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }
}
