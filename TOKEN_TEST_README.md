# FishGame Token Middleware Testing Tool

This Python script tests the token verification middleware in the FishGame application. It verifies that tokens are correctly generated, validated, and that protected endpoints properly enforce token verification.

## Requirements

- Python 3.6+
- `requests` library

Install the required library:

```bash
pip install requests
```

## How to Use

### Basic Usage

```bash
python test_token_middleware.py
```

This will run the basic test using default values:
- URL: http://localhost:8080
- Secret: 123456789
- Endpoint: user/basic
- User ID: testuser

### Customizing Tests

You can customize the test using command line arguments:

```bash
python test_token_middleware.py --url http://example.com:8080 --secret your_secret --endpoint some/endpoint --userid your_user --test-all
```

### Arguments

- `--url`: Base URL of the FishGame server (default: http://localhost:8080)
- `--secret`: Token secret that matches the server's `security.token.secret` configuration (default: 123456789)
- `--endpoint`: Endpoint to test without the leading slash (default: user/basic)
- `--userid`: User ID parameter for endpoints that require it (default: testuser)
- `--test-all`: Run all test cases including invalid tokens and timestamp tests

## Headers Used

The script uses three important headers:
- `FishGame-Request-Token`: The generated authentication token
- `FishGame-Request-Token-Timestamp`: Timestamp used for token generation
- `FishGame-Request-Timestamp`: Timestamp used for request freshness verification

## Test Cases

The script includes the following test cases:

1. **Server Token Test**: Obtains a token from the server and uses it to access a protected endpoint
2. **Local Token Test**: Generates a token locally using the same algorithm as the server and verifies it works
3. **Invalid Token Test**: Tests that the middleware rejects invalid tokens
4. **Old Timestamp Test**: Tests with an old timestamp (1 hour in the past)
5. **Future Timestamp Test**: Tests with a future timestamp (1 hour in the future)

Tests 2-5 only run when the `--test-all` flag is specified.

## Example Output

```
Testing FishGame token middleware at http://localhost:8080

--- Test 1: Get token from server and use it ---
Got token: abcdef1234... timestamp: 1631234567890
Response status: 200
Response body: {"code":200,"msg":"User found","userId":"testuser","username":"Test User"}
```

## Troubleshooting

- Make sure the FishGame server is running at the specified URL
- Verify that the secret matches the one configured in your server's application.properties
- Check that the endpoint and userId parameters are valid for your application
- If you're getting timestamp validation errors, note that by default the server rejects timestamps older than 5 minutes 