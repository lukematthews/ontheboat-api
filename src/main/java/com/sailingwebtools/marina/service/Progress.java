package com.sailingwebtools.marina.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@Slf4j
public class Progress {
    @Builder.Default
    private Integer increment = 10;
    private Integer total;
    @Builder.Default
    private AtomicLong counter = new AtomicLong();
    @Builder.Default
    private String format = "{}/{} {}% {}p/s";

    @Builder.Default
    private Date start = new Date();

    public void increment() {
        long current = counter.incrementAndGet();
        if (current % increment == 0) {
            Date now = new Date();
            double elapsed = (now.getTime() - start.getTime()) / 1000;
            double speed = current / elapsed;
            NumberFormat percentageFormat = NumberFormat.getPercentInstance();
            percentageFormat.setMaximumFractionDigits(2);
            DecimalFormat format1 = new DecimalFormat("##.##");
            String percentage = format1.format((double) current / total * 100);
            log.info(format, current, total, percentage, speed);
        }
    }
}
