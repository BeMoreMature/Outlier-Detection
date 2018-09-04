package com.ms.fxcashsnt.markservice.sentinel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * user: yandong.liu
 * date: 7/16/2018
 */
public interface MarkServiceConstants {
    List<String> IntraContextList = Arrays.asList("ASIA", "TKEOD", "LNEOD", "NYEOD");
    List<String> EndContextList = Arrays.asList("HKFRM", "TKFRM", "LNFRM", "NYFRM");
    List<String> IgnoreCurrencyList = Arrays.asList("ARA", "ARP", "ATS", "BEF", "BRC", "BRR", "CSD", "CYP", "DEM", "ECS", "EEK", "ESP", "FIM", "FRF", "GHC", "GRD", "IEP", "ITL", "LUF", "LVL", "MTL", "NLG", "PLZ", "PTE", "ROL", "RUR", "TRL", "VEB", "XEU", "ZAL");
}

