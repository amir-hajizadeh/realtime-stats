package com.ahajizadeh.rts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {
    private StatsCalculator statsCalculator;

    @Autowired
    public StatsController(StatsCalculator statsCalculator) {
        this.statsCalculator = statsCalculator;
    }

    @PostMapping("/transactions")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void insertTransaction(@RequestBody Transaction transaction) {
        statsCalculator.updateStats(transaction,System.currentTimeMillis());
    }

    @GetMapping("/statistics")
    public StatsEntity getStats() {
        return statsCalculator.getStats(System.currentTimeMillis());
    }

}
