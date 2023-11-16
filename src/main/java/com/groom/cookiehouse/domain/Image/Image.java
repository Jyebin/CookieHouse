package com.groom.cookiehouse.domain.Image;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@NoArgsConstructor
@Getter
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Entity
public abstract class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String imageUrl; //이미지 url을 나타내는 필드

    public Image(String imageUrl) { //이미지 url을 받는 생성자
        this.imageUrl = imageUrl;
    }

}
