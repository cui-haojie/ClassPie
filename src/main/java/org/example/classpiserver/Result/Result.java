//package org.example.classpiserver.Result;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//
//@Builder(toBuilder = true)
//@AllArgsConstructor
//@Setter
//@Getter
//@Slf4j
//public class Result<T> {
//    /**
//     * 提示信息
//     */
//    @Schema(description = "提示信息")
//    private String message;
//    /**
//     * 是否成功
//     */
//    @Schema(description = "是否成功")
//    private boolean success;
//    /**
//     * 返回状态码
//     */
//    @Schema(description = "返回状态码")
//    private Integer code;
//    /**
//     * 数据
//     */
//    @Schema(description = "数据")
//    private T data;
//
//    public Result() {
//    }
//
//    public static Result success() {
//        Result Result = new Result();
//        Result.setSuccess(Boolean.TRUE);
//        Result.setCode(ResultCode.SUCCESS.getCode());
//        Result.setMessage(ResultCode.SUCCESS.getMsg());
//        return Result;
//    }
//
//    public static Result success(String msg) {
//        Result Result = new Result();
//        Result.setMessage(msg);
//        Result.setSuccess(Boolean.TRUE);
//        Result.setCode(ResultCode.SUCCESS.getCode());
//        return Result;
//    }
//
//    public static Result success(Object data) {
//        Result Result = new Result();
//        Result.setData(data);
//        Result.setSuccess(Boolean.TRUE);
//        Result.setCode(ResultCode.SUCCESS.getCode());
//        Result.setMessage(ResultCode.SUCCESS.getMsg());
//        return Result;
//    }
//
//    /**
//     * 返回失败 消息
//     *
//     * @return Result
//     */
//    public static Result failure() {
//        Result Result = new Result();
//        Result.setSuccess(Boolean.FALSE);
//        Result.setCode(ResultCode.FAILURE.getCode());
//        Result.setMessage(ResultCode.FAILURE.getMsg());
//        return Result;
//    }
//
//    /**
//     * 返回失败 消息
//     *
//     * @param msg 失败信息
//     * @return Result
//     */
//    public static Result failure(String msg) {
//        Result Result = new Result();
//        Result.setSuccess(Boolean.FALSE);
//        Result.setCode(ResultCode.FAILURE.getCode());
//        Result.setMessage(msg);
//        return Result;
//    }
//
//    public static Result failure(Integer code, String msg) {
//        Result Result = new Result();
//        Result.setSuccess(Boolean.FALSE);
//        Result.setCode(code);
//        Result.setMessage(msg);
//        return Result;
//    }
//
//
//    public static Result failure(String msg, ResultCode exceptionCode) {
//        Result Result = new Result();
//        Result.setMessage(msg);
//        Result.setSuccess(Boolean.FALSE);
//        Result.setCode(exceptionCode.getCode());
//        Result.setData(exceptionCode.getMsg());
//        return Result;
//    }
//
//    /**
//     * 返回失败 消息
//     *
//     * @param exceptionCode 错误信息枚举
//     * @return Result
//     */
//    public static Result failure(ResultCode exceptionCode) {
//        Result Result = new Result();
//        Result.setSuccess(Boolean.FALSE);
//        Result.setCode(exceptionCode.getCode());
//        Result.setMessage(exceptionCode.getMsg());
//        return Result;
//    }
//
//    /**
//     * 返回失败 消息
//     *
//     * @param exceptionCode 错误信息枚举
//     * @param msg           自定义错误提示信息
//     * @return Result
//     */
//    public static Result failure(ResultCode exceptionCode, String msg) {
//        Result Result = new Result();
//        Result.setMessage(msg);
//        Result.setSuccess(Boolean.FALSE);
//        Result.setCode(exceptionCode.getCode());
//        return Result;
//    }
//
//    public enum ResultCode {
//        SUCCESS(200, "操作成功"),
//        CREATED(201, "创建成功"),
//        ACCEPTED(202, "请求已接受"),
//        NO_CONTENT(204, "操作成功，无返回内容"),
//
//        BAD_REQUEST(400, "参数错误"),
//        UNAUTHORIZED(401, "未授权"),
//        FORBIDDEN(403, "禁止访问"),
//        NOT_FOUND(404, "资源不存在"),
//        METHOD_NOT_ALLOWED(405, "方法不允许"),
//
//        INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
//        SERVICE_UNAVAILABLE(503, "服务不可用"),
//
//        // 通用失败状态码
//        FAILURE(1000, "操作失败"),
//
//        // 业务相关状态码（从1000开始）
//        BUSINESS_ERROR(1001, "业务异常"),
//        VALIDATION_ERROR(1002, "参数验证失败"),
//        DATA_NOT_EXIST(1003, "数据不存在"),
//        DATA_ALREADY_EXISTS(1004, "数据已存在");
//
//        private final Integer code;
//        private final String msg;
//
//        ResultCode(Integer code, String msg) {
//            this.code = code;
//            this.msg = msg;
//        }
//
//        public Integer getCode() {
//            return code;
//        }
//
//        public String getMsg() {
//            return msg;
//        }
//    }
//
//}
