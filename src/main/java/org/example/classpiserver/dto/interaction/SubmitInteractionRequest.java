package org.example.classpiserver.dto.interaction;

public class SubmitInteractionRequest {
    private Long activity_id;
    private String account;
    private Integer option_index;
    private String content;

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
}
