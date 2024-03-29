package com.groom.cookiehouse.controller.dto.request.guestBook;

import com.groom.cookiehouse.domain.GuestBook;
import com.groom.cookiehouse.domain.Ornament;
import com.groom.cookiehouse.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Setter
public class GuestBookRequestDto {

    @NotNull
    private Long userId;
    @NotBlank
    private String author;
    @NotNull
    private Long ornamentId;
    @NotBlank
    private String content;

}
