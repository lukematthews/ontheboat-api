package au.com.ontheboat.api.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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

    public void incrementProgressBar() {
        printProgress(start.getTime(), total, counter.incrementAndGet());
    }

    public static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 :
                (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string.append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }
}
