package com.example.rentalapartments.controller;

import com.example.rentalapartments.dto.ApartmentDto;
import com.example.rentalapartments.dto.ErrorResponse;
import com.example.rentalapartments.dto.PageResponse;
import com.example.rentalapartments.mapper.ApartmentMapper;
import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;
import com.example.rentalapartments.service.ApartmentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apartments")
@Tag(name = "Apartments", description = "CRUD, фільтрація, пагінація та часткове оновлення квартир")
public class ApartmentRestController {

    private final ApartmentService service;
    private final ObjectMapper objectMapper;

    public ApartmentRestController(ApartmentService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Отримати список квартир",
            description = "Повертає сторінку квартир. Підтримує фільтрацію за містом, кількістю кімнат, "
                    + "максимальною ціною та мінімальною площею, а також пагінацію параметрами page і size.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Квартири успішно отримано",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некоректні параметри запиту",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponse<ApartmentDto>> list(
            @Parameter(description = "Місто") @RequestParam(required = false) String city,
            @Parameter(description = "Кількість кімнат") @RequestParam(required = false) Integer rooms,
            @Parameter(description = "Максимальна ціна, грн/міс") @RequestParam(required = false) Integer maxPrice,
            @Parameter(description = "Мінімальна площа, м²") @RequestParam(required = false) Double minArea,
            @Parameter(description = "Номер сторінки, починаючи з 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Кількість елементів на сторінці") @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 50) {
            throw new IllegalArgumentException("page має бути ≥ 0, size — від 1 до 50");
        }
        SearchCriteria criteria = new SearchCriteria();
        criteria.setCity(city);
        criteria.setRooms(rooms);
        criteria.setMaxPrice(maxPrice);
        criteria.setMinArea(minArea);

        List<ApartmentDto> content = service.search(criteria, page, size).stream()
                .map(ApartmentMapper::toDto).toList();
        long total = service.count(criteria);

        return ResponseEntity.ok(new PageResponse<>(content, page, size, total));
    }

    @Operation(summary = "Отримати квартиру за ідентифікатором",
            description = "Повертає повну інформацію про одну квартиру.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Квартиру знайдено",
                    content = @Content(schema = @Schema(implementation = ApartmentDto.class))),
            @ApiResponse(responseCode = "404", description = "Квартиру з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApartmentDto> getOne(
            @Parameter(description = "Ідентифікатор квартири", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(ApartmentMapper.toDto(service.findById(id)));
    }

    @Operation(summary = "Створити квартиру",
            description = "Створює нову квартиру на основі переданих даних і повертає її разом із заголовком Location.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Квартиру успішно створено",
                    content = @Content(schema = @Schema(implementation = ApartmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Дані квартири некоректні",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApartmentDto> create(@Valid @RequestBody ApartmentDto dto) {
        Apartment saved = service.save(ApartmentMapper.toEntity(dto));
        URI location = URI.create("/api/apartments/" + saved.getId());
        return ResponseEntity.created(location).body(ApartmentMapper.toDto(saved));
    }

    @Operation(summary = "Повністю оновити квартиру",
            description = "Замінює всі поля квартири переданими значеннями (окрім ідентифікатора).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Квартиру успішно оновлено",
                    content = @Content(schema = @Schema(implementation = ApartmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Дані квартири некоректні",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Квартиру з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApartmentDto> update(
            @Parameter(description = "Ідентифікатор квартири", example = "1") @PathVariable Long id,
            @Valid @RequestBody ApartmentDto dto) {
        service.findById(id); // кине 404, якщо квартири немає
        Apartment entity = ApartmentMapper.toEntity(dto);
        entity.setId(id);
        Apartment saved = service.save(entity);
        return ResponseEntity.ok(ApartmentMapper.toDto(saved));
    }

    @Operation(summary = "Частково оновити квартиру (JSON Merge Patch, RFC 7386)",
            description = "Оновлює лише ті поля, що передані в тілі запиту. Поле зі значенням null видаляє "
                    + "(скидає) відповідне поле. Content-Type запиту — application/merge-patch+json.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Квартиру успішно оновлено",
                    content = @Content(schema = @Schema(implementation = ApartmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Патч некоректний або призвів до некоректних даних",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Квартиру з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON Merge Patch: об’єкт лише з тими полями квартири, які потрібно змінити. "
                    + "Поля, яких немає в тілі запиту, залишаються без змін.",
            required = true,
            content = @Content(mediaType = "application/merge-patch+json",
                    schema = @Schema(implementation = ApartmentDto.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"price\": 17500, \"description\": \"Новий опис\"}")))
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ApartmentDto> patch(
            @Parameter(description = "Ідентифікатор квартири", example = "1") @PathVariable Long id,
            @Parameter(description = "JSON Merge Patch (RFC 7386): об'єкт лише з полями, які треба змінити",
                    example = "{\"price\": 16000}")
            @RequestBody String patchJson) {
        
        JsonNode patchNode;
        try {
            patchNode = objectMapper.readTree(patchJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некоректний формат JSON у тілі запиту: " + e.getMessage());
        }

        Apartment existing = service.findById(id);
        JsonNode target = objectMapper.valueToTree(ApartmentMapper.toDto(existing));
        JsonNode merged = mergePatch(target, patchNode);

        ApartmentDto mergedDto;
        try {
            mergedDto = objectMapper.treeToValue(merged, ApartmentDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некоректний формат патча: " + e.getMessage());
        }

        Apartment entity = ApartmentMapper.toEntity(mergedDto);
        entity.setId(id);
        Apartment saved = service.save(entity);
        return ResponseEntity.ok(ApartmentMapper.toDto(saved));
    }

    @Operation(summary = "Видалити квартиру", description = "Видаляє квартиру за ідентифікатором. Квартиру, для якої існують бронювання, видалити не можна.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Квартиру успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Квартиру з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Квартиру не можна видалити: для неї існують бронювання",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Ідентифікатор квартири", example = "1") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private JsonNode mergePatch(JsonNode target, JsonNode patch) {
        if (patch == null || !patch.isObject()) {
            return patch;
        }
        ObjectNode result = target != null && target.isObject()
                ? ((ObjectNode) target).deepCopy()
                : objectMapper.createObjectNode();

        Iterator<Map.Entry<String, JsonNode>> fields = patch.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getValue().isNull()) {
                result.remove(entry.getKey());
            } else {
                JsonNode currentChild = result.has(entry.getKey())
                        ? result.get(entry.getKey())
                        : objectMapper.createObjectNode();
                result.set(entry.getKey(), mergePatch(currentChild, entry.getValue()));
            }
        }
        return result;
    }
}
