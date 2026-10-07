# Patch Exercise Notes

## What I fixed

1. **Search query filtering**
   - Fixed SQL operator precedence in `TaskRepository`.
   - Ensured archived tasks and status filters are applied correctly to both title and description searches.

2. **Task API validation**
   - Added validation for status, page, and page size.
   - Invalid values now return HTTP 400 instead of causing server errors.
   - Limited page size to a maximum of 100.

3. **Removed artificial API delay**
   - Removed unnecessary `Thread.sleep()` from `TaskController` to avoid adding latency to every request.

4. **Structured logging**
   - Replaced `System.out.println` with SLF4J logging.

5. **Request logging middleware**
   - Added `RequestLoggingFilter` to log HTTP method, path, response status, and request duration.

6. **API cache control**
   - Added no-cache response headers to prevent stale task data.

7. **Safe pagination**
   - Prevented integer overflow when calculating pagination offsets.

## Verification

Tested task search and status filtering through the frontend. Also verified API requests return successfully and request logging appears in the Spring Boot terminal.

## Not fixed

I intentionally avoided unrelated or larger changes such as API versioning, monitoring infrastructure, and audit logging to keep the patch focused and low-risk.

## AI usage

AI assistance was used to identify bugs, suggest focused fixes, and help verify implementation. I reviewed and tested the changes locally before applying them.