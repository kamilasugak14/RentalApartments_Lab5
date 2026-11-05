package com.example.rentalapartments.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rentalapartments.dto.BatchBookingRequest;
import com.example.rentalapartments.dto.BookingDto;
import com.example.rentalapartments.dto.ErrorResponse;
import com.example.rentalapartments.mapper.BookingMapper;
import com.example.rentalapartments.model.Booking;
import com.example.rentalapartments.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Бронювання квартир: пошук, створення (зокрема пакетне в одній транзакції), "
        + "скасування та видалення")
public class BookingRestController {

    private final BookingService service;

    public BookingRestController(BookingService service) {
        this.service = service;
    }

    @Operation(summary = "Отримати список бронювань",
            description = "Повертає список бронювань. Можна відфільтрувати за іменем клієнта "
                    + "(пошук за фрагментом без урахування регістру) та/або за ідентифікатором квартири. "
                    + "Без параметрів повертає всі бронювання.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронювання успішно отримано",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingDto.class)))),
            @ApiResponse(responseCode = "400", description = "Некоректні параметри запиту",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<BookingDto>> list(
            @Parameter(description = "Фрагмент імені клієнта", example = "Іван")
            @RequestParam(required = false) String clientName,
            @Parameter(description = "Ідентифікатор квартири", example = "1")
            @RequestParam(required = false) Long apartmentId) {

        List<Booking> bookings;
        if (clientName != null && !clientName.isBlank()) {
            bookings = service.findByClientName(clientName);
            if (apartmentId != null) {
                bookings = bookings.stream()
                        .filter(b -> apartmentId.equals(b.getApartmentId()))
                        .toList();
            }
        } else if (apartmentId != null) {
            bookings = service.findByApartmentId(apartmentId);
        } else {
            bookings = service.findAll();
        }
        return ResponseEntity.ok(bookings.stream().map(BookingMapper::toDto).toList());
    }

    @Operation(summary = "Отримати бронювання за ідентифікатором",
            description = "Повертає повну інформацію про одне бронювання.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронювання знайдено",
                    content = @Content(schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "404", description = "Бронювання з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getOne(
            @Parameter(description = "Ідентифікатор бронювання", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(BookingMapper.toDto(service.findById(id)));
    }

    @Operation(summary = "Створити бронювання",
            description = "Бронює одну квартиру для клієнта на вказаний період. Перед створенням "
                    + "перевіряється, що квартира існує та що період не перетинається з іншими "
                    + "активними бронюваннями цієї квартири. Статус нового бронювання – ACTIVE. "
                    + "У відповіді повертається створене бронювання та заголовок Location.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Бронювання успішно створено",
                    content = @Content(schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "400", description = "Дані бронювання некоректні",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Квартиру з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Квартира вже заброньована на цей період",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BookingDto> create(@Valid @RequestBody BookingDto dto) {
        Booking saved = service.create(BookingMapper.toEntity(dto));
        URI location = URI.create("/api/bookings/" + saved.getId());
        return ResponseEntity.created(location).body(BookingMapper.toDto(saved));
    }

    @Operation(summary = "Забронювати кілька квартир в одній транзакції",
            description = "Бронює одразу кілька квартир для одного клієнта на однаковий період. "
                    + "Операція виконується в одній транзакції: або бронюються всі зазначені квартири, "
                    + "або жодної. Якщо хоча б для однієї квартири виникає помилка (квартири не існує, "
                    + "вона вже заброньована на цей період або дані некоректні), усі бронювання, "
                    + "створені в межах цього запиту, скасовуються.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Усі квартири успішно заброньовано",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingDto.class)))),
            @ApiResponse(responseCode = "400", description = "Дані запиту некоректні",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Одну з квартир не знайдено (жодне бронювання не створено)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Одна з квартир уже заброньована на цей період (жодне бронювання не створено)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/batch")
    public ResponseEntity<List<BookingDto>> createBatch(@Valid @RequestBody BatchBookingRequest request) {
        List<Booking> created = service.createMany(request.getClientName(),
                request.getApartmentIds(), request.getDateFrom(), request.getDateTo());
        return ResponseEntity.status(201).body(created.stream().map(BookingMapper::toDto).toList());
    }

    @Operation(summary = "Скасувати бронювання",
            description = "Змінює статус бронювання на CANCELLED. Скасовані бронювання не блокують "
                    + "квартиру: на ці дати її можна забронювати знову.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронювання скасовано",
                    content = @Content(schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "404", description = "Бронювання з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancel(
            @Parameter(description = "Ідентифікатор бронювання", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(BookingMapper.toDto(service.cancel(id)));
    }

    @Operation(summary = "Видалити бронювання",
            description = "Остаточно видаляє бронювання з бази даних.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Бронювання успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Бронювання з таким id не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Ідентифікатор бронювання", example = "1") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
