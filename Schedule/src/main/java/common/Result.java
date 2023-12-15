package common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> build(ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> build(ResultCodeEnum resultCodeEnum, T data) {
        Result<T> result = build(resultCodeEnum);
        result.setData(data);

        return result;
    }

}