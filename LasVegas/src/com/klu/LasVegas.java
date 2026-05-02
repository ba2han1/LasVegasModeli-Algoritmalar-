package com.klu;
/*
 * ===============================================================
 *  Las Vegas Algoritması — Randomize Algoritma Ödevi
 *  Öğrenci No Son İki Hane : 37
 *  Algoritma Tipi          : Las Vegas (son hane TEK → 7)
 *  Veri Hacmi              : n = 10^6 (Y=7 >= 5)
 *  Hedef Koşul             : eleman mod 7 == 0
 *  Seed                    : 37
 * ===============================================================
 */

import java.util.Random;

public class LasVegas {

    // ──────────────────────────────────────────────
    // SABITLER
    // ──────────────────────────────────────────────
    static final long   SEED        = 37L;
    static final int    N           = 1_000_000;   // 10^6
    static final int    HEDEF_MOD   = 7;
    static final int    TEKRAR      = 100;
    static final double TEORIK_EX   = 7.0;          // E[X] = 1/p = 7
    static final double TEORIK_STD  = Math.sqrt(42);// σ = sqrt(42) ≈ 6.48

    // ──────────────────────────────────────────────
    // 1) VERİ SETİ OLUŞTURMA
    // ──────────────────────────────────────────────
    static int[] veriSetiniOlustur() {
        Random rng = new Random(SEED);
        int[] veri = new int[N];
        for (int i = 0; i < N; i++) {
            veri[i] = rng.nextInt(10_000_000) + 1; // [1, 10^7]
        }
        return veri;
    }

    // ──────────────────────────────────────────────
    // 2) LAS VEGAS ALGORİTMASI
    //    mod 7 = 0 olan bir eleman bulana kadar
    //    rastgele indeks seçer.
    //    Döndürür: adım sayısı
    // ──────────────────────────────────────────────
    static int lasVegas(int[] veri, long seedOffset) {
        Random rng = new Random(SEED + seedOffset);
        int adim = 0;
        while (true) {
            adim++;
            int idx = rng.nextInt(N);
            if (veri[idx] % HEDEF_MOD == 0) {
                return adim;
            }
        }
    }

    // ──────────────────────────────────────────────
    // YARDIMCI: Ortalama
    // ──────────────────────────────────────────────
    static double ortalama(int[] dizi) {
        long toplam = 0;
        for (int x : dizi) toplam += x;
        return (double) toplam / dizi.length;
    }

    static double ortalama(double[] dizi) {
        double toplam = 0;
        for (double x : dizi) toplam += x;
        return toplam / dizi.length;
    }

    // ──────────────────────────────────────────────
    // YARDIMCI: Standart Sapma
    // ──────────────────────────────────────────────
    static double stdSapma(int[] dizi, double ort) {
        double toplam = 0;
        for (int x : dizi) toplam += Math.pow(x - ort, 2);
        return Math.sqrt(toplam / (dizi.length - 1));
    }

    static double stdSapma(double[] dizi, double ort) {
        double toplam = 0;
        for (double x : dizi) toplam += Math.pow(x - ort, 2);
        return Math.sqrt(toplam / (dizi.length - 1));
    }

    // ──────────────────────────────────────────────
    // MAIN
    // ──────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("  Las Vegas Algoritması — Randomize Arama");
        System.out.println("============================================================");

        // --- Veri seti ---
        System.out.printf("%n[1] Veri seti oluşturuluyor... (n=%,d, seed=%d)%n", N, SEED);
        int[] veri = veriSetiniOlustur();

        // mod 7 = 0 olan eleman sayısını göster
        int hedefSayisi = 0;
        for (int x : veri) if (x % HEDEF_MOD == 0) hedefSayisi++;
        double pGercek = (double) hedefSayisi / N;

        System.out.printf("    Toplam eleman       : %,d%n", N);
        System.out.printf("    mod 7 = 0 olan      : %,d%n", hedefSayisi);
        System.out.printf("    Gerçek p (ampirik)  : %.6f%n", pGercek);
        System.out.printf("    Teorik  p           : %.6f%n", 1.0 / 7);

        // --- 100 kez çalıştırma ---
        System.out.printf("%n[2] Algoritma %d kez çalıştırılıyor...%n", TEKRAR);

        int[]    adimListesi = new int[TEKRAR];
        double[] sureListesi = new double[TEKRAR];

        for (int i = 0; i < TEKRAR; i++) {
            long baslangic = System.nanoTime();
            int adim = lasVegas(veri, i);
            long bitis = System.nanoTime();

            adimListesi[i] = adim;
            sureListesi[i] = (bitis - baslangic) / 1_000_000.0; // ms
        }

