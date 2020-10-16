package com.sidchai.music.exception;

import com.sidchai.music.result.ResultCodeEnum;
import lombok.Data;

/**
 *  自定义异常
 * @author sidchai
 * @since 2020-05-30
 */
@Data
public class MusicException extends RuntimeException {

    private Integer code;

    public MusicException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public MusicException(String message) {
        super(message);
    }

    public MusicException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "MusicException{" +
                "code=" + code +
                ",message=" + this.getMessage() +
                '}';
    }
}
