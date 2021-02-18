package com.st.demo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

@Setter
@Getter
public class SmapEntity {

    String code; //
    Integer market;
    String name; //
    Integer decimal;
    Double dktotal; //
    Double preKPrice;
    String[] klines; //

}
