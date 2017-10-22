package com.example.dima.mytestingapp.Activitys;

import com.example.dima.mytestingapp.Items.ItemRates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dima on 22.10.2017.
 */

public class CurrencyExchange {

    /**
     * base : EUR
     * date : 2016-04-01
     * rates : {"AUD":1.4884,"BGN":1.9558,"BRL":4.1296,"CAD":1.4894,"CHF":1.0946,"CNY":7.3903,"CZK":27.03,"DKK":7.4503,"GBP":0.7989,"HKD":8.8652,"HRK":7.5105,"HUF":313.3,"IDR":15007.54,"ILS":4.3182,"INR":75.8528,"JPY":128.07,"KRW":1315.78,"MXN":19.8996,"MYR":4.4382,"NOK":9.4401,"NZD":1.6518,"PHP":52.615,"PLN":4.244,"RON":4.4693,"RUB":77.543,"SEK":9.2413,"SGD":1.5389,"THB":40.115,"TRY":3.2285,"USD":1.1432,"ZAR":16.8758}
     */

    private String base;
    private String date;
    /**
     * AUD : 1.4884
     * BGN : 1.9558
     * BRL : 4.1296
     * CAD : 1.4894
     * CHF : 1.0946
     * CNY : 7.3903
     * CZK : 27.03
     * DKK : 7.4503
     * GBP : 0.7989
     * HKD : 8.8652
     * HRK : 7.5105
     * HUF : 313.3
     * IDR : 15007.54
     * ILS : 4.3182
     * INR : 75.8528
     * JPY : 128.07
     * KRW : 1315.78
     * MXN : 19.8996
     * MYR : 4.4382
     * NOK : 9.4401
     * NZD : 1.6518
     * PHP : 52.615
     * PLN : 4.244
     * RON : 4.4693
     * RUB : 77.543
     * SEK : 9.2413
     * SGD : 1.5389
     * THB : 40.115
     * TRY : 3.2285
     * USD : 1.1432
     * ZAR : 16.8758
     */

    private RatesEntity rates;

