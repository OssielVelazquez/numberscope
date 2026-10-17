package com.numberscope.phone;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber;
import com.numberscope.phone.dto.PhoneAnalyzeResponse;
import com.numberscope.region.LadaCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class PhoneAnalyzerService {

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private final LadaCoordinates ladaCoordinates;

    public PhoneAnalyzerService(LadaCoordinates ladaCoordinates) {
        this.ladaCoordinates = ladaCoordinates;
    }

    public PhoneAnalyzeResponse analyze(String rawInput, String defaultRegion) {
        if (rawInput == null || rawInput.isBlank()) {
            return errorResponse(rawInput, "Entrada vacía.");
        }

        String region = (defaultRegion == null || defaultRegion.isBlank())
                ? "MX"
                : defaultRegion.toUpperCase(Locale.ROOT);

        Phonenumber.PhoneNumber parsed;
        try {
            parsed = phoneUtil.parse(rawInput.trim(), region);
        } catch (NumberParseException e) {
            return errorResponse(rawInput, "No se pudo interpretar el número: " + e.getMessage());
        }

        boolean valid = phoneUtil.isValidNumber(parsed);
        String e164 = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);

        int countryCode = parsed.getCountryCode();
        String country = phoneUtil.getRegionCodeForCountryCode(countryCode);
        if (country == null) country = "Desconocido";

        String lineType = lineTypeLabel(phoneUtil.getNumberType(parsed));

        // Buscar la región del bloque de numeración (LADA)
        Double regionLat = null;
        Double regionLng = null;
        String regionName = null;
        String lada = null;

        if ("MX".equals(country)) {
            String national = String.valueOf(parsed.getNationalNumber());
            lada = findLada(national);
            if (lada != null) {
                double[] coords = ladaCoordinates.getCoordinates(lada);
                if (coords != null) {
                    regionLat = coords[0];
                    regionLng = coords[1];
                    regionName = "Región LADA " + lada;
                }
            }
        }

        return new PhoneAnalyzeResponse(
                rawInput,
                valid,
                e164,
                country,
                country,
                null,
                lineType,
                List.of(),
                regionLat,
                regionLng,
                regionName,
                lada,
                null
        );
    }

    /**
     * Determina la LADA del número intentando primero 3 dígitos y luego 2.
     * Cubre los formatos de numeración mexicanos.
     */
    private String findLada(String national) {
        if (national.length() < 10) return null;

        String lada3 = national.substring(0, 3);
        if (ladaCoordinates.hasLada(lada3)) {
            return lada3;
        }

        String lada2 = national.substring(0, 2);
        if (ladaCoordinates.hasLada(lada2)) {
            return lada2;
        }

        return null;
    }

    private String lineTypeLabel(PhoneNumberType type) {
        return switch (type) {
            case MOBILE -> "Móvil";
            case FIXED_LINE -> "Fijo";
            case FIXED_LINE_OR_MOBILE -> "Fijo o móvil";
            case TOLL_FREE -> "Línea gratuita";
            case PREMIUM_RATE -> "Tarifa premium";
            case VOIP -> "VoIP";
            case SHARED_COST -> "Costo compartido";
            case PERSONAL_NUMBER -> "Número personal";
            case PAGER -> "Buscapersonas";
            case UAN -> "UAN";
            case VOICEMAIL -> "Buzón de voz";
            default -> "Desconocido";
        };
    }

    private PhoneAnalyzeResponse errorResponse(String raw, String msg) {
        return new PhoneAnalyzeResponse(
                raw, false, null, null, null, null, null,
                List.of(), null, null, null, null, msg
        );
    }
}