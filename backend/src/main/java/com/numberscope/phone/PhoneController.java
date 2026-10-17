package com.numberscope.phone;

import com.numberscope.phone.dto.PhoneAnalyzeRequest;
import com.numberscope.phone.dto.PhoneAnalyzeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phone")
public class PhoneController {

    private final PhoneAnalyzerService analyzerService;
    private final PhoneQueryRepository repository;

    public PhoneController(PhoneAnalyzerService analyzerService, PhoneQueryRepository repository) {
        this.analyzerService = analyzerService;
        this.repository = repository;
    }

    @PostMapping("/analyze")
    public ResponseEntity<PhoneAnalyzeResponse> analyze(@Valid @RequestBody PhoneAnalyzeRequest request) {
        PhoneAnalyzeResponse response = analyzerService.analyze(request.rawInput(), request.defaultRegion());

        // Si el número tiene E.164 válido, comprobamos si ya existe
        PhoneQuery entity;
        if (response.e164() != null) {
            entity = repository
                    .findFirstByE164OrderByCreatedAtDesc(response.e164())
                    .orElseGet(PhoneQuery::new);
        } else {
            entity = new PhoneQuery();
        }

        // Actualizar todos los campos (sirve tanto para nueva entidad como para existente)
        entity.setRawInput(response.rawInput() == null ? "" : response.rawInput());
        entity.setE164(response.e164());
        entity.setCountry(response.country());
        entity.setRegion(response.region());
        entity.setCarrier(response.carrier());
        entity.setLineType(response.lineType());
        entity.setLada(response.lada());
        entity.setValid(response.valid());

        // Si es existente, actualizamos la fecha para que suba al principio
        if (entity.getId() != null) {
            entity.setCreatedAt(java.time.Instant.now());
        }

        repository.save(entity);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public List<PhoneQuery> history() {
        return repository.findTop100ByOrderByCreatedAtDesc();
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("ok", true, "deleted", id));
    }

    @DeleteMapping("/history")
    public ResponseEntity<?> clearAll() {
        long count = repository.count();
        repository.deleteAll();
        return ResponseEntity.ok(Map.of("ok", true, "deleted", count));
    }
}