    public List<ItemRates> getRatesList(){
        List<ItemRates> currencyList = new ArrayList<>();
        currencyList.add(new ItemRates("USD", rates.getUSD()));
        currencyList.add(new ItemRates("EUR", rates.getEUR()));
        currencyList.add(new ItemRates("RUB", rates.getRUB()));

        currencyList.add(new ItemRates("AUD", rates.getAUD()));
        currencyList.add(new ItemRates("BGN", rates.getBGN()));
        currencyList.add(new ItemRates("BRL", rates.getBRL()));
        currencyList.add(new ItemRates("CAD", rates.getCAD()));
        currencyList.add(new ItemRates("CHF", rates.getCHF()));

        currencyList.add(new ItemRates("CNY", rates.getCNY()));
        currencyList.add(new ItemRates("CZK", rates.getCZK()));
        currencyList.add(new ItemRates("DKK", rates.getDKK()));
        currencyList.add(new ItemRates("GBP", rates.getGBP()));
        currencyList.add(new ItemRates("HKD", rates.getHKD()));

        currencyList.add(new ItemRates("HRK", rates.getHRK()));
        currencyList.add(new ItemRates("HUF", rates.getHUF()));
        currencyList.add(new ItemRates("IDR", rates.getIDR()));
        currencyList.add(new ItemRates("ILS", rates.getILS()));
        currencyList.add(new ItemRates("INR", rates.getINR()));

        currencyList.add(new ItemRates("JPY", rates.getJPY()));
        currencyList.add(new ItemRates("KRW", rates.getKRW()));
        currencyList.add(new ItemRates("MXN", rates.getMXN()));
        currencyList.add(new ItemRates("MYR", rates.getMYR()));
        currencyList.add(new ItemRates("NOK", rates.getNOK()));

        currencyList.add(new ItemRates("NZD", rates.getNZD()));
        currencyList.add(new ItemRates("PHP", rates.getPHP()));
        currencyList.add(new ItemRates("PLN", rates.getPLN()));
        currencyList.add(new ItemRates("RON", rates.getRON()));

        currencyList.add(new ItemRates("SEK", rates.getSEK()));
        currencyList.add(new ItemRates("SGD", rates.getSGD()));
        currencyList.add(new ItemRates("THB", rates.getTHB()));
        currencyList.add(new ItemRates("TRY", rates.getTRY()));

        currencyList.add(new ItemRates("ZAR", rates.getZAR()));

        return currencyList;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RatesEntity getRates() {
        return rates;
    }

    public void setRates(RatesEntity rates) {
        this.rates = rates;
    }

    public static class RatesEntity {
        private double USD;
        private double EUR;
        private double RUB;
        private double AUD;
        private double BGN;
        private double BRL;
        private double CAD;
        private double CHF;
        private double CNY;
        private double CZK;
        private double DKK;
        private double GBP;
        private double HKD;
        private double HRK;
        private double HUF;
        private double IDR;
        private double ILS;
        private double INR;
        private double JPY;
        private double KRW;
        private double MXN;
        private double MYR;
        private double NOK;
        private double NZD;
        private double PHP;
        private double PLN;
        private double RON;
        private double SEK;
        private double SGD;
        private double THB;
        private double TRY;
        private double ZAR;

        public double getUSD() {
            return USD;
        }

        public double getEUR() {
            return EUR;
        }

        public void setEUR(double EUR) {
            this.EUR = EUR;
        }

        public void setUSD(double USD) {
            this.USD = USD;
        }

        public double getRUB() {
            return RUB;
        }

        public void setRUB(double RUB) {
            this.RUB = RUB;
        }

        public double getAUD() {
            return AUD;
        }

        public void setAUD(double AUD) {
            this.AUD = AUD;
        }

        public double getBGN() {
            return BGN;
        }

        public void setBGN(double BGN) {
            this.BGN = BGN;
        }

        public double getBRL() {
            return BRL;
        }

        public void setBRL(double BRL) {
            this.BRL = BRL;
        }

        public double getCAD() {
            return CAD;
        }

        public void setCAD(double CAD) {
            this.CAD = CAD;
        }

        public double getCHF() {
            return CHF;
        }

        public void setCHF(double CHF) {
            this.CHF = CHF;
        }

        public double getCNY() {
            return CNY;
        }

        public void setCNY(double CNY) {
            this.CNY = CNY;
        }

        public double getCZK() {
            return CZK;
        }

        public void setCZK(double CZK) {
            this.CZK = CZK;
        }

        public double getDKK() {
            return DKK;
        }

        public void setDKK(double DKK) {
            this.DKK = DKK;
        }

        public double getGBP() {
            return GBP;
        }

        public void setGBP(double GBP) {
            this.GBP = GBP;
        }

        public double getHKD() {
            return HKD;
        }

        public void setHKD(double HKD) {
            this.HKD = HKD;
        }

        public double getHRK() {
            return HRK;
        }

        public void setHRK(double HRK) {
            this.HRK = HRK;
        }

        public double getHUF() {
            return HUF;
        }

        public void setHUF(double HUF) {
            this.HUF = HUF;
        }

        public double getIDR() {
            return IDR;
        }

        public void setIDR(double IDR) {
            this.IDR = IDR;
        }

        public double getILS() {
            return ILS;
        }

        public void setILS(double ILS) {
            this.ILS = ILS;
        }

        public double getINR() {
            return INR;
        }

        public void setINR(double INR) {
            this.INR = INR;
        }

        public double getJPY() {
            return JPY;
        }

        public void setJPY(double JPY) {
            this.JPY = JPY;
        }

        public double getKRW() {
            return KRW;
        }

        public void setKRW(double KRW) {
            this.KRW = KRW;
        }

        public double getMXN() {
            return MXN;
        }

        public void setMXN(double MXN) {
            this.MXN = MXN;
        }

        public double getMYR() {
            return MYR;
        }

        public void setMYR(double MYR) {
            this.MYR = MYR;
        }

        public double getNOK() {
            return NOK;
        }

        public void setNOK(double NOK) {
            this.NOK = NOK;
        }

        public double getNZD() {
            return NZD;
        }

        public void setNZD(double NZD) {
            this.NZD = NZD;
        }

        public double getPHP() {
            return PHP;
        }

        public void setPHP(double PHP) {
            this.PHP = PHP;
        }

        public double getPLN() {
            return PLN;
        }

        public void setPLN(double PLN) {
            this.PLN = PLN;
        }

        public double getRON() {
            return RON;
        }

        public void setRON(double RON) {
            this.RON = RON;
        }

        public double getSEK() {
            return SEK;
        }

        public void setSEK(double SEK) {
            this.SEK = SEK;
        }

        public double getSGD() {
            return SGD;
        }

        public void setSGD(double SGD) {
            this.SGD = SGD;
        }

        public double getTHB() {
            return THB;
        }

        public void setTHB(double THB) {
            this.THB = THB;
        }

        public double getTRY() {
            return TRY;
        }

        public void setTRY(double TRY) {
            this.TRY = TRY;
        }

        public double getZAR() {
            return ZAR;
        }

        public void setZAR(double ZAR) {
            this.ZAR = ZAR;
        }
    }
}
