package com.app.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APIResponse <T> {
    // The status of the API response, indicating success or failure.
    private String status;

    // The HTTP status code associated with the API response.
    private Integer httpStatus;

    // A human-readable message providing additional information about the API response.
    private String message;

    // The data payload included in the API response, holding the actual content.
    private T data;

    /**
     * Creates an APIResponse for a successful operation.
     *
     * @param data             The data to include in the response.
     * @param <T>              The type of data to be included in the response.
     * @return An APIResponse indicating a successful operation.
     */
    public static <T> APIResponse<T> ok(T data, String message) {
        return APIResponse.<T>builder()
                .httpStatus(HttpStatus.OK.value())
                .status(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an APIResponse for a bad request operation.
     *
     * @param data             The data to include in the response.
     * @param <T>              The type of data to be included in the response.
     * @return An APIResponse indicating a failed operation.
     */
    public static <T> APIResponse<T> badRequest(T data, String message) {
        return APIResponse.<T>builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an APIResponse for a forbidden operation.
     *
     * @param data             The data to include in the response.
     * @param <T>              The type of data to be included in the response.
     * @return An APIResponse indicating a failed operation.
     */
    public static <T> APIResponse<T> forbidden(T data, String message) {
        return APIResponse.<T>builder()
                .httpStatus(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an APIResponse for a not found operation.
     *
     * @param data             The data to include in the response.
     * @param <T>              The type of data to be included in the response.
     * @return An APIResponse indicating a failed operation.
     */
    public static <T> APIResponse<T> notFound(T data, String message) {
        return APIResponse.<T>builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an APIResponse for an unauthorized operation.
     *
     * @param data             The data to include in the response.
     * @param <T>              The type of data to be included in the response.
     * @return An APIResponse indicating a failed operation.
     */
    public static <T> APIResponse<T> unauthorized(T data, String message) {
        return APIResponse.<T>builder()
                .httpStatus(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an APIResponse for an internal server error.
     *
     * @param data             The data to include in the response.
     * @param <T>              The type of data to be included in the response.
     * @return An APIResponse indicating a failed operation.
     */
    public static <T> APIResponse<T> internalServerError(T data, String message) {
        return APIResponse.<T>builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }
}
