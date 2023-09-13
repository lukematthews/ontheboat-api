package au.com.ontheboat.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ActionResponse {

    SAVE_SUCCESS("Saved Successfully", "OK");

    @Getter
    private String message;
    @Getter
    private String status;
}
