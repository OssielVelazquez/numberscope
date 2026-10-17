package com.numberscope.region;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class LadaCoordinates {

    private static final Map<String, double[]> COORDS = Map.ofEntries(
            // CDMX y área metropolitana
            Map.entry("55",  new double[]{19.4326, -99.1332}),
            Map.entry("56",  new double[]{19.4326, -99.1332}),

            // Guadalajara
            Map.entry("33",  new double[]{20.6597, -103.3496}),

            // Monterrey
            Map.entry("81",  new double[]{25.6866, -100.3161}),

            // Puebla
            Map.entry("222", new double[]{19.0414, -98.2063}),

            // Guasave (Sinaloa) — la que nos interesa
            Map.entry("687", new double[]{25.5733, -108.4668}),

            // Culiacán
            Map.entry("667", new double[]{24.8091, -107.3940}),

            // Mérida
            Map.entry("999", new double[]{20.9674, -89.5926}),

            // Querétaro
            Map.entry("442", new double[]{20.5888, -100.3899}),

            // Tijuana
            Map.entry("664", new double[]{32.5149, -117.0382}),

            // León
            Map.entry("477", new double[]{21.1220, -101.6820}),

            // Cancún
            Map.entry("998", new double[]{21.1619, -86.8515}),

            // Veracruz
            Map.entry("229", new double[]{19.1738, -96.1342}),

            // Acapulco
            Map.entry("744", new double[]{16.8531, -99.8237}),

            // Hermosillo
            Map.entry("662", new double[]{29.0729, -110.9559}),

            // Mexicali
            Map.entry("686", new double[]{32.6245, -115.4523}),

            // Morelia
            Map.entry("443", new double[]{19.7060, -101.1950}),

            // Saltillo
            Map.entry("844", new double[]{25.4232, -100.9953}),

            // Torreón
            Map.entry("871", new double[]{25.5428, -103.4068}),

            // Chihuahua
            Map.entry("614", new double[]{28.6353, -106.0889}),

            // San Luis Potosí
            Map.entry("444", new double[]{22.1565, -100.9855}),

            // Aguascalientes
            Map.entry("449", new double[]{21.8853, -102.2916}),

            // Tuxtla Gutiérrez
            Map.entry("961", new double[]{16.7569, -93.1292}),

            // Oaxaca
            Map.entry("951", new double[]{17.0654, -96.7236}),

            // Villahermosa
            Map.entry("993", new double[]{17.9892, -92.9475}),

            // Tampico
            Map.entry("833", new double[]{22.2331, -97.8611}),

            // Durango
            Map.entry("618", new double[]{24.0277, -104.6532}),

            // Zacatecas
            Map.entry("492", new double[]{22.7709, -102.5832}),

            // Pachuca
            Map.entry("771", new double[]{20.1011, -98.7591}),

            // Toluca
            Map.entry("722", new double[]{19.2826, -99.6557}),

            // Cuernavaca
            Map.entry("777", new double[]{18.9242, -99.2216}),

            // Colima
            Map.entry("312", new double[]{19.2452, -103.7240}),

            // Tepic
            Map.entry("311", new double[]{21.5042, -104.8945}),

            // La Paz
            Map.entry("612", new double[]{24.1426, -110.3128}),

            // Campeche
            Map.entry("981", new double[]{19.8301, -90.5349}),

            // Chetumal
            Map.entry("983", new double[]{18.5001, -88.2962}),

            // Reynosa
            Map.entry("899", new double[]{26.0924, -98.2771}),

            // Matamoros
            Map.entry("868", new double[]{25.8697, -97.5027}),

            // Nuevo Laredo
            Map.entry("867", new double[]{27.4761, -99.5075}),

            // Ciudad Juárez
            Map.entry("656", new double[]{31.6904, -106.4245}),

            // Ensenada
            Map.entry("646", new double[]{31.8667, -116.6000}),

            // Los Mochis
            Map.entry("668", new double[]{25.7905, -108.9881}),

            // Mazatlán
            Map.entry("669", new double[]{23.2494, -106.4111})
    );

    public double[] getCoordinates(String lada) {
        return COORDS.get(lada);
    }

    public boolean hasLada(String lada) {
        return COORDS.containsKey(lada);
    }
}