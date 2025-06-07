package com.lucio.erp_new_app_3.dtos.devise;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Currency {
    private String name;
    private String owner;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime creation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime modified;

    @JsonProperty("modified_by")
    private String modifiedBy;

    private int docstatus;
    private int idx;

    @JsonProperty("currency_name")
    private String currencyName;

    private int enabled;
    private String fraction;

    @JsonProperty("fraction_units")
    private int fractionUnits;

    @JsonProperty("smallest_currency_fraction_value")
    private double smallestCurrencyFractionValue;

    private String symbol;

    @JsonProperty("symbol_on_right")
    private int symbolOnRight;

    @JsonProperty("number_format")
    private String numberFormat;
}
