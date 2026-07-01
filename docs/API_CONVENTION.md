# 공통 API 응답 / 예외 처리 컨벤션

## 1. 응답 포맷

모든 API 응답은 `support:common`의 `ApiResponse<T>`로 감싸서 반환한다.

```java
public record ApiResponse<T>(boolean success, T data, ErrorPayload error) {
    public static <T> ApiResponse<T> success(T data) { ... }
    public static ApiResponse<Void> empty() { ... }
    public static <T> ApiResponse<T> error(String code, String message) { ... }

    public record ErrorPayload(String code, String message) { }
}
```

### 성공 응답

```json
{
  "success": true,
  "data": { "id": 1, "name": "beenie" },
  "error": null
}
```

반환할 데이터가 없는 경우 `ApiResponse.empty()`를 사용한다.

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

### 실패 응답

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "COMMON-404",
    "message": "요청한 리소스를 찾을 수 없습니다."
  }
}
```

Controller에서는 `ApiResponse.success(data)`만 신경 쓰면 되고, 에러 응답은
아래 예외 처리 흐름을 통해 `GlobalExceptionHandler`가 자동으로 만들어준다.

## 2. 예외 처리

### BusinessException + ErrorCode

- `support:exception`의 `ErrorCode` enum이 `(HttpStatus, code, message)`를 정의한다.
- 도메인/서비스 로직에서 검증 실패, 리소스 없음 등의 상황은 `BusinessException(ErrorCode)`를 던진다.

```java
if (user == null) {
    throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
}
```

### 기본 제공 ErrorCode

| code | HTTP Status | 설명 |
|---|---|---|
| `COMMON-400` | 400 Bad Request | 요청 값 검증 실패 |
| `COMMON-404` | 404 Not Found | 리소스를 찾을 수 없음 |
| `COMMON-500` | 500 Internal Server Error | 처리되지 않은 서버 오류 |

도메인이 늘어나면 `USER-404`, `ORDER-409`처럼 **도메인 접두사 + HTTP 상태 코드** 조합으로
`ErrorCode`에 항목을 추가한다. 공통 상황(`COMMON-*`)과 도메인 특화 상황을 구분하기 위함이다.

### GlobalExceptionHandler

`core/web/GlobalExceptionHandler`(`@RestControllerAdvice`)가 아래 세 가지를 처리한다.

| 예외 | 처리 방식 |
|---|---|
| `BusinessException` | 예외가 가진 `ErrorCode`의 status/code/message를 그대로 응답 |
| `MethodArgumentNotValidException` (`@Valid` 실패) | `COMMON-400` + 필드별 검증 메시지를 합쳐서 응답 |
| 그 외 모든 `Exception` | `COMMON-500`으로 응답 (내부 예외 메시지는 노출하지 않음) |

새로운 예외 상황을 표준화하고 싶다면, 새 `ErrorCode`를 추가하고 해당 상황에서
`BusinessException(ErrorCode.XXX)`를 던지는 것만으로 충분하다. `GlobalExceptionHandler`를
수정할 필요는 없다.

## 3. 검증 규칙

`GlobalExceptionHandlerTest`(`core/src/test`)가 `BusinessException`과 예상치 못한 예외가
올바른 상태 코드/응답 형태로 변환되는지 검증한다. 새 예외 처리 분기를 추가하면 테스트도 함께 갱신한다.