        System.out.println("    Tamamlandı ✓");

        // --- İstatistiksel Analiz ---
        double ortAdim = ortalama(adimListesi);
        double stdAdim = stdSapma(adimListesi, ortAdim);
        double ortSure = ortalama(sureListesi);
        double stdSure = stdSapma(sureListesi, ortSure);

        int minAdim = Integer.MAX_VALUE, maxAdim = Integer.MIN_VALUE;
        double minSure = Double.MAX_VALUE, maxSure = Double.MIN_VALUE;
        for (int i = 0; i < TEKRAR; i++) {
            if (adimListesi[i] < minAdim) minAdim = adimListesi[i];
            if (adimListesi[i] > maxAdim) maxAdim = adimListesi[i];
            if (sureListesi[i] < minSure) minSure = sureListesi[i];
            if (sureListesi[i] > maxSure) maxSure = sureListesi[i];
        }

        System.out.println();
        System.out.println("============================================================");
        System.out.println("  SONUÇLAR");
        System.out.println("============================================================");

        System.out.println("\n  ── Adım Sayısı Analizi ──");
        System.out.printf("    Teorik  E[X]          : %.4f%n", TEORIK_EX);
        System.out.printf("    Ampirik Ort. E[X]     : %.4f%n", ortAdim);
        System.out.printf("    Fark (%%)              : %.2f%%%n",
                Math.abs(ortAdim - TEORIK_EX) / TEORIK_EX * 100);
        System.out.printf("    Teorik  σ             : %.4f%n", TEORIK_STD);
        System.out.printf("    Ampirik σ             : %.4f%n", stdAdim);
        System.out.printf("    Min adım              : %d%n", minAdim);
        System.out.printf("    Max adım              : %d%n", maxAdim);

        System.out.println("\n  ── Çalışma Süresi Analizi ──");
        System.out.printf("    Ortalama süre         : %.4f ms%n", ortSure);
        System.out.printf("    Std sapma (süre)      : %.4f ms%n", stdSure);
        System.out.printf("    Min süre              : %.4f ms%n", minSure);
        System.out.printf("    Max süre              : %.4f ms%n", maxSure);
        System.out.println("============================================================");

        // --- Basit Metin Histogramı (Konsol Grafiği) ---
        System.out.println("\n[3] Adım Sayısı Dağılımı (Konsol Histogramı):");
        konsoldaHistogram(adimListesi, 10);

        System.out.println("\n[4] Çalışma Süresi Dağılımı (Konsol Histogramı):");
        konsoldaHistogramDouble(sureListesi, 10);
    }

    // ──────────────────────────────────────────────
    // KONSOL HİSTOGRAMI (int dizisi)
    // ──────────────────────────────────────────────
    static void konsoldaHistogram(int[] dizi, int aralikSayisi) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int x : dizi) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        double aralik = (double)(max - min) / aralikSayisi;
        int[] sayac = new int[aralikSayisi];

        for (int x : dizi) {
            int bin = (int)((x - min) / aralik);
            if (bin == aralikSayisi) bin--;
            sayac[bin]++;
        }

        int maxSayac = 0;
        for (int s : sayac) if (s > maxSayac) maxSayac = s;

        for (int i = 0; i < aralikSayisi; i++) {
            double alt = min + i * aralik;
            double ust = alt + aralik;
            int barUzunluk = (int)((double) sayac[i] / maxSayac * 30);
            String bar = "#".repeat(barUzunluk);
            System.out.printf("  [%5.1f - %5.1f] | %-30s | %d%n", alt, ust, bar, sayac[i]);
        }
    }

    // ──────────────────────────────────────────────
    // KONSOL HİSTOGRAMI (double dizisi)
    // ──────────────────────────────────────────────
    static void konsoldaHistogramDouble(double[] dizi, int aralikSayisi) {
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (double x : dizi) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        double aralik = (max - min) / aralikSayisi;
        int[] sayac = new int[aralikSayisi];

        for (double x : dizi) {
            int bin = (int)((x - min) / aralik);
            if (bin == aralikSayisi) bin--;
            sayac[bin]++;
        }

        int maxSayac = 0;
        for (int s : sayac) if (s > maxSayac) maxSayac = s;

        for (int i = 0; i < aralikSayisi; i++) {
            double alt = min + i * aralik;
            double ust = alt + aralik;
            int barUzunluk = (int)((double) sayac[i] / maxSayac * 30);
            String bar = "#".repeat(barUzunluk);
            System.out.printf("  [%6.3f - %6.3f ms] | %-30s | %d%n", alt, ust, bar, sayac[i]);
        }
    }
}