package ru.vlapin.examples;

import lombok.*;
import lombok.experimental.Wither;
import lombok.extern.log4j.Log4j2;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class Bean {

    @Wither
    private int x;
    private double y;
    private String s;

//    @Singular
//    private List<String> strings;

    @SneakyThrows
    public static void main(String... args) {

        Class.forName("java.lang.String");

//        val bean1 = Bean.builder()
//                .s("dfg")
//                .x(23)
//                .y(87.8)
//                .string("dfg")
//                .string("jygsdf")
//                .string("876345")
//                .build();


        val bean1 = new Bean();

        Bean bean2 = new Bean()
                .setX(234)
                .setS("dfg")
                .setY(98746.346);
//        int x = bean2.x();

        Bean bean3;
        Bean bean4;
        bean4 = bean3 = bean2 = new Bean();

        new Bean().setY(234.8);
        new Bean().setS("234.8");

        Bean bean = new Bean(1234, 9845.0, "");
    }
}
